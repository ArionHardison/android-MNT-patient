package com.oyola.app.activities;

import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.oyola.app.R;
import com.oyola.app.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationPickActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnMapLoadedCallback {

    private static final String TAG = LocationPickActivity.class.getSimpleName();
    private static final float DEFAULT_ZOOM_LEVEL = 18.0f;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cl_bottom_sheet)
    ConstraintLayout bottomSheetLayout;
    @BindView(R.id.ll_location)
    LinearLayout locationLayout;
    @BindView(R.id.tv_location_address)
    TextView locationAddressText;
    @BindView(R.id.iv_home)
    ImageView homeImage;
    @BindView(R.id.iv_work)
    ImageView workImage;
    @BindView(R.id.iv_other)
    ImageView otherImage;

    private FusedLocationProviderClient fusedLocationClient;
    private BottomSheetBehavior bottomSheetBehavior;
    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private boolean doesLocationChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_pick);

        initializeLocationClient();
        initializeViewReferences();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onCameraIdle() {
        Log.d(TAG, "onCameraIdle");
        LatLng latLng = mMap.getCameraPosition().target;
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        if (latitude != 0 && longitude != 0)
            getLocationAddress(latitude, longitude);
    }

    @Override
    public void onCameraMoveStarted(int i) {
        Log.d(TAG, "onCameraMoveStarted");
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onMapLoaded() {
        Log.d(TAG, "onMapLoaded");
        getDeviceLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.RC_PLACES_AUTOCOMPLETE_ACTIVITY) {

        }
    }

    @OnClick({R.id.ll_location, R.id.ll_home, R.id.ll_work, R.id.ll_other, R.id.iv_current_location,
            R.id.tv_confirm_location})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_location:
                launchActivityForResult(PlacePredictionsActivity.class,
                        Constants.RC_PLACES_AUTOCOMPLETE_ACTIVITY);
                break;

            case R.id.ll_home:
                setAddressCategory(R.color.colorButton, R.color.colorBlack, R.color.colorBlack);
                break;

            case R.id.ll_work:
                setAddressCategory(R.color.colorBlack, R.color.colorButton, R.color.colorBlack);
                break;

            case R.id.ll_other:
                setAddressCategory(R.color.colorBlack, R.color.colorBlack, R.color.colorButton);
                break;

            case R.id.iv_current_location:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                getDeviceLocation();
                break;

            case R.id.tv_confirm_location:
                break;

            default:
                break;
        }
    }

    private void initializeLocationClient() {
        // Create location services client.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void initializeViewReferences() {
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setHideable(false);

        locationLayout.setEnabled(getIntent().getBooleanExtra(Constants.ENABLE_PLACES_AUTOCOMPLETE, false));
    }

    private void setAddressCategory(int homeId, int workId, int otherId) {
        homeImage.setColorFilter(ContextCompat.getColor(getBaseContext(), homeId));
        workImage.setColorFilter(ContextCompat.getColor(getBaseContext(), workId));
        otherImage.setColorFilter(ContextCompat.getColor(getBaseContext(), otherId));
    }

    private void getDeviceLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location.getLatitude(), location.getLongitude()),
                                    DEFAULT_ZOOM_LEVEL)
                            );

                            doesLocationChanged = location.getLatitude() != latitude
                                    || location.getLongitude() != longitude;

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            if (doesLocationChanged)
                                getLocationAddress(latitude, longitude);
                        }
                    }
                });
    }

    private void getLocationAddress(double latitude, double longitude) {
        if (!Geocoder.isPresent()) {
            showToast(getString(R.string.no_geocoder_available));
            return;
        }
        startAddressIntentService(new AddressResultReceiver(new Handler()), latitude, longitude);
    }

    private class AddressResultReceiver extends ResultReceiver {


        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData == null) {
                return;
            }

            String result = resultData.getString(Constants.RESULT);
            if (resultCode == Constants.SUCCESS_RESULT) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                locationAddressText.setText(result);
            } else {
                showToast(result);
            }
        }
    }
}
