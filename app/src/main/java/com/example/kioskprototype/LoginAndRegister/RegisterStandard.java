package com.example.kioskprototype.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kioskprototype.MailSender.GmailSender;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;
import java.util.Random;

public class RegisterStandard extends AppCompatActivity {

    EditText editNameText;
    EditText editMailText;
    EditText editPhoneNumer;

    ImageButton confirmButton;

    ABikeObject bikeObject;

    String name;
    String mail;
    String phone;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_standard);

        editNameText = (EditText)findViewById(R.id.editNameText);
        editMailText = (EditText)findViewById(R.id.editEmailText);
        editPhoneNumer = (EditText)findViewById(R.id.editPhoneText);
        confirmButton = (ImageButton)findViewById(R.id.registerButton1);

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidInputData()){
                    name = editNameText.getText().toString();
                    name = name.replaceAll("\\s","");
                    mail = editMailText.getText().toString();
                    phone = editPhoneNumer.getText().toString();

                    startRegistration();
                };
            }
        });
    }

    public void startRegistration(){
        new ConnectionCheckUserData().execute();
    }

    public boolean checkValidInputData(){
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

    class ConnectionCheckUserData extends AsyncTask<String, String, String> {
        String result = "";
        @Override
        protected String doInBackground(String... params) {
            try{
                String host = "http://10.0.2.2/checkregisterdata.php?mail='"+ mail +"'";
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

    class ConnectionNewUserToDatabase extends AsyncTask<String, String, String>{
        String code = generateRandomNumber();
        String result = "";
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://10.0.2.2/input_std_registerdata.php?name=" + name + "&mail=" + mail+"&phonenumber=" + phone +"&code=" + code;
               // String host = "http://10.0.2.2/input_std_registerdata.php?name=Arno&mail=zeifjo&phonenumber=fref&code=fzfez";
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
            }catch (Exception e) {
                System.out.println("The exception: "+e.getMessage());
                return new String("The exception: " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println(result);
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    JSONArray userDetails = jsonResult.getJSONArray("message");
                    JSONObject userDetail = userDetails.getJSONObject(0);

                    int id = userDetail.getInt("id");
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

    public void toVerificationScreen(String code){
        final String verificationCode = generateVerificationCode();
        final String loginCode = code;
        Runnable mailRunnable = new Runnable() {
            @Override
            public void run() {
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
                    Log.e("SendMail", e.getMessage(), e);
                    System.out.println("The exception :" + e);
                }
            }
        };
        Thread mailThread = new Thread(mailRunnable);
        mailThread.start();
        Intent intent = new Intent(RegisterStandard.this, MailVerification.class);
        intent.putExtra("VerificationCode", verificationCode);
        intent.putExtra("Bike",(Serializable) bikeObject);
        intent.putExtra("Mail",mail);
        startActivity(intent);
    }

    public String generateRandomNumber(){
        Random random = new Random();
        int randomNumber = random.nextInt(9999-0)+0;
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

    public String generateVerificationCode(){
        Random random = new Random();
        int randomNumber = random.nextInt(999999-0)+0;
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
