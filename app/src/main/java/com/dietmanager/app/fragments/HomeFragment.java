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

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.gson.Gson;
import com.dietmanager.app.HomeActivity;
import com.dietmanager.app.OyolaApplication;
import com.dietmanager.app.R;
import com.dietmanager.app.activities.FilterActivity;
import com.dietmanager.app.activities.SetDeliveryLocationActivity;
import com.dietmanager.app.adapter.BannerAdapter;
import com.dietmanager.app.adapter.DiscoverAdapter;
import com.dietmanager.app.adapter.FavouriteCuisinesAdapter;
import com.dietmanager.app.adapter.OfferRestaurantAdapter;
import com.dietmanager.app.adapter.RestaurantsAdapter;
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
import com.dietmanager.app.utils.JavaUtils;
import com.dietmanager.app.utils.TextUtils;
import com.dietmanager.app.utils.Utils;

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

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        CuisineSelectFragment.OnSuccessListener {

    @BindView(R.id.animation_line_image)
    ImageView animationLineImage;
    @BindView(R.id.catagoery_spinner)
    Spinner catagoerySpinner;
    @BindView(R.id.restaurant_count_txt)
    TextView restaurantCountTxt;
    @BindView(R.id.offer_title_header)
    TextView offerTitleHeader;
    @BindView(R.id.favourite_title_header)
    TextView favouriteTitle;
    @BindView(R.id.freedelivery_title_header)
    TextView freeDeliveryTitle;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    @BindView(R.id.impressive_dishes_layout)
    LinearLayout impressiveDishesLayout;
    @BindView(R.id.favourites_dishes_layout)
    LinearLayout favouriteDishesLayout;
    @BindView(R.id.free_delivery_layout)
    LinearLayout freeDeliveryLayout;
    @BindView(R.id.kitchen_for_you_layout)
    LinearLayout kitchenForYouLayout;
    private SkeletonScreen skeletonScreen, skeletonScreen2, skeletonText1, skeletonText2,
            skeletonSpinner, skeletonFavourites, skeletonFavouriteTitle, skeletonFreeDelivery, skeletonFreeDeliveryTitle;
    private TextView addressLabel;
    private TextView addressTxt;
    private LinearLayout locationAddressLayout;
    private RelativeLayout errorLoadingLayout;

    private Button filterBtn;

    @BindView(R.id.restaurants_offer_rv)
    RecyclerView restaurantsOfferRv;
    @BindView(R.id.impressive_dishes_rv)
    RecyclerView bannerRv;
    @BindView(R.id.restaurants_rv)
    RecyclerView restaurantsRv;
    @BindView(R.id.favourite_dishes_rv)
    RecyclerView favouritesRv;
    @BindView(R.id.freedelivery_dishes_rv)
    RecyclerView freeDeliveryRv;
    @BindView(R.id.discover_rv)
    RecyclerView discoverRv;
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
    List<Shop> restaurantList;
    List<Shop> favouriteRestaurantList;
    List<Shop> freeDeliveryRestaurantList;
    RestaurantsAdapter adapterRestaurant;
    FavouriteCuisinesAdapter mFavouritesAdapter;
    FavouriteCuisinesAdapter mFreeDeliveryAdapter;
    public static boolean isFilterApplied = false;
    BannerAdapter bannerAdapter;
    List<Banner> bannerList;
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
            findRestaurant();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
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
        toolbar.setVisibility(View.VISIBLE);
        toolbarLayout = LayoutInflater.from(getActivity()).inflate(R.layout.toolbar_home, toolbar, false);
        addressLabel = toolbarLayout.findViewById(R.id.address_label);
        addressTxt = toolbarLayout.findViewById(R.id.address);
        locationAddressLayout = toolbarLayout.findViewById(R.id.location_ll);
        errorLoadingLayout = toolbarLayout.findViewById(R.id.error_loading_layout);
        locationAddressLayout.setVisibility(View.INVISIBLE);
        errorLoadingLayout.setVisibility(View.VISIBLE);
        bannerList = new ArrayList<>();
        bannerAdapter = new BannerAdapter(bannerList, getActivity(), getActivity());
        bannerRv.setHasFixedSize(true);
        bannerRv.setItemViewCacheSize(20);
        bannerRv.setDrawingCacheEnabled(true);
        bannerRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        bannerRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        bannerRv.setItemAnimator(new DefaultItemAnimator());
        skeletonScreen2 = Skeleton.bind(bannerRv)
                .adapter(bannerAdapter)
                .load(R.layout.skeleton_impressive_list_item)
                .count(3)
                .show();
        skeletonText1 = Skeleton.bind(offerTitleHeader)
                .load(R.layout.skeleton_label)
                .show();
        skeletonFavouriteTitle = Skeleton.bind(favouriteTitle)
                .load(R.layout.skeleton_label)
                .show();
        skeletonFreeDeliveryTitle = Skeleton.bind(freeDeliveryTitle)
                .load(R.layout.skeleton_label)
                .show();
        skeletonText2 = Skeleton.bind(restaurantCountTxt)
                .load(R.layout.skeleton_label)
                .show();
        skeletonSpinner = Skeleton.bind(catagoerySpinner)
                .load(R.layout.skeleton_label)
                .show();
        HomeActivity.updateNotificationCount(getActivity(), GlobalData.notificationCount);

        //Spinner
        //Creating the ArrayAdapter instance having the country shopList
        ArrayAdapter<String> aa = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, catagoery);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        catagoerySpinner.setAdapter(aa);
        catagoerySpinner.setOnItemSelectedListener(this);

        //Restaurant Adapter
        restaurantsRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        restaurantsRv.setItemAnimator(new DefaultItemAnimator());
        restaurantsRv.setHasFixedSize(true);
        favouritesRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        favouritesRv.setItemAnimator(new DefaultItemAnimator());
        favouritesRv.setHasFixedSize(true);
        freeDeliveryRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        freeDeliveryRv.setItemAnimator(new DefaultItemAnimator());
        freeDeliveryRv.setHasFixedSize(true);
        restaurantList = new ArrayList<>();
        favouriteRestaurantList = new ArrayList<>();
        freeDeliveryRestaurantList = new ArrayList<>();
        adapterRestaurant = new RestaurantsAdapter(restaurantList, getActivity(), getActivity());
        mFavouritesAdapter = new FavouriteCuisinesAdapter(favouriteRestaurantList, getActivity(), getActivity());
        mFreeDeliveryAdapter = new FavouriteCuisinesAdapter(freeDeliveryRestaurantList, getActivity(), getActivity());
        skeletonScreen = Skeleton.bind(restaurantsRv)
                .adapter(adapterRestaurant)
                .load(R.layout.skeleton_restaurant_list_item)
                .count(2)
                .show();
        skeletonFavourites = Skeleton.bind(favouritesRv)
                .adapter(mFavouritesAdapter)
                .load(R.layout.skeleton_restaurant_list_item)
                .count(2)
                .show();
        skeletonFreeDelivery = Skeleton.bind(freeDeliveryRv)
                .adapter(mFreeDeliveryAdapter)
                .load(R.layout.skeleton_restaurant_list_item)
                .count(2)
                .show();

        final ArrayList<Restaurant> offerRestaurantList = new ArrayList<>();
        offerRestaurantList.add(new Restaurant("Madras Coffee House", "Cafe, South Indian", "", "3.8", "51 Mins", "$20", ""));
        offerRestaurantList.add(new Restaurant("Behrouz Biryani", "Biriyani", "", "3.7", "52 Mins", "$50", ""));
        offerRestaurantList.add(new Restaurant("SubWay", "American fast food", "Flat 20% offer on all orders", "4.3", "30 Mins", "$5", "Close soon"));
        offerRestaurantList.add(new Restaurant("Dominoz Pizza", "Pizza shop", "", "4.5", "25 Mins", "$5", ""));
        offerRestaurantList.add(new Restaurant("Pizza hut", "Cafe, Bakery", "", "4.1", "45 Mins", "$5", "Close soon"));
        offerRestaurantList.add(new Restaurant("McDonlad's", "Pizza Food, burger", "Flat 20% offer on all orders", "4.6", "20 Mins", "$5", ""));
        offerRestaurantList.add(new Restaurant("Chai Kings", "Cafe, Bakery", "", "3.3", "36 Mins", "$5", ""));
        offerRestaurantList.add(new Restaurant("sea sell", "Fish, Chicken, mutton", "Flat 30% offer on all orders", "4.3", "20 Mins", "$5", "Close soon"));
        //Offer Restaurant Adapter
        restaurantsOfferRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        restaurantsOfferRv.setItemAnimator(new DefaultItemAnimator());
        restaurantsOfferRv.setHasFixedSize(true);
        OfferRestaurantAdapter offerAdapter = new OfferRestaurantAdapter(offerRestaurantList, getActivity());
        restaurantsOfferRv.setAdapter(offerAdapter);


        // Discover
        final List<Discover> discoverList = new ArrayList<>();
        discoverList.add(new Discover("Trending now ", "22 options", "1"));
        discoverList.add(new Discover("Offers near you", "51 options", "1"));
        discoverList.add(new Discover("Whats special", "7 options", "1"));
        discoverList.add(new Discover("Pocket Friendly", "44 options", "1"));

        DiscoverAdapter adapterDiscover = new DiscoverAdapter(discoverList, getActivity());
        discoverRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        discoverRv.setItemAnimator(new DefaultItemAnimator());
        discoverRv.setAdapter(adapterDiscover);
        adapterDiscover.setOnItemClickListener(new DiscoverAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Discover obj = discoverList.get(position);
            }
        });

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
            getCuisines();
        } else {
            Utils.displayMessage(getActivity(), getActivity(), getString(R.string.oops_connect_your_internet));
        }
    }

    private void showFavouritesDialog() {

    }

    private void findRestaurant() {
        HashMap<String, String> map = new HashMap<>();
        map.put("latitude", String.valueOf(latitude));
        map.put("longitude", String.valueOf(longitude));
        //get User Profile Data
        if (GlobalData.profileModel != null) {
            map.put("user_id", String.valueOf(GlobalData.profileModel.getId()));
        }
        if (isFilterApplied) {
            filterSelectionImage.setVisibility(View.VISIBLE);
            map.put("filter", "normal");
            if (isOfferApplied)
                map.put("offer", "1");
            if (isPureVegApplied)
                map.put("pure_veg", "1");
            if (cuisineIdArrayList != null && cuisineIdArrayList.size() != 0) {
                for (int i = 0; i < cuisineIdArrayList.size(); i++) {
                    map.put("cuisine[" + "" + i + "]", cuisineIdArrayList.get(i).toString());
                }
            }
        } else {
            filterSelectionImage.setVisibility(View.GONE);
        }
        if (connectionHelper.isConnectingToInternet()) {
            getRestaurant(map);
        } else {
            Utils.displayMessage(getActivity(), getActivity(), getString(R.string.oops_connect_your_internet));
        }
    }

    private void getRestaurant(HashMap<String, String> map) {
        Call<RestaurantsData> call = apiInterface.getshops(map);
        call.enqueue(new Callback<RestaurantsData>() {
            @Override
            public void onResponse(Call<RestaurantsData> call, Response<RestaurantsData> response) {
                skeletonScreen.hide();
                skeletonFavourites.hide();
                skeletonFreeDelivery.hide();
                skeletonScreen2.hide();
                skeletonText1.hide();
                skeletonFavouriteTitle.hide();
                skeletonFreeDeliveryTitle.hide();
                skeletonText2.hide();
                skeletonSpinner.hide();
                if (!response.isSuccessful()) {
                    ServerError serverError = new Gson().fromJson(response.errorBody().charStream(), ServerError.class);
                    String message = serverError != null ? serverError.getError() : null;
                    Toast.makeText(getActivity(), !TextUtils.isEmpty(message) ? message : getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                    showOrHideView(false);
                } else {
                    if (isAdded() && isVisible() && getUserVisibleHint()) {
                        RestaurantsData data = response.body();
                        List<Banner> bannerDataList = data.getBanners();
                        List<Shop> shopList = data.getShops();
                        List<Shop> favCuisineList = data.getFavouriteCuisines();
                        List<Shop> freeDeliveryList = data.getFreeDeliveryShops();

                        if (JavaUtils.isNullOrEmpty(bannerDataList) && JavaUtils.isNullOrEmpty(shopList) &&
                                JavaUtils.isNullOrEmpty(favCuisineList) && JavaUtils.isNullOrEmpty(freeDeliveryList)) {
                            showOrHideView(false);
                        } else {
                            showOrHideKitchenForYouView(shopList);
                            showOrHideOffersView(bannerDataList);
                            showOrHideFavouriteView(favCuisineList);
                            showOrHideFreeDeliveryView(freeDeliveryList);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RestaurantsData> call, Throwable throwable) {
                String message = throwable != null && !TextUtils.isEmpty(throwable.getMessage()) ? throwable.getMessage() : getString(R.string.something_went_wrong);
                showOrHideView(false);
//                CommonUtils.showToast(getActivity(), message);
            }
        });
    }

    private void showOrHideView(boolean isVisible) {
        impressiveDishesLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        favouriteDishesLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        freeDeliveryLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        kitchenForYouLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        errorLayout.setVisibility(!isVisible ? View.VISIBLE : View.GONE);
    }

    private void showOrHideOffersView(List<Banner> bannerDataList) {
        if (JavaUtils.isNullOrEmpty(bannerDataList) || isFilterApplied) {
            impressiveDishesLayout.setVisibility(View.GONE);
            if (isFilterApplied)
                errorLayout.setVisibility(View.VISIBLE);
            else
                errorLayout.setVisibility(View.GONE);
        } else {
            impressiveDishesLayout.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            bannerList.clear();
            bannerList.addAll(bannerDataList);
            sortBannersToDescending(bannerList);
            bannerAdapter.notifyDataSetChanged();
        }
    }

    private void showOrHideFavouriteView(List<Shop> favouriteList) {
        if (JavaUtils.isNullOrEmpty(favouriteList)) {
            favouriteDishesLayout.setVisibility(View.GONE);
        } else {
            favouriteDishesLayout.setVisibility(View.VISIBLE);
            favouriteRestaurantList.clear();
            favouriteRestaurantList.addAll(favouriteList);
            sortOrdersToDescending(favouriteRestaurantList);
            mFavouritesAdapter.notifyDataSetChanged();
        }
    }

    private void showOrHideKitchenForYouView(List<Shop> restaurantDataList) {
        if (JavaUtils.isNullOrEmpty(restaurantDataList)) {
            kitchenForYouLayout.setVisibility(View.GONE);
        } else {
            kitchenForYouLayout.setVisibility(View.VISIBLE);
            restaurantList.clear();
            restaurantList.addAll(restaurantDataList);
            sortOrdersToDescending(restaurantList);
            GlobalData.shopList = restaurantList;
            adapterRestaurant.notifyDataSetChanged();
        }
    }

    private void showOrHideFreeDeliveryView(List<Shop> freeDeliveryList) {
        if (JavaUtils.isNullOrEmpty(freeDeliveryList)) {
            freeDeliveryLayout.setVisibility(View.GONE);
        } else {
            freeDeliveryLayout.setVisibility(View.VISIBLE);
            freeDeliveryRestaurantList.clear();
            freeDeliveryRestaurantList.addAll(freeDeliveryList);
            sortOrdersToDescending(freeDeliveryRestaurantList);
            mFreeDeliveryAdapter.notifyDataSetChanged();
        }
    }

    private void sortOrdersToDescending(List<Shop> orderList) {
        Collections.sort(orderList, (lhs, rhs) -> {
            if (rhs.getCreatedAtDate() == null || lhs.getCreatedAtDate() == null) {
                return 0;
            }
            return rhs.getCreatedAtDate().compareTo(lhs.getCreatedAtDate());
        });
    }

    private void sortBannersToDescending(List<Banner> bannerList) {
        Collections.sort(bannerList, (lhs, rhs) -> {
            if (rhs.getShop() == null || lhs.getShop() == null || rhs.getShop().getCreatedAtDate() == null || lhs.getShop().getCreatedAtDate() == null) {
                return 0;
            }
            return rhs.getShop().getCreatedAtDate().compareTo(lhs.getShop().getCreatedAtDate());
        });
    }

    public void getCuisines() {
        Call<List<Cuisine>> call = apiInterface.getcuCuisineCall();
        call.enqueue(new Callback<List<Cuisine>>() {
            @Override
            public void onResponse(Call<List<Cuisine>> call, Response<List<Cuisine>> response) {
                if (!response.isSuccessful() && response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity(), jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    cuisineList = new ArrayList<>();
                    cuisineList.addAll(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Cuisine>> call, Throwable t) {

            }
        });
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
        if (!GlobalData.addressHeader.equalsIgnoreCase("")) {
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
            findRestaurant();
        }
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
                skeletonFavourites.show();
                skeletonFreeDelivery.show();
                skeletonScreen2.show();
                skeletonText1.show();
                skeletonFavouriteTitle.show();
                skeletonFreeDeliveryTitle.show();
                skeletonText2.show();
                skeletonSpinner.show();
                findRestaurant();
            }
        } else if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_CANCELED) {
            System.out.print("HomeFragment : Failure");
        }
        if (requestCode == FILTER_APPLIED_CHECK && resultCode == Activity.RESULT_OK) {
            System.out.print("HomeFragment : Filter Success");
            skeletonScreen.show();
            skeletonFavourites.show();
            skeletonFreeDelivery.show();
            skeletonScreen2.show();
            skeletonText1.show();
            skeletonFavouriteTitle.show();
            skeletonFreeDeliveryTitle.show();
            skeletonText2.show();
            skeletonSpinner.show();
            findRestaurant();
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
        skeletonFavourites.show();
        skeletonFreeDelivery.show();
        skeletonScreen2.show();
        skeletonText1.show();
        skeletonFavouriteTitle.show();
        skeletonFreeDeliveryTitle.show();
        skeletonText2.show();
        skeletonSpinner.show();
        findRestaurant();
    }
}