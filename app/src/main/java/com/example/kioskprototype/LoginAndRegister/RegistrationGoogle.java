package com.example.kioskprototype.LoginAndRegister;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.HashingObject;
import com.example.kioskprototype.InstructionVideo;
import com.example.kioskprototype.LoginAndRegister.GooglePollService.SyncService;
import com.example.kioskprototype.MailSender.GmailSender;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;
import com.example.kioskprototype.adapterView.PhoneDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

//Client id: 947283700713-eihb9cmvrb7te5v24bluo8dbc8dcneq3.apps.googleusercontent.com

/**
 * Class in charge of registering a new user to the system by using their Google account.
 */
public class RegistrationGoogle extends AppCompatActivity implements PhoneDialog.PhoneDialogListener{
    //UI layer objects:
    /**
     * TextView visualizing the url the user has to visit in order to sign in using Google at the UI layer
     */
    TextView googleUrlView;

    /**
     * TextView visualizing the code the user has to input at the google URL to sign in at the UI layer
     */
    TextView googleCodeView;

    /**
     * Dialog where the user can input his phone number in order to finish the registration, shown at UI layer
     */
    PhoneDialog phoneDialog;

    //Google sign in data
    /**
     * Device code of this device registered in the Google Oauth server
     */
    String deviceCode;

    /**
     * Code retrieved from the google Oauth server which the user needs to input at the given verification url for the sign in process.
     */
    String userCode;

    /**
     * Expiration time of the code at the google Oauth server
     */
    String expiresIn;

    /**
     * Url received from the google Oauth server where the user needs to input the userCode
     */
    String verificationUrl;

    /**
     * Polling interval to check when the user has successfully inputted the code at the verification url
     */
    int interval;

    /**
     * New Intent (service) thread which will poll the Oauth server checking if the sign in status
     */
    Intent pollIntent;

    /**
     * When the pollIntent services is ready a broadcast will be sent returning the received access codes to gather the user credentials
     */
    GoogleBroadcastReceiver googleBroadcastReceiver;

    /**
     * Access code received from the google Oath server
     * We can retrieve the users' credentials using this code
     */
    String accesToken;

    /**
     * Expiration time of the access token
     */
    int expiresInResult;

    /**
     * Scope of the token (whice data can be gathered)
     *  For us least amount of data: name & mail & profile picture only
     */
    String scope;

    /**
     * Type of the token
     */
    String tokenType;

    /**
     * Id of the token
     */
    String idToken;

    //User information
    /**
     * Name of the user, gathered from the Google account.
     */
    String userName;

    /**
     * Mail of the user, gathered from the Google account.
     */
    String userMail;

    /**
     * Phonenumber of the user, gathered from the phone dialog.
     */
    String phoneNumber;

    /**
     * Randomly generated login code coupled to the new user
     */
    String code;

    /**
     * Selected bike by the user, to be rented
     */
    ABikeObject bikeObject;


    /**
     * Login id of our application for the Google oauth2.0 api
     */
    final static String clientId = "947283700713-emdum0isd99jb2ihj4fnrrvtqtrambgt.apps.googleusercontent.com";

    /**
     * When the activity is created:
     *  - Gather the selected bike from previous activity
     *  - Initialize the new service intent (which will be polling the progress of sign in at the Oath google server)
     *  - Initialize the broadcast receiver (receiving the polling result when the service is ready)
     *  - Send sign in request to the Google Oauth server
     *
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_google);

        bikeObject = (ABikeObject) getIntent().getSerializableExtra("Bike");

        googleBroadcastReceiver = new GoogleBroadcastReceiver();
        googleBroadcastReceiver.setGoogleBroadcastListener(title -> {
            accesToken = googleBroadcastReceiver.getAccessToken();
            expiresInResult = googleBroadcastReceiver.getExpiresIn();
            scope = googleBroadcastReceiver.getScope();
            tokenType = googleBroadcastReceiver.getTokenType();
            idToken = googleBroadcastReceiver.getIdToken();
            stopService(pollIntent);
            //new GoogleGetNameAndMail().execute();
            new ConnectionGoogleCredentials().execute();
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction("GOOGLE_LOGIN");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(googleBroadcastReceiver, filter);

        googleUrlView = findViewById(R.id.googleViewUrl);
        googleCodeView = findViewById(R.id.codeViewGoogle);
        googleUrlView.setPaintFlags(googleUrlView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        new GoogleLoginRequest().execute();

    }

    /**
     * TextView initializer
     */
    public void setUserInfoViews(){
        googleUrlView.setText(verificationUrl);
        googleCodeView.setText(userCode);
    }

    /**
     * When we've sent a sign in request to the Google Oauth server we start polling the progress of this request
     * in this new thread (Service intent).
     */
    public void startPolling(){
        pollIntent = new Intent(getApplicationContext(), SyncService.class);
        pollIntent.putExtra("ClientId", clientId);
        pollIntent.putExtra("ClientSecret", userCode);
        pollIntent.putExtra("DeviceCode", deviceCode);
        pollIntent.putExtra("Interval", interval);
        startService(pollIntent);
    }

    /**
     * When we've received the Name and Mail of the user we check if there already exists a user coupled to this mail address.
     */
    public void checkNewUserToDatabase(){
        new ConnectionCheckNewGoogleRegistration().execute();
    }

    /**
     * Code generator for login code
     * @return
     *              Login code of the user to be used at the Kiosk or Bikes to login
     */
    public String generateRandomNumber(){
        Random random = new Random();
        int randomNumber = random.nextInt(9999  );
        if(randomNumber < 10){
            return "000"+randomNumber;
        }else if(randomNumber < 100){
            return "00"+randomNumber;
        }else if(randomNumber < 1000){
            return "0" + randomNumber;
        }else{
            return "" + randomNumber;
        }
    }

    /**
     * Last step of the registration: we request the phone number of the new user
     */
    public void phoneDialogPopup(){
        phoneDialog = new PhoneDialog();
        phoneDialog.show(getSupportFragmentManager(),"One last step..");
    }

    /**
     * Implementation of PhoneDialog applyTexts()
     *  - We set the phone number inputted in the PhoneDialog to the phonenumber of the activity
     * @param phonenumber
     *                  Phonenumber inserted into the PhoneDialog
     */
    @Override
    public void applyTexts(String phonenumber) {
        //When ok button is pushed and correct phone number has been inputted:
        this.phoneNumber = phonenumber;
        finishRegistration();
        phoneDialog.dismiss();
    }

    /**
     * Implementation of the PhoneDialog cancelPressed()
     *  - We return to previous activity (RegisterOptions)
     */
    @Override
    public void cancelPressed() {
        finish();
    }

    /**
     * If everything succeeded we insert the new user into the User table of the MySql Database
     */
    public void finishRegistration(){
            new ConnectionGoogleUserToDatabase().execute();
    }

    /**
     * Class in charge of sending a new login request to the Google Oauth server
     */
    @SuppressLint("StaticFieldLeak")
    class GoogleLoginRequest extends AsyncTask<String, String, String> {

        String output;
        int responseCode;

        /**
         * Method in charge of sending the login request to the Google Oauth server through an HTTP request.
         * @param strings
         *          Paramaters passed when the execution of the AsyncTask is called;
         * @return
         *          Returns the response of the database.
         */
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("https://oauth2.googleapis.com/device/code");

                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                String urlParameters ="client_id="+clientId+"&scope=email%20profile";
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                connection.setRequestProperty("Connection", "keep-alive");
                connection.setRequestProperty("Keep-Alive", "1800");

                connection.setDoOutput(true);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());

                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                responseCode = connection.getResponseCode();

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder responseOutput = new StringBuilder();

                while (null != (line = br.readLine())){
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

        /**
         * When the request is finished we handle the result:
         *  - If response is 200 (OK) we get information from the JSON response.
         *  - The data we gather are needed for polling the Oauth server in order to retrieve the access token
         *      With this access token we can query the Google Oauth server for the usrer's mail and name
         * @param s
         *      Returns the result of the doInBackground method
         */
        @Override
        protected void onPostExecute(String s) {
            if(responseCode == 200){
                try {
                    System.out.println("Result: " + responseCode);
                    JSONObject result = new JSONObject(s);
                    deviceCode = result.getString("device_code");
                    userCode = result.getString("user_code");
                    expiresIn = result.getString("expires_in");
                    verificationUrl = result.getString("verification_url");
                    interval = result.getInt("interval");

                    setUserInfoViews();
                    startPolling();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("Response code: " + responseCode);
                System.out.println("Response body: " + s);
            }
        }
    }

    /**
     * Broadcast receiver, needed to receive data from the polling thread when it has received the access token.
     */
    public static class GoogleBroadcastReceiver extends BroadcastReceiver {

        String accesToken;
        int expiresIn;
        String scope;
        String tokenType;
        String idToken;

        public interface GoogleBroadcastListener {
            void onObjectReady(String title);
        }

        /**
         * Listener for this class.
         */
        private GoogleBroadcastListener listener;

        public GoogleBroadcastReceiver(){
            this.listener = null;
        }

        public void setGoogleBroadcastListener(GoogleBroadcastListener listener){
            this.listener = listener;
        }

        /**
         * When the listener is ready we retrieve the acces token
         * @return
         *          Access token
         */
        public String getAccessToken(){
            return accesToken;
        }


        /**
         * Getters for the other parameters (scope, expiration , ... of the access token)
         */
        public int getExpiresIn() {
            return expiresIn;
        }

        public String getScope() {
            return scope;
        }

        public String getTokenType() {
            return tokenType;
        }

        public String getIdToken() {
            return idToken;
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
            Bundle b = intent.getExtras();
            assert b != null;
            accesToken = b.getString("AccessToken");
            expiresIn = b.getInt("ExpiresIn");
            scope = b.getString("Scope");
            tokenType = b.getString("TokenType");
            idToken = b.getString("IdToken");
            listener.onObjectReady("ready");
        }
    }


    /**
     * Class in charge retrieving Google new user name and mail for registration.
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionGoogleCredentials extends AsyncTask<String, String, String> {
        String result = "";

        /**
         * Method in charge of getting the user information from the Google Oauth server through an HTTP request using the access token.
         * @param strings
         *          Parameters passed when the execution of the AsyncTask is called;
         * @return
         *          Returns the response of the database.
         */
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accesToken;
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
         * Method in charge of handling the result gathered from the Google Oauth server:
         *  - If something went wrong we print the result
         *  - If the mail hasn't been verified we cancel the registration
         *  - If everything succeeds we set the name and mail and pop up the PhoneDialog
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @SuppressLint("ShowToast")
        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonResult = new JSONObject(result);
                if(jsonResult.has("error")){
                    //Something went wrong, we print it out
                    System.out.println("Error retrieving Google credentials: " + result);
                    return;
                }

                //We only accept users with verified user mails:
                boolean verifiedMail = jsonResult.getBoolean("verified_email");

                if(!verifiedMail){
                    //We return
                    System.out.println("Error retrieving Google credentials: mail not verified");
                    Toast.makeText(getApplicationContext(),"Google mail not verified, try with another account.", Toast.LENGTH_LONG);
                    finish();
                }

                //Successful, we check if mail already exists in MySql Database.
                userName = jsonResult.getString("name");
                userMail = jsonResult.getString("email");
                checkNewUserToDatabase();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Class in charge of checking whether the user already exists in the MySql Database
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionCheckNewGoogleRegistration extends AsyncTask<String, String, String> {
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
                String host = "http://"+ getResources().getString(R.string.ip) +"/checkregisterdata.php?mail='"+ userMail +"'";
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
         * Method in charge of handling the result gathered from the database.
         * - If the user already exist, we return to RegisterOptions activity
         * - If new user we go on with the registration
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 0){
                    JSONArray userDetails = jsonResult.getJSONArray("message");
                    JSONObject userDetail = userDetails.getJSONObject(0);

                    int id = userDetail.getInt("id");
                    String name = userDetail.getString("name");
                    //Code to hande when user already exists.
                    Toast.makeText(getApplicationContext(),"User alreadey exists.\n \n  User: " + id + " : " + name,Toast.LENGTH_LONG).show();
                    finish();

                }else if(success == 1){
                    //Code for new user
                    Toast.makeText(getApplicationContext(),userName+", welcome!",Toast.LENGTH_SHORT).show();
                    phoneDialogPopup();

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Class in charge of inserting the new user to the user table of the MySql Database
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionGoogleUserToDatabase extends AsyncTask<String, String, String>{
        String result = "";

        @Override
        protected String doInBackground(String... strings) {
            try{
                code = generateRandomNumber();

                System.out.println("New code: " + code);

                HashingObject hashingObject = new HashingObject(code);
                String hashCode = hashingObject.getGeneratedHash();

                userName = userName.replaceAll("\\s","");
                System.out.println("We here 2");
                String host = "http://"+ getResources().getString(R.string.ip) +"/input_std_registerdata.php?name=" + userName + "&mail=" + userMail+"&phonenumber=" + phoneNumber +"&code=" + hashCode;
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
            }catch (Exception e) {
                System.out.println("The exception: "+e.getMessage());
                return "The exception: " + e.getMessage();
            }
            System.out.println("We here 3");
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database.
         * If insertion was successful we send a verification mail & move on the instruction screen
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            System.out.println(result);
            try{
                JSONObject jsonResult = new JSONObject(result);
                System.out.println("Result: " + result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    JSONArray userDetails = jsonResult.getJSONArray("message");
                    JSONObject userDetail = userDetails.getJSONObject(0);
                    int id = userDetail.getInt("id");
                    String name = userDetail.getString("name");
                    System.out.println("Success");
                    Toast.makeText(getApplicationContext(),"User successfully registered.\n \n  User: " + id + " : " + name,Toast.LENGTH_LONG).show();
                    sendCode();

                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Registration failed...",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * When the registration is successful we send a verification mail containing the login code
     */
    public void sendCode(){
        final String loginCode = code;
        Runnable mailRunnable = () -> {
            try {
                GmailSender sender = new GmailSender("wowkioskmail@gmail.com",
                        "kioskmail123");
                sender.sendMail("WOW kiosk Verification mail",
                        "Hello "+ userName +"! Welcome at WOW solutions bike rental service.\n \n" +
                                "Your LOGIN code is:" + loginCode +". "
                                + "\n -> This code is used to login to the kiosk and the bikes."
                                +"\n \n Enjoy your ride!",
                        "wowkioskmail@gmail.com", userMail);
            } catch (Exception e) {
                System.out.println("The exception :" + e);
            }
        };
        Thread mailThread = new Thread(mailRunnable);
        mailThread.start();
        toInstructionWindow();
    }

    /**
     *  Last action of the success sequence: we go to instruction activity
     */
    public void toInstructionWindow(){
        Intent intent = new Intent(RegistrationGoogle.this, InstructionVideo.class);
        intent.putExtra("Bike", bikeObject);
        intent.putExtra("Mail",userMail);
        startActivity(intent);
    }
}
