package com.example.kioskprototype.AccountSettings;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.PastActivityAdaper;
import com.example.kioskprototype.adapterView.PastActivityObject;

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

public class PastActivities extends AppCompatActivity {

    Spinner spinner;

    String selected;

    int id;

    ArrayList<PastActivityObject> activityList;

    ListView pastActivityList;

    PastActivityAdaper activityAdapter;

    Button backButton;

    String newDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_activities);

        selected = "All";
        id = getIntent().getIntExtra("Id", 0);
        activityList = new ArrayList<>();
        activityAdapter = new PastActivityAdaper(this, R.layout.adapter_past_activities, activityList);
        pastActivityList = (ListView)findViewById(R.id.activityList);
        backButton = (Button)findViewById(R.id.returnButton);

        initSpinner();
        setSpinnerListener();
        setbackButtonListener();
        new ConnectionGetAllTransactions().execute();
    }

    public void setbackButtonListener(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }

    public void initSpinner(){
        spinner = (Spinner)findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner,
                getResources().getStringArray(R.array.entrylist)
        );
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinner.setAdapter(adapter);
    }

    public void setSpinnerListener(){
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

    public String getSelectedDate(){
        SimpleDateFormat format = new SimpleDateFormat("%27yyyy-MM-dd%20HH:mm:ss%27");
        if(selected.equals("All")){
            return "All";
        }else if(selected.equals("Last month")){
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, -30);
            date = c.getTime();
            String formattedDate = format.format(date);
            return formattedDate;
        }else if(selected.equals("Last week")){
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, -7);
            date = c.getTime();
            String formattedDate = format.format(date);
            return formattedDate;
        }else if(selected.equals("Today")){
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, -1);
            date = c.getTime();
            String formattedDate = format.format(date);
            return formattedDate;
        }
        return "";
    }

    public void updateList(){
        newDate = getSelectedDate();
        if(newDate.equals("All")){
            new ConnectionGetAllTransactions().execute();
        }else{
            new ConnectionGetCertainTransactions().execute();
        }
    }

    class ConnectionGetAllTransactions extends AsyncTask<String, String, String> {
        String result = "";

        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://10.0.2.2/getallpastactivities.php?userid=" + id;
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
                System.out.println("The exception: "+e.getMessage());
                return new String("The exception: " + e.getMessage());
            }
            return result;
        }
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

    class ConnectionGetCertainTransactions extends AsyncTask<String, String, String> {
        String result = "";

        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://10.0.2.2/getpastactivities.php?userid=" + id + "&date=" + newDate;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer stringBuffer = new StringBuffer("");

                String line ="";
                while((line = reader.readLine()) != null){
                    stringBuffer.append(line);
                    //break;
                }
                reader.close();
                result = stringBuffer.toString();
            } catch (Exception e) {
                System.out.println("The exception: "+e.getMessage());
                return new String("The exception: " + e.getMessage());
            }
            return result;
        }
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

