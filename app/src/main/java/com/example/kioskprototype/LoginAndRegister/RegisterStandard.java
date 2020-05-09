package com.example.kioskprototype.LoginAndRegister;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.HashingObject;
import com.example.kioskprototype.MailSender.GmailSender;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterAndObjects.ABikeObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Random;

/**
 * Activity in charge of registering a new user the standard way.
 */
public class RegisterStandard extends AppCompatActivity {

    /**
     * EditText object where user can input his name
     */
    EditText editNameText;

    /**
     * EditText object where user can input his E-mail address
     */
    EditText editMailText;

    /**
     * EditText object where user can input his phone number
     */
    EditText editPhoneNumer;

    /**
     * Confirm button when the user has inputted his register data correctly
     */
    ImageButton confirmButton;

    /**
     * Bike selected by the user, to be rented
     */
    ABikeObject bikeObject;

    /**
     * Name of the user
     */
    String name;

    /**
     * E-mail address of the user
     */
    String mail;

    /**
     * Phone number of the user
     */
    String phone;

    /**
     * Id of the user
     */
    int id;

    /**
     * Inputted mail will be checked against this pattern to check if mail structure is correct
     */
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    /**
     * When the activity is created:
     *  - EditTexts & Button initialized
     *  - Selected bike from previous activity is retrieved
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_standard);

        initButtons();
        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");
        setButtons();
    }

    /**
     * Connect buttons of UI layer with button objects
     */
    private void initButtons(){
        editNameText = findViewById(R.id.editNameText);
        editMailText = findViewById(R.id.editEmailText);
        editPhoneNumer = findViewById(R.id.editPhoneText);
        confirmButton = findViewById(R.id.registerButton1);
    }

    /**
     * Set confirmbutton when clicked:
     *  - check if inputted data is valid
     *  - if so we input in database
     */
    private void setButtons(){
        confirmButton.setOnClickListener(v -> {
            if(checkValidInputData()){
                name = editNameText.getText().toString();
                name = name.replaceAll("\\s","");
                mail = editMailText.getText().toString();
                phone = editPhoneNumer.getText().toString();

                startRegistration();
            }
        });
    }

    /**
     * We check if the user doesn't already exist inside the MySql Database
     */
    private void startRegistration(){
        new ConnectionCheckUserData().execute();
    }

    /**
     * We check if every EditText has been filled in correctly
     * @return
     *          True or false
     */
    private boolean checkValidInputData(){
        if(editNameText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(),"Name field empty.\nEnter your name please.",Toast.LENGTH_SHORT).show();
            return false;
        }else if(editMailText.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"Email field empty.\nEnter email address",Toast.LENGTH_SHORT).show();
            return false;
        }else if(!editMailText.getText().toString().trim().matches(emailPattern)){
            Toast.makeText(getApplicationContext(),"Non valid email entered.\nEnter a valid email address",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(editPhoneNumer.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"Phone number field empty.\nEnter your phone number please",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Class in charge of checking if the user already exists inside the MySal Database
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionCheckUserData extends AsyncTask<String, String, String> {
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

                }else if(success == 1){
                    //Code for new user
                    Toast.makeText(getApplicationContext(),name+", welcome!",Toast.LENGTH_SHORT).show();
                    new ConnectionNewUserToDatabase().execute();

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Class in charge of inserting the new users' data into the MySql Database
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionNewUserToDatabase extends AsyncTask<String, String, String>{
        String code = generateRandomNumber();
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
                System.out.println("New code: " + code);

                HashingObject hashingObject = new HashingObject(code);
                String hashCode = hashingObject.getGeneratedHash();

                String host = "http://"+ getResources().getString(R.string.ip) +"/input_std_registerdata.php?name=" + name + "&mail=" + mail+"&phonenumber=" + phone +"&code=" + hashCode;
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
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database.
         * - If successful we go to the MailVerification activity
         * - Else we return to the RegisterOptions activity
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            System.out.println(result);
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    JSONArray userDetails = jsonResult.getJSONArray("message");
                    JSONObject userDetail = userDetails.getJSONObject(0);

                    id = userDetail.getInt("id");
                    String name = userDetail.getString("name");
                    Toast.makeText(getApplicationContext(),"User successfully registered.\n \n  User: " + id + " : " + name,Toast.LENGTH_LONG).show();

                    toVerificationScreen(code);

                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Registration failed...",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * We send a mail with a verification code to check if the user actually inputted a valid E-mail address
     * After this we create the MailVerification activity
     * @param code
     *              Verification code
     */
    public void toVerificationScreen(String code){
        final String verificationCode = generateVerificationCode();
        final String loginCode = code;
        Runnable mailRunnable = () -> {
            try {
                GmailSender sender = new GmailSender("wowkioskmail@gmail.com",
                        "kioskmail123");
                sender.sendMail("WOW kiosk Verification mail",
                                "Hello "+ name +"! Welcome at WOW solutions bike rental service.\n \n" +
                                "Your verification code is: " + verificationCode
                                        +"\n \n Your LOGIN code is:" + loginCode +". "
                                        + "\n -> This code is used to login to the kiosk and the bikes."
                                        +"\n \n Enjoy your ride!",
                        "wowkioskmail@gmail.com", mail);
            } catch (Exception e) {
                System.out.println("The exception :" + e);
            }
        };
        Thread mailThread = new Thread(mailRunnable);
        mailThread.start();
        Intent intent = new Intent(RegisterStandard.this, MailVerification.class);
        intent.putExtra("VerificationCode", verificationCode);
        intent.putExtra("Bike", bikeObject);
        intent.putExtra("Mail",mail);
        intent.putExtra("Id", id);
        startActivity(intent);
    }

    /**
     * Login code generator
     * @return
     *              User login code
     */
    public String generateRandomNumber(){
        Random random = new Random();
        int randomNumber = random.nextInt(9999);
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
     * Verification code generator
     * @return
     *              Verification code
     */
    public String generateVerificationCode(){
        Random random = new Random();
        int randomNumber = random.nextInt(999999);
        if(randomNumber < 10){
            return "00000"+randomNumber;
        }else if(randomNumber < 100){
            return "0000"+randomNumber;
        }else if(randomNumber < 1000){
            return "000" + randomNumber;
        }else if(randomNumber < 10000){
            return "00" + randomNumber;
        }else if(randomNumber < 100000){
            return "0" + randomNumber;
        }else{
            return "" + randomNumber;
        }
    }
}
