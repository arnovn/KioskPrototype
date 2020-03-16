package com.example.kioskprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kioskprototype.Order.OrderConfirmation;
import com.example.kioskprototype.adapterView.ABikeAdapter;
import com.example.kioskprototype.adapterView.ABikeObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class BikeSelect extends AppCompatActivity {

    ListView listView;
    ABikeAdapter adapter;
    ArrayList<ABikeObject> bikeObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_select);

        bikeObjects = new ArrayList<>();
        adapter = new ABikeAdapter(this,R.layout.adapter_bike_select_layout,bikeObjects);
        listView = (ListView)findViewById(R.id.bikeArray);

        new ConnectionBikeGet().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ABikeObject bike = adapter.getItem(position);
                Intent intent = new Intent(BikeSelect.this, OrderConfirmation.class);
                intent.putExtra("Bike", (Serializable) bike);
                setResult(1, intent);
                startActivity(intent);
            }
        });
    }

    class ConnectionBikeGet extends AsyncTask<String,String,String>{
        String result = "";

        @Override
        protected String doInBackground(String... strings) {
            try {
                String host = "http://10.0.2.2/getbikesatkiosk.php?id=" + KioskInfo.get().getId();
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer stringBuffer = new StringBuffer();

                String line = "";
                while((line = reader.readLine()) != null){
                    stringBuffer.append(line);
                    break;
                }
                reader.close();
                result = stringBuffer.toString();
            }catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }

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
