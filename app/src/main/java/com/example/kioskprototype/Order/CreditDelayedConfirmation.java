package com.example.kioskprototype.Order;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;

import org.w3c.dom.Text;

public class CreditDelayedConfirmation extends AppCompatActivity {

    private int type;
    private String mail;
    private ABikeObject bikeObject;

    TextView bikeNameView;
    TextView bikeAmountView;
    TextView infoViewType;
    TextView infoViewExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_delayed_confirmation);

        type = (int)getIntent().getIntExtra("Type",0);
        mail = (String)getIntent().getStringExtra("Mail");
        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");

        setBikeTextviews();
        setInfoTextviews();

    }

    public void setBikeTextviews(){
        bikeNameView = (TextView)findViewById(R.id.bikeNameViewConf);
        bikeAmountView = (TextView)findViewById(R.id.bikeAmountViewConf);

        bikeNameView.setText("Bike"+bikeObject.getId()+bikeObject.getType());
        bikeAmountView.setText(1+"");

    }

    public void setInfoTextviews(){
        infoViewType = (TextView)findViewById(R.id.confirmSideText);
        infoViewExtra = (TextView)findViewById(R.id.confirmSideText2);

        switch (type){
            case 1:
                infoViewType.setText("You've selected: Credit payment");

                break;
            case 2:
                infoViewType.setText("You've selected: Delayed payement");
                break;
        }

        String infoString="You can use the our services until your credits are 0.";
        if(bikeObject.getType() == 1){
            //Electric bike
            infoString = infoString + "\nWhen this happens and you keep on driving: the battery of the bike will switch off and an alarm will sound.";
        }else if(bikeObject.getType() == 2){
            //Pedal bike
            infoString = infoString + "\nWhen this happens and you keep on driving: an alarm will sound.";
        }
        infoViewExtra.setText(infoString);
    }
}
