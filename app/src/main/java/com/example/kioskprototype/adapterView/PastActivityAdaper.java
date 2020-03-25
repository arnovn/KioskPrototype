package com.example.kioskprototype.adapterView;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Custom ArrayAdapter class for PastActivityObjects
 */
public class PastActivityAdaper extends ArrayAdapter<PastActivityObject> {

    /**
     * Current state of the application.
     */
    private Context mContext;

    /**
     * Layout resource of the adapter
     */
    private int mResource;

    /**
     * Constructor of the PastActivityObject
     * @param context
     *              Current context of the application
     * @param resource
     *              Layout resource passed to the adapter
     * @param activities
     *              List containing the elements which need to be visualized on the UI layer
     */
    public PastActivityAdaper(@NonNull Context context, int resource, List<PastActivityObject> activities){
        super(context, resource, activities);
        mContext = context;
        mResource = resource;

    }

    /**
     * Returns the View that displays the data (PastActivityObject) at the specified position (based on the list passed at the constructor
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
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        int bikeId = Objects.requireNonNull(getItem(position)).getBikeId();
        Date orderdate = Objects.requireNonNull(getItem(position)).getOrderDate();
        Date startrent = Objects.requireNonNull(getItem(position)).getStartRent();
        Date endrent = Objects.requireNonNull(getItem(position)).getEndRent();
        double amount = Objects.requireNonNull(getItem(position)).getAmount();
        double amountPayed = Objects.requireNonNull(getItem(position)).getAmountpayed();

        PastActivityObject activityObject  = new PastActivityObject(bikeId, orderdate, startrent, endrent, amount, amountPayed);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent,false);

        TextView bikeNameText = convertView.findViewById(R.id.bikeTypeViewPA);
        TextView orderDateText = convertView.findViewById(R.id.startDateViewPA);
        TextView timeRentedText = convertView.findViewById(R.id.timeViewPA);
        TextView tobePaidText = convertView.findViewById(R.id.tobePaidViewPA);
        TextView amountPayedText = convertView.findViewById(R.id.payedViewPA);

        bikeNameText.setText(activityObject.getBikeName());

        String pattern = "dd-MM-yy";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String simpleOrderDate = simpleDateFormat.format(orderdate);

        orderDateText.setText(simpleOrderDate);
        timeRentedText.setText(activityObject.getDuration());
        tobePaidText.setText("€" + activityObject.getAmount());
        amountPayedText.setText("€" + activityObject.getAmountpayed());

        return convertView;
    }
}
