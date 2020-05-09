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
import com.example.kioskprototype.MainActivity;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterAndObjects.ABikeObject;
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

    /**
     * ImageView visualizing the QR-code that needs to be scanned in order to pay using PayPal
     */
    ImageView qrView;

    /**
     * Selected bike which needs to be rented
     */
    ABikeObject bikeObject;

    /**
     * Mail of the user
     */
    String mail;

    /**
     * Type of flow
     */
    String type;

    /**
     * Id of the user
     */
    int userId;

    /**
     * Id of the order
     */
    int orderId;

    /**
     * Amount of credits the user wants to add
     */
    int amount;

    /**
     * Broadcasted order id
     */
    int broadcastOrderId;

    /**
     * Broadcasted order status
     */
    int broadcastOrderStatus;

    /**
     * Credits of the user
     */
    int credits;

    /**
     * Poll intent for polling the status of the order
     */
    Intent statusPollIntent;

    /**
     * BroadcastReceiver for the payment status poll service
     */
    OrderStatusBroadcastReceiver orderStatusBroadcastReceiver;

    /**
     * Filter of broadcasts
     */
    IntentFilter filter;

    /**
     * To finalize the order, the database needs to be updated through 3 async tasks:
     *  - Update the user: 1. set the credits 2. connect bike to his account
     *  - Update the bike: 1. set unavailable 2. connect user unlock code to bike (hashed)
     *  - Create new bike order: We create new bike order in charge of handling the bike rent (bike duration & cost will be saved here)
     */
    boolean asyncTask1, asyncTask2, asyncTask3, asyncTask4;

    /**
     * When the activity is created:
     *  - retrieve necessary information from previous activity
     *  - set async tasks on false (completion)
     *  - Create the QR-code which will contain data that, when read, can be converted into a JSON object
     *  - Start the broadcast receiver
     *  - Start polling the database on the progress
     * @param savedInstanceState
     *                  Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal_payment);

        getPreviousInfo();

        broadcastOrderStatus = 0;
        asyncTask1 = false;
        asyncTask2 = false;
        asyncTask3 = false;
        asyncTask4 = false;

        qrView = findViewById(R.id.qrPaypalView);

        initQR();
        setBroadcastReceiver();
        setBroadcastFilter();

        startPolling();
    }

    /**
     * Retrieve info from previous activity
     */
    private void getPreviousInfo(){
        type = getIntent().getStringExtra("Type");
        credits = getIntent().getIntExtra("Credits",0);
        bikeObject = (ABikeObject) getIntent().getSerializableExtra("Bike");
        mail = getIntent().getStringExtra("Mail");
        userId = getIntent().getIntExtra("UserId", 0);
        orderId = getIntent().getIntExtra("OrderId", 0);
        amount = getIntent().getIntExtra("Amount", 0);
        Toast.makeText(getApplicationContext(),"Type: " + type, Toast.LENGTH_SHORT).show();
    }

    /**
     * Initialize the QR code to be displayed on the Kiosk with the necessary info
     */
    private void initQR(){
        if(credits<0){
            amount = amount - credits;
        }
        String jsonData = "{\"userId\":\""+ userId +"\",\"orderId\":\""+  orderId +"\", \"amount\":\""+ amount +"\"}";
        QRGEncoder qrgEncoder = new QRGEncoder(jsonData,null, QRGContents.Type.TEXT, 500);
        try {
            Bitmap qrBit = qrgEncoder.encodeAsBitmap();
            qrView.setImageBitmap(qrBit);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method in charge of initializing the broadcastreceiver and handling the result when something has been received
     */
    private void setBroadcastReceiver(){
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
                        System.out.println("Order status: FAILED");
                        break;
                    default:
                        System.out.println("ERROR Order status: DEFAULT");
                }
            }
        });
    }

    /**
     * The the filter of the broadcast receiver to "PAYMENY STATUS", only handle these type of broadcasts
     */
    private void setBroadcastFilter(){
        filter = new IntentFilter();
        filter.addAction("PAYMENT_STATUS");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(orderStatusBroadcastReceiver, filter);
    }

    /**
     * Method in charge of initializing the polling service
     */
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
                        "wowkioskmail@gmail.com", "arnovanneste96@gmail.com");
            } catch (Exception e) {
                System.out.println("The exception :" + e);
            }
        };
        Thread mailThread = new Thread(mailRunnable);
        mailThread.start();
    }

    /**
     * When the order is successful it needs to be finalized:
     *  - AsyncTask 1: Update the user
     *      1. set the credits
     *      2. connect bike to his account
     *  - AsyncTask 2: Update the bike
     *      1. set unavailable
     *      2. connect user unlock code to bike (hashed)
     *  - AsyncTask 3: Create new bike order
     *      1. We create new bike order in charge of handling the bike rent (bike duration & cost will be saved here)
     */
    private void finalizeOrder(){
        //Start the 3 connections
        Toast.makeText(getApplicationContext(),"Finalizing order", Toast.LENGTH_SHORT).show();
        if(type.equals("rent")){
            Toast.makeText(getApplicationContext(),"RENT", Toast.LENGTH_SHORT).show();
            new ConnectionSetInputOrder().execute();
            new ConnectionSetUser().execute();
            new ConnectionSetBike().execute();
        }else if(type.equals("service")){
            Toast.makeText(getApplicationContext(),"Executing", Toast.LENGTH_SHORT).show();
            new ConnectionSetUserService().execute();
            new ConnectionUpdateUserDepths().execute();
        }
    }

    /**
     * When one of the tasks has successfully finished it is set to true
     * When all of the tasks have been set to true (the last async task successfully finished):
     *  - send confirmation mail
     *  - go to the final screen
     * @param task1
     *      AsyncTask 1: Update the user
     *            1. set the credits
     *            2. connect bike to his account
     * @param task2
     *      AsyncTask 2: Update the bike
     *            1. set unavailable
     *            2. connect user unlock code to bike (hashed)
     * @param task3
     *      AsyncTask 3: Create new bike order
     *            1. We create new bike order in charge of handling the bike rent (bike duration & cost will be saved here)
     */
    private void setTask(boolean task1, boolean task2, boolean task3, boolean task4){
        asyncTask1 = task1;
        asyncTask2 = task2;
        asyncTask3 = task3;
        asyncTask4 = task4;

        if(task1 && task2 && task3 && type.equals("rent")){
            sendConfirmationMail();
            toFinalScreen();
        }else if(task2 && task4 && type.equals("service")){
            Toast.makeText(getApplicationContext(),"TASK SUCESS", Toast.LENGTH_SHORT).show();
            sendConfirmationMail();

            Intent toMainIntent = new Intent(PaypalPayment.this, MainActivity.class);
            toMainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(toMainIntent);
        }
    }

    /**
     * Creates intent for last activity & starts this activity
     */
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
                    setTask(true,asyncTask2,asyncTask3,asyncTask4);
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
                System.out.println("executing");
                String host = "http://"+ getResources().getString(R.string.ip) +"/updateusercredit.php?userid="+ userId + "&bikeid=" + bikeObject.getId() + "&amount=" + amount;
                if(type.equals("service")){
                    System.out.println("executing2");
                    amount = amount + credits;
                    host = "http://"+ getResources().getString(R.string.ip) +"/updateusercreditservice.php?userid="+ userId + "&amount=" + amount;
                }
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
                System.out.println("executing3");
                if(result.equals("succes")){
                    Toast.makeText(getApplicationContext(),"Update of USER successul", Toast.LENGTH_SHORT).show();
                    setTask(asyncTask1,true,asyncTask3,asyncTask4);
                }else if(result.equals("error")){
                    Toast.makeText(getApplicationContext(),"Update of USER failed..", Toast.LENGTH_SHORT).show();
                }
                System.out.println("executing4");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Class in charge of updating the user so the bikeId is set to the id of the selected bike
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionSetUserService extends AsyncTask<String, String, String> {
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
                amount = amount + credits;
                String host = "http://"+ getResources().getString(R.string.ip) +"/updateusercreditservice.php?userid="+ userId + "&amount=" + amount;
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
                System.out.println("executing3");
                if(result.equals("succes")){
                    Toast.makeText(getApplicationContext(),"Update of USER successul", Toast.LENGTH_SHORT).show();
                    setTask(asyncTask1,true,asyncTask3,asyncTask4);
                }else if(result.equals("error")){
                    Toast.makeText(getApplicationContext(),"Update of USER failed..", Toast.LENGTH_SHORT).show();
                }
                System.out.println("executing4");
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
                    setTask(asyncTask1,asyncTask2, true, asyncTask4);
                }else if(result.equals("error")){
                    Toast.makeText(getApplicationContext(),"Update of BIKE failed..", Toast.LENGTH_SHORT).show();
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
    class ConnectionUpdateUserDepths extends AsyncTask<String, String, String> {
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
                String host = "http://"+ getResources().getString(R.string.ip) +"/updateuserdepths.php?userid="+ userId;
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
                    setTask(asyncTask1,asyncTask2, asyncTask3, true);
                }else if(result.equals("error")){
                    Toast.makeText(getApplicationContext(),"Update of BIKE failed..", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}