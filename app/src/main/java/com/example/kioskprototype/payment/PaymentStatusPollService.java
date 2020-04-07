package com.example.kioskprototype.payment;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.kioskprototype.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class PaymentStatusPollService extends Service {

    private Handler handler;

    private int orderId;

    private int paymentStatus;

    private int interval;

    private int timeoutCounter;

    private Context mContext;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        orderId = intent.getIntExtra("OrderId", 0);
        interval = intent.getIntExtra("PollInterval", 5);
        timeoutCounter = 0;

        handler = new Handler();
        handler.post(runnableService);
        mContext = getApplicationContext();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnableService);
        stopSelf();
        super.onDestroy();
    }

    private Runnable runnableService = new Runnable(){

        @Override
        public void run() {
            //create AsyncTask here
            if(timeoutCounter == 300){
                //After 5 min we timeout.
                broadCastTimedOut();
                new ConnectionDeleteOrder().execute();
            }

            new ConnectionPollOrderStatus().execute();
            timeoutCounter++;
            handler.postDelayed(runnableService, interval*1000);
        }
    };

    public void broadCastStatus(){
        Intent broadcastStatusIntent = new Intent();
        broadcastStatusIntent.setAction("PAYMENT_STATUS");
        broadcastStatusIntent.addCategory(Intent.CATEGORY_DEFAULT);
        Bundle bundle = new Bundle();
        bundle.putInt("OrderId", orderId);
        bundle.putInt("STATUS", paymentStatus);
        broadcastStatusIntent.putExtras(bundle);
        sendBroadcast(broadcastStatusIntent);
        System.out.println("BROADCASTING");
    }

    public void broadCastTimedOut(){
        Intent broadcastStatusIntent = new Intent();
        broadcastStatusIntent.setAction("PAYMENT_STATUS");
        broadcastStatusIntent.addCategory(Intent.CATEGORY_DEFAULT);
        Bundle bundle = new Bundle();
        bundle.putInt("OrderId", orderId);
        bundle.putInt("STATUS", 3);
        broadcastStatusIntent.putExtras(bundle);
        sendBroadcast(broadcastStatusIntent);
        System.out.println("BROADCASTING");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     *  Class in charge of creating new order entry for the payment, status: PENDING
     */
    @SuppressLint("StaticFieldLeak")
    public class ConnectionPollOrderStatus extends AsyncTask<String, String, String> {
        String result = "";

        /**
         * Method in charge of querying the database through an HTTP request.
         * @param strings
         *          Paramaters passed when the execution of the AsyncTask is called;
         * @return
         *          Returns the response of the database.
         */
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://"+ getResources().getString(R.string.ip) +"/pollcreditorder.php?orderid=" + orderId;
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
                System.out.println("The exception: "+e.getMessage());
                return "The exception: " + e.getMessage();
            }
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database:
         *  - if successful pending payment objects are created & added to the list
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonResult = new JSONObject(result);
                System.out.println("Result: " + jsonResult.toString());
                int success = jsonResult.getInt("success");
                if(success == 1){
                    JSONObject jsonObject = jsonResult.getJSONObject("status");
                    paymentStatus = jsonObject.getInt("progresscode");
                    broadCastStatus();
                    System.out.println(jsonObject.toString());
                }else if(success == 0){
                    paymentStatus = 0;
                    broadCastStatus();
                    Toast.makeText(getApplicationContext(),"Error: something went wrong when polling the payment",Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     *  Class in charge of creating new order entry for the payment, status: PENDING
     */
    @SuppressLint("StaticFieldLeak")
    public class ConnectionDeleteOrder extends AsyncTask<String, String, String> {
        String result = "";

        /**
         * Method in charge of querying the database through an HTTP request.
         * @param strings
         *          Paramaters passed when the execution of the AsyncTask is called;
         * @return
         *          Returns the response of the database.
         */
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://"+ getResources().getString(R.string.ip) +"/deletetimedoutorder.php?orderid=" + orderId;
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
                System.out.println("The exception: "+e.getMessage());
                return "The exception: " + e.getMessage();
            }
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database:
         *  - if successful pending payment objects are created & added to the list
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            try{
                if(result.equals("error")){
                    Toast.makeText(getApplicationContext(), "Timedout deletion failed.", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
