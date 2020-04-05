package com.example.kioskprototype.payment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.MainActivity;
import com.example.kioskprototype.Order.CreditDelayedConfirmation;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Activity where the user can choose the payment option:
 *  - Credit card payment
 *  - Bancontact payment
 *  - Delayed payment
 *  - Credit payment (is enough credits remain on the users account)
 */
public class PaymentSelect extends AppCompatActivity {

    /**
     * TextView visualizing the amount of remaining credits on the users' account at the UI layer
     */
    TextView creditView;

    /**
     * TextView visualizing the price for renting the bike for a certain amount of time at the UI layer
     */
    TextView priceBikeView;

    /**
     * Extra info TextView at the UI layer
     */
    TextView infoView;

    /**
     * Selection buttons guiding the user to the preferred payment option or logout if the user changed his mind during the process
     */
    Button cardButton;
    Button paypalButton;
    Button creditsButton;
    Button delayedButton;
    Button signOutButton;

    /**
     * The selected bike the be rented by the user
     */
    ABikeObject bikeObject;

    /**
     * E-mail address of the user
     */
    String mail;

    /**
     * 0 if delayedpayment not set
     * >0 if delayedpayment set (usually 1)
     */
    int delayedPayment;

    /**
     * Logincode of the user
     */
    String code;

    //TODO if the user has negative amount of credits: first pay depths before renting new bike
    /**
     * Credits of the user
     */
    double credits;

    /**
     * Bike type
     */
    int type;

    /**
     * Price of renting the bike for a certain amount of time
     */
    double priceperhour;

    /**
     * User id
     */
    int id;

    /**
     * When the activity is created:
     *  - Buttons & TextViews are set
     *  - Selected bike is retrieved from previous activity
     *  - Mail of the user is retrieved from previous activity
     *  - User info necessary for mayment is retrieved from the database
     * @param savedInstanceState
     *             Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_select);

        creditView          = findViewById(R.id.creditView);
        priceBikeView       = findViewById(R.id.bikePriceView);
        infoView            = findViewById(R.id.infoViewPS);
        cardButton    = findViewById(R.id.cardButton);
        paypalButton    = findViewById(R.id.paypalButton);
        creditsButton       = findViewById(R.id.creditsButton);
        delayedButton       = findViewById(R.id.delayedPaymentButton);
        signOutButton       = findViewById(R.id.signOutButton);

        bikeObject          = (ABikeObject)getIntent().getSerializableExtra("Bike");
        mail                = getIntent().getStringExtra("Mail");
        id                  = getIntent().getIntExtra("Id", 0);

        new ConnectionGetUserPaymentInfo().execute();

        setDelayedButton();
        setCreditsButton();
        setCardButton();
        setPaypalButton();
        setSignOutButton();
    }

    /**
     * Delayed payment button initializer
     *  -   If not set: Toast to the user ha can't payed delayed
     *  -   If set: we finalize the order
     */
    public void setDelayedButton(){
        delayedButton.setOnClickListener(v -> {
            if(delayedPayment == 0){
                Toast.makeText(getApplicationContext(),"Failed: delayed payment not set for your account.",Toast.LENGTH_SHORT).show();
            }else{
                //Handle delayed payment.
                Intent intent = new Intent(PaymentSelect.this, CreditDelayedConfirmation.class);
                intent.putExtra("Bike", bikeObject);
                intent.putExtra("Type", 1);
                intent.putExtra("Mail", mail);
                startActivity(intent);
            }
        });
    }

    /**
     * Credit payment button initializer
     *  - If sufficient amount of credits: we finalize the order
     *  - If not sufficient amount of credits: toast to user, pay another way
     */
    public void setCreditsButton(){
        creditsButton.setOnClickListener(v -> {
            if(credits < 5.0){
                Toast.makeText(getApplicationContext(),"Insufficient credits, has to be at least 5 euro", Toast.LENGTH_SHORT).show();
            }else{
                //Handle credit payment.
                Intent intent = new Intent(PaymentSelect.this, CreditDelayedConfirmation.class);
                intent.putExtra("Bike", bikeObject);
                intent.putExtra("Type", 1);
                intent.putExtra("Mail", mail);
                startActivity(intent);
            }
        });
    }

    /**
     * Bancontact button initializer
     */
    public void setCardButton(){
        cardButton.setOnClickListener(v -> {
            //Handle bancontact payment.
        });
    }

    /**
     * Creditcard button initializer
     */
    public void setPaypalButton(){
        paypalButton.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentSelect.this, AddCreditAmount.class);
            intent.putExtra("Bike", bikeObject);
            intent.putExtra("Type", 1);
            intent.putExtra("Mail", mail);
            intent.putExtra("Id", id);
            intent.putExtra("Method", "Paypal");
            intent.putExtra("UserCredits", credits);
            startActivity(intent);
        });
    }

    /**
     * Signout button initializer
     */
    public void setSignOutButton(){
        signOutButton.setOnClickListener(v -> startActivity(new Intent(PaymentSelect.this, MainActivity.class)));
    }

    /**
     * Class in charge of retrieving necessary payment info of the user from the MySql Database
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionGetUserPaymentInfo extends AsyncTask<String, String, String> {
        String result ="";

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
                String host = "http://"+ getResources().getString(R.string.ip) +"/getuserpaymentinfo.php?mail='"+ mail+"'";
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
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database:
         *  - if successful we set this payment info
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    JSONArray paymentinfos  = jsonResult.getJSONArray("info");
                    JSONObject paymentinfo  = paymentinfos.getJSONObject(0);

                    code                    = paymentinfo.getString("code");
                    credits                 = paymentinfo.getDouble("credits");
                    delayedPayment          = paymentinfo.getInt("delayedpayment");

                    creditView.setText(credits+" euro");
                    new ConnectionBikeTypeInfo().execute();

                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Payment get failed...",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Class in charge of retrieving the bike type info from the MySql Database
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionBikeTypeInfo extends AsyncTask<String, String, String> {
        String result ="";

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
                type = 1;
                String host = "http://"+ getResources().getString(R.string.ip) +"/getbiketypeinfo.php?type="+ type;
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
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database:
         *  - if successful we set the bike info
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    JSONArray bikeinfos = jsonResult.getJSONArray("bikeinfo");
                    JSONObject bikeinfo = bikeinfos.getJSONObject(0);

                    priceperhour = bikeinfo.getDouble("priceperhour");
                    Toast.makeText(getApplicationContext(),"Price per hour: " + priceperhour,Toast.LENGTH_LONG).show();

                    priceBikeView.setText(priceperhour+" euro/min");
                    infoView.setText("With the remaining credits on your account you can ride for " + credits/priceperhour +" minutes." +
                            "\nIf this amount of time is sufficient you can press the 'CREDITS' button");

                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Payment get failed...",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
