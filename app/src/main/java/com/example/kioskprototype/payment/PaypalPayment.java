package com.example.kioskprototype.payment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.FinalScreen;
import com.example.kioskprototype.R;
import com.example.kioskprototype.adapterView.ABikeObject;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class PaypalPayment extends AppCompatActivity {

    //Sandbox account: sb-hu4yp1369124@business.example.com
    //ClientID: AWbtJWYvpTOJPPc_iO-4tqMjdRB0DmWm27a_23m61ctvWSyVs1wC25zmc25meWOtikVU56VW_dztbY9o
    //Secret: EGV-UG8S6BLBfdGsPljkVOB0uLQ0TwPetGu49CL_dJkD0WehJTR41iKUCh26vg0xmR1eMc5tLzdUU-Pz

    ImageView qrView;

    ABikeObject bikeObject;
    String mail;
    int userId;
    int orderId;
    int amount;

    Intent statusPollIntent;
    int broadcastOrderId;
    int broadcastOrderStatus;
    OrderStatusBroadcastReceiver orderStatusBroadcastReceiver;
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal_payment);

        bikeObject = (ABikeObject) getIntent().getSerializableExtra("Bike");
        mail = getIntent().getStringExtra("Mail");
        userId = getIntent().getIntExtra("UserId", 0);
        orderId = getIntent().getIntExtra("OrderId", 0);
        amount = getIntent().getIntExtra("Amount", 0);
        broadcastOrderStatus = 0;

        qrView = findViewById(R.id.qrPaypalView);

        String jsonData = "{\"userId\":\""+ userId +"\",\"orderId\":\""+  orderId +"\", \"amount\":\""+ amount +"\"}";
        String jsonData2 = "{\"userId\":\""+ userId +"\", \"amount\": \"50\"}";

        QRGEncoder qrgEncoder = new QRGEncoder(jsonData,null, QRGContents.Type.TEXT, 500);
        try {
            Bitmap qrBit = qrgEncoder.encodeAsBitmap();
            qrView.setImageBitmap(qrBit);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        orderStatusBroadcastReceiver = new OrderStatusBroadcastReceiver();
        orderStatusBroadcastReceiver.setOrderStatusBroadcastListener(title -> {
            broadcastOrderId = orderStatusBroadcastReceiver.getOrderId();
            broadcastOrderStatus = orderStatusBroadcastReceiver.getOrderStatus();

            System.out.println("RECEIVED LISTENER");
            if(broadcastOrderId == orderId){
                switch(broadcastOrderStatus){
                    case 0:
                        System.out.println("Order status: INIT");
                        break;
                    case 1:
                        System.out.println("Order status: PENDING");
                        break;
                    case 2:
                        System.out.println("Order status: SUCCESS");
                        stopService(statusPollIntent);
                        //TODO: Update database: 1. update user credits in DB 2. connect user & bike in DB 3. send confirmation mail
                        toFinalScreen();
                        break;
                    case 3:
                        System.out.println("Order status: TIMEDOUT");
                        break;
                    case -1:
                        System.out.println("Order status: FAILED");
                        break;
                    default:
                        System.out.println("ERROR Order status: DEFAULT");
                }
            }
        });

        filter = new IntentFilter();
        filter.addAction("PAYMENT_STATUS");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(orderStatusBroadcastReceiver, filter);

        startPolling();
    }

    private void startPolling(){
        statusPollIntent = new Intent(getApplicationContext(), PaymentStatusPollService.class);
        statusPollIntent.putExtra("OrderId", orderId);
        statusPollIntent.putExtra("PollInterval", 5);
        startService(statusPollIntent);
    }

    private void toFinalScreen(){
        Intent finalIntent = new Intent(PaypalPayment.this, FinalScreen.class);
        finalIntent.putExtra("Bike", bikeObject);
        startActivity(finalIntent);
    }

    /**
     * Broadcast receiver, needed to receive data from the polling thread when it has received the access token.
     */
    public static class OrderStatusBroadcastReceiver extends BroadcastReceiver {

        int orderId;
        int orderStatus;

        public interface OrderStatusBroadcastListener {
            void onObjectReady(String title);
        }

        /**
         * Listener for this class.
         */
        private OrderStatusBroadcastListener listener;

        public OrderStatusBroadcastReceiver(){
            this.listener = null;
        }

        public void setOrderStatusBroadcastListener(OrderStatusBroadcastListener listener){
            this.listener = listener;
        }

        /**
         * Getters for the orderId
         * @return
         *              Order id of the broadcast sent
         */
        public int getOrderId() {
            return orderId;
        }

        /**
         * Getters for the orderStatus
         * @return
         *              Order status of the broadcast sent
         */
        public int getOrderStatus(){
            return orderStatus;
        }

        /**
         * When the BroadcastReceiver receives a broadcast we try to get the received data from it.
         * @param context
         *              Current context of the activity
         * @param intent
         *              PollingIntent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("RECEIVING");
            Bundle b = intent.getExtras();
            assert b != null;
            orderId = b.getInt("OrderId");
            orderStatus = b.getInt("STATUS");
            listener.onObjectReady("ready");
        }
    }
}
