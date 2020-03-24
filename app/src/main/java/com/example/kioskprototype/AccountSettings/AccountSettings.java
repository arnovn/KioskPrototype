package com.example.kioskprototype.AccountSettings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class AccountSettings extends AppCompatActivity {

    TextView welcomeView;
    TextView creditView;
    Button pastactivitiesButton;
    Button membercardButton;
    Button newLoginButton;
    Button signOutButton;

    String mail;
    String name;
    String newCode;
    int id;
    final int RESULT_FAILED = 2;
    double credits;

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

    public void initTextViews(){
        welcomeView = (TextView)findViewById(R.id.accountSettingTitle);
        creditView = (TextView)findViewById(R.id.creditsWindow);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        AlertDialog alertDialog;
        if(requestCode == 0){
            switch (resultCode){
                case RESULT_CANCELED:
                    alertDialog = new AlertDialog.Builder(AccountSettings.this)
                            .setTitle("REQUEST CANCELED")
                            .setMessage("Your request has been canceled.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();

                    break;
                case RESULT_OK:
                    alertDialog = new AlertDialog.Builder(AccountSettings.this)
                            .setTitle("REQUEST SUCCESFULL")
                            .setMessage("Your card will be sent to you within 5 working days.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    break;
                case RESULT_FAILED:
                    alertDialog = new AlertDialog.Builder(AccountSettings.this)
                            .setTitle("REQUEST FAILED")
                            .setMessage("Something went wrong during the request, try again later.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    break;
            }
        }
    }

    public void initButtons(){
        pastactivitiesButton = (Button)findViewById(R.id.pastActivityButton);
        membercardButton = (Button)findViewById(R.id.membercardButton);
        newLoginButton = (Button)findViewById(R.id.requestNewUserCode);
        signOutButton = (Button)findViewById(R.id.signOutButton);
    }

    public void setButtonListeners(){
        pastactivitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountSettings.this, PastActivities.class);
                intent.putExtra("Id", id);
                startActivityForResult(intent,1);
            }
        });

        newLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCode = generateNewCode();
                new ConnectionUpdateCode().execute();
                sendCodeMail();
            }
        });

        membercardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountSettings.this, RequestMemberCard.class);
                intent.putExtra("Id",id);
                intent.putExtra("Mail", mail);
                startActivityForResult(intent,0);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void sendCodeMail(){
        final String loginCode = newCode;
        Runnable mailRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    GmailSender sender = new GmailSender("wowkioskmail@gmail.com",
                            "kioskmail123");
                    sender.sendMail("WOW kiosk Verification mail",
                            "Hello "+ name +"! You've requested a new logincode.\n \n"
                                    + "Your new login code is:" + loginCode +". "
                                    + "\n -> This code is used to login to the kiosk and the bikes.",
                            "wowkioskmail@gmail.com", mail);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                    System.out.println("The exception :" + e);
                }
            }
        };
        Thread mailThread = new Thread(mailRunnable);
        mailThread.start();
    }


    public String generateNewCode(){
        Random random = new Random();
        int randomNumber = random.nextInt(9999-0)+0;
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

    public void setTextViews(){
        welcomeView.setText("Welcome " + name + "!");
        creditView.setText("Account Credits: €" + credits);
    }

    class ConnectionGetNameAndCredits extends AsyncTask<String, String, String>{
        String result = "";
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://10.0.2.2/getusernamecredits.php?id='"+ id +"'";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer stringBuffer = new StringBuffer("");

                String line ="";
                while((line = reader.readLine()) != null){
                    stringBuffer.append(line);
                    break;
                }
                reader.close();
                result = stringBuffer.toString();
            } catch (Exception e) {
                System.out.println("The exception: "+e.getMessage());
                return new String("The exception: " + e.getMessage());
            }
            return result;
        }

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

                StringBuffer stringBuffer = new StringBuffer("");

                String line ="";
                while((line = reader.readLine()) != null){
                    stringBuffer.append(line);
                    break;
                }
                reader.close();
                result = stringBuffer.toString();
            } catch (Exception e) {
                System.out.println("The exception: "+e.getMessage());
                return new String("The exception: " + e.getMessage());
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
