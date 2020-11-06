package com.dietmanager.app.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.dietmanager.app.HomeActivity;
import com.dietmanager.app.OyolaApplication;
import com.dietmanager.app.R;
import com.dietmanager.app.activities.FilterActivity;
import com.dietmanager.app.activities.LoginActivity;
import com.dietmanager.app.activities.SetDeliveryLocationActivity;
import com.dietmanager.app.activities.SubscribePlanActivity;
import com.dietmanager.app.adapter.DaysAdapter;
import com.dietmanager.app.adapter.DaysAdapter;
import com.dietmanager.app.adapter.FoodAdapter;
import com.dietmanager.app.adapter.RestaurantsAdapter;
import com.dietmanager.app.adapter.TimeCategoryAdapter;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.exception.ServerError;
import com.dietmanager.app.helper.ConnectionHelper;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.helper.SharedHelper;
import com.dietmanager.app.models.Address;
import com.dietmanager.app.models.Banner;
import com.dietmanager.app.models.Cuisine;
import com.dietmanager.app.models.Discover;
import com.dietmanager.app.models.Restaurant;
import com.dietmanager.app.models.RestaurantsData;
import com.dietmanager.app.models.Shop;
import com.dietmanager.app.models.food.FoodItem;
import com.dietmanager.app.models.food.FoodResponse;
import com.dietmanager.app.models.timecategory.TimeCategoryItem;
import com.dietmanager.app.utils.JavaUtils;
import com.dietmanager.app.utils.TextUtils;
import com.dietmanager.app.utils.Utils;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dietmanager.app.OyolaApplication.commonAccess;
import static com.dietmanager.app.helper.GlobalData.addressList;
import static com.dietmanager.app.helper.GlobalData.cuisineIdArrayList;
import static com.dietmanager.app.helper.GlobalData.cuisineList;
import static com.dietmanager.app.helper.GlobalData.isOfferApplied;
import static com.dietmanager.app.helper.GlobalData.isPureVegApplied;
import static com.dietmanager.app.helper.GlobalData.latitude;
import static com.dietmanager.app.helper.GlobalData.longitude;
import static com.dietmanager.app.helper.GlobalData.selectedAddress;


public class HomeDietFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        CuisineSelectFragment.OnSuccessListener, DaysAdapter.IDayListener,TimeCategoryAdapter.ITimeCategoryListener {

    @BindView(R.id.animation_line_image)
    ImageView animationLineImage;


    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    @BindView(R.id.dummy_navigate)
    TextView dummy_navigate;
    @BindView(R.id.days_layout)
    LinearLayout daysLayout;
    @BindView(R.id.food_layout)
    LinearLayout foodLayout;
    private SkeletonScreen skeletonScreen, skeletonText1,
            skeletonText2;
    private TextView addressLabel;
    private TextView addressTxt;
    private LinearLayout locationAddressLayout;
    private RelativeLayout errorLoadingLayout;

    private Button filterBtn;

    @BindView(R.id.days_rv)
    RecyclerView daysRv;
    @BindView(R.id.time_category_rv)
    RecyclerView timeCategoryRv;
    @BindView(R.id.food_rv)
    RecyclerView foodRv;

    int ADDRESS_SELECTION = 1;
    int FILTER_APPLIED_CHECK = 2;
    ImageView filterSelectionImage;
    ImageView favouriteSelectionImage;

    private ViewGroup toolbar;
    private View toolbarLayout;
    AnimatedVectorDrawableCompat avdProgress;
    public static ArrayList<Integer> cuisineSelectedList = null;

    String[] catagoery = {"Relevance", "Cost for Two", "Delivery Time", "Rating"};

    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

    public static boolean isFilterApplied = false;
    DaysAdapter daysAdapter;
    private TimeCategoryAdapter timeCategoryAdapter;
    private FoodAdapter foodAdapter;

    private int selectedDay = 1;
    private int selectedTimeCategory= 1;
    private String selectedTimeCategoryName = "breakfast";

    List<Integer> daysList;
    private List<TimeCategoryItem> timeCategoryList = new ArrayList<>();
    private List<FoodItem> foodItems = new ArrayList<>();
    ConnectionHelper connectionHelper;
    boolean mIsFromSignUp = false;
    CuisineSelectFragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragment = new CuisineSelectFragment();
        mFragment.setListener(this);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            errorLoadingLayout.setVisibility(View.GONE);
            locationAddressLayout.setVisibility(View.VISIBLE);
            if (selectedAddress != null && GlobalData.profileModel != null) {
                GlobalData.addressHeader = selectedAddress.getType();
                addressLabel.setText(selectedAddress.getType());
                addressTxt.setText((selectedAddress.getBuilding() != null ? selectedAddress.getBuilding() + ", " : "") + selectedAddress.getMapAddress());
                latitude = selectedAddress.getLatitude();
                longitude = selectedAddress.getLongitude();
                GlobalData.addressHeader = (selectedAddress.getBuilding() != null ? selectedAddress.getBuilding() + ", " : "") + selectedAddress.getMapAddress();
            } else if (addressList != null && addressList.getAddresses().size() != 0 && GlobalData.profileModel != null) {
                for (int i = 0; i < addressList.getAddresses().size(); i++) {
                    Address address1 = addressList.getAddresses().get(i);
                    if (getDoubleThreeDigits(latitude) == getDoubleThreeDigits(address1.getLatitude()) && getDoubleThreeDigits(longitude) == getDoubleThreeDigits(address1.getLongitude())) {
                        selectedAddress = address1;
                        addressLabel.setText(GlobalData.addressHeader);
                        addressTxt.setText(GlobalData.address);
                        addressLabel.setText(selectedAddress.getType());
                        addressTxt.setText((selectedAddress.getBuilding() != null ? selectedAddress.getBuilding() + ", " : "") + selectedAddress.getMapAddress());
                        latitude = selectedAddress.getLatitude();
                        longitude = selectedAddress.getLongitude();
                        break;
                    } else {
                        String address = getAddress(latitude, longitude);
                        if (address != null) {
                            addressLabel.setText(context.getString(R.string.home));
                            addressTxt.setText(address);
                        }
                        /*addressLabel.setText(GlobalData.addressHeader);
                        addressTxt.setText(GlobalData.address);*/
                    }
                }
            } else {
                addressLabel.setText(GlobalData.addressHeader);
                addressTxt.setText(GlobalData.address);
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_diet, container, false);
        ButterKnife.bind(this, view);
        mIsFromSignUp = getArguments().getBoolean("isFromSignUp");
        if (mIsFromSignUp) {
            mFragment.show(getFragmentManager(), "CuisineSelectFragment");
        } else {
//            new CuisineSelectFragment().show(getFragmentManager(), "CuisineSelectFragment");
        }
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("HomeFragment");
        commonAccess = "";
        connectionHelper = new ConnectionHelper(OyolaApplication.getAppInstance());
        toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        toolbarLayout = LayoutInflater.from(getActivity()).inflate(R.layout.toolbar_home, toolbar, false);
        addressLabel = toolbarLayout.findViewById(R.id.address_label);
        addressTxt = toolbarLayout.findViewById(R.id.address);
        locationAddressLayout = toolbarLayout.findViewById(R.id.location_ll);
        errorLoadingLayout = toolbarLayout.findViewById(R.id.error_loading_layout);
        locationAddressLayout.setVisibility(View.INVISIBLE);
        errorLoadingLayout.setVisibility(View.VISIBLE);
        toolbarLayout.setVisibility(View.GONE);

        daysList = new ArrayList<>();
        daysAdapter = new DaysAdapter(getActivity(), 0, this);
        daysRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        daysRv.setHasFixedSize(true);
        daysRv.setAdapter(daysAdapter);
        for (int i = 1; i <= 30; i++)
            daysList.add(i);
        daysAdapter.setList(daysList);
        skeletonText1 = Skeleton.bind(daysRv)
                .adapter(daysAdapter)
                .load(R.layout.skeleton_label)
                .count(2)
                .show();


        HomeActivity.updateNotificationCount(getActivity(), GlobalData.notificationCount);

        //Spinner
        //Creating the ArrayAdapter instance having the country shopList
        ArrayAdapter<String> aa = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, catagoery);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner




        // timeCategory Adapter
        timeCategoryAdapter = new TimeCategoryAdapter(getActivity(), 0, this);
        timeCategoryRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        timeCategoryRv.setHasFixedSize(true);
        timeCategoryRv.setAdapter(timeCategoryAdapter);
        skeletonText2 = Skeleton.bind(timeCategoryRv)
                .adapter(timeCategoryAdapter)
                .load(R.layout.skeleton_label)
                .count(2)
                .show();


        //food Adapter
        foodAdapter = new FoodAdapter(getActivity());
        foodRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        foodRv.setHasFixedSize(true);
        foodRv.setAdapter(foodAdapter);
        skeletonScreen = Skeleton.bind(foodRv)
                .adapter(foodAdapter)
                .load(R.layout.skeleton_impressive_list_item)
                .count(2)
                .show();




             locationAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalData.profileModel != null) {
                    startActivityForResult(new Intent(getActivity(), SetDeliveryLocationActivity.class).putExtra("get_address", true).putExtra("home_page", true), ADDRESS_SELECTION);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                } else {
                    Toast.makeText(getActivity(), "Please login", Toast.LENGTH_SHORT).show();
                }
            }
        });
        filterBtn = toolbarLayout.findViewById(R.id.filter);
        filterSelectionImage = toolbarLayout.findViewById(R.id.filter_selection_image);
        favouriteSelectionImage = toolbarLayout.findViewById(R.id.favourite_selection_image);

        if (SharedHelper.getKey(getActivity(), "logged") != null && SharedHelper.getKey(getActivity(), "logged").equalsIgnoreCase("true")) {
            favouriteSelectionImage.setVisibility(View.VISIBLE);
        } else {
            favouriteSelectionImage.setVisibility(View.GONE);
        }
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), FilterActivity.class), FILTER_APPLIED_CHECK);
                getActivity().overridePendingTransition(R.anim.slide_up, R.anim.anim_nothing);
            }
        });
        dummy_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SubscribePlanActivity.class));
            }
        });

        favouriteSelectionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.show(getFragmentManager(), "CuisineSelectFragment");
            }
        });
        toolbar.addView(toolbarLayout);
        //initialize image line
        initializeAvd();
        //Get cuisine values
        if (connectionHelper.isConnectingToInternet()) {
            getTimeCategory();
            getFood();
            skeletonScreen.show();
        } else {
            Utils.displayMessage(getActivity(), getActivity(), getString(R.string.oops_connect_your_internet));
        }
    }



    private void getTimeCategory() {

        Call<List<TimeCategoryItem>> call = apiInterface.getTimeCategory();
        call.enqueue(new Callback<List<TimeCategoryItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<TimeCategoryItem>> call, @NonNull Response<List<TimeCategoryItem>> response) {
                if (response.isSuccessful()) {
                    timeCategoryList.clear();
                    List<TimeCategoryItem> timeCategoryItemList = response.body();
                    if (timeCategoryItemList != null && !Utils.isNullOrEmpty(timeCategoryItemList)) {
                        selectedTimeCategory=timeCategoryItemList.get(0).getId();
                        selectedTimeCategoryName=timeCategoryItemList.get(0).getName();
                        timeCategoryList.addAll(timeCategoryItemList);
                        timeCategoryAdapter.setList(timeCategoryList);
                        daysAdapter.setList(daysList);
                        skeletonText1.hide();
                        skeletonText2.hide();
                        showOrHideView(false);
                    }
                } else {
                    Gson gson = new Gson();
                    try {
                      //  showOrHideView(false);
                        ServerError serverError = gson.fromJson(response.errorBody().charStream(), ServerError.class);
                        Utils.displayMessage(getActivity(), getActivity(), serverError.getError());
                        if (response.code() == 401) {
                            startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            getActivity().finish();
                        }
                    } catch (JsonSyntaxException e) {
                        Utils.displayMessage(getActivity(), getActivity(), getString(R.string.something_went_wrong));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<TimeCategoryItem>> call, Throwable t) {
                Utils.displayMessage(getActivity(), getActivity(), getString(R.string.something_went_wrong));
            }
        });
    }

    private void getFood() {
        Call<List<FoodItem>> call =apiInterface.getFood(selectedDay,selectedTimeCategory);
        call.enqueue(new Callback<List<FoodItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<FoodItem>> call, @NonNull Response<List<FoodItem>> response) {
                if (response.isSuccessful()) {
                    foodItems.clear();
                    List<FoodItem> ItemList = response.body();
                    if (ItemList != null && !Utils.isNullOrEmpty(ItemList)) {
                        foodItems.addAll(ItemList);
                        foodAdapter.setList(foodItems);
                        skeletonScreen.hide();
                        showOrHideView(true);
                    }else {
                        skeletonScreen.hide();
                        showOrHideView(false);
                    }
                } else {
                    Gson gson = new Gson();
                    try {
                        ServerError serverError = gson.fromJson(response.errorBody().charStream(), ServerError.class);
                        Utils.displayMessage(getActivity(), getActivity(), serverError.getError());
                        //    showOrHideView(false);
                        if (response.code() == 401) {
                            startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            getActivity().finish();
                        }
                    } catch (JsonSyntaxException e) {
                        Utils.displayMessage(getActivity(), getActivity(), getString(R.string.something_went_wrong));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<FoodItem>> call, Throwable t) {
                Utils.displayMessage(getActivity(), getActivity(), getString(R.string.something_went_wrong));
            }
        });

    }



    private void showOrHideView(boolean isVisible) {
        foodLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        errorLayout.setVisibility(!isVisible ? View.VISIBLE : View.GONE);
    }













    public double getDoubleThreeDigits(Double value) {
        return new BigDecimal(value.toString()).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }

    Runnable action = new Runnable() {
        @Override
        public void run() {
            avdProgress.stop();
            if (animationLineImage != null)
                animationLineImage.setVisibility(View.INVISIBLE);
        }
    };

    private void initializeAvd() {
        avdProgress = AnimatedVectorDrawableCompat.create(getActivity(), R.drawable.avd_line);
        animationLineImage.setBackground(avdProgress);
        repeatAnimation();
    }

    private void repeatAnimation() {
        avdProgress.start();
        animationLineImage.
                postDelayed(action, 3000); // Will repeat animation in every 1 second
    }

    @Override
    public void onResume() {
        super.onResume();
        errorLayout.setVisibility(View.GONE);
        HomeActivity.updateNotificationCount(getActivity(), GlobalData.notificationCount);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("location"));
       /* if (!GlobalData.addressHeader.equalsIgnoreCase("")) {
            errorLoadingLayout.setVisibility(View.GONE);
            locationAddressLayout.setVisibility(View.VISIBLE);
            if (selectedAddress != null && GlobalData.profileModel != null) {
                GlobalData.addressHeader = selectedAddress.getType();
                addressLabel.setText(selectedAddress.getType());
                addressTxt.setText((selectedAddress.getBuilding() != null ? selectedAddress.getBuilding() + ", " : "") + selectedAddress.getMapAddress());
                latitude = selectedAddress.getLatitude();
                longitude = selectedAddress.getLongitude();
                GlobalData.addressHeader = (selectedAddress.getBuilding() != null ? selectedAddress.getBuilding() + ", " : "") + selectedAddress.getMapAddress();
            } else if (addressList != null && addressList.getAddresses().size() != 0 && GlobalData.profileModel != null) {
                for (int i = 0; i < addressList.getAddresses().size(); i++) {
                    Address address1 = addressList.getAddresses().get(i);
                    if (getDoubleThreeDigits(latitude) == getDoubleThreeDigits(
                            address1.getLatitude()) && getDoubleThreeDigits(longitude)
                            == getDoubleThreeDigits(address1.getLongitude())) {
                        selectedAddress = address1;
                        addressLabel.setText(GlobalData.addressHeader);
                        addressTxt.setText(GlobalData.address);
                        addressLabel.setText(selectedAddress.getType());
                        addressTxt.setText((selectedAddress.getBuilding() != null ? selectedAddress.getBuilding() + ", " : "") + selectedAddress.getMapAddress());
                        latitude = selectedAddress.getLatitude();
                        longitude = selectedAddress.getLongitude();
                        break;
                    } else {
                        String address = getAddress(latitude, longitude);
                        if (address != null) {
                            addressLabel.setText(address);
                            addressTxt.setText(address);
                        } else {
                            addressLabel.setText(GlobalData.addressHeader);
                            addressTxt.setText(GlobalData.address);
                        }
                    }
                }
            } else {
                String address = getAddress(latitude, longitude);
                if (address != null) {
                    addressLabel.setText(getActivity().getString(R.string.home));
                    addressTxt.setText(address);
                } else {
                    addressLabel.setText(GlobalData.addressHeader);
                    addressTxt.setText(GlobalData.address);
                }
            }

        }*/
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && addresses.size() > 0) {
                android.location.Address address = addresses.get(0);
//            String addressLine = address.getAddressLine(0);
                String addressLine = addresses.get(0).getAddressLine(0);
/*                String add = obj.getAddressLine(0);
                add = add + "\n" + obj.getCountryName();
                add = add + "\n" + obj.getCountryCode();
                add = add + "\n" + obj.getAdminArea();
                add = add + "\n" + obj.getPostalCode();
                add = add + "\n" + obj.getSubAdminArea();
                add = add + "\n" + obj.getLocality();
                add = add + "\n" + obj.getSubThoroughfare();*/
                Log.v("seenu", "Address" + addressLine);
                return addressLine;
                // Toast.makeText(this, "Address=>" + add,
                // Toast.LENGTH_SHORT).show();
                // TennisAppActivity.showDialog(add);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
//            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (toolbar != null) {
            toolbar.removeView(toolbarLayout);
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_OK) {
            System.out.print("HomeFragment : Success");
            if (selectedAddress != null) {
                addressLabel.setText(GlobalData.addressHeader);
                addressTxt.setText(GlobalData.address);
                addressLabel.setText(selectedAddress.getType());
                addressTxt.setText((selectedAddress.getBuilding() != null ? selectedAddress.getBuilding() + ", " : "") + selectedAddress.getMapAddress());
                latitude = selectedAddress.getLatitude();
                longitude = selectedAddress.getLongitude();
                skeletonScreen.show();
                skeletonText1.show();
                skeletonText2.show();

            }
        } else if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_CANCELED) {
            System.out.print("HomeFragment : Failure");
        }
        if (requestCode == FILTER_APPLIED_CHECK && resultCode == Activity.RESULT_OK) {
            System.out.print("HomeFragment : Filter Success");
            skeletonScreen.show();
            skeletonText1.show();
            skeletonText2.show();


        } else if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_CANCELED) {
            System.out.print("HomeFragment : Filter Failure");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(getActivity(), catagoery[position], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void refreshHome() {
        skeletonScreen.show();
        skeletonText1.show();
        skeletonText2.show();

    }

    @Override
    public void onDayClicked(int day) {
        selectedDay = day;
    }

    @Override
    public void onCategoryClicked(int category, String categoryName) {
        selectedTimeCategory = category;
        selectedTimeCategoryName = categoryName;
        getFood();
        skeletonScreen.show();
    }
}