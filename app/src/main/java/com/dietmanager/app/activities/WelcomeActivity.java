package com.dietmanager.app.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;
import com.dietmanager.app.R;
import com.dietmanager.app.adapter.WelcomePagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

public class WelcomeActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 0;
    private static final int PLAY_SERVICES_REQUEST = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 2000;

    @BindView(R.id.vp_welcome)
    ViewPager welcomePager;
    @BindView(R.id.tl_page_indicator)
    TabLayout pageIndicatorTab;
    @BindView(R.id.btn_continue)
    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initializeViewReferences();

        // check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            }
        }
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(WelcomeScreenActivity.this, LoginActivity.class)
//                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
////                finish();
//            }
//        });
//        signUpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(WelcomeScreenActivity.this, MobileNumberActivity
//                .class).putExtra("signup", true).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
////                finish();
//            }
//        });
    }

    @OnClick({R.id.btn_continue})
    public void onClick(View v) {
        if (v.getId() == R.id.btn_continue) {
            //setPageSelection();
            startActivity(new Intent(getApplicationContext(), MobileNumberActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
            finish();
        }
    }

   /* @OnPageChange(R.id.vp_welcome)
    public void onPageSelected(int i) {
        if (i == WelcomePagerAdapter.PAGE_COUNT - 1)
            continueButton.setText(getString(R.string.get_started));
        else
            continueButton.setText(getString(R.string.continue_txt));
    }*/

    private void initializeViewReferences() {
        ButterKnife.bind(this);
        welcomePager = findViewById(R.id.vp_welcome);
        welcomePager.setOffscreenPageLimit(3);
        pageIndicatorTab = findViewById(R.id.tl_page_indicator);
        pageIndicatorTab.setupWithViewPager(welcomePager, true);
        continueButton = findViewById(R.id.btn_continue);

        WelcomePagerAdapter adapter = new WelcomePagerAdapter(getBaseContext());
        welcomePager.setAdapter(adapter);
        welcomePager.setCurrentItem(0, true);
        continueButton.setText(getString(R.string.signin));
    }

    private void setPageSelection() {
        if (welcomePager.getCurrentItem() == WelcomePagerAdapter.PAGE_COUNT - 1) {
           // startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            startActivity(new Intent(getApplicationContext(), MobileNumberActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
            finish();
        } else if (welcomePager.getCurrentItem() == WelcomePagerAdapter.PAGE_COUNT - 2) {
            continueButton.setText(getString(R.string.get_started));
            welcomePager.setCurrentItem(welcomePager.getCurrentItem() + 1, true);
        } else if (welcomePager.getCurrentItem() < WelcomePagerAdapter.PAGE_COUNT) {
            welcomePager.setCurrentItem(welcomePager.getCurrentItem() + 1, true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        // Google client to interact with Google API
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize
                        // location requests here
//                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(WelcomeActivity.this,
                                    REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean FINE_LOCATIONPermission =
                            grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean COARSE_LOCATIONPermission =
                            grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (FINE_LOCATIONPermission && COARSE_LOCATIONPermission) {


                    } else {
                        Snackbar.make(this.findViewById(android.R.id.content),
                                "Please Grant Permissions to start service",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ActivityCompat.requestPermissions(WelcomeActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                                    }
                                }).show();
                    }
                }
                break;
        }
    }
}