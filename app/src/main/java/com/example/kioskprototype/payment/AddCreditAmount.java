package com.example.kioskprototype.payment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;
import com.example.kioskprototype.adapterView.ExtraCreditAdapter;
import com.example.kioskprototype.adapterView.ExtraCreditObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

public class AddCreditAmount extends AppCompatActivity {

    /**
     * Id of the user
     */
    int userId;

    /**
     * Id of the order
     */
    int orderId;

    /**
     * Mail of the user
     */
    String mail;

    /**
     * Method of payment
     */
    String paymentMethod;

    /**
     * Selected bike by the user
     */
    ABikeObject bikeObject;

    /**
     * Amount of extra credits to be added
     */
    int amountOfExtraCredits;

    /**
     * Amount of credits the user has on his account.
     */
    double userCredits;

    ExtraCreditAdapter extraCreditAdapter;
    ArrayList<ExtraCreditObject> extraCreditObjectList;

    TextView titleView;
    TextView amountView;
    ListView extraCreditListView;
    ArrayList<Button> extraCreditButtons;
    Button checkOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_amount);

        userId = getIntent().getIntExtra("Id", 0);
        mail = getIntent().getStringExtra("Mail");
        bikeObject = (ABikeObject) getIntent().getSerializableExtra("Bike");
        paymentMethod = getIntent().getStringExtra("Method");
        userCredits = getIntent().getDoubleExtra("UserCredits",0);

        initExtraCreditObjects();
        initTextViews();
        initButtons();
        initSetOnClickListeners();
    }

    @SuppressLint("SetTextI18n")
    private void initTextViews(){
        titleView = findViewById(R.id.titleViewCreditAmount);
        titleView.setText("ADD CREDITS " + paymentMethod.toUpperCase());
        amountView = findViewById(R.id.amountViewAddCredit);
        amountView.setText("€" + 0);
    }

    private void initExtraCreditObjects(){
        extraCreditObjectList = new ArrayList<>();
        extraCreditAdapter = new ExtraCreditAdapter(this,R.layout.adapter_extra_credits, extraCreditObjectList);
        extraCreditListView = findViewById(R.id.extraCreditList);
        extraCreditListView.setAdapter(extraCreditAdapter);

        extraCreditListView.setOnItemClickListener((parent, view, position, id) -> {
            ExtraCreditObject creditObject = extraCreditAdapter.getItem(position);
            assert creditObject != null;
            extraCreditObjectList.remove(position);
            extraCreditAdapter.notifyDataSetChanged();
            updateAmountView();
        });
    }

    private void initButtons(){
        extraCreditButtons = new ArrayList<>();
        Button fiveEuroButton = findViewById(R.id.fiveEuroButtonAddCredit);
        Button tenEuroButton = findViewById(R.id.tenEuroButtonAddCredit);
        Button fifteenEuroButton = findViewById(R.id.fifteenEuroButtonAddCredit);
        Button twentyEuroButton = findViewById(R.id.twentyEuroButtonAddCredit);
        extraCreditButtons.add(fiveEuroButton);
        extraCreditButtons.add(tenEuroButton);
        extraCreditButtons.add(fifteenEuroButton);
        extraCreditButtons.add(twentyEuroButton);

        checkOutButton = findViewById(R.id.checkoutButtonAddCredit);
    }

    private void initSetOnClickListeners(){
        for(Button button: extraCreditButtons){
            button.setOnClickListener(v -> {
                String buttonText = button.getText().toString();
                int amount;
                switch(buttonText){
                    case "+5":
                        amount = 5;
                        break;
                    case "+10":
                        amount = 10;
                        break;
                    case "+15":
                        amount = 15;
                        break;
                    case "+20":
                        amount = 20;
                        break;
                    default:
                        amount = 0;
                        break;
                }
                Date date = new Date();
                ExtraCreditObject extraCreditObject = new ExtraCreditObject(date, amount);
                extraCreditObjectList.add(extraCreditObject);
                extraCreditAdapter.notifyDataSetChanged();
                updateAmountView();
            });
        }

        checkOutButton.setOnClickListener(v -> {
            amountOfExtraCredits = getExtraCredits();
            new ConnectionInsertNewPayment().execute();
            Toast.makeText(getApplicationContext(), "Total amount :" + amountOfExtraCredits, Toast.LENGTH_LONG).show();
        });
    }

    private int getExtraCredits(){
        int accumulator = 0;
        if(extraCreditObjectList.isEmpty()){ return accumulator;}
        for(ExtraCreditObject extraCredits : extraCreditObjectList){
            accumulator = (int) (accumulator + extraCredits.getAmount());
        }
        return accumulator;
    }

    @SuppressLint("SetTextI18n")
    private void updateAmountView(){
        int partialTotal = 0;
        if(extraCreditObjectList.size() == 0){
            amountView.setText("€"+partialTotal);
            return;
        }
        for(ExtraCreditObject extraCredit: extraCreditObjectList){
            partialTotal = (int) (partialTotal + extraCredit.getAmount());
        }
        amountView.setText("€"+partialTotal);
    }

    public void toPaypalPayment(){
        Intent paypalIntent = new Intent(AddCreditAmount.this, PaypalPayment.class);
        paypalIntent.putExtra("Bike", bikeObject);
        paypalIntent.putExtra("Mail", mail);
        paypalIntent.putExtra("UserId", userId);
        paypalIntent.putExtra("OrderId", orderId);
        paypalIntent.putExtra("Amount", amountOfExtraCredits);
        startActivity(paypalIntent);
    }

    /**
     *  Class in charge of creating new order entry for the payment, status: PENDING
     */
    @SuppressLint("StaticFieldLeak")
    public class ConnectionInsertNewPayment extends AsyncTask<String, String, String> {
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
                String host = "http://"+ getResources().getString(R.string.ip) +"/insertcreditorder.php?ordername=" + paymentMethod + "&userid=" + userId + "&amount=" + amountOfExtraCredits;
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
         *  - if successful pending payment objects are created & added to the list
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    orderId = jsonResult.getInt("id");
                    toPaypalPayment();
                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Error: something went wrong when creating the payment order entry",Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
