package com.example.kioskprototype.POI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.kioskprototype.R;

public class PoiSelectType extends AppCompatActivity {

    Button restoButton;
    Button visitButton;
    Button routesButton;
    Button activitiesButton;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_select_type);
        restoButton = (Button)findViewById(R.id.restButton);
        visitButton =(Button)findViewById(R.id.visitButton);
        routesButton = (Button)findViewById(R.id.routesButton);
        activitiesButton = (Button)findViewById(R.id.activitiesButton);

        intent = new Intent(PoiSelectType.this, PoiArrayView.class);

        restoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Type", 1);
                startActivity(intent);
            }
        });

        visitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Type",2);
                startActivity(intent);
            }
        });

        routesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Type",3);
                startActivity(intent);
            }
        });

        activitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Type",4);
                startActivity(intent);
            }
        });
    }


}
