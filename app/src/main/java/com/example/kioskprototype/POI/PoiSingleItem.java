package com.example.kioskprototype.POI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterAndObjects.PoiObject1;

import java.net.URL;

/**
 * Class in charge of visualizing the selected Point Of Interest
 */
public class PoiSingleItem extends AppCompatActivity {

    /**
     * POI id
     */
    int id;

    /**
     * Title TextView at the UI layer
     */
    TextView hoofdView;

    /**
     * Address of the POI TextView at the UI layer
     */
    TextView addressView;

    /**
     * Distance of the POI from the Kiosk TextView at the UI layer
     */
    TextView distanceView;

    /**
     * Description of the POI TextView at the UI layer
     */
    TextView descriptionsView;

    /**
     * Header image of the POI at the UI layer
     */
    ImageView poiImageView;

    /**
     * Button guiding the user to the POI route activity from the kiosk
     */
    Button poiRouteButton;

    /**
     * Selected POI from last activity
     */
    PoiObject1 object1;


    /**
     * When the activity is created:
     *  - Initialize TextViews
     *  - Retrieve the remaining selected POI information from the MySql Database
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_single_item);

        initViews();
        getPreviousData();

        assert object1 != null;
        Toast toast = Toast.makeText(getApplicationContext(),
                object1.getName(),
                Toast.LENGTH_SHORT);

        toast.show();
        id = object1.getId();

        setViews();

        ConnectionPoi poiImage = new ConnectionPoi();
        poiImage.execute();
        buttonListener();

    }

    /**
     * In charge of connection TextView & Button of UI layer to objects
     */
    private void initViews(){
        hoofdView = findViewById(R.id.hoofdView);
        addressView = findViewById(R.id.addressText);
        distanceView = findViewById(R.id.distanceText);
        descriptionsView = findViewById(R.id.descriptionText);
        poiImageView = findViewById(R.id.poiImageView);
        poiRouteButton = findViewById(R.id.poiRouteButton);
    }

    /**
     * Set the poiRouteButton listener, leading to PoiSingleRoute activity visualizing the route from the kiosk to the POI
     */
    private void buttonListener(){
        poiRouteButton.setOnClickListener(v->{
            Intent routeIntent = new Intent(PoiSingleItem.this, PoiSingleRoute.class);
            routeIntent.putExtra("POI", object1);
            startActivity(routeIntent);
        });
    }

    /**
     * Get data from past activity
     */
    private void getPreviousData(){
        object1 = (PoiObject1) getIntent().getSerializableExtra("Object");
    }

    /**
     * Set the textviews of the UI layers with the necessary data
     */
    private void setViews(){
        hoofdView.setText(object1.getName());
        addressView.setText(object1.getAddress());
        distanceView.setText(String.valueOf(object1.getDistance()));
        descriptionsView.setText(object1.getDescription());
    }

    /**
     * Class in charge of retrieving the header image of the POI
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionPoi extends AsyncTask<String,Void,Bitmap> {
        ProgressDialog loading;

        /**
         * Method in charge of querying the database through an HTTP request.
         * @param params
         *          Paramaters passed when the execution of the AsyncTask is called;
         * @return
         *          Returns the response of the database.
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap image = null;
            URL url;
            String host = "http://"+ getResources().getString(R.string.ip) +"/getpoiimage.php?id="+ id;
            try {
                url = new URL(host);
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return image;
        }

        /**
         * Before execution we start loading instance.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(PoiSingleItem.this,"retrieving...", null, true, true);
        }

        /**
         * Method in charge of handling the result gathered from the database:
         *  - if successful we set the ImageView at the UI layer to the retrieved bitmap (image).
         * @param bitmap
         *          bitmap passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            loading.dismiss();
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Image loading should be succesfull",
                    Toast.LENGTH_SHORT);
            poiImageView.setImageBitmap(bitmap);
            toast.show();
        }
    }
}
