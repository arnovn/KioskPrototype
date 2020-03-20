package com.example.kioskprototype.adapterView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.kioskprototype.R;

import java.util.Date;
import java.util.List;

public class ExtraCreditAdapter extends ArrayAdapter<ExtraCreditObject> {

    private static final String TAG = "extraCreditAdapter";
    private Context mContext;
    private int mResource;

    TextView timeView;
    TextView amountView;
    Button deleteButton;

    public ExtraCreditAdapter(@NonNull Context context, int resource, List<ExtraCreditObject> extraCreditObjects) {
        super(context, resource, extraCreditObjects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        Date date = getItem(position).getTimeOrdered();
        Double amount = getItem(position).getAmount();

        ExtraCreditObject extraCreditObject = new ExtraCreditObject(date, amount);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        timeView = (TextView)convertView.findViewById(R.id.startDateView);
        amountView = (TextView)convertView.findViewById(R.id.amountView);

        timeView.setText(date.toString());
        amountView.setText("€" + amount);

        return convertView;
    }
}
