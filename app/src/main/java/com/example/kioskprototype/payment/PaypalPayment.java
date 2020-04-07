package com.example.kioskprototype.payment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.FinalScreen;
import com.example.kioskprototype.MailSender.GmailSender;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;
import com.google.zxing.WriterException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class PaypalPayment extends AppCompatActivity {

    //Sandbox account: sb-hu4yp1369124@business.example.com
    //ClientID: AWbtJWYvpTOJPPc_iO-4tqMjdRB0DmWm27a_23m61ctvWSyVs1wC25zmc25meWOtikVU56VW_dztbY9o
    //Secret: EGV-UG8S6BLBfdGsPljkVOB0uLQ0TwPetGu49CL_dJkD0WehJTR41iKUCh26vg0xmR1eMc5tLzdUU-Pz

    ImageView qrView;

    ABikeObject bikeObject;
    String mail;
    int userId;
    int orderId;
    int amount;

    Intent statusPollIntent;
    int broadcastOrderId;
    int broadcastOrderStatus;
    OrderStatusBroadcastReceiver orderStatusBroadcastReceiver;
    IntentFilter filter;

    boolean asyncTask1, asyncTask2, asyncTask3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal_payment);

        bikeObject = (ABikeObject) getIntent().getSerializableExtra("Bike");
        mail = getIntent().getStringExtra("Mail");
        userId = getIntent().getIntExtra("UserId", 0);
        orderId = getIntent().getIntExtra("OrderId", 0);
        amount = getIntent().getIntExtra("Amount", 0);
        broadcastOrderStatus = 0;
        asyncTask1 = false;
        asyncTask2 = false;
        asyncTask3 = false;

        qrView = findViewById(R.id.qrPaypalView);

        String jsonData = "{\"userId\":\""+ userId +"\",\"orderId\":\""+  orderId +"\", \"amount\":\""+ amount +"\"}";
        String jsonData2 = "{\"userId\":\""+ userId +"\", \"amount\": \"50\"}";

        QRGEncoder qrgEncoder = new QRGEncoder(jsonData,null, QRGContents.Type.TEXT, 500);
        try {
            Bitmap qrBit = qrgEncoder.encodeAsBitmap();
            qrView.setImageBitmap(qrBit);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        orderStatusBroadcastReceiver = new OrderStatusBroadcastReceiver();
        orderStatusBroadcastReceiver.setOrderStatusBroadcastListener(title -> {
            broadcastOrderId = orderStatusBroadcastReceiver.getOrderId();
            broadcastOrderStatus = orderStatusBroadcastReceiver.getOrderStatus();

            System.out.println("RECEIVED LISTENER");
            if(broadcastOrderId == orderId){
                switch(broadcastOrderStatus){
                    case 0:
                        //We wait
                        System.out.println("Order status: INIT");
                        break;
                    case 1:
                        //We wait
                        System.out.println("Order status: PENDING");
                        break;
                    case 2:
                        System.out.println("Order status: SUCCESS");
                        stopService(statusPollIntent);
                        finalizeOrder();
                        break;
                    case 3:
                        //Order will be deleted from database
                        System.out.println("Order status: TIMEDOUT");
                        stopService(statusPollIntent);
                        finish();
                        break;
                    case -1:
                        //TODO: handle when failed
                        System.out.println("Order status: FAILED");
                        break;
                    default:
                        System.out.println("ERROR Order status: DEFAULT");
                }
            }
        });

        filter = new IntentFilter();
        filter.addAction("PAYMENT_STATUS");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(orderStatusBroadcastReceiver, filter);

        startPolling();
    }

    private void startPolling(){
        statusPollIntent = new Intent(getApplicationContext(), PaymentStatusPollService.class);
        statusPollIntent.putExtra("OrderId", orderId);
        statusPollIntent.putExtra("PollInterval", 5);
        startService(statusPollIntent);
    }

    /**
     * When the payment is successful we send a confirmation mail
     */
    public void sendConfirmationMail(){
        Runnable mailRunnable = () -> {
            try {
                GmailSender sender = new GmailSender("wowkioskmail@gmail.com",
                        "kioskmail123");
                sender.sendMail("WOW kiosk Verification mail",
                        "Payment confirmation. \n \nYou've succesfull added €" + amount + " to your account."+
                        "\nConfirmation number: " + orderId,
                        "wowkioskmail@gmail.com", mail);
            } catch (Exception e) {
                System.out.println("The exception :" + e);
            }
        };
        Thread mailThread = new Thread(mailRunnable);
        mailThread.start();
    }

    private void finalizeOrder(){
        //Start the 3 connections
        new ConnectionSetInputOrder().execute();
        new ConnectionSetUser().execute();
        new ConnectionSetBike().execute();
    }

    private void setTask(boolean task1, boolean task2, boolean task3){
        asyncTask1 = task1;
        asyncTask2 = task2;
        asyncTask3 = task3;

        if(task1 & task2 & task3){
            sendConfirmationMail();
            toFinalScreen();
        }
    }

    private void toFinalScreen(){
        Intent finalIntent = new Intent(PaypalPayment.this, FinalScreen.class);
        finalIntent.putExtra("Bike", bikeObject);
        startActivity(finalIntent);
    }

    /**
     * Broadcast receiver, needed to receive data from the polling thread when it has received the access token.
     */
    public static class OrderStatusBroadcastReceiver extends BroadcastReceiver {

        int orderId;
        int orderStatus;

        public interface OrderStatusBroadcastListener {
            void onObjectReady(String title);
        }

        /**
         * Listener for this class.
         */
        private OrderStatusBroadcastListener listener;

        public OrderStatusBroadcastReceiver(){
            this.listener = null;
        }

        public void setOrderStatusBroadcastListener(OrderStatusBroadcastListener listener){
            this.listener = listener;
        }

        /**
         * Getters for the orderId
         * @return
         *              Order id of the broadcast sent
         */
        public int getOrderId() {
            return orderId;
        }

        /**
         * Getters for the orderStatus
         * @return
         *              Order status of the broadcast sent
         */
        public int getOrderStatus(){
            return orderStatus;
        }

        /**
         * When the BroadcastReceiver receives a broadcast we try to get the received data from it.
         * @param context
         *              Current context of the activity
         * @param intent
         *              PollingIntent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("RECEIVING");
            Bundle b = intent.getExtras();
            assert b != null;
            orderId = b.getInt("OrderId");
            orderStatus = b.getInt("STATUS");
            listener.onObjectReady("ready");
        }
    }

    /**
     * Class in charge of inserting the new order into the BikeOrder table of the MySql Database
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionSetInputOrder extends AsyncTask<String, String, String> {
        String result = "";

        /**
         * Method in charge of querying the database through an HTTP request.
         * @param params
         *          Paramaters passed when the execution of the AsyncTask is called;
         * @return
         *          Returns the response of the database.
         */
        @Override
        protected String doInBackground(String... params) {
            try{
                String host = "http://"+ getResources().getString(R.string.ip) +"/inputneworder.php?userid="+ userId + "&bikeid=" + bikeObject.getId();
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuilder stringBuffer = new StringBuilder();

                String line;
                while((line = reader.readLine()) != null){
                    stringBuffer.append(line);
                }
                reader.close();
                result = stringBuffer.toString();
            } catch (Exception e) {
                return "The exception: " + e.getMessage();
            }
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database:
         *  - If successful we set this task to true meaning it has finished
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            try{
                if(result.equals("succes")){
                    Toast.makeText(getApplicationContext(),"Insertion of ORDER successul", Toast.LENGTH_SHORT).show();
                    setTask(true,asyncTask2,asyncTask3);
                }else if(result.equals("error")){
                    Toast.makeText(getApplicationContext(),"Insertion of ORDER failed..", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Class in charge of updating the user so the bikeId is set to the id of the selected bike
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionSetUser extends AsyncTask<String, String, String> {
        String result = "";

        /**
         * Method in charge of querying the database through an HTTP request.
         * @param params
         *          Parameters passed when the execution of the AsyncTask is called;
         * @return
         *          Returns the response of the database.
         */
        @Override
        protected String doInBackground(String... params) {
            try{
                String host = "http://"+ getResources().getString(R.string.ip) +"/updateusercredit.php?userid="+ userId + "&bikeid=" + bikeObject.getId() + "&amount=" + amount;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuilder stringBuffer = new StringBuilder();

                String line;
                while((line = reader.readLine()) != null){
                    stringBuffer.append(line);
                }
                reader.close();
                result = stringBuffer.toString();
            } catch (Exception e) {
                return "The exception: " + e.getMessage();
            }
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database:
         *  - If successful we set this task to true meaning it has finished
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            try{
                if(result.equals("succes")){
                    Toast.makeText(getApplicationContext(),"Update of USER successul", Toast.LENGTH_SHORT).show();
                    setTask(asyncTask1,true,asyncTask3);
                }else if(result.equals("error")){
                    Toast.makeText(getApplicationContext(),"Update of USER failed..", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Class in charge of updating the selected bike so the user id in the MySql Database is set to the id of the user
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionSetBike extends AsyncTask<String, String, String> {
        String result = "";

        /**
         * Method in charge of querying the database through an HTTP request.
         * @param params
         *          Parameters passed when the execution of the AsyncTask is called;
         * @return
         *          Returns the response of the database.
         */
        @Override
        protected String doInBackground(String... params) {
            try{
                String host = "http://"+ getResources().getString(R.string.ip) +"/updatebike.php?userid="+ userId + "&bikeid=" + bikeObject.getId();
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuilder stringBuffer = new StringBuilder();

                String line;
                while((line = reader.readLine()) != null){
                    stringBuffer.append(line);
                }
                reader.close();
                result = stringBuffer.toString();
            } catch (Exception e) {
                return "The exception: " + e.getMessage();
            }
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database:
         *  - If successful we set this task to true meaning it has finished
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            try{
                if(result.equals("succes")){
                    Toast.makeText(getApplicationContext(),"Update of BIKE successful", Toast.LENGTH_SHORT).show();
                    setTask(asyncTask1,asyncTask2, true);
                }else if(result.equals("error")){
                    Toast.makeText(getApplicationContext(),"Update of BIKE failed..", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}