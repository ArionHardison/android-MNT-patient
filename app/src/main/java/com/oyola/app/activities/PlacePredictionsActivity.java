package com.oyola.app.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.oyola.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class PlacePredictionsActivity extends BaseActivity {

    private static final String TAG = PlacePredictionsActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_back)
    ImageView backImage;
    @BindView(R.id.et_places_search)
    EditText placesSearchEdit;
    @BindView(R.id.iv_clear)
    ImageView clearImage;
    @BindView(R.id.rv_places)
    RecyclerView placesRecycler;

    private PlacesClient placesClient;
    private AutocompleteSessionToken token;
    private CharacterStyle STYLE_BOLD;
    private CharacterStyle STYLE_NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_predictions);

        STYLE_BOLD = new StyleSpan(Typeface.BOLD);
        STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);
        initializePlacesClient();
        initializeViewReferences();
    }

    private void initializePlacesClient() {
        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        // Create a new Places client instance
        placesClient = Places.createClient(this);// Create location services client.
        // Create a new token for the autocomplete session.
        token = AutocompleteSessionToken.newInstance();
    }

    private void initializeViewReferences() {
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
    }

    private void getPredictions(String query) {
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                //.setCountry("IN")
                .setTypeFilter(TypeFilter.GEOCODE)
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onSuccess(FindAutocompletePredictionsResponse response) {
                        for (AutocompletePrediction prediction :
                                response.getAutocompletePredictions()) {
                            Log.i(TAG, prediction.getPlaceId());
                            Log.i(TAG, prediction.getPrimaryText(STYLE_BOLD).toString());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            Log.e(TAG, "Place not found: " + apiException.getMessage());
                        }
                    }
                });
    }

    @OnClick({R.id.iv_back, R.id.iv_clear})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.iv_clear:
                placesSearchEdit.setText("");
                break;

            default:
                break;
        }
    }

    @OnTextChanged(R.id.et_places_search)
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            clearImage.setVisibility(View.GONE);
        } else {
            clearImage.setVisibility(View.VISIBLE);
            getPredictions(String.valueOf(s));
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
