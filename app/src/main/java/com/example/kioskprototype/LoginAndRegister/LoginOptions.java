package com.example.kioskprototype.LoginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterAndObjects.ABikeObject;

/**
 * Activity which gives the different login options:
 *  - Standard (code)
 *  - Sms
 *  - Membercard
 *  - Fingerprint
 */
public class LoginOptions extends AppCompatActivity {

    /**
     * Button which takes the user to the LoginStandardCode activity
     */
    Button codeLogin;

    /**
     * Button which takes the user to the LoginSms activity
     */
    Button smsLogin;

    /**
     * Button which takes the user to the LoginMemberCardUSB activity
     */
    Button rfidButton;

    /**
     * Selected bike, to be rented by the User which wants to log in.
     */
    ABikeObject bikeObject;

    /**
     * Type of the bike.
     */
    String type;

    /**
     * When the activity is created:
     *  - Type & bike are retreived from previous activity
     *  - Buttons are set
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_options);

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");
        type = getIntent().getStringExtra("Type");

        initButtons();
    }

    private void initButtons(){
        codeLogin = findViewById(R.id.standardButton);
        smsLogin = findViewById(R.id.smsButton);
        rfidButton = findViewById(R.id.rfidButton);

        codeLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginOptions.this, LoginStandardCode.class);
            intent.putExtra("Bike", bikeObject);
            intent.putExtra("Type", type);
            startActivity(intent);
        });

        rfidButton.setOnClickListener(v->{
            Intent intent = new Intent(LoginOptions.this, LoginMemberCardBluetooth.class);
            intent.putExtra("Bike", bikeObject);
            intent.putExtra("Type", type);
            startActivity(intent);
        });

        smsLogin.setOnClickListener(v->{
            Intent intent = new Intent(LoginOptions.this, LoginSms.class);
            intent.putExtra("Bike", bikeObject);
            intent.putExtra("Type", type);
            startActivityForResult(intent,1);
        });
    }
}
