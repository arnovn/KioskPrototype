package com.example.kioskprototype;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.Order.OrderConfirmation;
import com.example.kioskprototype.adapterAndObjects.ABikeAdapter;
import com.example.kioskprototype.adapterAndObjects.ABikeObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

/**
 * Activity in charge of representing the different available bikes at the Kiosk
 */
public class BikeSelect extends AppCompatActivity {

    /**
     * ListView which will contain the available bike objects at the Kiosk
     */
    ListView listView;

    /**
     * Custom ArrayAdapter for the ListView
     */
    ABikeAdapter adapter;

    /**
     * List containing the available bike objects
     */
    ArrayList<ABikeObject> bikeObjects;

    /**
     * When the activity is created:
     *  - Retrieve available bikes
     *  - Initialize ListView & Adapater
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_select);

        bikeObjects = new ArrayList<>();
        adapter = new ABikeAdapter(this,R.layout.adapter_bike_select_layout,bikeObjects);
        listView = findViewById(R.id.bikeArray);

        new ConnectionBikeGet().execute();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            ABikeObject bike = adapter.getItem(position);
            Intent intent = new Intent(BikeSelect.this, OrderConfirmation.class);
            intent.putExtra("Bike", bike);
            setResult(1, intent);
            startActivity(intent);
        });
    }

    /**
     * Class in charge of retrieving the avalaible bikes at the Kiosk from the MySql Database
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionBikeGet extends AsyncTask<String,String,String>{
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
            try {
                String host = "http://"+ getResources().getString(R.string.ip) +"/getbikesatkiosk.php?id=" + KioskInfo.get().getId();
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
            }catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database:
         *  - if successful we create Bike objects and add them to the List
         *  - We set this list with as input data for the ListView
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    JSONArray bikes = jsonResult.getJSONArray("bikes");
                    for(int i = 0; i < bikes.length(); i++){
                        JSONObject bike = bikes.getJSONObject(i);
                        int id = bike.getInt("id");
                        int type = bike.getInt("type");
                        double batteryLevel = bike.getDouble("batterylevel");
                        double latitude = bike.getDouble("latitude");
                        double longitude = bike.getDouble("longitude");
                        int code = bike.getInt("code");
                        int bikesStand = bike.getInt("bikestand");
                        ABikeObject bikeObject = new ABikeObject(id, type, batteryLevel, latitude, longitude, code, bikesStand);
                        bikeObjects.add(bikeObject);
                        listView.setAdapter(adapter);
                    }
                }else if(success == 0){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No data",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
