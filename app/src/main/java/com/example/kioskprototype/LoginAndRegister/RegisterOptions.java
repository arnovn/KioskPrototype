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

public class RegisterOptions extends AppCompatActivity {

    Button facebookRegister;
    Button googleRegister;
    Button itsmeRegister;
    Button standardRegister;
    ABikeObject bikeObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_options);

        facebookRegister = (Button)findViewById(R.id.facebookButton);
        googleRegister = (Button)findViewById(R.id.googleButton);
        itsmeRegister = (Button)findViewById(R.id.itsmeButton);
        standardRegister = (Button)findViewById(R.id.standardButton);

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");
        Toast toast = Toast.makeText(getApplicationContext(),
                bikeObject.getCode()+"",
                Toast.LENGTH_SHORT);
        toast.show();

        setStandardButton();
        setFacebookButton();
    }

    public void setStandardButton(){
        standardRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterOptions.this, RegisterStandard.class);
                intent.putExtra("Bike",(Serializable)bikeObject);
                startActivity(intent);
            }
        });
    }

    public void setFacebookButton(){
        facebookRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterOptions.this, RegisterFacebook.class);
                intent.putExtra("Bike", (Serializable)bikeObject);
                startActivity(intent);
            }
        });
    }
}
