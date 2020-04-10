package com.example.kioskprototype.payment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ExtraCreditAdapter;
import com.example.kioskprototype.adapterView.ExtraCreditObject;
import com.example.kioskprototype.adapterView.PendingPaymentAdapter;
import com.example.kioskprototype.adapterView.PendingPaymentObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Activity in charge of handling pay for service requests
 *  - Paying for depths
 *  - Adding credits to your account
 */
//TODO: imeplment checkout payment (paypal)
public class PayForServices extends AppCompatActivity {

    /**
     * Id of the user logged in at the Kiosk
     */
    int userId;

    /**
     * Amount of credits the user has on his account (gathered from MySql Database)
     */
    double credits;

    /**
     * Amount of depth the user has (when credits are negative)
     */
    double amount;

    /**
     * Extra credits the user wants to add to his account
     */
    double extraCredits;

    /**
     * Total amount of credits to be payed by the user visualization for the UI layer
     */
    TextView amountView;

    /**
     * Amount of depth the user has visualization for the UI layer
     */
    TextView depthView;

    /**
     * List visualizing the different Orders which cause the depth at UI layer
     */
    ListView listView;

    /**
     * List visualizing the extra credits to be added on the UI layer
     */
    ListView creditsListView;

    /**
     * Adapter for the depth ListView
     */
    PendingPaymentAdapter adapter;

    /**
     * Adapter for the extra credit ListView
     */
    ExtraCreditAdapter creditAdapter;

    /**
     * Objects of all pending payments that the user needs to pay for
     */
    ArrayList<PendingPaymentObject> pendingPaymentObjects;

    /**
     * Objects of all extra credits to be added to the users' account
     */
    ArrayList<ExtraCreditObject> extraCreditObjects;

    /**
     * Buttons on UI layer so the user can add a certain amount of credits to his account
     */
    Button add5Credits;
    Button add10Credits;
    Button add15Credits;
    Button add20Credits;

    /**
     * When the activity is created:
     *  - Lists, TextViews & Buttons are initialized
     *  - Credits/depth is retrieved and set
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_for_services);

        pendingPaymentObjects = new ArrayList<>();
        extraCreditObjects = new ArrayList<>();
        adapter = new PendingPaymentAdapter(this,R.layout.adapter_pending_payments,pendingPaymentObjects);
        creditAdapter = new ExtraCreditAdapter(this, R.layout.adapter_extra_credits, extraCreditObjects);
        listView = findViewById(R.id.paymentList);
        creditsListView = findViewById(R.id.extraCreditList);
        creditsListView.setAdapter(creditAdapter);

        amount = 0;
        extraCredits = 0;
        userId = getIntent().getIntExtra("Id", 0);

        setTextViews();
        setButtons();
        setCreditListViewListener();

        new ConnectionGetUserCredits().execute();

    }

    /**
     * When the credits are retrieved from the database, we set the depth & textviews
     * - if no depth we just leave it at 0
     * @param credits
     *              Credits retrieved from the MySql Database
     */
    @SuppressLint("SetTextI18n")
    public void setCredits(double credits){
        this.credits = credits;

        if(this.credits >= 0){
            amountView.setText(amount + " euro");
            depthView.setText("You have no depths.");
        }else if(this.credits < 0){
            amount = 0-credits;
            amountView.setText(amount + " euro");
            depthView.setText("You have " + amount +"depths.");
        }
    }

    /**
     * When the user added extra credits to his account he can remove them
     *  - when clicked on an object inside the extra credit ListView it is removed and everything is updated
     */
    public void setCreditListViewListener(){
        creditsListView.setOnItemClickListener((parent, view, position, id) -> {
            ExtraCreditObject creditObject = creditAdapter.getItem(position);
            assert creditObject != null;
            extraCredits = extraCredits - creditObject.getAmount();
            extraCreditObjects.remove(position);
            creditAdapter.notifyDataSetChanged();
            updateAmountView();
        });
    }

    /**
     * Method in charge of updating the total amount to be payed when the uses adds or deletes extra credits
     */
    @SuppressLint("SetTextI18n")
    public void updateAmountView(){
        double totalAmount;
        if(credits < 0){
            totalAmount = extraCredits - credits;
        }else{
            totalAmount = extraCredits;
        }
        amountView.setText(totalAmount + " euro");
    }

    /**
     * Extra credit button initialization
     */
    public void setButtons(){
        add5Credits = findViewById(R.id.fiveEuroButton);
        add10Credits = findViewById(R.id.tenEuroButton);
        add15Credits = findViewById(R.id.fifteenEuroButton);
        add20Credits = findViewById(R.id.twentyEuroButton);

        add5Credits.setOnClickListener(v -> {
            Date date = new Date();
            ExtraCreditObject extraCredit = new ExtraCreditObject(date, 5);
            extraCreditObjects.add(extraCredit);
            creditAdapter.notifyDataSetChanged();
            extraCredits = extraCredits + 5;
            updateAmountView();
        });

        add10Credits.setOnClickListener(v -> {
            Date date = new Date();
            ExtraCreditObject extraCredit = new ExtraCreditObject(date, 10);
            extraCreditObjects.add(extraCredit);
            adapter.notifyDataSetChanged();
            extraCredits = extraCredits + 10;
            updateAmountView();
        });

        add15Credits.setOnClickListener(v -> {
            Date date = new Date();
            ExtraCreditObject extraCredit = new ExtraCreditObject(date, 15);
            extraCreditObjects.add(extraCredit);
            adapter.notifyDataSetChanged();
            extraCredits = extraCredits + 15;
            updateAmountView();
        });

        add20Credits.setOnClickListener(v -> {
            Date date = new Date();
            ExtraCreditObject extraCredit = new ExtraCreditObject(date, 20);
            extraCreditObjects.add(extraCredit);
            adapter.notifyDataSetChanged();
            extraCredits = extraCredits + 20;
            updateAmountView();
        });
    }

    /**
     * TextVie initialization
     */
    public void setTextViews(){
        amountView = findViewById(R.id.amountView);
        depthView = findViewById(R.id.depthView);
    }

    /**
     *  Class in charge of retrieving the users' credits
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionGetUserCredits extends AsyncTask<String, String, String>{
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
                String host = "http://"+ getResources().getString(R.string.ip) +"/getusercredits.php?userid=" + userId;
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
         *  - if successful set the credits & retrieve the pending payments for the pending payment list
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    JSONArray creditinfos = jsonResult.getJSONArray("credits");
                    JSONObject creditinfo = creditinfos.getJSONObject(0);

                    double userCredit = creditinfo.getDouble("credits");
                    setCredits(userCredit);

                    new ConnectionGetPendingPayments().execute();

                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Failed: credits couldn't be retreived.",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     *  Class in charge of retrieving the different pending payments of the user for the pending payment list
     */
    @SuppressLint("StaticFieldLeak")
    public class ConnectionGetPendingPayments extends AsyncTask<String, String, String>{
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
                String host = "http://"+ getResources().getString(R.string.ip) +"/getpendingpayments.php?userid=" + userId;
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
                    JSONArray pendingPayments = jsonResult.getJSONArray("pendingpayments");
                    for(int i = 0; i < pendingPayments.length(); i++){
                        JSONObject pendingPayment = pendingPayments.getJSONObject(i);
                        int id = pendingPayment.getInt("id");
                        int bikeid = pendingPayment.getInt("bikeid");
                        String startRent = pendingPayment.getString("startrent");
                        String endRent = pendingPayment.getString("endrent");
                        double amount = pendingPayment.getDouble("amount");
                        double amountPayed = pendingPayment.getDouble("amountpayed");
                        int type = pendingPayment.getInt("type");
                        double pricePerHour = pendingPayment.getDouble("priceperhour");
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        format.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date startStamp = format.parse(startRent);
                        Date endStamp = format.parse(endRent);


                        PendingPaymentObject pendingPaymentObject = new PendingPaymentObject(id, bikeid, startStamp, endStamp, amount, amountPayed, type, pricePerHour);
                        pendingPaymentObjects.add(pendingPaymentObject);
                        listView.setAdapter(adapter);
                    }


                    //Set listview to max height
                }else if(success == 0){
                    listView.setAdapter(adapter);
                    Toast.makeText(getApplicationContext(),"No pending payments.",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
