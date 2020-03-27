package com.example.kioskprototype.Order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.LoginAndRegister.MemberOrNot;
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
 * Activity which visualizes the order the user has made when the user ahs selected a bike at the Kiosk
 * If he confirms the we go on with the sequence by going to the MemberOrNot activity
 */
public class OrderConfirmation extends AppCompatActivity {

    /**
     * TextView visualizing the bike name on the UI layer
     */
    TextView bikeNameView;

    /**
     * TextView visualizing the amount of bikes ordered on the UI layer
     *  - For this version the order amount per user is limited to 1
     */
    TextView bikeAmountView;

    /**
     * TextView visualizing price per hour of the bike on the UI layer
     */
    TextView pricePerHourView;

    /**
     * TextView visualizing extra bike info on the UI layer
     */
    TextView infoView;

    /**
     * The bike selected by the user
     */
    ABikeObject bikeObject;

    /**
     * Confirmation button
     */
    Button confirmButton;

    /**
     * Type of the bike
     */
    int bikeType;

    /**
     * Extra info of the bike type
     */
    String infoString;

    /**
     * Price of the bike
     */
    Double bikePrice;


    /**
     * When the activity is created:
     *  - Bike data is retrieved from the MySql Database
     *  - TextViews and Button are set
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        bikeNameView = findViewById(R.id.bikeNameView);
        bikeAmountView = findViewById(R.id.bikeAmountView);
        pricePerHourView = findViewById(R.id.bikePriceView);
        infoView = findViewById(R.id.infoView);
        confirmButton = findViewById(R.id.confirmButton);

        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");

        ConnectionBikeInfo connectionBikeInfo = new ConnectionBikeInfo();
        connectionBikeInfo.execute();

        confirmButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(OrderConfirmation.this, MemberOrNot.class);
            intent2.putExtra("Bike", bikeObject);
            startActivity(intent2);
        });
    }

    /**
     * Class in charge of retrieving the bike info
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionBikeInfo extends AsyncTask<String, String, String> {
        String result = "";

        /**
         * Method in charge of querying the database through an HTTP request.
         * @param params
         *          Paramaters passed when the execution of the AsyncTask is called;
         * @return
         *          Returns the response of the database.
         */
        @Override
        protected String doInBackground(String... params) {
            try{
                String host = "http://"+ getResources().getString(R.string.ip) +"/getbiketypeinfo.php?type="+ bikeObject.getType();
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
                return "The exception: " + e.getMessage();
            }
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database:
         *  - If successfull we set all the attributes and TextViews
         *  - Else: error is Toasted
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
