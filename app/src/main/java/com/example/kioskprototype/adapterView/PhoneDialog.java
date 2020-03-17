package com.example.kioskprototype.adapterView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.kioskprototype.R;

public class PhoneDialog extends AppCompatDialogFragment {

    private EditText editTextPhone;
    private PhoneDialogListener listener;
    private Button okButton;
    private Button cancelButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_phonenumber, null);
        editTextPhone = view.findViewById(R.id.phoneText);
        okButton = view.findViewById(R.id.okButtonFB);
        cancelButton = view.findViewById(R.id.cancelButtonFB);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTextPhone.getText().toString().isEmpty()){
                    listener.applyTexts(editTextPhone.getText().toString());
                }else{
                    Toast.makeText(getContext(),"Phone number empty.", Toast.LENGTH_SHORT).show();
                }
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
            listener = (PhoneDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement PhoneDialogListener");
        }
    }

    public interface PhoneDialogListener{
        void applyTexts(String phonenumber);
        void cancelPressed();
    }
}
