package com.example.kioskprototype.LoginAndRegister;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.AccountSettings.AccountSettings;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;
import com.example.kioskprototype.payment.PayForServices;
import com.example.kioskprototype.payment.PaymentSelect;

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

/**
 * Activity in charge of logging in the user.
 *  - Checking if inputted mail exists inside the MySql Database.
 *  - Checking the inputted code is the same as the code from the user attached to the given mail in the mySql Database.
 */
public class LoginStandardCode extends AppCompatActivity {

    /**
     * Code entered by the user.
     */
    String enteredCode;

    /**
     * Type of the bike
     */
    String type;

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
     * EditText where the user can input his E-mail address.
     */
    EditText editMailTextStd;

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
    String code;

    /**
     * Id of the user
     */
    int id;

    /**
     * Whent he activity is created:
     *  - EnteredCode & Code & entryList are initialized
     *  - Selected bike is retreived from previous activity
     *  - Type of the selected bike is retreived from previous activity
     *  - EditTexts, TextViews, Buttons are initialized.
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_standard_code);

        enteredCode = "";
        code ="";
        entryList = new ArrayList<>();

        bikeObject = (ABikeObject) getIntent().getSerializableExtra("Bike");
        type = getIntent().getStringExtra("Type");

        editMailTextStd = findViewById(R.id.editEmailText);

        connectTextViews();
        connectButtons();
        setOnClickListeners();
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
        loginButton = findViewById(R.id.loginButtonStd);
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
        if(checkMailEdit()){
            //First check if mail address in database & get code assigned with the mail address
            mail = editMailTextStd.getText().toString();
            new ConnectionGetUserCode().execute();
            //Check if code assigned to mail address is same as code inputted at kiosk
        }else{
            //User needs to input valid mail address.
            Toast.makeText(getApplicationContext(),"Input valid mail.",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Check if inputted mail by the user is of the correct format.
     * @return
     *          true or false
     */
    public boolean checkMailEdit(){
        return editMailTextStd.getText().toString().trim().matches(mailPattern);
    }

    /**
     * Check if the inputted code is the same as the code from the MySql Database assigned to the mail.
     * @return
     *          true or false.
     */
    public boolean checkCodesMatch(){
        return code.equals(enteredCode);
    }

    /**
     * If everything succeeded & type = rentabike: we go to the PaymentSelect activity.
     */
    public void toPaymentWindow(){
        Intent intent = new Intent(LoginStandardCode.this, PaymentSelect.class);
        intent.putExtra("Bike", bikeObject);
        intent.putExtra("Mail", mail);
        startActivity(intent);
    }

    /**
     * If everything succeeded & type = payforservices: we go to the PayForServices activity
     */
    public void toPayForServicesWindow(){
        //Intent which goes to the pay for services class.
        Intent intent = new Intent(LoginStandardCode.this, PayForServices.class);
        intent.putExtra("Mail", mail);
        intent.putExtra("Id", id);
        startActivity(intent);
    }

    /**
     * If everything succeeded & type = accountsettings: we got to AccountSettings activity
     */
    public void toAccountSettingsWindow(){
        Intent intent = new Intent(LoginStandardCode.this, AccountSettings.class);
        intent.putExtra("Mail", mail);
        intent.putExtra("Id", id);
        startActivity(intent);
    }

    /**
     * WHen everything succeeded: we check the type & go to the correct activity.
     */
    public void toNextWindow(){
        switch (type) {
            case "RentABike":
                toPaymentWindow();
                break;
            case "PayForServices":
                toPayForServicesWindow();
                break;
            case "AccountSettings":
                toAccountSettingsWindow();
                break;
        }
    }

    /**
     * Class in charge retrieving the login code of the user.
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionGetUserCode extends AsyncTask<String, String, String> {
        String result = "";

        /**
         * Method in charge of querying the database through an HTTP request.
         * @param strings
         *          Paramaters passed when the execution of the AsyncTask is called;
         * @return
         *          Returns the response of the database.
         */
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://10.0.2.2/getusercode.php?mail='"+ mail +"'";
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
            } catch (Exception e) {
                System.out.println("The exception: "+e.getMessage());
                return "The exception: " + e.getMessage();
            }
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database:
         *  - We check if the retrieved code matches the inputted code
         *  - IF successful: we go to the next window
         *  - ELSE: login failed
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    JSONArray codeinfos = jsonResult.getJSONArray("code");
                    JSONObject codeinfo = codeinfos.getJSONObject(0);

                    code = codeinfo.getString("code");
                    id = codeinfo.getInt("id");

                    //Check if codes match
                    if(checkCodesMatch()){
                        //Successfull!
                        toNextWindow();

                    }else{
                        //Unsuccesfull.
                        Toast.makeText(getApplicationContext(), "Failed: codes don't match", Toast.LENGTH_LONG).show();
                    }

                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Failed: No user coupled to given mail.",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
