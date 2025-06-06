package com.dietmanager.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dietmanager.app.R;
import com.dietmanager.app.adapter.DeliveryLocationAdapter;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.Address;
import com.dietmanager.app.models.AddressList;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetDeliveryLocationActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.delivery_location_rv)
    RecyclerView deliveryLocationRv;
    @BindView(R.id.current_location_ll)
    LinearLayout currentLocationLl;
    LinearLayoutManager manager;
    @BindView(R.id.animation_line_cart_add)
    ImageView animationLineCartAdd;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private int SET_COMPLETE = 20;
    @BindView(R.id.find_place_ll)
    LinearLayout findPlaceLl;
    private String TAG = "DeliveryLocationActi";
    private DeliveryLocationAdapter adapter;
    private List<AddressList> modelListReference = new ArrayList<>();
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    List<AddressList> modelList = new ArrayList<>();
    AnimatedVectorDrawableCompat avdProgress;

    public static boolean isAddressSelection = false;
    public static boolean isCustomerAddress = false;
    public static boolean isHomePage = false;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_delivery_location);
        ButterKnife.bind(this);
        activity = SetDeliveryLocationActivity.this;

        //Intialize Animation line
        initializeAvd();

        // Initialize Places.
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));

        isAddressSelection = getIntent().getBooleanExtra("get_address", false);
        isCustomerAddress = getIntent().getBooleanExtra("isCustomer_address", false);
        isHomePage = getIntent().getBooleanExtra("home_page", false);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        modelListReference.clear();
        AddressList addressList = new AddressList();
        if (GlobalData.profileModel != null) {
            addressList.setHeader(getResources().getString(R.string.saved_addresses));
            addressList.setAddresses(GlobalData.profileModel.getAddresses());
        }
        modelListReference.clear();
        modelListReference.add(addressList);
        manager = new LinearLayoutManager(this);
        deliveryLocationRv.setLayoutManager(manager);
        adapter = new DeliveryLocationAdapter(this, activity, modelListReference);
        deliveryLocationRv.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isCustomerAddress){
            getCustomerAddress();
        }else {
            getAddress();
        }

    }


    private void initializeAvd() {
        avdProgress = AnimatedVectorDrawableCompat.create(getApplicationContext(), R.drawable.avd_line);
        animationLineCartAdd.setBackground(avdProgress);
        animationLineCartAdd.setVisibility(View.VISIBLE);
        avdProgress.start();
    }


    private void getAddress() {
        Call<List<Address>> call = apiInterface.getCustomerAddresses();
        call.enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(@NonNull Call<List<Address>> call, @NonNull Response<List<Address>> response) {
                if (response.isSuccessful()) {
                    modelList.clear();
                    animationLineCartAdd.setVisibility(View.GONE);
                    avdProgress.stop();
                    AddressList model = new AddressList();
                    model.setHeader(getResources().getString(R.string.saved_addresses));
                    model.setAddresses(response.body());
                    GlobalData.profileModel.setAddresses(response.body());
                    modelList.add(model);
                    modelListReference.clear();
                    modelListReference.addAll(modelList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Address>> call, @NonNull Throwable t) {

            }
        });
    }

     private void getCustomerAddress() {
        Call<List<Address>> call = apiInterface.getCustomerAddresses();
        call.enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(@NonNull Call<List<Address>> call, @NonNull Response<List<Address>> response) {
                if (response.isSuccessful()) {
                    modelList.clear();
                    animationLineCartAdd.setVisibility(View.GONE);
                    avdProgress.stop();
                    AddressList model = new AddressList();
                    model.setHeader(getResources().getString(R.string.saved_addresses));
                    model.setAddresses(response.body());
                    GlobalData.profileModel.setAddresses(response.body());
                    modelList.add(model);
                    modelListReference.clear();
                    modelListReference.addAll(modelList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Address>> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Intent intent = new Intent(SetDeliveryLocationActivity.this, SaveDeliveryLocationActivity.class);
                intent.putExtra("skip_visible", isHomePage);
                if (place.getAddress().toString().contains(place.getName().toString())) {
                    intent.putExtra("place_address", place.getAddress());
                }else{
                    intent.putExtra("place_address", place.getName() + ", " + place.getAddress());
                }
                intent.putExtra("isCustomer_address", isCustomerAddress);
                intent.putExtra("latitude", ""+place.getLatLng().latitude);
                intent.putExtra("longitude", ""+place.getLatLng().longitude);
                startActivityForResult(intent,SET_COMPLETE);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @OnClick({R.id.find_place_ll, R.id.current_location_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.find_place_ll:
                findPlace();
                break;
            case R.id.current_location_ll:
                startActivity(new Intent(SetDeliveryLocationActivity.this, SaveDeliveryLocationActivity.class).putExtra("skip_visible", isHomePage).putExtra("isCustomer_address", isCustomerAddress));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                break;
        }
    }

    private void findPlace() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this);
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }

}
