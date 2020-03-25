package com.example.kioskprototype.LoginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.InstructionVideo;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity in charge of verifying the E-mail address of the user during registration.
 */
public class MailVerification extends AppCompatActivity {

    /**
     * Code entered by the user.
     */
    String enteredCode;

    /**
     * TextViews visualizing the inputted digits of the user by *
     * - Giving feedback to the user
     */
    TextView firstEntry;
    TextView secondEntry;
    TextView thirdEntry;
    TextView fourthEntry;
    TextView fifthEntry;
    TextView sixthEntry;

    /**
     * List of the Entry TextViews
     */
    List<TextView> entryList;

    /**
     * Buttons the user can interact with for inputting the verification code.
     */
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

    /**
     * Selected bike for the new user to rent
     */
    ABikeObject bikeObject;

    /**
     * Mail of the user
     */
    String mail;

    /**
     * Generated verification code
     */
    String verificationCode;

    /**
     * When the activity is created:
     *  - initialize the buttons & textviews
     *  - retrieve the bike, mail & verificationcode from previous activity
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_verification);

        enteredCode ="";
        entryList = new ArrayList<>();

        bikeObject = (ABikeObject) getIntent().getSerializableExtra("Bike");
        mail = getIntent().getStringExtra("Mail");
        verificationCode = getIntent().getStringExtra("VerificationCode");

        connectTextViews();
        connectButtons();
        setOnClickListeners();
    }

    /**
     * Connect the TextView Objects to the TextViews in the UI layer.
     * Add them to the entryList
     */
    public void connectTextViews(){
        firstEntry = findViewById(R.id.codeView1);
        secondEntry = findViewById(R.id.codeView2);
        thirdEntry = findViewById(R.id.codeView3);
        fourthEntry = findViewById(R.id.codeView4);
        fifthEntry = findViewById(R.id.codeView5);
        sixthEntry = findViewById(R.id.codeView6);
        entryList.add(firstEntry);
        entryList.add(secondEntry);
        entryList.add(thirdEntry);
        entryList.add(fourthEntry);
        entryList.add(fifthEntry);
        entryList.add(sixthEntry);
    }

    /**
     * When one of the input buttons (0-9) has been pressed we add a * to one of the TextViews for feedback to the user
     * @param entry
     *              the code up to now.
     */
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

    /**
     * When the verification has been inputted we check it with the actual verification code
     * - If true: We go the InstructionVideo activity
     * - Else: Toast("failed")
     */
    public void checkCode(){
        if(enteredCode.equals(verificationCode)){
            Toast.makeText(getApplicationContext(),"Verification succesful!",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MailVerification.this, InstructionVideo.class);
            intent.putExtra("Bike", bikeObject);
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

    /**
     *  When the DeleteButton is pressed we remove one of the * of one of the TextViews for feedback to the user
     */
    public  void deleteEntry(){
        if(enteredCode.length()>0){
            enteredCode = enteredCode.substring(0, enteredCode.length()-1);
            entryList.get(enteredCode.length()).setText("");
        }
    }

    /**
     * Connect the Button objects to the Buttons on the UI layer.
     */
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

    /**
     * Connect the Button objects to the Buttons on the UI layer.
     */
    public void connectButtons(){
        deleteButton = findViewById(R.id.buttonDel);
        button1 = findViewById(R.id.entryButton1);
        button2 = findViewById(R.id.entryButton2);
        button3 = findViewById(R.id.entryButton3);
        button4 = findViewById(R.id.entryButton4);
        button5 = findViewById(R.id.entryButton5);
        button6 = findViewById(R.id.entryButton6);
        button7 = findViewById(R.id.entryButton7);
        button8 = findViewById(R.id.entryButton8);
        button9 = findViewById(R.id.entryButton9);
        button0 = findViewById(R.id.entryButton0);
    }


    public void setDeletButton(){
        deleteButton.setOnClickListener(v -> deleteEntry());
    }

    public void setButton1(){
        button1.setOnClickListener(v -> {
            String entry = button1.getText().toString();
            addEntry(entry);
        });
    }

    public void setButton2(){
        button2.setOnClickListener(v -> {
            String entry = button2.getText().toString();
            addEntry(entry);
        });
    }

    public void setButton3(){
        button3.setOnClickListener(v -> {
            String entry = button3.getText().toString();
            addEntry(entry);
        });
    }

    public void setButton4(){
        button4.setOnClickListener(v -> {
            String entry = button4.getText().toString();
            addEntry(entry);
        });
    }

    public void setButton5(){
        button5.setOnClickListener(v -> {
            String entry = button5.getText().toString();
            addEntry(entry);
        });
    }
    public void setButton6(){
        button6.setOnClickListener(v -> {
            String entry = button6.getText().toString();
            addEntry(entry);
        });
    }

    public void setButton7(){
        button7.setOnClickListener(v -> {
            String entry = button7.getText().toString();
            addEntry(entry);
        });
    }

    public void setButton8(){
        button8.setOnClickListener(v -> {
            String entry = button8.getText().toString();
            addEntry(entry);
        });
    }

    public void setButton9(){
        button9.setOnClickListener(v -> {
            String entry = button9.getText().toString();
            addEntry(entry);
        });
    }

    public void setButton0(){
        button0.setOnClickListener(v -> {
            String entry = button0.getText().toString();
            addEntry(entry);
        });
    }
}
