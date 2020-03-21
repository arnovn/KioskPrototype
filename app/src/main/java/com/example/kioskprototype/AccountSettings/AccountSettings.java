package com.example.kioskprototype.AccountSettings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
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

public class AccountSettings extends AppCompatActivity {

    TextView welcomeView;
    TextView creditView;
    Button pastactivitiesButton;
    Button membercardButton;
    Button newLoginButton;

    String mail;
    String name;
    int id;
    double credits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        mail = getIntent().getStringExtra("Mail");
        id = getIntent().getIntExtra("Id",0);

        welcomeView = (TextView)findViewById(R.id.accountSettingTitle);
        creditView = (TextView)findViewById(R.id.creditsWindow);
        pastactivitiesButton = (Button)findViewById(R.id.pastActivityButton);
        membercardButton = (Button)findViewById(R.id.membercardButton);
        newLoginButton = (Button)findViewById(R.id.requestNewUserCode);

        new ConnectionGetNameAndCredits().execute();
    }

    public void setTextViews(){
        welcomeView.setText("Welcome " + name + "!");
        creditView.setText("Account Credits: €" + credits);
    }

    class ConnectionGetNameAndCredits extends AsyncTask<String, String, String>{
        String result = "";
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://10.0.2.2/getusernamecredits.php?id='"+ id +"'";
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
                    JSONArray datainfos = jsonResult.getJSONArray("data");
                    JSONObject datainfo = datainfos.getJSONObject(0);

                    name = datainfo.getString("name");
                    credits = datainfo.getDouble("credits");

                    setTextViews();

                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Failed: No user found.",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
