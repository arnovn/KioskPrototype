package com.example.kioskprototype.AccountSettings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterAndObjects.PastActivityAdaper;
import com.example.kioskprototype.adapterAndObjects.PastActivityObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Class in charge of gathering the past activities of the current user logged in at the Kiosk
 * In this activity the user can choose to vies:
 *  - all past activities
 *  - activities of the past month
 *  - activities of the past week
 *  - today's activities
 */
public class PastActivities extends AppCompatActivity {

    /**
     * Drop down menu from which user can choose which activities need to gathered
     */
    Spinner spinner;

    /**
     * Saves the result of the dropdown menu when the user selects another option
     */
    String selected;

    /**
     * Id of the user in the MySql database
     */
    int id;

    /**
     * List of gathered activities from the MySql database
     */
    ArrayList<PastActivityObject> activityList;

    /**
     * ListView object to visualize the past activities at the UI layer
     */
    ListView pastActivityList;

    /**
     * Converts PastActivityObjects to a View which the ListView will visualize
     */
    PastActivityAdaper activityAdapter;

    /**
     * Button to return to the AccountSettings activity
     */
    Button backButton;

    /**
     * Data up to which activities need to be gathered.
     */
    String newDate;

    /**
     * When the activity is created:
     *  - All objects are initialized
     *  - All past activities of the user are gathered
     * @param savedInstanceState
     *          Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_activities);

        selected = "All";
        id = getIntent().getIntExtra("Id", 0);
        activityList = new ArrayList<>();
        activityAdapter = new PastActivityAdaper(this, R.layout.adapter_past_activities, activityList);
        pastActivityList = findViewById(R.id.activityList);
        backButton = findViewById(R.id.returnButton);

        initSpinner();
        setSpinnerListener();
        setbackButtonListener();
        new ConnectionGetAllTransactions().execute();
    }

    /**
     * ButtonListener: when the user presses this, we return to the AccountSettings activity
     */
    private void setbackButtonListener(){
        backButton.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        });
    }

    /**
     * We set the spinner layout as custom_spinner layout
     * We set the dropdown layout as custom_spinner_dropdown layout
     * We fill the entries with the entryList save in the Strings file
     */
    private void initSpinner(){
        spinner = findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner,
                getResources().getStringArray(R.array.entrylist)
        );
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinner.setAdapter(adapter);
    }

    /**
     * Sets a Listener for the spinner, when a new item is selected the pastActivityList needs to be updated.
     * When nothing has been selected, nothing needs to happen.
     */
    private void setSpinnerListener(){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = spinner.getItemAtPosition(position).toString();
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
    }

    /**
     * Based on what the user selected we calculate the date from which past activities needs to be gathered.
     * If all is selected we return "All" and use a different method for querying the MySql database.
     * @return
     *          Returns the date from which past activities need to gathered.
     */
    private String getSelectedDate(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("%27yyyy-MM-dd%20HH:mm:ss%27");
        switch (selected) {
            case "All":
                return "All";
            case "Last month": {
                Date date = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DAY_OF_MONTH, -30);
                date = c.getTime();
                return format.format(date);
            }
            case "Last week": {
                Date date = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DAY_OF_MONTH, -7);
                date = c.getTime();
                return format.format(date);
            }
            case "Today": {
                Date date = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DAY_OF_MONTH, -1);
                date = c.getTime();
                return format.format(date);
            }
        }
        return "";
    }

    /**
     * Method to choose how we will query the MySql database.
     */
    private void updateList(){
        newDate = getSelectedDate();
        if(newDate.equals("All")){
            new ConnectionGetAllTransactions().execute();
        }else{
            new ConnectionGetCertainTransactions().execute();
        }
    }

    /**
     * Class in charge of retrieving all past activities of the user currently logged in at the Kiosk.
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionGetAllTransactions extends AsyncTask<String, String, String> {
        String result = "";

        /**
         * Method in charge of querying the database through an HTTP request.
         * @param strings
         *          Parameters passed when the execution of the AsyncTask is called;
         * @return
         *          Returns the response of the database.
         */
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://10.0.2.2/getallpastactivities.php?userid=" + id;
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
                System.out.println("The exception: "+e.getMessage());
                return "The exception: " + e.getMessage();
            }
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database.
         * If there are activities:
         *  - new PastActivityObjects are created and added to the PastActivityList
         *  - The lsitview is updated.
         *  If something goes wrong:
         *  - A Toast pops up saying the request failed
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @SuppressLint("SimpleDateFormat")
        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                activityList.clear();
                if(success == 1){
                    JSONArray activities = jsonResult.getJSONArray("activities");
                    for(int i=0; i<activities.length();i++) {
                        JSONObject activity = activities.getJSONObject(i);

                        int bikeId = activity.getInt("bikeid");
                        String orderdate = activity.getString("orderdate");
                        String startrent = activity.getString("startrent");
                        String endrent = activity.getString("endrent");
                        double amount = activity.getDouble("amount");
                        double amountpayed = activity.getDouble("amountpayed");

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        format.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date orderStamp = format.parse(orderdate);
                        Date startStamp;
                        Date endStamp;
                        if(!startrent.equals("null")){startStamp = format.parse(startrent);}
                        else{startStamp = new SimpleDateFormat("dd/MM/yyyy").parse("31/12/1998");}
                        if(!endrent.equals("null")){endStamp = format.parse(endrent);}
                        else{endStamp = new SimpleDateFormat("dd/MM/yyyy").parse("31/12/1998");}

                        PastActivityObject activityObject = new PastActivityObject(bikeId, orderStamp, startStamp,endStamp, amount, amountpayed);
                        activityList.add(activityObject);
                        pastActivityList.setAdapter(activityAdapter);
                    }


                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Failed: No activities.",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Class in charge of retrieving past activities starting from a certain date from the user currently logged in at the Kiosk.
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionGetCertainTransactions extends AsyncTask<String, String, String> {
        String result = "";

        /**
         * Method in charge of querying the database through an HTTP request.
         * @param strings
         *          Parameters passed when the execution of the AsyncTask is called;
         * @return
         *          Returns the response of the database.
         */
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://"+ getResources().getString(R.string.ip) +"/getpastactivities.php?userid=" + id + "&date=" + newDate;
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
                System.out.println("The exception: "+e.getMessage());
                return "The exception: " + e.getMessage();
            }
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database.
         * If there are activities:
         *  - new PastActivityObjects are created and added to the PastActivityList
         *  - The lsitview is updated.
         *  If something goes wrong:
         *  - A Toast pops up saying the request failed
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @SuppressLint("SimpleDateFormat")
        @Override
        protected void onPostExecute(String s) {
            try{
                System.out.println("Result :" + result);
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    JSONArray activities = jsonResult.getJSONArray("activities");
                    activityList.clear();
                    for(int i=0; i<activities.length();i++) {
                        JSONObject activity = activities.getJSONObject(i);

                        int bikeId = activity.getInt("bikeid");
                        String orderdate = activity.getString("orderdate");
                        String startrent = activity.getString("startrent");
                        String endrent = activity.getString("endrent");
                        double amount = activity.getDouble("amount");
                        double amountpayed = activity.getDouble("amountpayed");

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        format.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date orderStamp = format.parse(orderdate);
                        Date startStamp;
                        Date endStamp;
                        if(!startrent.equals("null")){startStamp = format.parse(startrent);}
                        else{startStamp = new SimpleDateFormat("dd/MM/yyyy").parse("31/12/1998");}
                        if(!endrent.equals("null")){endStamp = format.parse(endrent);}
                        else{endStamp = new SimpleDateFormat("dd/MM/yyyy").parse("31/12/1998");}

                        PastActivityObject activityObject = new PastActivityObject(bikeId, orderStamp, startStamp,endStamp, amount, amountpayed);
                        activityList.add(activityObject);
                        activityAdapter.notifyDataSetChanged();
                    }


                }else if(success == 0){
                    activityList.clear();
                    activityAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(),"Failed: No activities.",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

