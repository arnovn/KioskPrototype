package com.example.kioskprototype.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;
import com.facebook.login.Login;

public class LoginOptions extends AppCompatActivity {

    Button codeLogin;
    Button smsLogin;
    ABikeObject bikeObject;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_options);

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");
        type = (String)getIntent().getStringExtra("Type");

        codeLogin = (Button)findViewById(R.id.standardButton);
        smsLogin = (Button)findViewById(R.id.smsButton);

        codeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOptions.this, LoginStandardCode.class);
                intent.putExtra("Bike", bikeObject);
                intent.putExtra("Type", type);
                startActivity(intent);
            }
        });
    }
}
