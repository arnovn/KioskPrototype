package com.example.kioskprototype.adapterView;

import android.annotation.SuppressLint;
import android.content.Context;
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
import java.util.Objects;

/**
 * Custon ArrayAdapter class for PoiObjects
 */
public class PoiAdapter1 extends ArrayAdapter<PoiObject1> {

    /**
     * Current state of the application.
     */
    private Context mContext;

    /**
     * Layout resource of the adapter
     */
    private int mResource;

    /**
     * Constructor of PendingPaymentAdapter
     * @param context
     *              Current context of the application
     * @param resource
     *              Layout resource passed to the adapter
     * @param objects
     *              List containing the elements which need to be visualized on the UI layer
     */
    public PoiAdapter1(@NonNull Context context, int resource, @NonNull List<PoiObject1> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    /**
     * Returns the View that displays the data (PoiObject) at the specified position (based on the list passed at the constructor)
     * @param position
     *              Position inside the dataset
     * @param convertView
     *              Example view layout which will  be implemented in the adapter
     * @param parent
     *              ViewGroup in which the convertView will be visualized.
     * @return
     *              Returns the ConvertView at of the object at the given position in the dataset.
     */
    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int id = Objects.requireNonNull(getItem(position)).getId();
        int type = Objects.requireNonNull(getItem(position)).getType();
        String name = Objects.requireNonNull(getItem(position)).getName();
        String address = Objects.requireNonNull(getItem(position)).getAddress();
        float distance = Objects.requireNonNull(getItem(position)).getDistance();
        String description = Objects.requireNonNull(getItem(position)).getDescription();

        PoiObject1 object1 = new PoiObject1(id,name,address,distance,description,type);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView poiNameText = convertView.findViewById(R.id.nameView);
        TextView poiAddressText = convertView.findViewById(R.id.addressView);
        TextView poiDistanceText = convertView.findViewById(R.id.distView);
        ImageView icon = convertView.findViewById(R.id.icon);

        poiNameText.setText(name);
        poiAddressText.setText(address);
        poiDistanceText.setText(distance +"km");

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
