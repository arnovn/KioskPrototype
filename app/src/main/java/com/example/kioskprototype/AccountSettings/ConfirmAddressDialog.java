package com.example.kioskprototype.AccountSettings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.kioskprototype.R;

public class ConfirmAddressDialog extends AppCompatDialogFragment {
    private TextView addresView;
    private Button confirmButton;
    private Button cancelButton;

    private ConfirmAddressDialogListener listener;

    String address;

    public ConfirmAddressDialog(String address){
        this.address = address;
    }

    public String getAddress(){
        return address;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_addres_confirmation, null);

        addresView = view.findViewById(R.id.addressText);
        confirmButton = view.findViewById(R.id.confirmButtonAC);
        cancelButton = view.findViewById(R.id.cancelButtonAC);

        addresView.setText(address);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.confirmPressed();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancelPressed();
            }
        });

        dialog.setContentView(view);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ConfirmAddressDialog.ConfirmAddressDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ConfirmAddressDialogListener");
        }
    }

    public interface ConfirmAddressDialogListener{
        void confirmPressed();
        void cancelPressed();
    }
}
