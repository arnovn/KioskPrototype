package com.example.kioskprototype;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.LoginAndRegister.LoginOptions;
import com.example.kioskprototype.POI.PoiSelectType;

/**
 * Starting activity of the Kiosk
 * In charge of presenting the different general activities the users can selects from:
 *  - Renting a bike
 *  - Looking at Points Of Interest near the Kiosk
 *  - Paying for depths/adding extra credits to your account
 *  - Changing your account settings (request new login code, request membercard, look at past activities with your account)
 *
 *  IP's:
 *      - 192.168.0.122
 *      - 10.0.2.2
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Different buttons guiding the user to the selected activity.
     */
    Button rentBikeButton;
    Button poiButton;
    Button payButton;
    Button settingsButton;

    /**
     * When the activity is created:
     *  - Initialize buttons
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rentBikeButton = findViewById(R.id.rentabikebutton);
        poiButton = findViewById(R.id.poibutton);
        payButton = findViewById(R.id.paybutton);
        settingsButton = findViewById(R.id.settingsbutton);

        rentBikeButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BikeSelect.class)));
        poiButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PoiSelectType.class)));

        payButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginOptions.class);
            intent.putExtra("Type", "PayForServices");
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginOptions.class);
            intent.putExtra("Type", "AccountSettingsLogin");
            startActivity(intent);
        });
    }
}
