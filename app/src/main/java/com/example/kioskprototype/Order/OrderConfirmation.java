package com.example.kioskprototype.Order;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kioskprototype.KioskInfo;
import com.example.kioskprototype.LoginAndRegister.MemberOrNot;
import com.example.kioskprototype.POI.PoiSingleItem;
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
import java.io.Serializable;
import java.net.URI;
import java.net.URL;

import javax.mail.internet.InternetAddress;

public class OrderConfirmation extends AppCompatActivity {

    TextView bikeNameView;
    TextView bikeAmountView;
    TextView pricePerHourView;
    TextView infoView;
    ABikeObject bikeObject;
    Button confirmButton;

    int bikeType;
    String infoString;
    Double bikePrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        bikeNameView = (TextView)findViewById(R.id.bikeNameView);
        bikeAmountView = (TextView)findViewById(R.id.bikeAmountView);
        pricePerHourView = (TextView)findViewById(R.id.bikePriceView);
        infoView = (TextView)findViewById(R.id.infoView);
        confirmButton = (Button)findViewById(R.id.confirmButton);

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");

        ConnectionBikeInfo connectionBikeInfo = new ConnectionBikeInfo();
        connectionBikeInfo.execute();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(OrderConfirmation.this, MemberOrNot.class);
                intent2.putExtra("Bike",(Serializable) bikeObject);
                startActivity(intent2);
            }
        });
    }

    class ConnectionBikeInfo extends AsyncTask<String, String, String> {
        String result = "";
        @Override
        protected String doInBackground(String... params) {
            try{
                String host = "http://10.0.2.2/getbiketypeinfo.php?type="+ bikeObject.getType();
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
                    JSONArray bikesInfos = jsonResult.getJSONArray("bikeinfo");
                    JSONObject bikeInfo = bikesInfos.getJSONObject(0);

                    bikeType = bikeInfo.getInt("bikeType");
                    infoString = bikeInfo.getString("info");
                    bikePrice = bikeInfo.getDouble("priceperhour");

                    bikeNameView.setText("Bike" + bikeObject.getId());
                    pricePerHourView.setText(bikePrice+" euro/min");
                    infoView.setText(infoString);

                    //For now we can only rent one bike at the kiosk
                    bikeAmountView.setText(1+" bike(s)");

                }else if(success == 0){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No data",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
