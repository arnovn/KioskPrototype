package com.example.kioskprototype.AccountSettings;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
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

/**
 * Class in charge of handling member card requests.
 *  - Address needs to be inputted by the user
 *  - Confirmation popup
 *  - Address needs to be updated in the database.
 */
public class RequestMemberCard extends AppCompatActivity implements ConfirmAddressDialog.ConfirmAddressDialogListener{

    /**
     * TAG so the MapBox fragment knows where to be shown.
     */
    static final String TAG = "searchTag";

    /**
     * Confirmation dialog for the address inputted.
     */
    ConfirmAddressDialog confirmAddressDialog;

    /**
     * Address of the user is stored in this String.
     */
    String address;

    /**
     * Id of the user inside the MySql Database.
     */
    int id;

    /**
     * Mail of the user stored inside the MySql Database.
     */
    String mail;

    /**
     * When something goes wrong during the insertion of the address inside the database we return
     * to the previous activity (AccountSettings) passing this code.
     */
    final int RESULT_FAILED = 2;

    /**
     * When the activity is created:
     *  - Gather the user's settings.
     *  - Initialize the autocomplete fragment (where the user will search his address).
     * @param savedInstanceState
     *          Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_member_card);

        id = getIntent().getIntExtra("Id",0);
        mail = getIntent().getStringExtra("Mail");

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

        assert autocompleteFragment != null;
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

    /**
     * Implementation for ConfirmAddressDialog confirmPressed.
     *  - We get the address passed to the dialog
     *  - We update the user's address in the MySql Database.
     */
    @Override
    public void confirmPressed() {
        address = confirmAddressDialog.getAddress();
        new ConnectionInsertAddress().execute();
    }

    /**
     * Implementation for ConfirmAddressDialog cancelPressed.
     *  - We return to previous activity passing the RESULT_CANCELED code.
     */
    @Override
    public void cancelPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * Class in charge of inserting the address into the MySql Database for the given user id.
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionInsertAddress extends AsyncTask<String, String, String> {
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
            String convertedAddress = address.replaceAll("\\s", "%20");
            System.out.println("Adress: " + convertedAddress);
            try{
                String host = "http://"+ getResources().getString(R.string.ip) +"/insertaddress.php?userid=" + id + "&address=" + "'" + convertedAddress + "'";
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
            }catch (Exception e) {
                System.out.println("The exception: "+e.getMessage());
                return "The exception: " + e.getMessage();
            }
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database.
         * If insertion was succesfull we send a verification mail & return to previous activity passing the RESULT_OK code.
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
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

    /**
     * Sends the verification mail to mail couple to the user id.
     */
    public void sendVerificationMail(){
        Runnable mailRunnable = () -> {
            try {
                GmailSender sender = new GmailSender("wowkioskmail@gmail.com",
                        "kioskmail123");
                sender.sendMail("WOW kiosk Verification mail",
                        "You've succesfully requested a new membercard.\n \n"
                                + "This card will be delivered to: " + address +". ",
                        "wowkioskmail@gmail.com", mail);
            } catch (Exception e) {
                System.out.println("The exception :" + e);
            }
        };
        Thread mailThread = new Thread(mailRunnable);
        mailThread.start();
    }
}
