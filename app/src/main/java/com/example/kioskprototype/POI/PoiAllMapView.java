package com.example.kioskprototype.POI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.KioskInfo;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.PoiObject1;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PoiAllMapView extends AppCompatActivity implements OnMapReadyCallback, Callback<DirectionsResponse> {


    private MapView mapView;
    private MapboxMap mapBoxMap;
    private Marker destinationMarker;
    Point markerLocation;
    KioskInfo kioskInfo;

    Button goToPoiButton;
    List<PoiObject1> poiMapObjects;
    List<MarkerOptions> poiMarkers;
    PoiObject1 selectedPoiObject;

    private NavigationMapRoute navigationMapRoute;
    private static final String TAG = "PoiAllMapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this,getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_poi_all_map_view);

        kioskInfo = KioskInfo.get();
        poiMapObjects = new ArrayList<>();
        poiMarkers = new ArrayList<>();
        selectedPoiObject = null;

        new ConnectionGetAllPoi().execute();

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        initializeButton();

    }

    private void initializeButton(){
        goToPoiButton = findViewById(R.id.goToPoiButton);

        goToPoiButton.setOnClickListener(v->{
            //Checking if there is a POI selected
            if(selectedPoiObject == null){
                Toast.makeText(getApplicationContext(), "No POI selected.", Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(PoiAllMapView.this, PoiSingleItem.class);
            intent.putExtra("Object", selectedPoiObject);
            setResult(1,intent);
            startActivity(intent);

            //TODO: initialize this view in PoiSingleItem
            //startActivity(new Intent(PoiAllMapView.this, PoiSingleRoute.class));
        });
    }

    private void initializeMarkerListener(){
        mapBoxMap.setOnMarkerClickListener(marker ->
        {
            String name = marker.getTitle();
            for(PoiObject1 poiMapObject: poiMapObjects){
                if(poiMapObject.getName().equals(name)){
                    selectedPoiObject = poiMapObject;
                    System.out.println("Selected: " + selectedPoiObject.getName());
                }
            }
            return false;
        });
    }

    private void generateMarkers(){
        for(PoiObject1 poiObject: poiMapObjects){
            MarkerOptions newMarker = new MarkerOptions();
            newMarker.position(new LatLng(poiObject.getLatitude(), poiObject.getLongitude()));
            newMarker.setTitle(poiObject.getName());
            newMarker.setSnippet(poiObject.getStringType());
            poiMarkers.add(newMarker);
        }
        initializeMap();
    }

    private void initializeMap(){
        mapView.getMapAsync(mapboxMap -> {
            mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments.

            });

            mapBoxMap = mapboxMap;
            setCameraPosition();

            for(MarkerOptions marker : poiMarkers){
               //Add the markers
                mapBoxMap.addMarker(marker);
            }
            initializeMarkerListener();
        });
    }

    private void setCameraPosition(){
        mapBoxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(kioskInfo.getLatitude(), kioskInfo.getLongitude()), 13.0));
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

    }

    @Override
    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

    }

    @Override
    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

    }

    /**
     * Class in charge of retrieving the POIs
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionGetAllPoi extends AsyncTask<String,String,String> {
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
                String host = "http://"+ getResources().getString(R.string.ip) +"/getallpoiofkiosk.php?id="+ KioskInfo.get().getId();
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
                    JSONArray pois = jsonResult.getJSONArray("restos");
                    for(int i=0; i<pois.length();i++){
                        JSONObject poi = pois.getJSONObject(i);
                        int id = poi.getInt("id");
                        String name = poi.getString("name");
                        String address= poi.getString("address");
                        float distance = ((float) poi.getDouble("distance"));
                        String desc = poi.getString("description");
                        double lat = poi.getDouble("latitude");
                        double lon = poi.getDouble("longitude");
                        int type = poi.getInt("type");

                        PoiObject1 newPoi = new PoiObject1(id, name,address,distance, desc, type, lat, lon);
                        poiMapObjects.add(newPoi);
                    }

                    generateMarkers();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

