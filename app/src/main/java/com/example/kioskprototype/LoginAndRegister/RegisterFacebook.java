package com.example.kioskprototype.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.example.kioskprototype.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import com.facebook.FacebookSdk;
import com.facebook.login.DeviceLoginManager;
import com.facebook.login.widget.DeviceLoginButton;
import com.facebook.login.widget.LoginButton;


public class RegisterFacebook extends AppCompatActivity {

    DeviceLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_facebook);

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        loginButton = (DeviceLoginButton)viewGroup.findViewById(R.id.login_button);
    }


}
