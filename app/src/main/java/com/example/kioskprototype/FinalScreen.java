package com.example.kioskprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kioskprototype.adapterView.ABikeObject;

import java.util.Timer;
import java.util.TimerTask;

public class FinalScreen extends AppCompatActivity {

    TextView bikeView;
    TextView bikeStandView;
    Button lastSignoutButton;
    ABikeObject bikeObject;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_screen);

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");

        setTimer();
        setViews();
        setButton();

    }

    public void setViews(){
        bikeView = (TextView)findViewById(R.id.bikeView1);
        bikeStandView = (TextView)findViewById(R.id.bikeStandView);

        bikeView.setText("Bike" + bikeObject.getId());
        bikeStandView.setText("Bike stand: " + bikeObject.getBikeStand());

    }

    public void setButton(){
        lastSignoutButton = (Button)findViewById(R.id.lastSignOut);

        lastSignoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FinalScreen.this, MainActivity.class));
            }
        });
    }

    public void setTimer(){
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(FinalScreen.this, MainActivity.class));
            }
        }, 10000);

    }
}
