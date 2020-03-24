package com.example.kioskprototype.AccountSettings;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.kioskprototype.MailSender.GmailSender;
import com.example.kioskprototype.R;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class RequestMemberCard extends AppCompatActivity implements ConfirmAddressDialog.ConfirmAddressDialogListener{

    static final String TAG = "searchTag";
    ConfirmAddressDialog confirmAddressDialog;
    String address;
    int id;
    String mail;
    final int RESULT_FAILED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_member_card);

        id = getIntent().getIntExtra("Id",0);
        mail = getIntent().getStringExtra("Mail");
        Toast.makeText(getApplicationContext(), "Id:" + id, Toast.LENGTH_LONG).show();

        PlaceAutocompleteFragment autocompleteFragment;

        if (savedInstanceState == null) {
            autocompleteFragment = PlaceAutocompleteFragment.newInstance("pk.eyJ1IjoiYXJub3Zhbm5lc3RlIiwiYSI6ImNrODRsaWl0YTFqYWYzbHJ1ZnBzbWw5NmgifQ.Y8hF7FV-cQeS1I8ZEh4eNA");

            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, autocompleteFragment,TAG);
            transaction.commit();

        } else {
            autocompleteFragment = (PlaceAutocompleteFragment)
                    getSupportFragmentManager().findFragmentByTag(TAG);
        }

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(CarmenFeature carmenFeature) {
                Toast.makeText(RequestMemberCard.this,
                        carmenFeature.placeName(), Toast.LENGTH_LONG).show();

                confirmAddressDialog = new ConfirmAddressDialog(carmenFeature.placeName());
                confirmAddressDialog.show(getSupportFragmentManager(),"Confirm address");
            }

            @Override
            public void onCancel() {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public void confirmPressed() {
        address = confirmAddressDialog.getAddress();
        new ConnectionInsertAddress().execute();
    }

    @Override
    public void cancelPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    class ConnectionInsertAddress extends AsyncTask<String, String, String> {
        String result = "";
        @Override
        protected String doInBackground(String... strings) {
            String convertedAddress = address.replaceAll("\\s", "%20");
            System.out.println("Adress: " + convertedAddress);
            try{
                String host = "http://10.0.2.2/insertaddress.php?userid=" + id + "&address=" + "'" + convertedAddress + "'";
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
            }catch (Exception e) {
                System.out.println("The exception: "+e.getMessage());
                return new String("The exception: " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println(result);
            try{
                if(result.equals("succes")){
                    sendVerificationMail();
                    setResult(RESULT_OK);
                    finish();

                }else{

                    setResult(RESULT_FAILED);
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void sendVerificationMail(){
        Runnable mailRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    GmailSender sender = new GmailSender("wowkioskmail@gmail.com",
                            "kioskmail123");
                    sender.sendMail("WOW kiosk Verification mail",
                            "You've succesfully requested a new membercard.\n \n"
                                    + "This card will be delivered to: " + address +". ",
                            "wowkioskmail@gmail.com", mail);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                    System.out.println("The exception :" + e);
                }
            }
        };
        Thread mailThread = new Thread(mailRunnable);
        mailThread.start();
    }
}
