package com.example.kioskprototype.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class PayForServices extends AppCompatActivity {

    int userId;
    double credits;

    double amount;

    TextView amountView;
    TextView depthView;
    ListView listView;
    ListView creditsListView;

    PendingPaymentAdapter adapter;
    ExtraCreditAdapter creditAdapter;
    ArrayList<PendingPaymentObject> pendingPaymentObjects;
    ArrayList<ExtraCreditObject> extraCreditObjects;

    Button add5Credits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_for_services);

        pendingPaymentObjects = new ArrayList<>();
        extraCreditObjects = new ArrayList<>();
        adapter = new PendingPaymentAdapter(this,R.layout.adapter_pending_payments,pendingPaymentObjects);
        creditAdapter = new ExtraCreditAdapter(this, R.layout.adapter_extra_credits, extraCreditObjects);
        listView = (ListView)findViewById(R.id.paymentList);
        creditsListView = (ListView)findViewById(R.id.extraCreditList);
        creditsListView.setAdapter(creditAdapter);

        amount = 0;
        userId = getIntent().getIntExtra("Id", 0);

        setTextViews();
        setButtons();

        new ConnectionGetUserCredits().execute();

    }

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

    public void setButtons(){
        add5Credits = (Button)findViewById(R.id.fiveEuroButton);

        add5Credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                ExtraCreditObject extraCredit = new ExtraCreditObject(date, 5);
                extraCreditObjects.add(extraCredit);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void setTextViews(){
        amountView = (TextView)findViewById(R.id.amountView);
        depthView = (TextView)findViewById(R.id.depthView);
    }

    class ConnectionGetUserCredits extends AsyncTask<String, String, String>{
        String result = "";
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://10.0.2.2/getusercredits.php?userid=" + userId;
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

    public class ConnectionGetPendingPayments extends AsyncTask<String, String, String>{

        String result = "";
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://10.0.2.2/getpendingpayments.php?userid=" + userId;
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
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        format.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date startStamp = format.parse(startRent);
                        Date endStamp = format.parse(endRent);


                        PendingPaymentObject pendingPaymentObject = new PendingPaymentObject(id, bikeid, startStamp, endStamp, amount, amountPayed, type, pricePerHour);
                        pendingPaymentObjects.add(pendingPaymentObject);
                        listView.setAdapter(adapter);
                    }


                    //Set listview to max height
                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Failed: credits couldn't be retreived.",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
