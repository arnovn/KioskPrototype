package com.example.kioskprototype.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kioskprototype.InstructionVideo;
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
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class LoginStandardCode extends AppCompatActivity {

    String enteredCode;
    String type;

    TextView firstEntry;
    TextView secondEntry;
    TextView thirdEntry;
    TextView fourthEntry;
    List<TextView> entryList;

    EditText editMailTextStd;

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

    ABikeObject bikeObject;
    String mail;
    String mailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String code;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_standard_code);

        enteredCode = "";
        code ="";
        entryList = new ArrayList<>();

        bikeObject = (ABikeObject) getIntent().getSerializableExtra("Bike");
        type = (String)getIntent().getStringExtra("Type");

        editMailTextStd = (EditText)findViewById(R.id.editEmailText);

        connectTextViews();
        connectButtons();
        setOnClickListeners();
    }

    public void connectTextViews(){
        firstEntry = (TextView)findViewById(R.id.codeView1);
        secondEntry = (TextView)findViewById(R.id.codeView2);
        thirdEntry = (TextView)findViewById(R.id.codeView3);
        fourthEntry = (TextView)findViewById(R.id.codeView4);

        entryList.add(firstEntry);
        entryList.add(secondEntry);
        entryList.add(thirdEntry);
        entryList.add(fourthEntry);
    }

    public void connectButtons(){
        deleteButton = (Button)findViewById(R.id.buttonDel);
        loginButton = (Button)findViewById(R.id.loginButtonStd);
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

    public void setOnClickListeners(){
        setDeletButton();
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

    public void setDeletButton(){
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEntry();
            }
        });
    }

    public void setLoginButton(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInput();
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

    public void addEntry(String entry){
        if(enteredCode.length()<4){
            enteredCode = enteredCode+entry;
            entryList.get(enteredCode.length()-1).setText("*");
        }
    }

    public  void deleteEntry(){
        if(enteredCode.length()>0){
            enteredCode = enteredCode.substring(0, enteredCode.length()-1);
            entryList.get(enteredCode.length()).setText("");
        }
    }

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

    public boolean checkMailEdit(){
        return editMailTextStd.getText().toString().trim().matches(mailPattern);
    }

    public boolean checkCodesMatch(){
        if(code.equals(enteredCode) ){
            return true;
        }else{
            return false;
        }
    }

    public void toPaymentWindow(){
        Intent intent = new Intent(LoginStandardCode.this, PaymentSelect.class);
        intent.putExtra("Bike", (Serializable)bikeObject);
        intent.putExtra("Mail", mail);
        startActivity(intent);
    }

    public void toPayForServicesWindow(){
        //Intent which goes to the pay for services class.
        Intent intent = new Intent(LoginStandardCode.this, PayForServices.class);
        intent.putExtra("Mail", mail);
        intent.putExtra("Id", id);
        startActivity(intent);
    }

    class ConnectionGetUserCode extends AsyncTask<String, String, String> {
        String result = "";
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://10.0.2.2/getusercode.php?mail='"+ mail +"'";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer stringBuffer = new StringBuffer("");

                String line ="";
                while((line = reader.readLine()) != null){
                    stringBuffer.append(line);
                    break;
                }
                reader.close();
                result = stringBuffer.toString();
            } catch (Exception e) {
                System.out.println("The exception: "+e.getMessage());
                return new String("The exception: " + e.getMessage());
            }
            return result;
        }

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
                        if(type.equals("RentABike")){
                            toPaymentWindow();
                        }else if(type.equals("PayForServices")){
                            toPayForServicesWindow();
                        }

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
