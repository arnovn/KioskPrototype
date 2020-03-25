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

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Custom ArrayAdapter class for ExtraCreditObjects
 */
public class ExtraCreditAdapter extends ArrayAdapter<ExtraCreditObject> {

    /**
     * Current state of the application.
     */
    private Context mContext;

    /**
     * Layout resource of the adapter
     */
    private int mResource;

    /**
     * Constructor of the ExtraCreditAdapter
     * @param context
     *          Current context of the application
     * @param resource
     *          Layout resource passed to the adapter
     * @param extraCreditObjects
     *          List containing the elements which need to be visualized on the UI layer
     */
    public ExtraCreditAdapter(@NonNull Context context, int resource, List<ExtraCreditObject> extraCreditObjects) {
        super(context, resource, extraCreditObjects);
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
        Date date = Objects.requireNonNull(getItem(position)).getTimeOrdered();
        double amount = Objects.requireNonNull(getItem(position)).getAmount();

        ExtraCreditObject extraCreditObject = new ExtraCreditObject(date, amount);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView amountView = convertView.findViewById(R.id.amountView);

        amountView.setText("€" + amount);

        return convertView;
    }
}
