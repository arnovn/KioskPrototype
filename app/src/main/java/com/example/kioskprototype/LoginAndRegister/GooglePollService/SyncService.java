package com.example.kioskprototype.LoginAndRegister.GooglePollService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Service class in charge of polling the Google Oauth server for the access token of the user trying to login
 */
public class SyncService extends Service {

    /**
     * Handler used to schedule Runnable ()polling task) in this thread
     */
    private Handler handler;

    /**
     * Id of the client
     */
    String clientId;

    /**
     * ClientSecret: deprecated, code2 is used for this
     */
    String clientSecret;

    /**
     * Id of this application inside the Oauth server retrieved from the Oauth server
     */
    String deviceCode;

    /**
     * ClientSecret
     */
    String code2 = "EcnJRp5h48uirEFovlNFofuI";

    /**
     * Polling interval
     */
    int interval;

    /**
     * Context of the activity
     */
    Context mContext;

    /**
     * Response of the HTTP request
     */
    int responseCode;

    /**
     * Retrieved acces token & extra information coupled to this access token
     */
    String accessToken;
    int expiresIn;
    String refreshToken;
    String scope;
    String tokenType;
    String idToken;

    /**
     * Paramters of the HTTP request
     */
    String params;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        clientId = intent.getStringExtra("ClientId");
        clientSecret = intent.getStringExtra("ClientSecret");
        deviceCode = intent.getStringExtra("DeviceCode");
        interval = intent.getIntExtra("Interval",5);
        handler = new Handler();
        handler.post(runnableService);
        mContext = getApplicationContext();

        return START_STICKY;
    }

    public void signInSuccesfull(){
        Intent broadcastIntent = new Intent("GOOGLE_LOGIN");
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        Bundle bundle = new Bundle();
        bundle.putString("AccessToken", accessToken);
        bundle.putInt("ExpiresIn", expiresIn);
        bundle.putString("Scope", scope);
        bundle.putString("TokenType", tokenType);
        bundle.putString("IdToken", idToken);
        broadcastIntent.putExtras(bundle);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnableService);
        stopSelf();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Runnable runnableService = new Runnable(){

        @Override
        public void run() {
            //create AsyncTask here


            new GoogleSigninChecker2().execute();
            handler.postDelayed(runnableService, interval*1000);
        }
    };

    @SuppressLint("StaticFieldLeak")
    class GoogleSigninChecker2 extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            String output;

            try {
                params ="client_id=" + clientId+ "&client_secret=" + code2 +"&code="+ deviceCode +"&grant_type=http://oauth.net/grant_type/device/1.0";


                URL url = new URL("https://oauth2.googleapis.com/token");

                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                String urlParameters = params;
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

                connection.setDoOutput(true);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());

                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                responseCode = connection.getResponseCode();

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder responseOutput = new StringBuilder();

                while ((line=br.readLine()) != null){
                    responseOutput.append(line);
                }
                br.close();

                output = responseOutput.toString();
                return output;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Ok";
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println("Responsecode: " + responseCode);
            if(responseCode == 200){
                try {
                    JSONObject result = new JSONObject(s);
                    accessToken = result.getString("access_token");
                    expiresIn = result.getInt("expires_in");
                    refreshToken = result.getString("refresh_token");
                    scope = result.getString("scope");
                    tokenType = result.getString("token_type");
                    idToken = result.getString("id_token");
                    System.out.println("Result: " + result);
                    signInSuccesfull();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("Response code: " + responseCode);
                System.out.println("Response body: " + s);
            }
        }
    }
}
