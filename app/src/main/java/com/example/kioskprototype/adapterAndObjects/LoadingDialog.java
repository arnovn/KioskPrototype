package com.example.kioskprototype.adapterAndObjects;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.kioskprototype.R;

/**
 * Class in charge of creating loading dialog while the Kiosk is connecting to the RFID reader
 */
public class LoadingDialog {

    /**
     * Activity that created the object
     */
    private Activity activity;

    /**
     * The AlertDialog that will be created
     */
    private AlertDialog dialog;

    /**
     * Builder for an AlertDialog
     */
    private AlertDialog.Builder builder;

    /**
     * Constructor for LoadingDialog
     * @param myActivity
     *              Activity which wants to create LoadingDialog object
     */
    public LoadingDialog(Activity myActivity){
        activity = myActivity;
    }

    /**
     * When Dialog needs to be shown on the UI layer we "start loading"
     * Custom layout has been created: R.layout.loading_dialog
     */
    @SuppressLint("InflateParams")
    public void startLoadingDialog(){
        builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    /**
     * When the dialog no longer needs to be visualized on the UI layer we dismiss it
     */
    public void dismissDialog(){
        dialog.dismiss();
    }

    /**
     * Builder getter (needed for OnClickListener)
     * @return
     *          Builder of the dialog
     */
    public AlertDialog.Builder getBuilder(){
        return builder;
    }
}
