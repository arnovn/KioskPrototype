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

import java.util.List;

public class ABikeAdapter extends ArrayAdapter<ABikeObject> {

    private static final String TAG = "aBikeAdapter";
    private Context mContext;
    int mResource;

    public ABikeAdapter(@NonNull Context context,int resource, List<ABikeObject> bikes) {
        super(context, resource, bikes);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        int id = getItem(position).getId();
        int type = getItem(position).getType();
        double level = getItem(position).getBatteryLevel();
        double latitude = getItem(position).getLatitude();
        double longitude = getItem(position).getLongitude();
        int code = getItem(position).getCode();
        int bikeStand = getItem(position).getBikeStand();

        ABikeObject bikeObject = new ABikeObject(id, type, level, latitude, longitude, code, bikeStand);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent,false);

        TextView bikeName = (TextView)convertView.findViewById(R.id.nameEdit);
        TextView batteryView = (TextView)convertView.findViewById(R.id.batteryEdit);
        TextView bikeStandView = (TextView)convertView.findViewById(R.id.bikestandEdit);

        bikeName.setText("Bike " + id);
        batteryView.setText(level + "%");
        bikeStandView.setText(bikeStand+"");

        return convertView;
    }
}
