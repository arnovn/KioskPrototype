package com.example.kioskprototype.adapterAndObjects;

import android.annotation.SuppressLint;
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
import java.util.Objects;

/**
 * Custom ArrayAdapter class for ABikeObjects
 */
public class ABikeAdapter extends ArrayAdapter<ABikeObject> {

    /**
     * Current state of the application.
     */
    private Context mContext;

    /**
     * Layout resource of the adapter
     */
    private int mResource;

    /**
     * Bike at position
     */
    private ABikeObject bike;

    /**
     * Constructor of the ABikeAdapter object
     * @param context
     *          Current context of the application
     * @param resource
     *          Layout resource passed to the adapter
     * @param bikes
     *          List containing the elements which need to be visualized on the UI layer
     */
    public ABikeAdapter(@NonNull Context context,int resource, List<ABikeObject> bikes) {
        super(context, resource, bikes);
        mContext = context;
        mResource = resource;
    }

    /**
     * Returns the View that displays the data (ABikeObject) at the specified position (based on the list passed at the constructor)
     * @param position
     *          Position inside the dataset
     * @param convertView
     *          Example view layout which will  be implemented in the adapter
     * @param parent
     *          ViewGroup in which the convertView will be visualized.
     * @return
     *          Returns the ConvertView at of the object at the given position in the dataset.
     */
    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        bike = getItem(position);

        int id = Objects.requireNonNull(bike).getId();
        int type = Objects.requireNonNull(bike).getType();
        double level = Objects.requireNonNull(bike).getBatteryLevel();
        double latitude = Objects.requireNonNull(bike).getLatitude();
        double longitude = Objects.requireNonNull(bike).getLongitude();
        int code = Objects.requireNonNull(bike).getCode();
        int bikeStand = Objects.requireNonNull(bike).getBikeStand();

        ABikeObject bikeObject = new ABikeObject(id, type, level, latitude, longitude, code, bikeStand);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent,false);

        TextView bikeName = convertView.findViewById(R.id.nameEdit);
        TextView batteryView = convertView.findViewById(R.id.batteryEdit);
        TextView bikeStandView = convertView.findViewById(R.id.bikestandEdit);

        bikeName.setText("Bike " + id);
        batteryView.setText(level + "%");
        bikeStandView.setText(bikeStand+"");

        return convertView;
    }
}
