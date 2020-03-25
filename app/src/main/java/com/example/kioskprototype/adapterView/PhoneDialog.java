package com.example.kioskprototype.adapterView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.kioskprototype.R;

/**
 * Custom dialog for inserting a phone number during registration
 */
public class PhoneDialog extends AppCompatDialogFragment {

    /**
     * EditText where phone will be inputted
     */
    private EditText editTextPhone;

    /**
     * listener so dialog knows when it needs to be instantiated
     */
    private PhoneDialogListener listener;

    /**
     * Instantiation of the Dialog.
     *  - EditText is set
     *  - Buttons are set
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     * @return
     *      Returns the instantiated dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_phonenumber, null);
        editTextPhone = view.findViewById(R.id.phoneText);
        Button okButton = view.findViewById(R.id.okButtonFB);
        Button cancelButton = view.findViewById(R.id.cancelButtonFB);

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

    /**
     * Attach the listener to the context of the activity.
     * @param context
     *              Context of the activity whi will create the dialog.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (PhoneDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement PhoneDialogListener");
        }
    }

    /**
     * Methods to be implemented at the activity which creates this Dialog-object to handle:
     *  - ApplyTexts: when confirm button pressed, the inserted phone number is passed to the activity.
     *  - Cancel event: when cancelled is pressed the activity calls the cancelPressed() method.
     */
    public interface PhoneDialogListener{
        void applyTexts(String phonenumber);
        void cancelPressed();
    }
}
