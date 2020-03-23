package com.example.kioskprototype.adapterView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.kioskprototype.R;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PendingPaymentAdapter extends ArrayAdapter<PendingPaymentObject> {

    private static final String TAG = "paymentAdapter";
    private Context mContext;
    private int mResource;
    private TypeConverter converter;
    DateFormat df;

    TextView bikeType;
    TextView startDate;
    TextView timeDriven;
    TextView priceStillToBePaidView;

    public PendingPaymentAdapter(@NonNull Context context, int resource, List<PendingPaymentObject> bikes) {
        super(context, resource, bikes);
        converter = new TypeConverter();
        mContext = context;
        mResource = resource;
        df = new SimpleDateFormat("dd--MM-yyyy");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        int id = getItem(position).getId();
        int bikeid = getItem(position).getBikeid();
        Date startRent = getItem(position).getStartRent();
        Date endRent = getItem(position).getEndRent();
        double amount = getItem(position).getAmount();
        double amountPayed = getItem(position).getAmountPayed();
        int type = getItem(position).getType();
        double pricePerHour = getItem(position).getPricePerHour();

        PendingPaymentObject pendingPaymentObject = new PendingPaymentObject(id, bikeid, startRent, endRent, amount, amountPayed, type, pricePerHour);

        ArrayList<Long> timeList = pendingPaymentObject.getTimeRented();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        bikeType = (TextView)convertView.findViewById(R.id.bikeTypeView);
        startDate = (TextView)convertView.findViewById(R.id.startDateView);
        timeDriven = (TextView)convertView.findViewById(R.id.timeView);
        priceStillToBePaidView = (TextView)convertView.findViewById(R.id.tobePaidView);

        String typeString = converter.getType(type);
        bikeType.setText(typeString);
        startDate.setText(df.format(startRent));
        setTime(timeList);

        priceStillToBePaidView.setText("€"+(amount - amountPayed));

        return  convertView;
    }

    public void setTime(List timeList){
        if(!timeList.get(3).toString().equals("0")){
            String time = timeList.get(3).toString() + "d " + timeList.get(2) + "h" + timeList.get(1) + "min " + timeList.get(0) + "s";
            timeDriven.setText(time);
        }else if(!timeList.get(2).toString().equals("0")){
            String time = timeList.get(2) + "h" + timeList.get(1) + "min " + timeList.get(0) + "s";
            timeDriven.setText(time);
        }else if(!timeList.get(1).toString().equals("0")){
            String time = timeList.get(1) + "min " + timeList.get(0) + "s";
            timeDriven.setText(time);
        }else if(!timeList.get(0).toString().equals("0")){
            String time = timeList.get(0) + "s";
            timeDriven.setText(time);
        }else{
            timeDriven.setText("Error retreiving time..");
        }
    }
}
