package com.example.kioskprototype.POI;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.PoiObject1;

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

        hoofdView = findViewById(R.id.hoofdView);
        addressView = findViewById(R.id.addressText);
        distanceView = findViewById(R.id.distanceText);
        descriptionsView = findViewById(R.id.descriptionText);
        poiImageView = findViewById(R.id.poiImageView);

        PoiObject1 object1 = (PoiObject1) getIntent().getSerializableExtra("Object");
        assert object1 != null;
        Toast toast = Toast.makeText(getApplicationContext(),
                object1.getName(),
                Toast.LENGTH_SHORT);

        toast.show();
        id = object1.getId();
        hoofdView.setText(object1.getName());
        addressView.setText(object1.getAddress());
        distanceView.setText(String.valueOf(object1.getDistance()));
        descriptionsView.setText(object1.getDescription());

        ConnectionPoi poiImage = new ConnectionPoi();
        poiImage.execute();

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
            String host = "http://10.0.2.2/getpoiimage.php?id="+ id;
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
