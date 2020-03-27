package com.example.kioskprototype.LoginAndRegister;

import android.os.Bundle;
import android.serialport.SerialPortFinder;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kioskprototype.R;
import com.example.kioskprototype.SerialCommunnication.PrefHelper;

public class LoginMemberCard extends AppCompatActivity {

    private Device mDevice;

    private int mDeviceIndex;
    private int mBaudrateIndex;

    private String[] mDevices;
    private String[] mBaudrates;

    private boolean mOpened = false;

    private PrefHelper prefHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_member_card);

        prefHelper.initDefault(this);
        initDevice();
    }

    private void initDevice(){
        SerialPortFinder serialPortFinder = new SerialPortFinder();

        //Device
        mDevices = serialPortFinder.getAllDevicesPath();
        if (mDevices.length == 0) {
            mDevices = new String[] {
                    getString(R.string.no_serial_device)
            };
        }

        //Baudrate
        mBaudrates =getResources().getStringArray(R.array.baudrates);
        mDeviceIndex = prefHelper.getDefault().getInt("serial_port_devices", 0);
        mDeviceIndex = mDeviceIndex >= mDevices.length ? mDevices.length - 1 : mDeviceIndex;
        mBaudrateIndex = prefHelper.getDefault().getInt("baud_rate", 0);

        mDevice = new Device(mDevices[mDeviceIndex], mBaudrates[mBaudrateIndex]);
        Toast.makeText(getApplicationContext(), mDevice.toString(), Toast.LENGTH_LONG);
        System.out.println("The device: " + mDevice.toString());
    }

    public class Device {

        private String path;
        private String baudrate;

        public Device() {
        }

        public Device(String path, String baudrate) {
            this.path = path;
            this.baudrate = baudrate;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getBaudrate() {
            return baudrate;
        }

        public void setBaudrate(String baudrate) {
            this.baudrate = baudrate;
        }

        @Override
        public String toString() {
            return "Device{" + "path='" + path + '\'' + ", baudrate='" + baudrate + '\'' + '}';
        }
    }


}
