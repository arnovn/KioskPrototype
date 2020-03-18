package com.example.kioskprototype.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.kioskprototype.R;

public class LoginOptions extends AppCompatActivity {

    Button codeLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_options);

        codeLogin = (Button)findViewById(R.id.standardButton);
    }
}
