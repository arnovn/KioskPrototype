package com.example.kioskprototype.LoginAndRegister;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.AccountSettings.AccountSettings;
import com.example.kioskprototype.HashingObject;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;
import com.example.kioskprototype.adapterView.LoadingDialog;
import com.example.kioskprototype.payment.PayForServices;
import com.example.kioskprototype.payment.PaymentSelect;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

/**
 * Class in charge of:
 *  - Connecting with the RFID-reader using Bluetooth
 *  - Polling & handling the bluetooth connection
 *  - Logging in the user when a registered card has been scanned which is coupled to the user
 *  - Registering a new card (overwriting the old code) when a new MemberCard is registered in the AccountSettings
 */
public class LoginMemberCardBluetooth extends AppCompatActivity {

    /**
     * Connection status strings passed ot the reader.
     */
    final String READ ="READ";
    final String STATUS = "STATUS";
    final String CANCEL = "CANCEL";
    final String FINISH = "FINISH";

    /**
     * Bluetooth connection object.
     */
    BluetoothSPP bluetooth;

    /**
     * Cancel button object of the cancel button on the UI layer
     */
    Button cancelButton;

    /**
     * EditText object in which the user can input the login mail if necessary, on the UI layer
     */
    EditText mailText;

    /**
     * Id of the user
     */
    int id;

    /**
     * Mail of the user
     */
    String mail;

    /**
     * The inputted mail is compared with this pattern to check if it is a legit E-mail address
     */
    String mailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    /**
     * Type of flow (RentABike, AccountSettings, ...)
     */
    String type;

    /**
     * Bike selected by the user when in the RentABike flow
     */
    ABikeObject bikeObject;

    /**
     * Name of the bluetooth device
     */
    String deviceName;

    /**
     * Loading dialog
     */
    LoadingDialog loadingDialog;

    /**
     * Handler which polls the status of the Reader each 5s
     */
    Handler androidPollHandler;

    /**
     * RFID code received from the RFID reader
     */
    String codeRFID;

    /**
     * Runnable which will query the status of the RFID reader
     */
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            bluetooth.send(STATUS, true);
        }
    };

    /**
     * Interval for querying the RFID reader's status
     */
    final int handlerInterval = 5000;
    /**
     * AccountSettings type: When inserting the user in the user fails this is returned to the AccountSettings activity
     */
    final int RESULT_FAILED = 2;

    /**
     * When the activity is created:
     *  - Initialize bluetooth connection with the RFID reader
     *  - Initialize Handler for polling the RFID reader
     *  - Retrieve necessary information from previous activity (Type, mail, bike, ...)
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_member_card_bluetooth);

        bluetooth = new BluetoothSPP(this);
        deviceName = "Not connected";
        loadingDialog = new LoadingDialog(LoginMemberCardBluetooth.this);
        androidPollHandler = new Handler();
        mailText = findViewById(R.id.editEmailTextBT);
        type = getIntent().getStringExtra("Type");

        //Check if in AccountSettings flow, if so, we  retrieve mail, else BikeObject should be passed
        if (type.equals("AccountSettings")) {
            mailText.setVisibility(View.INVISIBLE);
            mail = getIntent().getStringExtra("Mail");
        }else{
            bikeObject = (ABikeObject) getIntent().getSerializableExtra("Bike");
        }

        cancelButton = findViewById(R.id.cancelBluetoothButton);

        checkBluetoothAvailable();
        setBluetoothListener();
        checkConnected();

        androidPollHandler.postDelayed(runnable, handlerInterval);    //After 10s we check status of card reader
        cancelButton.setOnClickListener(v -> bluetooth.send(CANCEL, true));

    }

    /**
     * Sending to the RFID reader we could like to read a card
     */
    private void sendReadMessage(){
        bluetooth.send(READ, false);
    }

    /**
     * Check if inputted mail by the user is of the correct format.
     * @return
     *          true or false
     */
    private boolean checkMailEdit(){
        return mailText.getText().toString().trim().matches(mailPattern);
    }

    /**
     * When we start:
     *  - if bluetooth hasn't been enbale we ask permission to put on bluetooth
     */
    public void onStart() {
        super.onStart();
        if (!bluetooth.isBluetoothEnabled()) {
            bluetooth.enable();
        } else {
            if (!bluetooth.isServiceAvailable()) {
                bluetooth.setupService();
                bluetooth.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }

    /**
     * When the activity is destroyed we finish the bluetooth connection
     */
    public void onDestroy() {
        super.onDestroy();
        bluetooth.stopService();
    }

    /**
     * If bluetooth isn't set we should set it otherwise we return
     */
    private void checkBluetoothAvailable(){
        if (!bluetooth.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            //TODO: query for set bluetooth on
            bluetooth.disconnect();
            finish();
        }
    }

    /**
     * Listener for the bluetooth connection:
     *  - onDeviceConnected: OK, we can start reading card
     *  - onDeviceDisconnected: NOT OK, we go back and try again if we want
     *  - onDeviceFailure: NOT OK, we go bakc and try again if we want
     */
    private void setBluetoothListener(){
        bluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                loadingDialog.dismissDialog();
                deviceName = name;
                checkConnected();
                setOnReceiveListener();
                sendReadMessage();
            }

            public void onDeviceDisconnected() {
                loadingDialog.dismissDialog();
                finish();
            }

            public void onDeviceConnectionFailed() {
                loadingDialog.dismissDialog();
                Toast.makeText(getApplicationContext(), "ERROR: FAILED", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Listener for receiving messages from the RFID reader
     */
    private void setOnReceiveListener(){
        bluetooth.setOnDataReceivedListener((data, message) -> {
            checkMessage(message);
        });
    }

    /**
     * When we've receive something we check what the message is:
     *  - 1010: Reading, confirmation of the reader we've send a READ request, the RFID reader is now scanning for cards
     *  - 2020: Cancel, confirmation of the reader we've cancelled the activity so the FRID reader is set to inactive
     *  - 3030: Pending: we've asked the RFID reader for its STATUS, pending means it's waiting for a card to be presented by the user
     *  - 4040: Read_ended: we've asked the RFID read for its STATUS, read_ended means a card has been read so we should've received a code
     *  - 5050: Card_read: the reader has read a card, we should've received a code
     *      - based on the flow we login or register this code as new login code
     *  - 6060: Finish, confirmation we no longer need to read a card from the RFID reader
     * @param message
     *          Message sent by the RFID-reader
     */
    private void checkMessage(String message){
        String[] result = message.split("-");
        switch (result[0]){
            case "1010":
                //Reading
                System.out.println("Reading confirmed");
                break;
            case "2020":
                //Cancel
                System.out.println("Reading cancelled " + message);
                bluetooth.disconnect();
                finish();
                break;
            case "3030":
                //Pending
                System.out.println("Scanning for card" + message);
                androidPollHandler.postDelayed(runnable, handlerInterval);
                break;
            case "4040":
                //Read_ended
                if(codeRFID == null) {
                    Toast.makeText(getApplicationContext(), "Error: reader ended but code not received" + message, Toast.LENGTH_SHORT).show();
                    bluetooth.disconnect();
                    finish();
                }
                break;
            case "5050":
                //Card_read
                System.out.println("Received 2: " + result[1]);
                codeRFID = result[1].replaceAll("\\s","");
                if(type.equals("AccountSettings")){
                    inputRfidUser();
                }else if (checkMailEdit()){
                    checkRfidUser();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Input valid login mail before scanning.", Toast.LENGTH_SHORT).show();
                }
            case "6060":
                //Finish successfully received
                System.out.println("Card details being processed");
        }
    }

    /**
     * If in Registration new card mode we execute the InputCode async task updating the rfid of the user in the MySql Database
     */
    private void inputRfidUser(){
        new ConnectionInputNewCode().execute();
    }

    /**
     * Id we're in a login flow we retrieve the saved code from the MySql Database and check it with the scanned card
     */
    private void checkRfidUser(){
        mail = mailText.getText().toString();
        new ConnectionGetRfidCode().execute();
    }

    /**
     * We've retrieved the rfid code coupled to the users account & compare it with the scanned code
     * @param receivedCode
     *          Code we retrieved from the database
     * @throws NoSuchAlgorithmException
     *          If the SHA-256 algorithm isn't available on the device we throw this exception
     */
    public void compareCodes(String receivedCode) throws NoSuchAlgorithmException {
        HashingObject hashingObject = new HashingObject(codeRFID, receivedCode);

        if(receivedCode.equals(hashingObject.getGeneratedHash())){
            switch (type){
                case "RentABike":
                    bluetooth.disconnect();
                    toPaymentWindow();
                case "PayForServices":
                    bluetooth.disconnect();
                    toPayForServicesWindow();
                case "AccountSettingsLogin":
                    bluetooth.disconnect();
                    toAccounSettingsWindow();
            }
        }
    }

    /**
     * When we're in the Account Settings flow & logged in successfully we move to the AccountSettings activity
     */
    private void toAccounSettingsWindow(){
        Intent intent = new Intent(LoginMemberCardBluetooth.this, AccountSettings.class);
        intent.putExtra("Mail", mail);
        intent.putExtra("Id", id);
        intent.putExtra("Type", "AccountSettings");
        startActivity(intent);
    }

    /**
     * When we're in the Pay For Services flow & logged in successfully we move to the PayForServices activity
     */
    private void toPayForServicesWindow(){
        //Intent which goes to the pay for services class.
        Intent intent = new Intent(LoginMemberCardBluetooth.this, PayForServices.class);
        intent.putExtra("Mail", mail);
        intent.putExtra("Id", id);
        startActivity(intent);
    }

    /**
     * When we're in the Rent A Bike flow & logged in Successfullt we move to the PaymentSelect activity
     */
    private void toPaymentWindow(){
        Intent intent = new Intent(LoginMemberCardBluetooth.this, PaymentSelect.class);
        intent.putExtra("Bike", bikeObject);
        intent.putExtra("Mail", mail);
        intent.putExtra("Id", id);
        startActivity(intent);
    }

    /**
     * Result handlers of activities: REQUEST_CONNECT_DEVICE & ERQUEST_ENABLE_BLUETOOTH
     * @param requestCode
     *          The request code
     * @param resultCode
     *          The result of the request
     * @param data
     *          Data passed from the intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                bluetooth.connect(data);
                loadingDialog.startLoadingDialog();
                loadingDialog.getBuilder().setOnCancelListener(v->{
                    bluetooth.disconnect();
                    finish();
                });

            }
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bluetooth.setupService();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * Checker for the bluetooth connection
     */
    private void checkConnected(){
        if (bluetooth.getServiceState() == BluetoothState.STATE_CONNECTED) {
            if(deviceName == null){
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }

        } else {
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        }
    }

    /**
     * Class in charge updating the RFID code of the user in the MySql Database
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionInputNewCode extends AsyncTask<String, String, String> {
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
                HashingObject hashingObject = new HashingObject(codeRFID);
                String hashCode = hashingObject.getGeneratedHash();

                String host = "http://"+ getResources().getString(R.string.ip) +"/inputrfidcode.php?mail='"+ mail +"'&rfid='"+hashCode+"'";
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
         * On post execute we finish passing the correct parameters to the activity which called this activity
         *  - RESULT_OK: data successfully updated, the user has registered a new RFID card
         *  - RESULT_FAILED: something went wrong during the process of querying the database
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    bluetooth.send(FINISH, false);
                    setResult(RESULT_OK);
                    bluetooth.disconnect();
                    finish();

                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Failed: No user coupled to given mail.",Toast.LENGTH_SHORT).show();
                    setResult(RESULT_FAILED);
                    bluetooth.disconnect();
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * Class in charge retrieving the login code of the user.
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionGetRfidCode extends AsyncTask<String, String, String> {
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
                String host = "http://"+ getResources().getString(R.string.ip) +"/getrfidcode.php?mail='"+ mail+"'";
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
                    JSONObject jsonCodeObject = jsonResult.getJSONObject("data");
                    String receivedCode = jsonCodeObject.getString("rfid");
                    id = jsonCodeObject.getInt("id");
                    compareCodes(receivedCode);

                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Failed: No user coupled to given mail.",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
