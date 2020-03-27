package com.example.kioskprototype.POI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.KioskInfo;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.PoiAdapter1;
import com.example.kioskprototype.adapterView.PoiObject1;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;

/**
 * Class in charge of showing all Points Of Interest of a certain type:
 *  - Type 1: Restaurants
 *  - Type 2: Worth to visit
 *  - Type 3: Tours
 *  - Type 4: Activities
 */
public class PoiArrayView extends AppCompatActivity {

    /**
     * ListView visualizing the different POI objects on thz UI layer
     */
    ListView listView;

    /**
     * Adapter for the ListView
     */
    PoiAdapter1 adapter;

    /**
     * Title of the UI layer
     */
    TextView hoofdView;

    /**
     * List of the different POI objects
     */
    ArrayList<PoiObject1> poiObjects;

    /**
     * POI type the user selected
     */
    int type;

    /**
     * When the activity is created:
     *  - Retrieve the POI's of the selected type from the MySql Database assigned to the Kiosks' id
     *  - Initialize the ListView and TextViews
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_array_view);
        poiObjects = new ArrayList<>();

        listView = findViewById(R.id.listviewPoiArray);
        adapter = new PoiAdapter1(this,R.layout.adapter_view_poi,poiObjects);
        hoofdView = findViewById(R.id.hoofdView);

        type = getIntent().getIntExtra("Type",5);
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

        listView.setOnItemClickListener((parent, view, position, id) -> {
            PoiObject1 object = adapter.getItem(position);
            Intent intent = new Intent(PoiArrayView.this, PoiSingleItem.class);
            intent.putExtra("Object", object);
            intent.putExtra("Type",type);
            setResult(1,intent);
            startActivity(intent);
        });
    }

    /**
     * Class in charge of retrieving the POIs
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionPoiType1 extends AsyncTask<String,String,String>{
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
            try {
                String host = "http://"+ getResources().getString(R.string.ip) +"/getpoiarray2.php?id="+ KioskInfo.get().getId()+"&type=" + type;
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
         *  - if successful we create the POI objects and add them to the List
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
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
