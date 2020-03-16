package com.example.kioskprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kioskprototype.adapterView.ABikeObject;
import com.example.kioskprototype.payment.PaymentSelect;

import java.io.Serializable;

public class InstructionVideo extends AppCompatActivity {

    VideoView videoView;
    Button buttonNext;
    ABikeObject bikeObject;
    String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_video);

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");
        mail = (String)getIntent().getStringExtra("Mail");

        videoView = (VideoView)findViewById(R.id.videoView);
        String uriPath = "android.resource://com.example.kioskprototype/"+R.raw.wowvideo;
        Uri uri = Uri.parse(uriPath);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

        buttonNext = (Button)findViewById(R.id.nextButton);

        final Intent intent = new Intent(InstructionVideo.this, PaymentSelect.class);
        intent.putExtra("Bike",(Serializable)bikeObject);
        intent.putExtra("Mail",mail);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }
}
