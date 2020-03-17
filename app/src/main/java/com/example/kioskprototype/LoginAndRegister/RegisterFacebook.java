package com.example.kioskprototype.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.kioskprototype.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;
import java.util.Random;

import com.example.kioskprototype.adapterView.ABikeObject;
import com.example.kioskprototype.adapterView.PhoneDialog;
import com.example.kioskprototype.payment.PaymentSelect;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.DeviceLoginManager;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.DeviceLoginButton;
import com.facebook.login.widget.LoginButton;


public class RegisterFacebook extends AppCompatActivity implements PhoneDialog.PhoneDialogListener {

    DeviceLoginButton loginButton;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    AccessToken token;

    private String name;
    private String mail;
    private String phonenumber;
    private ABikeObject bikeObject;

    PhoneDialog phoneDialog;

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

        loginButton = (DeviceLoginButton)viewGroup.findViewById(R.id.login_button);
        loginButton.setReadPermissions(EMAIL);
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //What to do on success
                token = loginResult.getAccessToken();
                getUserData();
                openPhoneDialog();
            }

            @Override
            public void onCancel() {
                //What to do on cancel
                returnToRegisterSelect();
            }

            @Override
            public void onError(FacebookException error) {
                //What to do on error
                Toast.makeText(getApplicationContext(),error+"",Toast.LENGTH_SHORT);
            }
        });

        Button finishRegistration = (Button)findViewById(R.id.finishRegButton);
        finishRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Time to finish the registration:
                finishRegistration();
            }
        });


    }

    public void getUserData(){
        if(token != null && !token.isExpired()) {
            GraphRequest request = GraphRequest.newMeRequest(
                    token,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            // Application code
                            try {
                                mail = object.getString("email");
                                name = object.getString("name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), "Successfuly retreived login info: " + name + " - " + mail, Toast.LENGTH_SHORT).show();
                            checkUserAlreadyExists();
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email");
            request.setParameters(parameters);
            request.executeAsync();
        }else{
            Toast.makeText(getApplicationContext(), "Error with access token.", Toast.LENGTH_SHORT).show();
        }
    }

    public void openPhoneDialog(){
        phoneDialog = new PhoneDialog();
        phoneDialog.show(getSupportFragmentManager(),"One last step..");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void applyTexts(String phonenumber) {
        //When ok button is pushed and correct phone number has been inputted:
        this.phonenumber = phonenumber;
        finishRegistration();
        phoneDialog.dismiss();
    }

    @Override
    public void cancelPressed() {
        //Implement logic when cancel pressed.
        returnToRegisterSelect();
    }

    public void returnToRegisterSelect(){
        LoginManager.getInstance().logOut();
        this.finish();
    }

    public void checkUserAlreadyExists(){
        new ConnectionCheckNewFBRegistration().execute();
    }

    public void finishRegistration(){
        new ConnectionNewUserToDatabaseFB().execute();
    }

    class ConnectionCheckNewFBRegistration extends AsyncTask<String, String, String> {
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

    class ConnectionNewUserToDatabaseFB extends AsyncTask<String, String, String>{
        String code = generateRandomNumber();
        String result = "";
        @Override
        protected String doInBackground(String... strings) {
            try{
                name = name.replaceAll("\\s","");
                System.out.println("We here 2");
                String host = "http://10.0.2.2/input_std_registerdata.php?name=" + name + "&mail=" + mail+"&phonenumber=" + phonenumber +"&code=" + code;
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
            System.out.println("We here 3");
            return result;
        }

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
                    Toast.makeText(getApplicationContext(),"We here 5", Toast.LENGTH_SHORT).show();
                    int id = userDetail.getInt("id");
                    String name = userDetail.getString("name");
                    System.out.println("Success");
                    Toast.makeText(getApplicationContext(),"User successfully registered.\n \n  User: " + id + " : " + name,Toast.LENGTH_LONG).show();
                    toPaymentWindow();

                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Registration failed...",Toast.LENGTH_SHORT).show();
                    returnToRegisterSelect();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void toPaymentWindow(){
        Intent intent = new Intent(RegisterFacebook.this, PaymentSelect.class);
        intent.putExtra("Bike",(Serializable) bikeObject);
        intent.putExtra("Mail",mail);
        startActivity(intent);
    }
}
