package com.example.kioskprototype.LoginAndRegister;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.HashingObject;
import com.example.kioskprototype.MailSender.GmailSender;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;
import com.example.kioskprototype.adapterView.PhoneDialog;
import com.example.kioskprototype.payment.PaymentSelect;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.DeviceLoginButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Random;

/**
 * Activity in charge of registering a new user using the Facebook SDK
 */
public class RegisterFacebook extends AppCompatActivity implements PhoneDialog.PhoneDialogListener {

    /**
     * Facebook login button
     */
    DeviceLoginButton loginButton;

    /**
     * CallbackManager managing callbacks into the Facebook SDK from this activity
     */
    CallbackManager callbackManager;

    /**
     * String used for ReadPermissions in the Facebook SDK
     */
    private static final String EMAIL = "email";

    /**
     * Token received when login was successful meaning the user has logged in correctly and we can retrieve their name & E-mail address
     */
    AccessToken token;

    /**
     * Name of the user
     */
    private String name;

    /**
     * E-mail address of the user
     */
    private String mail;

    /**
     * Phone number of the user
     */
    private String phonenumber;

    /**
     * Selected bike by the user, to be rented
     */
    private ABikeObject bikeObject;

    /**
     * Login code for the new registering user
     */
    String code;

    /**
     * Dialog for inserting the phone number of the user
     */
    PhoneDialog phoneDialog;

    /**
     * When the activity is created:
     *  - Bike object from previous activity is retrieved
     *  - Check if someone was already logged in on the Kiosk with Facebook
     *  - Initialize Facebook login button & callbackmanager
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_facebook);

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");

        AccessToken checkCurrent = AccessToken.getCurrentAccessToken();
        if(checkCurrent != null){
            LoginManager.getInstance().logOut();
        }

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        loginButton = viewGroup.findViewById(R.id.login_button);
        loginButton.setReadPermissions(EMAIL);
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            /**
             * When the login is successful we retrieve the users' data & open the PhoneDialog
             * @param loginResult
             *                  Result of the login operation (contains accesstoken, grantedpremissions & deniedpermissions)
             */
            @Override
            public void onSuccess(LoginResult loginResult) {
                //What to do on success
                token = loginResult.getAccessToken();
                getUserData();
                openPhoneDialog();
            }

            /**
             * When the login is canceled we return to the RegisterOptions activity
             */
            @Override
            public void onCancel() {
                //What to do on cancel
                returnToRegisterSelect();
            }

            /**
             * On error we print out the error
             * @param error
             *              login error
             */
            @SuppressLint("ShowToast")
            @Override
            public void onError(FacebookException error) {
                //What to do on error
                Toast.makeText(getApplicationContext(),error+"",Toast.LENGTH_SHORT);
            }
        });

        Button finishRegistration = findViewById(R.id.finishRegButton);
        finishRegistration.setOnClickListener(v -> {
            //Time to finish the registration:
            finishRegistration();
        });


    }

    /**
     * When the login is successful we retreive the name and E-mail address from the logged in user Facebook account
     */
    public void getUserData(){
        if(token != null && !token.isExpired()) {
            GraphRequest request = GraphRequest.newMeRequest(
                    token,
                    (object, response) -> {
                        // Application code
                        try {
                            mail = object.getString("email");
                            name = object.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), "Successfuly retreived login info: " + name + " - " + mail, Toast.LENGTH_SHORT).show();
                        checkUserAlreadyExists();
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email");
            request.setParameters(parameters);
            request.executeAsync();
        }else{
            Toast.makeText(getApplicationContext(), "Error with access token.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * After we retrieved the name & mail we want the phone number of the new user, therefore we let the user input this
     * through a phone dialog
     */
    public void openPhoneDialog(){
        phoneDialog = new PhoneDialog();
        phoneDialog.show(getSupportFragmentManager(),"One last step..");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
        this.phonenumber = phonenumber;
        finishRegistration();
        phoneDialog.dismiss();
    }

    /**
     * Implementation of the PhoneDialog cancelPressed()
     *  - We return to previous activity (RegisterOptions)
     */
    @Override
    public void cancelPressed() {
        //Implement logic when cancel pressed.
        returnToRegisterSelect();
    }

    /**
     * Returns to the RegisterOptions activity
     */
    public void returnToRegisterSelect(){
        LoginManager.getInstance().logOut();
        this.finish();
    }

    /**
     * We check if the mail of the user logged in to facebook at the Kiosk already exists in the MySql Database.
     */
    public void checkUserAlreadyExists(){
        new ConnectionCheckNewFBRegistration().execute();
    }

    /**
     * If everything succeeded we insert the new user into the User table of the MySql Database
     */
    public void finishRegistration(){
        new ConnectionNewUserToDatabaseFB().execute();
    }

    /**
     * Class in charge of checking whether the user already exists in the MySql Database
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionCheckNewFBRegistration extends AsyncTask<String, String, String> {
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
                String host = "http://"+ getResources().getString(R.string.ip) +"/checkregisterdata.php?mail='"+ mail +"'";
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
                    returnToRegisterSelect();

                }else if(success == 1){
                    //Code for new user
                    Toast.makeText(getApplicationContext(),name+", welcome!",Toast.LENGTH_SHORT).show();

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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
     * Class in charge of inserting the new user to the user table of the MySql Database.
     * We also hash the users code for safety.
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionNewUserToDatabaseFB extends AsyncTask<String, String, String>{
        String result = "";
        @Override
        protected String doInBackground(String... strings) {
            try{
                code = generateRandomNumber();
                System.out.println("New code : " + code);
                HashingObject hashingObject = new HashingObject(code);
                String hashCode = hashingObject.getGeneratedHash();

                name = name.replaceAll("\\s","");
                System.out.println("We here 2");
                String host = "http://"+ getResources().getString(R.string.ip) +"/input_std_registerdata.php?name=" + name + "&mail=" + mail+"&phonenumber=" + phonenumber +"&code=" + hashCode ;
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
                System.out.println("We here 4");
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
                    returnToRegisterSelect();
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
                        "Hello "+ name +"! Welcome at WOW solutions bike rental service.\n \n" +
                                "Your LOGIN code is:" + loginCode +". "
                                + "\n -> This code is used to login to the kiosk and the bikes."
                                +"\n \n Enjoy your ride!",
                        "wowkioskmail@gmail.com", mail);
            } catch (Exception e) {
                System.out.println("The exception :" + e);
            }
        };
        Thread mailThread = new Thread(mailRunnable);
        mailThread.start();
        toPaymentWindow();
    }

    //TODO : go to instruction video window instead of payment window
    /**
     *  Last action of the success sequence: we go to instruction activity
     */
    public void toPaymentWindow(){
        Intent intent = new Intent(RegisterFacebook.this, PaymentSelect.class);
        intent.putExtra("Bike", bikeObject);
        intent.putExtra("Mail",mail);
        startActivity(intent);
    }
}
