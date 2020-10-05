package com.dietmanager.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.dietmanager.app.R;
import com.dietmanager.app.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FetchAddressIntentService extends IntentService {

    private static final String TAG = FetchAddressIntentService.class.getSimpleName();
    private ResultReceiver receiver;

    public FetchAddressIntentService() {
        super(FetchAddressIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null)
            return;

        receiver = intent.getParcelableExtra(Constants.RECEIVER);
        double latitude = intent.getDoubleExtra(Constants.LATITUDE, -1);
        double longitude = intent.getDoubleExtra(Constants.LONGITUDE, -1);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        String errorMessage = "";


        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long);
            Log.e(TAG, errorMessage + ": " + "Latitude = " + latitude + ", Longitude = " +
                    longitude, illegalArgumentException);
        }

        if (addresses == null || addresses.isEmpty()) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            // Fetch the address lines using getAddressLine, join them, and send them to the thread.
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            String streetAddress = TextUtils.join(
                    Objects.requireNonNull(System.getProperty("line.separator")),
                    addressFragments
            );
            Log.i(TAG, streetAddress);
            deliverResultToReceiver(Constants.SUCCESS_RESULT, streetAddress);
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT, message);
        receiver.send(resultCode, bundle);
    }
}
