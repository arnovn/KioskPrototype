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

import java.util.Date;
import java.util.List;

public class PastActivityAdaper extends ArrayAdapter<PastActivityObject> {
    private static final String TAG = "PastActivityAdapter";
    private Context mContext;
    private int mResource;

    public PastActivityAdaper(@NonNull Context context, int resource, List<PastActivityObject> activities){
        super(context, resource, activities);
        mContext = context;
        mResource = resource;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        int bikeId = getItem(position).getBikeId();
        Date orderdate = getItem(position).getOrderDate();
        Date startrent = getItem(position).getStartRent();
        Date endrent = getItem(position).getEndRent();
        double amount = getItem(position).getAmount();
        double amountPayed = getItem(position).getAmountpayed();

        PastActivityObject activityObject  = new PastActivityObject(bikeId, orderdate, startrent, endrent, amount, amountPayed);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent,false);

        TextView bikeNameText = (TextView)convertView.findViewById(R.id.bikeTypeViewPA);
        TextView orderDateText = (TextView)convertView.findViewById(R.id.startDateViewPA);
        TextView timeRentedText = (TextView)convertView.findViewById(R.id.timeViewPA);
        TextView tobePaidText = (TextView)convertView.findViewById(R.id.tobePaidViewPA);
        TextView amountPayedText = (TextView)convertView.findViewById(R.id.payedViewPA);

        bikeNameText.setText(activityObject.getBikeName());
        orderDateText.setText(activityObject.getOrderDate().toString());
        timeRentedText.setText(activityObject.getDuration());
        tobePaidText.setText("€" + activityObject.getAmount());
        amountPayedText.setText("€" + activityObject.getAmountpayed());

        return convertView;
    }
}
