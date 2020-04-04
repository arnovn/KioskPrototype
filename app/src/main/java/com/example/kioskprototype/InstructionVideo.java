package com.example.kioskprototype;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.adapterView.ABikeObject;
import com.example.kioskprototype.payment.PaymentSelect;

/**
 * When a new user has been registered, a little instruction video will be shown on how to use the services of Wow-Solutions
 */
public class InstructionVideo extends AppCompatActivity {

    /**
     * VideoView playing the instruction video at the UI layer
     */
    VideoView videoView;

    /**
     * If the user wishes to skip the video the next button can be pressed
     */
    Button buttonNext;

    /**
     * Selected bike by the user, to be rented.
     */
    ABikeObject bikeObject;

    /**
     * E-mail address of the user
     */
    String mail;

    /**
     * Id of the user
     */
    int id;

    /**
     * When the activity is created:
     *  - Retrieve bike and mail from previous activity
     *  - Initialize the VideoView and Button
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_video);

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");
        mail = getIntent().getStringExtra("Mail");
        id = getIntent().getIntExtra("Id", 0);

        videoView = findViewById(R.id.videoView);
        String uriPath = "android.resource://com.example.kioskprototype/"+R.raw.wowvideo;
        Uri uri = Uri.parse(uriPath);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

        buttonNext = findViewById(R.id.nextButton);

        final Intent intent = new Intent(InstructionVideo.this, PaymentSelect.class);
        intent.putExtra("Bike", bikeObject);
        intent.putExtra("Mail",mail);
        intent.putExtra("Id", id);

        buttonNext.setOnClickListener(v -> startActivity(intent));
    }
}
