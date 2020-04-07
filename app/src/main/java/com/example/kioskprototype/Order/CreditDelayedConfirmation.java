package com.example.kioskprototype.Order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.FinalScreen;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Class in charge of finishing an order when the payment is set: DELAYED
 */
public class CreditDelayedConfirmation extends AppCompatActivity {

    /**
     * Type of the bike
     */
    private int type;

    /**
     * E-mail address of the user
     */
    private String mail;

    /**
     * Selected bike which the user will rent
     */
    private ABikeObject bikeObject;

    /**
     * Id of the user
     */
    private int id;

    /**
     * TextView which visualizes the bike name on the UI layer
     */
    TextView bikeNameView;


    /**
     * TextView which visualizes the bike amount on the UI layer
     *  - For this version only one bike per user can be rented
     */
    TextView bikeAmountView;

    /**
     * TextView which visualizes the payment info on the UI layer
     */
    TextView infoViewType;

    /**
     * TextView which visualizes extra info on the UI layer
     */
    TextView infoViewExtra;

    /**
     * Async task 1 checker
     */
    boolean taskReady1;

    /**
     * Async task 2 checker
     */
    boolean taskReady2;

    /**
     * Async task 3 checker
     */
    boolean taskReady3;

    /**
     * Confirmation button
     */
    Button confirm;

    /**
     * When the activity is created:
     *  - payment type is retrieved
     *  - user mail is retrieved
     *  - selected bike from previous activity is retrieved
     *  - initialize TextViews and Button
     * @param savedInstanceState
     *          Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_delayed_confirmation);

        initTaskBooleans();

        type = getIntent().getIntExtra("Type",0);
        mail = getIntent().getStringExtra("Mail");
        bikeObject = (ABikeObject)getIntent().getSerializableExtra("Bike");

        setBikeTextviews();
        setInfoTextviews();
        setconfirm();

    }

    /**
     * Initialize if asynctasks are ready
     */
    public void initTaskBooleans(){
        taskReady1 = false;
        taskReady2 = false;
        taskReady3 = false;
    }

    /**
     * When one of the asyntasks is ready we set them to true
     * When all asynctasks are finished, the order is completed and we can return to the final window!!
     * @param task1
     *              Input new Order into the BikeOrder table of the MySql Database
     * @param task2
     *              Update the bike in the Bike table of the MySql Database
     * @param task3
     *              Update the user in the User table of the MySql Database
     */
    public void setTasks(boolean task1, boolean task2, boolean task3){
        taskReady1 = task1;
        taskReady2 = task2;
        taskReady3 = task3;

        if(taskReady1 && taskReady2 && taskReady3){
            toFinalScreen();
        }
    }

    /**
     * When the order is finished we move to the final activity
     */
    public void toFinalScreen(){
        Intent intent = new Intent(CreditDelayedConfirmation.this, FinalScreen.class);
        intent.putExtra("Bike", bikeObject);
        startActivity(intent);
    }

    /**
     * Confirm button setter
     */
    public void setconfirm(){
        confirm = findViewById(R.id.confirmButtonCfn);
        confirm.setOnClickListener(v -> new ConnectionGetId().execute());
    }

    /**
     * The three async tasks which will update three parts of the database (see setTasks())
     */
    public void finishOrder(){
        new ConnectionSetInputOrder().execute();
        new ConnectionSetUser().execute();
        new ConnectionSetBike().execute();
    }

    /**
     * TextView setters
     */
    @SuppressLint("SetTextI18n")
    public void setBikeTextviews(){
        bikeNameView = findViewById(R.id.bikeNameViewConf);
        bikeAmountView = findViewById(R.id.bikeAmountViewConf);

        bikeNameView.setText("Bike"+bikeObject.getId()+bikeObject.getType());
        bikeAmountView.setText(1+"");

    }

    /**
     * TextView setters
     */
    @SuppressLint("SetTextI18n")
    public void setInfoTextviews(){
        infoViewType = findViewById(R.id.confirmSideText);
        infoViewExtra = findViewById(R.id.confirmSideText2);

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

    /**
     * Class in charge of retrieving the User id
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionGetId extends AsyncTask<String, String, String> {
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
            try{
                String host = "http://"+ getResources().getString(R.string.ip) +"/getuserid.php?mail='"+ mail+"'";
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
         *  - If we retrieve and id we set it & finish the order
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
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

    /**
     * Class in charge of inserting the new order into the BikeOrder table of the MySql Database
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionSetInputOrder extends AsyncTask<String, String, String> {
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
            try{
                String host = "http://"+ getResources().getString(R.string.ip) +"/inputneworder.php?userid="+ id + "&bikeid=" + bikeObject.getId();
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
         *  - If successful we set this task to true meaning it has finished
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
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

    /**
     * Class in charge of updating the user so the bikeId is set to the id of the selected bike
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionSetUser extends AsyncTask<String, String, String> {
        String result = "";

        /**
         * Method in charge of querying the database through an HTTP request.
         * @param params
         *          Parameters passed when the execution of the AsyncTask is called;
         * @return
         *          Returns the response of the database.
         */
        @Override
        protected String doInBackground(String... params) {
            try{
                String host = "http://"+ getResources().getString(R.string.ip) +"/updateuser.php?userid="+ id + "&bikeid=" + bikeObject.getId();
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
         *  - If successful we set this task to true meaning it has finished
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
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

    /**
     * Class in charge of updating the selected bike so the user id in the MySql Database is set to the id of the user
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionSetBike extends AsyncTask<String, String, String> {
        String result = "";

        /**
         * Method in charge of querying the database through an HTTP request.
         * @param params
         *          Parameters passed when the execution of the AsyncTask is called;
         * @return
         *          Returns the response of the database.
         */
        @Override
        protected String doInBackground(String... params) {
            try{
                String host = "http://"+ getResources().getString(R.string.ip) +"/updatebike.php?userid="+ id + "&bikeid=" + bikeObject.getId();
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
         *  - If successful we set this task to true meaning it has finished
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
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
