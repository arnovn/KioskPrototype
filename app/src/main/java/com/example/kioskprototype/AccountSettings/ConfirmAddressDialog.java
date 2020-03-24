package com.example.kioskprototype.AccountSettings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.kioskprototype.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ConfirmAddressDialog extends AppCompatDialogFragment {

    /**
     * listener so dialog knows when it needs to be instantiated
     */
    private ConfirmAddressDialogListener listener;

    /**
     * Address of the user logged in to the Kiosk.
     */
    private String address;

    /**
     * Constructor
     * @param address
     *              Address inputted by the user at the RequestMemberCard activity
     */
    ConfirmAddressDialog(String address){
        this.address = address;
    }

    /**
     * User address getter
     * @return
     *          Returns the address of the user.
     */
    public String getAddress(){
        return address;
    }

    /**
     * Instantiation of the Dialog.
     *  - TextViews are set
     *  - Buttons are set
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     * @return
     *      Returns the instantiated dialog.
     */
    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.layout_addres_confirmation, null);

        TextView addresView = view.findViewById(R.id.addressText);
        Button confirmButton = view.findViewById(R.id.confirmButtonAC);
        Button cancelButton = view.findViewById(R.id.cancelButtonAC);

        addresView.setText(address);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        confirmButton.setOnClickListener(v -> listener.confirmPressed());
        cancelButton.setOnClickListener(v -> listener.cancelPressed());

        dialog.setContentView(view);
        return dialog;
    }

    /**
     * Attacht the listener to the context of the activity.
     * @param context
     *              Context of the activity whi will create the dialog.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ConfirmAddressDialog.ConfirmAddressDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ConfirmAddressDialogListener");
        }
    }

    /**
     * Methods to be implemented at the activity which creates this Dialog object to handle:
     *  - Confirmation event
     *  - Cancel event.
     */
    public interface ConfirmAddressDialogListener{
        void confirmPressed();
        void cancelPressed();
    }
}
