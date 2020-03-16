package com.example.kioskprototype.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class PaymentSelect extends AppCompatActivity {

    TextView creditView;
    TextView priceBikeView;
    TextView infoView;
    Button creditCardButton;
    Button bancontactButton;
    Button creditsButton;
    Button delayedButton;

    ABikeObject bikeObject;
    String mail;

    int delayedPayment;
    String code;
    double credits;
    int type;

    double priceperhour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_select);

        creditView = (TextView)findViewById(R.id.creditView);
        priceBikeView = (TextView)findViewById(R.id.bikePriceView);
        infoView = (TextView)findViewById(R.id.infoViewPS);
        creditCardButton = (Button)findViewById(R.id.creditButton);
        bancontactButton = (Button)findViewById(R.id.bancontactButton);
        creditsButton = (Button)findViewById(R.id.creditsButton);
        delayedButton = (Button)findViewById(R.id.delayedPaymentButton);

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");
        mail = (String)getIntent().getStringExtra("Mail");

        //type = bikeObject.getType();

        new ConnectionGetUserPaymentInfo().execute();

        setDelayedButton();
        setCreditsButton();
        setBancontactButton();
        setCreditCardButton();
    }

    public void setDelayedButton(){
        delayedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(delayedPayment == 0){
                    Toast.makeText(getApplicationContext(),"Failed: delayed payment not set for your account.",Toast.LENGTH_SHORT);
                }else{
                    //Handle delayed payment.
                }
            }
        });
    }

    public void setCreditsButton(){
        creditsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(credits < 5.0){
                    Toast.makeText(getApplicationContext(),"Insufficient credits, has to be at least 5 euro", Toast.LENGTH_SHORT);
                }else{
                    //Handle credit payment.
                }
            }
        });
    }

    public void setBancontactButton(){
        bancontactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Handle bancontact payment.
            }
        });
    }

    public void setCreditCardButton(){
        creditCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Handle creditcard payment.
            }
        });
    }

    class ConnectionGetUserPaymentInfo extends AsyncTask<String, String, String> {
        String result ="";
        @Override
        protected String doInBackground(String... strings) {
            try{
                mail = "arnovanneste96@gmail.com";
                String host = "http://10.0.2.2/getuserpaymentinfo.php?mail='"+ mail+"'";
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
            }catch (Exception e) {
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
                    JSONArray paymentinfos = jsonResult.getJSONArray("info");
                    JSONObject paymentinfo = paymentinfos.getJSONObject(0);

                    code = paymentinfo.getString("code");
                    credits = paymentinfo.getDouble("credits");
                    delayedPayment = paymentinfo.getInt("delayedpayment");
                    Toast.makeText(getApplicationContext(),"User credits: " + credits + "\nUser code: " + code,Toast.LENGTH_LONG).show();

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

    class ConnectionBikeTypeInfo extends AsyncTask<String, String, String> {
        String result ="";
        @Override
        protected String doInBackground(String... strings) {
            try{
                type = 1;
                String host = "http://10.0.2.2/getbiketypeinfo.php?type="+ type;
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
            }catch (Exception e) {
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
