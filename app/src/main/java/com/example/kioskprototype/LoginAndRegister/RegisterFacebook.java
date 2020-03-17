package com.example.kioskprototype.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kioskprototype.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.DeviceLoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.DeviceLoginButton;
import com.facebook.login.widget.LoginButton;


public class RegisterFacebook extends AppCompatActivity {

    DeviceLoginButton loginButton;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    AccessToken token;

    private String name;
    private String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_facebook);

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
            }

            @Override
            public void onCancel() {
                //What to do on canceel
            }

            @Override
            public void onError(FacebookException error) {
                //What to do on error
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
