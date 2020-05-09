package com.example.kioskprototype.POI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.R;

/**
 * Activity which present the different POI options the user can select
 *  -   Restaurants
 *  -   Worth to visit
 *  -   Tours
 *  -   Activities
 */
public class PoiSelectType extends AppCompatActivity {

    /**
     * Buttons which guide the user to the selected PoiArrayView activity
     */
    Button restoButton;
    Button visitButton;
    Button routesButton;
    Button activitiesButton;
    Button poiMapButton;
    Intent intent;

    /**
     * When the acitivity is created:
     *  - Initialize the intent
     *  - Initialize buttons
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_select_type);

        initButtons();

        intent = new Intent(PoiSelectType.this, PoiArrayView.class);

        setOnClickListeners();
    }

    private void initButtons(){
        restoButton = findViewById(R.id.restButton);
        visitButton = findViewById(R.id.visitButton);
        routesButton = findViewById(R.id.routesButton);
        activitiesButton = findViewById(R.id.activitiesButton);
        poiMapButton = findViewById(R.id.allMapPoiButton);
    }

    private void setOnClickListeners(){
        restoButton.setOnClickListener(v -> {
            intent.putExtra("Type", 1);
            startActivity(intent);
        });

        visitButton.setOnClickListener(v -> {
            intent.putExtra("Type",2);
            startActivity(intent);
        });

        routesButton.setOnClickListener(v -> {
            intent.putExtra("Type",3);
            startActivity(intent);
        });

        activitiesButton.setOnClickListener(v -> {
            intent.putExtra("Type",4);
            startActivity(intent);
        });

        poiMapButton.setOnClickListener(v-> startActivity(new Intent(PoiSelectType.this, PoiAllMapView.class)));
    }
}
