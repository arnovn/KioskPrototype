package com.example.kioskprototype.payment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class PaypalPayment extends AppCompatActivity {

    //Sandbox account: sb-hu4yp1369124@business.example.com
    //ClientID: AWbtJWYvpTOJPPc_iO-4tqMjdRB0DmWm27a_23m61ctvWSyVs1wC25zmc25meWOtikVU56VW_dztbY9o
    //Secret: EGV-UG8S6BLBfdGsPljkVOB0uLQ0TwPetGu49CL_dJkD0WehJTR41iKUCh26vg0xmR1eMc5tLzdUU-Pz

    ImageView qrView;

    ABikeObject bikeObject;
    String mail;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal_payment);

        bikeObject = (ABikeObject) getIntent().getSerializableExtra("Bike");
        mail = getIntent().getStringExtra("Mail");
        id = getIntent().getIntExtra("Id", 0);

        qrView = findViewById(R.id.qrPaypalView);

        Bundle bundle = new Bundle();
        bundle.putInt("Amount", 50);
        bundle.putInt("Id", 0);
        bundle.putString("Name", "Arno");
        String data = "Amount: 50";

        String jsonData = "{\"id\":\""+ id +"\", \"amount\": \"50\"}";

        QRGEncoder qrgEncoder = new QRGEncoder(jsonData,null, QRGContents.Type.TEXT, 500);
        try {
            Bitmap qrBit = qrgEncoder.encodeAsBitmap();
            qrView.setImageBitmap(qrBit);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }
}
