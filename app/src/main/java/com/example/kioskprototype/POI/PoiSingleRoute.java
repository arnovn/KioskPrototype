package com.example.kioskprototype.POI;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.KioskInfo;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.PoiObject1;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
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
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class in charge of representing the route of a POI from the Kiosk to the user on an interactive map
 */
public class PoiSingleRoute extends AppCompatActivity implements OnMapReadyCallback, Callback<DirectionsResponse> {

    /**
     * Interactive map at UI layer
     */
    private MapView mapView;

    /**
     * Interactie map object, for interactin with the UI layer map
     */
    private MapboxMap mapBoxMap;

    /**
     * POI marker
     */
    private Marker destinationMarker;

    /**
     * Kiosk object for gathering the necessary information (lat, long, id)
     */
    KioskInfo kioskInfo;

    /**
     * The selected point of interest
     */
    PoiObject1 poiObject1;

    /**
     * TextView visualizing the distance of this route at the UI layer
     */
    TextView distanceTextView;

    /**
     * Textview visualizing the name of the POI + "Route"
     */
    TextView titleView;

    /**
     * Navigation object, will calculate the best route from Kiosk to POI by bike
     */
    private NavigationMapRoute navigationMapRoute;

    /**
     * When the activity is created:
     *  - The interactive map is initialized
     *  - The selected point of interest is retrieved from previous activity
     *  - Kiosks' information is retrieved
     *  - TextViews are initialized
     *  - Route from startposition (Kiosk) to endpoistion (POI) is calculated
     *  - The route is visualized on the UI layer
     *
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this,getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_poi_single_route);

        poiObject1 = (PoiObject1) getIntent().getSerializableExtra("POI");

        kioskInfo = KioskInfo.get();
        distanceTextView = findViewById(R.id.distanceViewRoute);
        titleView = findViewById(R.id.routeNameView);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> {
            mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments.

            });

            mapBoxMap = mapboxMap;
            setCameraPosition();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(poiObject1.getLatitude(), poiObject1.getLongitude()));
            markerOptions.setTitle(poiObject1.getName());
            markerOptions.setSnippet(poiObject1.getStringType());
            destinationMarker = mapBoxMap.addMarker(markerOptions);

            Point startPos = Point.fromLngLat(kioskInfo.getLongitude(), kioskInfo.getLatitude());
            Point endPos = Point.fromLngLat(destinationMarker.getPosition().getLongitude(), destinationMarker.getPosition().getLatitude());
            getRoute(startPos, endPos);

        });
        titleView.setText(poiObject1.getName() + " ROUTE");
    }

    /**
     * We set the Kiosks' position as the center point of the map when the map is generated.
     */
    private void setCameraPosition(){
        mapBoxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(kioskInfo.getLatitude(), kioskInfo.getLongitude()), 13.0));
    }

    /**
     * Mathod in charge of calculating the optimal route from origin to destination by bike.
     * @param origin
     *              Starting point
     * @param destination
     *              Destination point
     */
    private void getRoute(Point origin, Point destination){
        NavigationRoute.builder(this)
                .accessToken(getString(R.string.mapbox_access_token))
                .origin(origin)
                .profile(DirectionsCriteria.PROFILE_CYCLING)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NotNull Call<DirectionsResponse> call, @NotNull Response<DirectionsResponse> response) {
                        if(response.body() == null){
                            System.out.println("No routes found, check right user and accestoken");
                            return;
                        }else if(response.body().routes().size() == 0){
                            System.out.println("No routes found");
                            return;
                        }

                        //Now we know we have at least one route, we get the top recommended of the list (index = 0)
                        DirectionsRoute currentRoute = response.body().routes().get(0);

                        if(navigationMapRoute != null){
                            navigationMapRoute.removeRoute();
                        }else{
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapBoxMap);
                        }
                        navigationMapRoute.addRoute(currentRoute);

                        double distance = currentRoute.distance()/1000;
                        DecimalFormat df2 = new DecimalFormat("#.##");
                        distanceTextView.setText("Route distance: " + df2.format(distance) + "km");

                    }

                    @Override
                    public void onFailure(@NotNull Call<DirectionsResponse> call, @NotNull Throwable t) {
                        System.out.println("Error: " + t.getMessage());
                    }
                });
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
    public void onResponse(@NotNull Call<DirectionsResponse> call, @NotNull Response<DirectionsResponse> response) {

    }

    @Override
    public void onFailure(@NotNull Call<DirectionsResponse> call, @NotNull Throwable t) {

    }
}

