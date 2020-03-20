package com.example.kioskprototype.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;

import java.io.Serializable;

public class MemberOrNot extends AppCompatActivity {

    Button registerButton;
    Button loginButton;
    ABikeObject bikeObject;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_or_not);

        registerButton = (Button)findViewById(R.id.newUserButton);
        loginButton = (Button)findViewById(R.id.memberbutton);

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberOrNot.this, RegisterOptions.class);
                intent.putExtra("Bike",(Serializable)bikeObject);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberOrNot.this, LoginOptions.class);
                intent.putExtra("Bike",(Serializable)bikeObject);
                intent.putExtra("Type","RentABike");
                startActivity(intent);
            }
        });
    }
}
