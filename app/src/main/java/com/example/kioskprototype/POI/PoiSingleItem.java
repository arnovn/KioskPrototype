package com.example.kioskprototype.POI;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.PoiObject1;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class PoiSingleItem extends AppCompatActivity {

    int id;
    TextView hoofdView;
    TextView addressView;
    TextView distanceView;
    TextView descriptionsView;
    ImageView poiImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_single_item);

        hoofdView = (TextView)findViewById(R.id.hoofdView);
        addressView = (TextView)findViewById(R.id.addressText);
        distanceView = (TextView)findViewById(R.id.distanceText);
        descriptionsView = (TextView)findViewById(R.id.descriptionText);
        poiImageView = (ImageView)findViewById(R.id.poiImageView);

        PoiObject1 object1 = (PoiObject1) getIntent().getSerializableExtra("Object");
        Toast toast = Toast.makeText(getApplicationContext(),
                object1.getName().toString(),
                Toast.LENGTH_SHORT);

        toast.show();
        id = object1.getId();
        hoofdView.setText(object1.getName().toString());
        addressView.setText(object1.getAddress().toString());
        distanceView.setText(String.valueOf(object1.getDistance()));
        descriptionsView.setText(object1.getDescription());

        ConnectionPoi poiImage = new ConnectionPoi();
        poiImage.execute();

    }

    class ConnectionPoi extends AsyncTask<String,Void,Bitmap> {
        String result = "";
        ProgressDialog loading;
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap image = null;
            URL url = null;
            String host = "http://10.0.2.2/getpoiimage.php?id="+ id;
            try {
                url = new URL(host);
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(PoiSingleItem.this,"retrieving...", null, true, true);
        }

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
