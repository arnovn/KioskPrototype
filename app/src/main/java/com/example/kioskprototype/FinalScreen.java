package com.example.kioskprototype;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.adapterAndObjects.ABikeObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * After the whole selection & login/registration & payment process we end up in this activity.
 * This activity is in charge of showing the place of the bike near the Kiosk so the user can find it easily.
 */
public class FinalScreen extends AppCompatActivity {

    /**
     * TextView visualizing the bike's name at the UI layer
     */
    TextView bikeView;

    /**
     * TextView visualizing the bike stand near the Kiosk at the UI layer
     */
    TextView bikeStandView;

    /**
     * Sign out button so the user can log out and the application will return to the MainActivity
     */
    Button lastSignoutButton;

    /**
     * Selected bike rented by the user!!
     */
    ABikeObject bikeObject;

    /**
     * After a certain time the user will automatically return to the MainActivity
     */
    Timer timer;

    /**
     * When the activity is created:
     *  - Retrieve bike from previous activity
     *  - Initialize timer, TextViews & Button
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_screen);

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");

        setTimer();
        setViews();
        setButton();

    }

    /**
     * Initialization of the TextViews
     */
    @SuppressLint("SetTextI18n")
    private void setViews(){
        bikeView = findViewById(R.id.bikeView1);
        bikeStandView = findViewById(R.id.bikeStandView);

        bikeView.setText("Bike" + bikeObject.getId());
        bikeStandView.setText("Bike stand: " + bikeObject.getBikeStand());

    }

    /**
     * Initialization of the button
     */
    private void setButton(){
        lastSignoutButton =  findViewById(R.id.lastSignOut);

        lastSignoutButton.setOnClickListener(v -> startActivity(new Intent(FinalScreen.this, MainActivity.class)));
    }

    /**
     * Initialization of the timer
     */
    private void setTimer(){
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(FinalScreen.this, MainActivity.class));
            }
        }, 10000);

    }
}
