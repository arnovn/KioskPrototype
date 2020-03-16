package com.example.kioskprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.kioskprototype.LoginAndRegister.MailVerification;
import com.example.kioskprototype.POI.PoiSelectType;

public class MainActivity extends AppCompatActivity {

    Button rentBikeButton;
    Button poiButton;
    Button payButton;
    Button settingsButton;

    Button toCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rentBikeButton = (Button)findViewById(R.id.rentabikebutton);
        poiButton = (Button)findViewById(R.id.poibutton);
        payButton = (Button)findViewById(R.id.paybutton);
        settingsButton = (Button)findViewById(R.id.settingsbutton);

        rentBikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BikeSelect.class));
            }
        });
        poiButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, PoiSelectType.class));
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StdRegister.class));
            }
        });

        toCurrent = (Button)findViewById(R.id.jumpToCurrentScreen);

        toCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InstructionVideo.class));
            }
        });
    }
}
