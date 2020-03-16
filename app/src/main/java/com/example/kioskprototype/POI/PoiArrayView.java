package com.example.kioskprototype.POI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.math.BigDecimal;

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

import com.example.kioskprototype.KioskInfo;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.PoiAdapter1;
import com.example.kioskprototype.adapterView.PoiObject1;

import javax.mail.internet.InternetAddress;

public class PoiArrayView extends AppCompatActivity {

    ListView listView;
    PoiAdapter1 adapter;
    TextView hoofdView;
    ArrayList<PoiObject1> poiObjects;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_array_view);
        poiObjects = new ArrayList<>();

        listView = (ListView)findViewById(R.id.listviewPoiArray);
        adapter = new PoiAdapter1(this,R.layout.adapter_view_poi,poiObjects);
        hoofdView = (TextView)findViewById(R.id.hoofdView);

        type = (int)getIntent().getIntExtra("Type",5);
        new ConnectionPoiType1().execute();

        switch (type){
            case 1:
                hoofdView.setText("RESTAURANTS");
                break;
            case 2:
                hoofdView.setText("WORTH TO VISIT");
                break;
            case 3:
                hoofdView.setText("ROUTES");
                break;
            case 4:
                hoofdView.setText("ACTIVITIES");
                break;
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiObject1 object = adapter.getItem(position);
                Intent intent = new Intent(PoiArrayView.this, PoiSingleItem.class);
                intent.putExtra("Object", (Serializable) object);
                intent.putExtra("Type",type);
                setResult(1,intent);
                startActivity(intent);
            }
        });
    }

    class ConnectionPoiType1 extends AsyncTask<String,String,String>{
        String result = "";

        @Override
        protected String doInBackground(String... params) {
            try {
                String host = "http://10.0.2.2/getpoiarray2.php?id="+ KioskInfo.get().getId()+"&type=" + type;
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
            try {
                JSONObject jsonResult = new JSONObject(result);
                int succes = jsonResult.getInt("success");
                if(succes ==1){
                    JSONArray restos = jsonResult.getJSONArray("restos");
                    for(int i=0; i<restos.length();i++){
                        JSONObject resto = restos.getJSONObject(i);
                        int id = resto.getInt("id");
                        String name = resto.getString("name");
                        String address = resto.getString("address");
                        float distance = BigDecimal.valueOf(resto.getDouble("distance")).floatValue();
                        String desc = resto.getString("description");
                       // BigDecimal latitude = BigDecimal.valueOf(resto.getDouble("latitude"));
                       // BigDecimal longitude = BigDecimal.valueOf(resto.getDouble("longitude"));
                        PoiObject1 object1 = new PoiObject1(id,name, address,distance, desc, type);
                        poiObjects.add(object1);
                        listView.setAdapter(adapter);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
