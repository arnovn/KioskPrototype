package com.example.kioskprototype.LoginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterAndObjects.ABikeObject;

/**
 * Activity which gives the user the different register options
 */
public class RegisterOptions extends AppCompatActivity {

    /**
     * Button which will guide the user to the RegisterFacebook activity
     */
    Button facebookRegister;

    /**
     * Button which takes the user to the RegisterGoogle activity
     */
    Button googleRegister;

    /**
     * Button which will guide the user to the RegisterItsme activity
     */
    Button itsmeRegister;

    /**
     * Button which will guide the user to the RegisterStandard activity
     */
    Button standardRegister;

    /**
     * Bike selected by the user, to be rented
     */
    ABikeObject bikeObject;

    /**
     * When the activity is created:
     *  - Initialize buttons
     *  - Retrieve selected bike from previous activity
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_options);

        initButtons();

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");
        Toast toast = Toast.makeText(getApplicationContext(),
                bikeObject.getCode()+"",
                Toast.LENGTH_SHORT);
        toast.show();

        setStandardButton();
        setFacebookButton();
        setGoogleButton();
    }

    /**
     * Connect buttons of UI layer to button objects
     */
    private void initButtons(){
        facebookRegister = findViewById(R.id.facebookButton);
        googleRegister = findViewById(R.id.googleButton);
        itsmeRegister = findViewById(R.id.itsmeButton);
        standardRegister = findViewById(R.id.standardButton);
    }

    /**
     * Initialize OnClickListener of standard code button
     */
    public void setStandardButton(){
        standardRegister.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterOptions.this, RegisterStandard.class);
            intent.putExtra("Bike", bikeObject);
            startActivity(intent);
        });
    }

    /**
     * Initialize OnClickListener of google register code button
     */
    public void setGoogleButton(){
        googleRegister.setOnClickListener(v->{
            Intent intent = new Intent(RegisterOptions.this, RegistrationGoogle.class);
            intent.putExtra("Bike", bikeObject);
            startActivity(intent);
        });
    }

    /**
     * Initialize OnClickListener of facebook register button
     */
    public void setFacebookButton(){
        facebookRegister.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterOptions.this, RegisterFacebook.class);
            intent.putExtra("Bike", bikeObject);
            startActivity(intent);
        });
    }
}
