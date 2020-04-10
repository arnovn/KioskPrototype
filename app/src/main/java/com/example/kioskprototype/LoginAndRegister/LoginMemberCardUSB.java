package com.example.kioskprototype.LoginAndRegister;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.R;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class in charge of connecting to the RFID reader through a USB connection
 * Note: this class isn't used in the project, the tablet I'm using can't deliver enough power to the Arduino, therefore
 *       I swithed to bluetooth. Nevertheless this class could be used in the actual kiosk when a RS233 connection would be user
 */
public class LoginMemberCardUSB extends AppCompatActivity {

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";

    EditText sendArduinoText;
    Button sendButton;
    Button startButton;

    UsbManager usbManager;
    UsbDevice device;
    UsbDeviceConnection connection;
    UsbSerialDevice serialPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_member_card_usb);

        sendArduinoText = findViewById(R.id.sendArduinoText);
        sendButton = findViewById(R.id.sendArduinoButton);
        startButton = findViewById(R.id.startArduinoButton);

        try {
            initUsbManager();
        } catch (Exception e) {
            e.printStackTrace();
        }

        UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
            //Defining a Callback which triggers whenever data is read.
            @Override
            public void onReceivedData(byte[] arg0) {
                String data = null;
                try {
                    data = new String(arg0, "UTF-8");
                    data.concat("/n");
                    Toast.makeText(getApplicationContext(), "CALLBACK: " + data, Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        };

        //Set Serial Connection Parameters.
        //
        //onClickStart(startButton); -> now we can send
        //onClickStop(stopButton);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                    boolean granted =
                            intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if (granted) {
                        connection = usbManager.openDevice(device);
                        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                        if (serialPort != null) {
                            if (serialPort.open()) { //Set Serial Connection Parameters.
                                serialPort.setBaudRate(9600);
                                serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                                serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                                serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                                serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                                serialPort.read(mCallback); //
                                Toast.makeText(getApplicationContext(), "SERIAL PORT OPENED", Toast.LENGTH_LONG).show();

                            } else {
                                System.out.println("SERIAL : PORT NOT OPEN");
                            }
                        } else {
                            System.out.println("SERIAL : PORT IS NULL");
                        }
                    } else {
                        System.out.println("SERIAL : PERM NOT GRANTED");
                    }
                } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                    //onClickStart(startButton); -> now we can send
                } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                    //onClickStop(stopButton);
                }
            }

            ;
        };
            startClick();

        }

    private void initUsbManager() throws Exception {
        usbManager = (UsbManager) getApplicationContext().getSystemService(Context.USB_SERVICE);

        if (usbManager == null) {
            throw new Exception("no usb service");
        }
    }

    private void startClick(){
        startButton.setOnClickListener(v->{

            HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
            if (!usbDevices.isEmpty()) {
                boolean keep = true;
                for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                    device = entry.getValue();
                    int deviceVID = device.getVendorId();
                    if (deviceVID == 0x2341)//Arduino Vendor ID
                    {
                        PendingIntent pi = PendingIntent.getBroadcast(this, 0,
                                new Intent(ACTION_USB_PERMISSION), 0);
                        usbManager.requestPermission(device, pi);
                        keep = false;
                    } else {
                        connection = null;
                        device = null;
                    }

                    if (!keep)
                        break;
                }
            }

        });
    }
}
