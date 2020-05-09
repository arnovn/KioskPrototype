package com.example.kioskprototype.LoginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterAndObjects.ABikeObject;

/**
 * Activity which gives the user to option to select if he is a new user or a member.
 */
public class MemberOrNot extends AppCompatActivity {

    /**
     * Button which will guide the user to the RegisterOptions activity.
     */
    Button registerButton;

    /**
     * Button which will guide the user to the LoginOptions activity.
     */
    Button loginButton;

    /**
     * Selected bike by the user, to be rented.
     */
    ABikeObject bikeObject;

    /**
     * When the activity is created:
     *  - Buttons are initialized
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_or_not);

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");

        initButtons();
    }

    /**
     * Connect buttons of UI layer to button objects
     */
    private void initButtons(){
        registerButton = findViewById(R.id.newUserButton);
        loginButton = findViewById(R.id.memberbutton);

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MemberOrNot.this, RegisterOptions.class);
            intent.putExtra("Bike", bikeObject);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MemberOrNot.this, LoginOptions.class);
            intent.putExtra("Bike", bikeObject);
            intent.putExtra("Type","RentABike");
            startActivity(intent);
        });
    }
}
