package com.example.kioskprototype.AccountSettings;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.kioskprototype.R;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener;

public class RequestMemberCard extends AppCompatActivity {

    static final String TAG = "searchTag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_member_card);


        PlaceAutocompleteFragment autocompleteFragment;

        if (savedInstanceState == null) {
            autocompleteFragment = PlaceAutocompleteFragment.newInstance("pk.eyJ1IjoiYXJub3Zhbm5lc3RlIiwiYSI6ImNrODRsaWl0YTFqYWYzbHJ1ZnBzbWw5NmgifQ.Y8hF7FV-cQeS1I8ZEh4eNA");

            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, autocompleteFragment,TAG);
            transaction.commit();

        } else {
            autocompleteFragment = (PlaceAutocompleteFragment)
                    getSupportFragmentManager().findFragmentByTag(TAG);
        }

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(CarmenFeature carmenFeature) {
                Toast.makeText(RequestMemberCard.this,
                        carmenFeature.placeName(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                finish();
            }
        });
    }
}
