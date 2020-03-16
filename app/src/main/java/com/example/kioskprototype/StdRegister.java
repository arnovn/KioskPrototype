package com.example.kioskprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import javax.activation.DataHandler;
import javax.mail.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import javax.mail.Address;
import javax.mail.internet.MimeMessage;

import com.example.kioskprototype.Retrofit.INodeJS;
import com.example.kioskprototype.Retrofit.RetrofitClient;

import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class StdRegister extends AppCompatActivity implements View.OnClickListener{
    //View
    EditText fullName;
    EditText email;
    String mailString;
    EditText phoneNumber;
    int password;
    ImageButton confirmButton;

    //Database connection
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    //Mail
    Session session = null;
    ProgressDialog pdialog = null;
    Context context = null;


    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_std_register);

        //Init API
        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        //View
        fullName = (EditText)findViewById(R.id.editNameText);
        email = (EditText)findViewById(R.id.editEmailText);
        phoneNumber = (EditText)findViewById(R.id.editPhoneText);
        confirmButton = (ImageButton)findViewById(R.id.confirmButton);
        fullName.setOnClickListener(this);
        email.setOnClickListener(this);
        phoneNumber.setOnClickListener(this);
        mailString = email.getText().toString();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Random r = new Random();
             //   password = r.nextInt(0 - 999999);
                password = 123456;

                //registerUser(fullName.getText().toString(), email.getText().toString(),String.valueOf(password), phoneNumber.getText().toString());
               // sendMail();
            }
        });
    }

    private void sendMail(){
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol","smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication("arnovanneste96@gmail.com","Av020996!");
            }
        });

        pdialog = ProgressDialog.show(this,"","Sending verification mail",true);

        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();
    }

    private void registerUser(String name, String pass, String email, String phone) {
        compositeDisposable.add(myAPI.registerUser(name,pass,email,phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>(){
                    @Override
                    public void accept (String s) throws Exception{
                        Toast.makeText(StdRegister.this,""+s, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    class RetreiveFeedTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            try {
                /*Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("arnovanneste96@gmail.com"));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailString));
                message.setSubject("Verficiationmail register at WOW-Solutions kiosk");
                message.setContent("You've registered, your passcode is 123456.","text/html; charset=uft-8");

                Transport.send(message);*/
                MimeMessage message = new MimeMessage(session);
                DataHandler handler = new DataHandler(new ByteArrayDataSource("You've registered, your passcode is 123456.".getBytes(), "text/plain"));
                message.setSender(new InternetAddress("arnovanneste96@gmail.com"));
                message.setSubject("Verficiationmail register at WOW-Solutions kiosk");
                message.setDataHandler(handler);
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailString));
            }catch (MessagingException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            pdialog.dismiss();
            Toast.makeText(getApplicationContext(),"Verification mail sent",Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onClick(View v) {
        if(v == fullName){
            fullName.getText().clear();
        }else if(v == email){
            email.getText().clear();
        }else if(v == phoneNumber){
            phoneNumber.getText().clear();
        }
    }
}
