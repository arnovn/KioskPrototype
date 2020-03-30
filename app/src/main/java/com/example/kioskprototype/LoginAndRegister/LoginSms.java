package com.example.kioskprototype.LoginAndRegister;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.InstructionVideo;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;
import com.example.kioskprototype.adapterView.MailDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class in charge for logging in the user by sms verification.
 */
public class LoginSms extends AppCompatActivity implements MailDialog.MailDialogListener{

    /**
     * Different buttons that the user can use to:
     *  - insert the login code
     *  - delete a number
     *  - login to the system is everything is inserted correctly
     */
    Button deleteButton;
    Button loginButton;
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
     * TextView visualizing the first digit of the code with *
     *  - Giving feedback to the user he has inserted the first digit.
     */
    TextView firstEntry;

    /**
     * TextView visualizing the second digit of the code with *
     *  - Giving feedback to the user he has inserted the second digit.
     */
    TextView secondEntry;

    /**
     * TextView visualizing the third digit of the code with *
     *  - Giving feedback to the user he has inserted the third digit.
     */
    TextView thirdEntry;

    /**
     * TextView visualizing the fourth digit of the code with *
     *  - Giving feedback to the user he has inserted the fourth digit.
     */
    TextView fourthEntry;

    /**
     * List containing the four textviews
     */
    List<TextView> entryList;

    /**
     * Code entered by the user.
     */
    String enteredCode;

    /**
     * Selected bike by the user, to be rented.
     */
    ABikeObject bikeObject;

    /**
     * E-mail address of the user
     */
    String mail;

    /**
     * The inputted mail is compared with this pattern to check if it is a legit E-mail address
     */
    String mailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    /**
     * Actual login code
     */
    String verificationCode;

    /**
     * Custom dialog for inserting the mail coupled to the users' account
     */
    MailDialog mailDialog;

    /**
     * Phone number retrieved MySql Database;, from inputted mail address in dialog by the user
     */
    String phoneNumber;

    /**
     * When the activity is created:
     *  - Buttons, TextViews and attributes are initialized
     *  - MailDialog is created and displayed
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sms);

        enteredCode = "";
        verificationCode ="";
        entryList = new ArrayList<>();

        bikeObject = (ABikeObject) getIntent().getSerializableExtra("Bike");

        connectTextViews();
        connectButtons();
        setOnClickListeners();

        openMailDialog();
    }

    /**
     * SMS sender to users' phone
     */
    public void sendVerificationCode(){
        /*
         * Normally we input the phone number (phoneNumber) we retrieved from the database
         * Since an SMS center is needed for sending text messages using the internet
         * and none of them are free I'm using another android emulator.
         */
        String destinationAddress = "5556"; //Normally: phoneNumber

        verificationCode = generateCode();

        String smsBody = "Your Wow-verificationcode is: " + verificationCode;

        //sentIntent = PendingIntent.getBroadcast(this, 0,new Intent(SENT), 0);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(destinationAddress, null, smsBody, null, null);
    }

    /**
     * Code generator for login code
     * @return
     *              Login code of the user to be used at the Kiosk or Bikes to login
     */
    public String generateCode(){
        Random random = new Random();
        int randomNumber = random.nextInt(9999  );
        if(randomNumber < 10){
            return "000"+randomNumber;
        }else if(randomNumber < 100){
            return "00"+randomNumber;
        }else if(randomNumber < 1000){
            return "0" + randomNumber;
        }else{
            return "" + randomNumber;
        }
    }

    /**
     * Method in charge of generating the Mail dialog
     */
    private void openMailDialog(){
        mailDialog = new MailDialog();
        mailDialog.show(getSupportFragmentManager(),"insert mail");
    }

    /**
     * Implementation of MailDialog applyTexts()
     *  - We set the mail inputted in the MailDialog to the mail of the activity
     * @param mailaddress
     *                  Phonenumber inserted into the PhoneDialog
     */
    @Override
    public void applyTexts(String mailaddress) {
        this.mail = mailaddress;
        if(!checkMailEdit()){
            Toast.makeText(getApplicationContext(),"Input valid mail.", Toast.LENGTH_LONG).show();
            return;
        }
        mailDialog.dismiss();
        new ConnectionGetUserPhone().execute();
    }

    /**
     * Implementation of the MailDialog cancelPressed()
     *  - We return to previous activity (LoginOptions)
     */
    @Override
    public void cancelPressed() {
        finish();
    }

    /**
     * Connect the TextView objects to the TextViews on the UI layer.
     * Add the TextView objects to the entryList.
     */
    public void connectTextViews(){
        firstEntry = findViewById(R.id.codeView1);
        secondEntry = findViewById(R.id.codeView2);
        thirdEntry = findViewById(R.id.codeView3);
        fourthEntry = findViewById(R.id.codeView4);

        entryList.add(firstEntry);
        entryList.add(secondEntry);
        entryList.add(thirdEntry);
        entryList.add(fourthEntry);
    }

    /**
     * Connect the Button objects to the Buttons on the UI layer.
     */
    public void connectButtons(){
        deleteButton = findViewById(R.id.buttonDel);
        loginButton = findViewById(R.id.loginButtonSMS);
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

    /**
     * Initialize the OnClickListeners of the buttons.
     */
    public void setOnClickListeners(){
        setDeleteButton();
        setLoginButton();
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
     * Initialize the OnClickListeners of the buttons
     */
    public void setDeleteButton(){
        deleteButton.setOnClickListener(v -> deleteEntry());
    }

    public void setLoginButton(){
        loginButton.setOnClickListener(v -> checkInput());
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

    /**
     * When one of the input buttons (0-9) has been pressed we add a * to one of the TextViews for feedback to the user
     * @param entry
     *              the code up to now.
     */
    public void addEntry(String entry){
        if(enteredCode.length()<4){
            enteredCode = enteredCode+entry;
            entryList.get(enteredCode.length()-1).setText("*");
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
     * Check the user mail input is inside the MySql Database.
     */
    public void checkInput() {
        //check if mail valid
        if(verificationCode.equals(enteredCode)){
            //GO TO NEXT INTENT
            Intent instructionIntent = new Intent(LoginSms.this, InstructionVideo.class);
            instructionIntent.putExtra("Bike", bikeObject);
            instructionIntent.putExtra("Mail", mail);
            startActivity(instructionIntent);
        }else{
            Toast.makeText(getApplicationContext(), "Faulty verification code.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Check if inputted mail by the user is of the correct format.
     * @return
     *          true or false
     */
    public boolean checkMailEdit(){
        return mail.trim().matches(mailPattern);
    }

    /**
     * Class in charge of retrieving the users' phone from the MySql Database.
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionGetUserPhone extends AsyncTask<String, String, String> {
        String result = "";
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://"+ getResources().getString(R.string.ip) +"/getuserphone.php?mail='" + mail+"'";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuilder stringBuffer = new StringBuilder();

                String line;
                while((line = reader.readLine()) != null){
                    stringBuffer.append(line);
                }
                reader.close();
                result = stringBuffer.toString();
            }catch (Exception e) {
                System.out.println("The exception: "+e.getMessage());
                return "The exception: " + e.getMessage();
            }
            System.out.println("We here 3");
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database.
         *  - We send the verification code to the retrieved phone number
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            System.out.println(result);
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    JSONArray userDetails = jsonResult.getJSONArray("phone");
                    JSONObject userDetail = userDetails.getJSONObject(0);
                    phoneNumber = userDetail.getString("phone");
                    sendVerificationCode();

                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Retrieving phone number failed...",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
