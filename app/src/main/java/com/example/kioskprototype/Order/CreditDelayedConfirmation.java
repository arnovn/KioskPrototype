package com.example.kioskprototype.Order;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kioskprototype.FinalScreen;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;

public class CreditDelayedConfirmation extends AppCompatActivity {

    private int type;
    private String mail;
    private ABikeObject bikeObject;
    private int id;

    TextView bikeNameView;
    TextView bikeAmountView;
    TextView infoViewType;
    TextView infoViewExtra;

    boolean taskReady1;
    boolean taskReady2;
    boolean taskReady3;

    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_delayed_confirmation);

        initTaskBooleans();

        type = (int)getIntent().getIntExtra("Type",0);
        mail = (String)getIntent().getStringExtra("Mail");
        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");

        setBikeTextviews();
        setInfoTextviews();
        setconfirm();

    }

    public void initTaskBooleans(){
        taskReady1 = false;
        taskReady2 = false;
        taskReady3 = false;
    }

    public void setTasks(boolean task1, boolean task2, boolean task3){
        taskReady1 = task1;
        taskReady2 = task2;
        taskReady3 = task3;

        if(taskReady1 && taskReady2 && taskReady3){
            toFinalScreen();
        }
    }

    public void toFinalScreen(){
        Intent intent = new Intent(CreditDelayedConfirmation.this, FinalScreen.class);
        intent.putExtra("Bike", (Serializable)bikeObject);
        startActivity(intent);
    }

    public void setconfirm(){
        confirm = (Button)findViewById(R.id.confirmButtonCfn);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConnectionGetId().execute();
            }
        });
    }

    public void finishOrder(){
        new ConnectionSetInputOrder().execute();
        new ConnectionSetUser().execute();
        new ConnectionSetBike().execute();
    }

    public void setBikeTextviews(){
        bikeNameView = (TextView)findViewById(R.id.bikeNameViewConf);
        bikeAmountView = (TextView)findViewById(R.id.bikeAmountViewConf);

        bikeNameView.setText("Bike"+bikeObject.getId()+bikeObject.getType());
        bikeAmountView.setText(1+"");

    }

    public void setInfoTextviews(){
        infoViewType = (TextView)findViewById(R.id.confirmSideText);
        infoViewExtra = (TextView)findViewById(R.id.confirmSideText2);

        switch (type){
            case 1:
                infoViewType.setText("You've selected: Credit payment");

                break;
            case 2:
                infoViewType.setText("You've selected: Delayed payement");
                break;
        }

        String infoString="You can use the our services until your credits are 0.";
        if(bikeObject.getType() == 1){
            //Electric bike
            infoString = infoString + "\nWhen this happens and you keep on driving: the battery of the bike will switch off and an alarm will sound.";
        }else if(bikeObject.getType() == 2){
            //Pedal bike
            infoString = infoString + "\nWhen this happens and you keep on driving: an alarm will sound.";
        }
        infoViewExtra.setText(infoString);
    }

    class ConnectionGetId extends AsyncTask<String, String, String> {
        String result = "";
        @Override
        protected String doInBackground(String... params) {
            try{
                String host = "http://10.0.2.2/getuserid.php?mail='"+ mail+"'";
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
                return new String("The exception: " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    JSONArray userids = jsonResult.getJSONArray("id");
                    JSONObject userid = userids.getJSONObject(0);

                    id = userid.getInt("id");
                    finishOrder();

                }else if(success == 0){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No data",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class ConnectionSetInputOrder extends AsyncTask<String, String, String> {
        String result = "";
        @Override
        protected String doInBackground(String... params) {
            try{
                String host = "http://10.0.2.2/inputneworder.php?userid="+ id + "&bikeid=" + bikeObject.getId();
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
                return new String("The exception: " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                if(result.equals("succes")){
                    Toast.makeText(getApplicationContext(),"Insertion of ORDER successul", Toast.LENGTH_SHORT).show();
                    setTasks(true,taskReady2,taskReady3);
                }else if(result.equals("error")){
                    Toast.makeText(getApplicationContext(),"Insertion of ORDER failed..", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class ConnectionSetUser extends AsyncTask<String, String, String> {
        String result = "";
        @Override
        protected String doInBackground(String... params) {
            try{
                String host = "http://10.0.2.2/updateuser.php?userid="+ id + "&bikeid=" + bikeObject.getId();
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
                return new String("The exception: " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                if(result.equals("succes")){
                    Toast.makeText(getApplicationContext(),"Update of USER successul", Toast.LENGTH_SHORT).show();
                    setTasks(taskReady1,true,taskReady3);
                }else if(result.equals("error")){
                    Toast.makeText(getApplicationContext(),"Update of USER failed..", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class ConnectionSetBike extends AsyncTask<String, String, String> {
        String result = "";
        @Override
        protected String doInBackground(String... params) {
            try{
                String host = "http://10.0.2.2/updatebike.php?userid="+ id + "&bikeid=" + bikeObject.getId();
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
                return new String("The exception: " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                if(result.equals("succes")){
                    Toast.makeText(getApplicationContext(),"Update of BIKE successful", Toast.LENGTH_SHORT).show();
                    setTasks(taskReady1,taskReady2,true);
                }else if(result.equals("error")){
                    Toast.makeText(getApplicationContext(),"Update of BIKE failed..", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
