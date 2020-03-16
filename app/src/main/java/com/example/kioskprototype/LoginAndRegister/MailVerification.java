package com.example.kioskprototype.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kioskprototype.InstructionVideo;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

public class MailVerification extends AppCompatActivity {

    String enteredCode;

    TextView firstEntry;
    TextView secondEntry;
    TextView thirdEntry;
    TextView fourthEntry;
    TextView fifthEntry;
    TextView sixthEntry;

    List<TextView> entryList;

    Button deleteButton;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;
    Button button8;
    Button button9;
    Button button0;

    ABikeObject bikeObject;
    String mail;
    String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_verification);

        enteredCode ="";
        entryList = new ArrayList<TextView>();

        bikeObject = (ABikeObject) getIntent().getSerializableExtra("Bike");
        mail = (String)getIntent().getStringExtra("Mail");
        verificationCode = (String)getIntent().getStringExtra("VerificationCode");

        connectTextViews();
        connectButtons();
        setOnClickListeners();
    }

    public void connectTextViews(){
        firstEntry = (TextView)findViewById(R.id.codeView1);
        secondEntry = (TextView)findViewById(R.id.codeView2);
        thirdEntry = (TextView)findViewById(R.id.codeView3);
        fourthEntry = (TextView)findViewById(R.id.codeView4);
        fifthEntry = (TextView)findViewById(R.id.codeView5);
        sixthEntry = (TextView)findViewById(R.id.codeView6);
        entryList.add(firstEntry);
        entryList.add(secondEntry);
        entryList.add(thirdEntry);
        entryList.add(fourthEntry);
        entryList.add(fifthEntry);
        entryList.add(sixthEntry);
    }

    public void addEntry(String entry){
        if(enteredCode.length()<6){
            enteredCode = enteredCode+entry;
            entryList.get(enteredCode.length()-1).setText("*");
        }
        if(enteredCode.length() == 6){
            //Check with code from mail.
            //Toast.makeText(getApplicationContext(),"Code to check: " + enteredCode,Toast.LENGTH_SHORT).show();
            checkCode();
        }
    }

    public void checkCode(){
        if(enteredCode.equals(verificationCode)){
            Toast.makeText(getApplicationContext(),"Verification succesful!",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MailVerification.this, InstructionVideo.class);
            intent.putExtra("Bike",(Serializable)bikeObject);
            intent.putExtra("Mail", mail);
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(),"Code failure: code doesn't match verification code.",Toast.LENGTH_SHORT).show();
            enteredCode="";
            for(TextView textviews : entryList){
                textviews.setText("");
            }
        }
    }

    public  void deleteEntry(){
        if(enteredCode.length()>0){
            enteredCode = enteredCode.substring(0, enteredCode.length()-1);
            entryList.get(enteredCode.length()).setText("");
        }
    }

    public void setOnClickListeners(){
        setDeletButton();
        setButton1();
        setButton2();
        setButton3();
        setButton4();
        setButton5();
        setButton6();
        setButton7();
        setButton8();
        setButton9();
        setButton0();
    }

    public void connectButtons(){
        deleteButton = (Button)findViewById(R.id.buttonDel);
        button1 = (Button)findViewById(R.id.entryButton1);
        button2 = (Button)findViewById(R.id.entryButton2);
        button3 = (Button)findViewById(R.id.entryButton3);
        button4 = (Button)findViewById(R.id.entryButton4);
        button5 = (Button)findViewById(R.id.entryButton5);
        button6 = (Button)findViewById(R.id.entryButton6);
        button7 = (Button)findViewById(R.id.entryButton7);
        button8 = (Button)findViewById(R.id.entryButton8);
        button9 = (Button)findViewById(R.id.entryButton9);
        button0 = (Button)findViewById(R.id.entryButton0);
    }


    public void setDeletButton(){
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEntry();
            }
        });
    }

    public void setButton1(){
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entry = button1.getText().toString();
                addEntry(entry);
            }
        });
    }

    public void setButton2(){
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entry = button2.getText().toString();
                addEntry(entry);
            }
        });
    }

    public void setButton3(){
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entry = button3.getText().toString();
                addEntry(entry);
            }
        });
    }

    public void setButton4(){
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entry = button4.getText().toString();
                addEntry(entry);
            }
        });
    }

    public void setButton5(){
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entry = button5.getText().toString();
                addEntry(entry);
            }
        });
    }
    public void setButton6(){
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entry = button6.getText().toString();
                addEntry(entry);
            }
        });
    }

    public void setButton7(){
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entry = button7.getText().toString();
                addEntry(entry);
            }
        });
    }

    public void setButton8(){
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entry = button8.getText().toString();
                addEntry(entry);
            }
        });
    }

    public void setButton9(){
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entry = button9.getText().toString();
                addEntry(entry);
            }
        });
    }

    public void setButton0(){
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entry = button0.getText().toString();
                addEntry(entry);
            }
        });
    }
}
