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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Custon ArrayAdapter class for PendingPaymentObjects
 */
public class PendingPaymentAdapter extends ArrayAdapter<PendingPaymentObject> {

    /**
     * Current state of the application.
     */
    private Context mContext;

    /**
     * Layout resource of the adapter
     */
    private int mResource;

    /**
     * Converter type(int) to type(String)
     */
    private TypeConverter converter;

    /**
     * DateFormat for converting Dates to another format
     */
    private DateFormat df;

    /**
     * Textview which visualizes the time a user has driven for a certain order.
     */
    private TextView timeDriven;

    /**
     * Constructor of PendingPaymentAdapter
     * @param context
     *              Current context of the application
     * @param resource
     *              Layout resource passed to the adapter
     * @param paymentObjects
     *              List containing the elements which need to be visualized on the UI layer
     */
    @SuppressLint("SimpleDateFormat")
    public PendingPaymentAdapter(@NonNull Context context, int resource, List<PendingPaymentObject> paymentObjects) {
        super(context, resource, paymentObjects);
        converter = new TypeConverter();
        mContext = context;
        mResource = resource;
        df = new SimpleDateFormat("dd--MM-yyyy");
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
        int id = Objects.requireNonNull(getItem(position)).getId();
        int bikeid = Objects.requireNonNull(getItem(position)).getBikeid();
        Date startRent = Objects.requireNonNull(getItem(position)).getStartRent();
        Date endRent = Objects.requireNonNull(getItem(position)).getEndRent();
        double amount = Objects.requireNonNull(getItem(position)).getAmount();
        double amountPayed = Objects.requireNonNull(getItem(position)).getAmountPayed();
        int type = Objects.requireNonNull(getItem(position)).getType();
        double pricePerHour = Objects.requireNonNull(getItem(position)).getPricePerHour();

        PendingPaymentObject pendingPaymentObject = new PendingPaymentObject(id, bikeid, startRent, endRent, amount, amountPayed, type, pricePerHour);

        ArrayList timeList = pendingPaymentObject.getTimeRented();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView bikeType = convertView.findViewById(R.id.bikeTypeView);
        TextView startDate = convertView.findViewById(R.id.startDateView);
        timeDriven = convertView.findViewById(R.id.timeView);
        TextView priceStillToBePaidView = convertView.findViewById(R.id.tobePaidView);

        String typeString = converter.getType(type);
        bikeType.setText(typeString);
        startDate.setText(df.format(startRent));
        setTime(timeList);

        priceStillToBePaidView.setText("€"+(amount - amountPayed));

        return  convertView;
    }

    /**
     * Method in charge of converting the List which has the duration of a rent to a string & setting the timeDriven TextView
     * @param timeList
     *              List consisting the duration of one bike rent
     *               - list[3]: amount of days
     *               - list[2]: remaining hours
     *               - list[1]: remaining minutes
     *               - list[0]: remaining seconds
     */
    @SuppressLint("SetTextI18n")
    private void setTime(List timeList){
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
