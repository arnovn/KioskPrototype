package com.example.kioskprototype.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kioskprototype.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class PayForServices extends AppCompatActivity {

    int userId;
    double credits;

    double amount;

    TextView amountView;
    TextView depthView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_for_services);

        amount = 0;
        userId = getIntent().getIntExtra("Id", 0);

        setTextViews();

        new ConnectionGetUserCredits().execute();
    }

    public void setCredits(double credits){
        this.credits = credits;

        if(this.credits >= 0){
            amountView.setText(amount + " euro");
            depthView.setText("You have no depths.");
        }else if(this.credits < 0){
            amount = 0-credits;
            amountView.setText(amount + " euro");
            depthView.setText("You have " + amount +"depths.");
        }
    }

    public void setTextViews(){
        amountView = (TextView)findViewById(R.id.amountView);
        depthView = (TextView)findViewById(R.id.depthView);
    }


    //Set listview to max height
    /*if(adapter.getCount() > 5){
        View item = adapter.getView(0, null, listView);
        item.measure(0, 0);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, (int) (5.5 * item.getMeasuredHeight()));
        listView.setLayoutParams(params);
    }*/

    class ConnectionGetUserCredits extends AsyncTask<String, String, String>{
        String result = "";
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://10.0.2.2/getusercredits.php?userid=" + userId;
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
                if(success == 1){
                    JSONArray creditinfos = jsonResult.getJSONArray("credits");
                    JSONObject creditinfo = creditinfos.getJSONObject(0);

                    double userCredit = creditinfo.getDouble("credits");
                    setCredits(userCredit);

                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Failed: credits couldn't be retreived.",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
