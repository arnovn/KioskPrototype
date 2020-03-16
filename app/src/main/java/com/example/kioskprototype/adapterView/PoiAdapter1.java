package com.example.kioskprototype.adapterView;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.kioskprototype.R;

import java.util.List;

public class PoiAdapter1 extends ArrayAdapter<PoiObject1> {

    private static final String TAG = "poiAdapter1";
    private Context mContext;
    int mResource;


    public PoiAdapter1(@NonNull Context context, int resource, @NonNull List<PoiObject1> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int id = getItem(position).getId();
        int type = getItem(position).getType();
        String name = getItem(position).getName();
        String address = getItem(position).getAddress();
        float distance = getItem(position).getDistance();
        String description = getItem(position).getDescription();

        PoiObject1 object1 = new PoiObject1(id,name,address,distance,description,type);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView poiNameText = (TextView)convertView.findViewById(R.id.nameView);
        TextView poiAddressText = (TextView)convertView.findViewById(R.id.addressView);
        TextView poiDistanceText = (TextView)convertView.findViewById(R.id.distView);
        ImageView icon = (ImageView)convertView.findViewById(R.id.icon);

        poiNameText.setText(name);
        poiAddressText.setText(address);
        poiDistanceText.setText(String.valueOf(distance)+"km");

        switch (type){
            case 1:
                icon.setImageResource(R.drawable.restaurant);
                break;
            case 2:
                icon.setImageResource(R.drawable.map);
                break;
            case 3:
                icon.setImageResource(R.drawable.activities);
                break;
            case 4:
                icon.setImageResource(R.drawable.party);
                break;
        }

        return convertView;
    }

}
