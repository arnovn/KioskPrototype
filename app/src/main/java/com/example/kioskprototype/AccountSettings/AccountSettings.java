package com.example.kioskprototype.AccountSettings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.MailSender.GmailSender;
import com.example.kioskprototype.R;

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
 * Class in charge for the account settings selection.
 * In this activity the user can choose to
 *  - request a new member card
 *  - request a new user code
 *  - view past transactions/activities
 */
public class AccountSettings extends AppCompatActivity {

    /**
     * Textview object used to welcome the user to this activity page.
     */
    TextView welcomeView;

    /**
     * Textview object to visualize the amount of credits a user has on his account.
     */
    TextView creditView;

    /**
     * Button which will guide the user to the PastActivities activity.
     */
    Button pastactivitiesButton;

    /**
     * Button which will guide the user to the RequestMemberCard activity.
     */
    Button membercardButton;

    /**
     * Button which will instantiate the process of assigning a new login code to the user.
     */
    Button newLoginButton;

    /**
     * Button which will 'sign out' the user and return to the MainActivity activity.
     */
    Button signOutButton;

    /**
     * Mail of the current user logged in at the Kiosk.
     */
    String mail;

    /**
     * Name of the current user logged in at the Kiosk.
     */
    String name;

    /**
     * New code which will be assigned to the user when requested.
     */
    String newCode;

    /**
     * Database id of the user current logged in at the Kiosk.
     */
    int id;

    /**
     * Code returned by RequestMemberCard activity if the process of requesting a new membercard failed.
     */
    final int RESULT_FAILED = 2;

    /**
     * Credits of the user logged in at the Kiosk.
     */
    double credits;

    /**
     * When the activity is created:
     *  - mail and id are retreived from past activity
     *  - textview are initiated base on the id
     *  - buttons are initiated
     *  - buttonlisteners are set
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        mail = getIntent().getStringExtra("Mail");
        id = getIntent().getIntExtra("Id",0);

        initTextViews();
        initButtons();
        setButtonListeners();

        new ConnectionGetNameAndCredits().execute();
    }

    /**
     * Method in charge of connecting the TextView objects to the TextViews in the UI layer.
     */
    public void initTextViews(){
        welcomeView = findViewById(R.id.accountSettingTitle);
        creditView = findViewById(R.id.creditsWindow);
    }

    /**
     * When a prior activity created by this activity is finished this method is called.
     * Based on the requestcode & resultcode different actions are performed.
     *
     * Requestcode: 0
     *  - Returned from RequestMemberCard
     *  - If resultCode = RESULT_CANCELED: pop up dialog saying request was canceled.
     *  - If resultCode = RESULT_OK: pop up dialog saying the request was succesful.
     *  - If resultCode = RESULT_FAILED: pop up dialog saying the request failed.
     *
     * @param requestCode
     *          Code to identify where the result is coming from.
     * @param resultCode
     *          Code to identify the result of the activity
     * @param data
     *          prior intent.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            switch (resultCode){
                case RESULT_CANCELED:
                    new AlertDialog.Builder(AccountSettings.this)
                            .setTitle("REQUEST CANCELED")
                            .setMessage("Your request has been canceled.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", (dialog, which) -> {

                            }).show();

                    break;
                case RESULT_OK:
                    new AlertDialog.Builder(AccountSettings.this)
                            .setTitle("REQUEST SUCCESFULL")
                            .setMessage("Your card will be sent to you within 5 working days.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", (dialog, which) -> {

                            }).show();
                    break;
                case RESULT_FAILED:
                    new AlertDialog.Builder(AccountSettings.this)
                            .setTitle("REQUEST FAILED")
                            .setMessage("Something went wrong during the request, try again later.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", (dialog, which) -> {

                            }).show();
                    break;
            }
        }
    }

    /**
     * Method in charge of connection the Button objects to the Buttons of the UI layer.
     */
    public void initButtons(){
        pastactivitiesButton = findViewById(R.id.pastActivityButton);
        membercardButton = findViewById(R.id.membercardButton);
        newLoginButton = findViewById(R.id.requestNewUserCode);
        signOutButton = findViewById(R.id.signOutButton);
    }

    /**
     * Method in charge of initializing the OnClickListeners of the Buttons.
     * - For pastactivitiesButton: start the PastActivities activity.
     * - For newLoginButton: generate new login code, update datebase & send new code by mail.
     * - For membercardButton: start the RequestMemberCard activity.
     * - For signOutButton: return to mains screen.
     */
    public void setButtonListeners(){
        pastactivitiesButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettings.this, PastActivities.class);
            intent.putExtra("Id", id);
            startActivityForResult(intent,1);
        });

        newLoginButton.setOnClickListener(v -> {
            newCode = generateNewCode();
            new ConnectionUpdateCode().execute();
            sendCodeMail();
        });

        membercardButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettings.this, RequestMemberCard.class);
            intent.putExtra("Id",id);
            intent.putExtra("Mail", mail);
            startActivityForResult(intent,0);
        });

        signOutButton.setOnClickListener(v -> finish());
    }

    /**
     * Send the new login code to the E-mail address of the user.
     */
    public void sendCodeMail(){
        final String loginCode = newCode;
        Runnable mailRunnable = () -> {
            try {
                GmailSender sender = new GmailSender("wowkioskmail@gmail.com",
                        "kioskmail123");
                sender.sendMail("WOW kiosk Verification mail",
                        "Hello "+ name +"! You've requested a new logincode.\n \n"
                                + "Your new login code is:" + loginCode +". "
                                + "\n -> This code is used to login to the kiosk and the bikes.",
                        "wowkioskmail@gmail.com", mail);
            } catch (Exception e) {
                System.out.println("The exception :" + e);
            }
        };
        Thread mailThread = new Thread(mailRunnable);
        mailThread.start();
    }

    /**
     * Method in charge of generating a new login code ranging from 0000 to 9999
     * @return
     *          returns the new code as String
     */
    public String generateNewCode(){
        Random random = new Random();
        int randomNumber = random.nextInt(9999);
        if(randomNumber < 10){
            return "000"+randomNumber;
        }else if(randomNumber < 100){
            return "00"+randomNumber;
        }else if(randomNumber < 1000){
            return "0" + randomNumber;
        }else {
            return randomNumber+"";
        }
    }

    /**
     * On creating the activity:
     *  - The welcome text is set including the name of the user.
     *  - The credits text is set to the amount of credits the user still has on his account.
     */
    @SuppressLint("SetTextI18n")
    public void setTextViews(){
        welcomeView.setText("Welcome " + name + "!");
        creditView.setText("Account Credits: €" + credits);
    }

    /**
     * Class in charge retrieving the name and credits of the user currently logged in at the kiosk.
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionGetNameAndCredits extends AsyncTask<String, String, String>{
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
                String host = "http://"+ getResources().getString(R.string.ip) +"/getusernamecredits.php?id='"+ id +"'";
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
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    JSONArray datainfos = jsonResult.getJSONArray("data");
                    JSONObject datainfo = datainfos.getJSONObject(0);

                    name = datainfo.getString("name");
                    credits = datainfo.getDouble("credits");

                    setTextViews();

                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Failed: No user found.",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Class in charge of updating the old code to the new code of the user logged in at the Kiosk.
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionUpdateCode extends AsyncTask<String, String, String>{
        String result = "";
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://10.0.2.2/updateusercode.php?id="+ id + "&code=" + newCode;
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

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    Toast.makeText(getApplicationContext(),"Succes: new code has been sent to your mail",Toast.LENGTH_LONG).show();

                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Failed: No user found.",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
