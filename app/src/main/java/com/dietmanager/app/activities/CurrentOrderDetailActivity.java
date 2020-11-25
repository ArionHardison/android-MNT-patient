package com.dietmanager.app.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dietmanager.app.models.FoodOrder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.dietmanager.app.HomeActivity;
import com.dietmanager.app.R;
import com.dietmanager.app.adapter.OrderFlowAdapter;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.fragments.OrderViewFragment;
import com.dietmanager.app.helper.CustomDialog;
import com.dietmanager.app.helper.DataParser;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.Message;
import com.dietmanager.app.models.NotificationData;
import com.dietmanager.app.models.Order;
import com.dietmanager.app.models.OrderFlow;
import com.dietmanager.app.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dietmanager.app.build.configure.BuildConfigure.BASE_URL;
import static com.dietmanager.app.helper.GlobalData.ORDER_STATUS;
import static com.dietmanager.app.helper.GlobalData.isSelectedFoodOrder;
import static com.dietmanager.app.helper.GlobalData.isSelectedOrder;

public class CurrentOrderDetailActivity extends BaseActivity implements OnMapReadyCallback, LocationListener,
        GoogleMap.OnMarkerDragListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnCameraMoveListener {

    @BindView(R.id.order_id_txt)
    TextView orderIdTxt;
    @BindView(R.id.order_item_txt)
    TextView orderItemTxt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.order_otp)
    TextView orderOtp;
    /*@BindView(R.id.order_eta)
    TextView order_eta;*/
    /*@BindView(R.id.transporter_image)
    CircleImageView transporter_image;*/
    /*@BindView(R.id.transporter_call)
    ImageView transporter_call;*/
    /*@BindView(R.id.transporter_details)
    LinearLayout transporter_details;
    @BindView(R.id.transporter_name)
    TextView transporterName;*/
    /*@BindView(R.id.call_transporter)
    Button callTransporter;*/
    @BindView(R.id.order_status_txt)
    TextView orderStatusTxt;
    @BindView(R.id.order_succeess_image)
    ImageView order_succeess_image;
    @BindView(R.id.order_status_layout)
    RelativeLayout orderStatusLayout;
    @BindView(R.id.order_id_txt_2)
    TextView orderIdTxt2;
    @BindView(R.id.order_placed_time)
    TextView orderPlacedTime;
    /*@BindView(R.id.tvShopAddress)
    TextView tvShopAddress;*/

    Fragment orderFullViewFragment;
    FragmentManager fragmentManager;
    double priceAmount = 0;
    int itemQuantity = 1;
    String currency = "";
    @BindView(R.id.order_flow_rv)
    RecyclerView orderFlowRv;
    int rating = 5;
    public static TextView orderCancelTxt;
    public static Location prevLoc;
    Context context;
    Intent orderIntent;
    OrderFlowAdapter adapter;
    boolean isOrderPage = false;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    @BindView(R.id.map_touch_rel)
    RelativeLayout mapTouchRel;
    @BindView(R.id.transparent_image)
    ImageView transparentImage;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    Handler handler;
    Runnable orderStatusRunnable;
    String previousStatus = "";
    CustomDialog customDialog;
    GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private Marker sourceMarker;
    private Marker destinationMarker;
    private Marker providerMarker;
    private LatLng sourceLatLng;
    private LatLng destLatLng;

    String TransporterNumber = "";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private Location currentLocation;
    boolean isImageOpened = true;
    boolean isRateOpened = true;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order_detail2);
        ButterKnife.bind(this);
        context = CurrentOrderDetailActivity.this;

        isOrderPage = getIntent().getBooleanExtra("is_order_page", false);
        final NotificationData customdata = (NotificationData) getIntent().getSerializableExtra("customdata");
        final int orderId = getIntent().getIntExtra("orderId", -1);

        //set Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        toolbar.setContentInsetsAbsolute(0, 0);
        orderCancelTxt = findViewById(R.id.order_cancel);
        orderCancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        /*callTransporter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCall();
            }
        });*/

        /*transporter_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCall();
            }
        });*/

        handler = new Handler();
        orderStatusRunnable = new Runnable() {
            public void run() {
                if (customdata != null) {
                    getParticularOrders(customdata.getCustomData().get(0).getOrderId());
                } else if (orderId != -1) {
                    getParticularOrders(orderId);
                } else {
                    if (isSelectedFoodOrder!=null) {
                    getParticularOrders(isSelectedFoodOrder.getId());
                    }
                }
                handler.postDelayed(this, 5000);
            }
        };

        transparentImage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        // Disallow ScrollView to intercept touch events.
                        nestedScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;
                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        nestedScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;
                    default:
                        return true;
                }
            }
        });

        if (GlobalData.isSelectedFoodOrder != null) {
            updateOrderDetail();
            FoodOrder order = GlobalData.isSelectedFoodOrder;
            orderIdTxt.setText("ORDER #000" + order.getId().toString());
//            itemQuantity = order.getInvoice().getQuantity();
            priceAmount = order.getPayable();
            currency = GlobalData.currencySymbol;
            if (itemQuantity == 1)
                orderItemTxt.setText(String.valueOf(itemQuantity) + " Item, " + currency + String.valueOf(priceAmount));
            else
                orderItemTxt.setText(String.valueOf(itemQuantity) + " Items, " + currency + String.valueOf(priceAmount));

            orderIdTxt2.setText("#000" + order.getId().toString());
            //orderOtp.setText(" : " + isSelectedFoodOrder.getOrderOtp());
            orderPlacedTime.setText(getTimeFromString(order.getCreatedAt()));

            //set Fragment
            orderFullViewFragment = new OrderViewFragment();
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.order_detail_fargment, orderFullViewFragment).commit();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                } else {
                    //Request Location Permission
                }
            } else {
                buildGoogleApiClient();
            }
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    private void goToCall() {
        if (TransporterNumber != null && !TransporterNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + TransporterNumber));
            startActivity(intent);
        } else {
            Toast.makeText(context, R.string.no_number_available, Toast.LENGTH_LONG).show();
        }
    }

    public void updateOrderDetail() {
        List<OrderFlow> orderFlowList = new ArrayList<>();
        if (GlobalData.isSelectedFoodOrder != null) {
                orderFlowList.add(new OrderFlow(getString(R.string.pickup_order_received), getString(R.string.pickup_description_1), R.drawable.ic_order_placed, ORDER_STATUS.get(0)));
                orderFlowList.add(new OrderFlow(getString(R.string.pickup_order_accepted), getString(R.string.pickup_description_2), R.drawable.ic_order_confirmed, ORDER_STATUS.get(1)));
                orderFlowList.add(new OrderFlow(getString(R.string.order_accepted_arriving), getString(R.string.arrived_description_2), R.drawable.ic_order_confirmed, ORDER_STATUS.get(5)));
                orderFlowList.add(new OrderFlow(getString(R.string.order_accepted_arrived), getString(R.string.arrived_description_3), R.drawable.ic_order_processed, ORDER_STATUS.get(6) /*+ORDER_STATUS.get(2) + ORDER_STATUS.get(3) + ORDER_STATUS.get(4) + ORDER_STATUS.get(7) + ORDER_STATUS.get(8)*/));
                orderFlowList.add(new OrderFlow(getString(R.string.pickup_order_processed_new), getString(R.string.pickup_description_3), R.drawable.ic_order_processed, ORDER_STATUS.get(3) /*+ORDER_STATUS.get(2) + ORDER_STATUS.get(3) + ORDER_STATUS.get(4) + ORDER_STATUS.get(7) + ORDER_STATUS.get(8)*/));
                orderFlowList.add(new OrderFlow(getString(R.string.pickup_order_processed), getString(R.string.prepared_description_4), R.drawable.ic_order_picked_up, ORDER_STATUS.get(13) /*+ ORDER_STATUS.get(6) + ORDER_STATUS.get(9)*/));
                orderFlowList.add(new OrderFlow(getString(R.string.pickup_order_completed), getString(R.string.pickup_description_5), R.drawable.ic_order_delivered, ORDER_STATUS.get(7) + ORDER_STATUS.get(10)));

        /*if (GlobalData.isSelectedFoodOrder.getStatus().equalsIgnoreCase("PREPARED")){
            if (isImageOpened){
                ((CurrentOrderDetailActivity) context).showImage();
                isImageOpened = false;
            }
        }*/

        /*if (GlobalData.isSelectedFoodOrder.getStatus().equalsIgnoreCase("COMPLETED")){
            if (isRateOpened){
                ((CurrentOrderDetailActivity) context).rate();
                isRateOpened = false;
            }
        }*/

        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        orderFlowRv.setLayoutManager(manager);
        adapter = new OrderFlowAdapter(orderFlowList, this);
        orderFlowRv.setAdapter(adapter);
        orderFlowRv.setHasFixedSize(false);
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(this, R.anim.item_animation_slide_right);
        orderFlowRv.setLayoutAnimation(controller);
        orderFlowRv.scheduleLayoutAnimation();

        if (GlobalData.isSelectedOrder != null) {
//            updateOrderDeatail();
            Order order = GlobalData.isSelectedOrder;
            orderIdTxt.setText(getResources().getString(R.string.order_details_page) + " #000" + order.getId().toString());
            itemQuantity = order.getItems().size();
            priceAmount = order.getInvoice().getPayable();
            currency = order.getItems().get(0).getProduct().getPrices().getCurrency();
            if (itemQuantity == 1)
                orderItemTxt.setText(itemQuantity + " " + getResources().getString(R.string.item_count) + " , " + currency + priceAmount);
            else
                orderItemTxt.setText(itemQuantity + " " + getResources().getString(R.string.items_counts) + " , " + currency + priceAmount);

            orderIdTxt2.setText("#000" + order.getId().toString());
            if (isSelectedOrder.getOrderOtp() != null)
                orderOtp.setText(getString(R.string.otp) + " : " + isSelectedOrder.getOrderOtp());
            orderPlacedTime.setText(getTimeFromString(order.getCreatedAt()));
            /*if (order.getPickUpRestaurant() == 1) {
                tvShopAddress.setVisibility(View.VISIBLE);
                tvShopAddress.setText((order.getShop().getMapsAddress() != null ? order.getShop().getMapsAddress() : ""));
            } else {
                tvShopAddress.setVisibility(View.GONE);
            }*/

            //set Fragment
            orderFullViewFragment = new OrderViewFragment();
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.order_detail_fargment, orderFullViewFragment).commit();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                }
            } else {
                buildGoogleApiClient();
            }
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    private void updateOrder(HashMap<String, String> map) {
        System.out.println(map.toString());
        Call<Order> call = apiInterface.updateOrder(isSelectedFoodOrder.getId(),map);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                if (response.errorBody() != null) {
                    //finish();
                } else if (response.isSuccessful()) {
                    //Message message = response.body();
                    Toast.makeText(context, "Approved", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(context, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    //finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                Toast.makeText(context, "Something wrong - updateOrder", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context, OrdersActivity.class));
                finish();
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
//        if (isSelectedOrder.getPickUpRestaurant() == 1) {
            /*if (currentLocation != null) {
                //Map
                String url = getUrl(currentLocation.getLatitude(), currentLocation.getLongitude()
                        , isSelectedOrder.getShop().getLatitude(), isSelectedOrder.getShop().getLongitude());
                FetchUrl fetchUrl = new FetchUrl();
                fetchUrl.execute(url);
            } else {*/
                /*destLatLng = new LatLng(isSelectedOrder.getShop().getLatitude(), isSelectedOrder.getShop().getLongitude());
                if (destinationMarker != null)
                    destinationMarker.remove();
                MarkerOptions destMarker = new MarkerOptions()
                        .position(destLatLng).title("Destination").draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker));
                destinationMarker = mMap.addMarker(destMarker);
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(destLatLng, 14);
                mMap.moveCamera(cu);*/

                if (isSelectedFoodOrder.getCustomerAddress()!=null) {
                    destLatLng = new LatLng(isSelectedFoodOrder.getCustomerAddress().getLatitude(), isSelectedFoodOrder.getCustomerAddress().getLongitude());
                    if (destinationMarker != null)
                        destinationMarker.remove();
                    MarkerOptions destMarker = new MarkerOptions()
                            .position(destLatLng).title("Home").draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_marker));
                    destinationMarker = mMap.addMarker(destMarker);
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(destLatLng, 14);
                    mMap.moveCamera(cu);
                }

//
    }

    @Override
    public void onCameraMove() {
        nestedScrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            context, R.raw.style_json));
            if (!success) {
                Log.i("Map:Style", "Style parsing failed.");
            } else {
                Log.i("Map:Style", "Style Applied.");
            }
        } catch (Resources.NotFoundException e) {
            Log.i("Map:Style", "Can't find style. Error: ");
        }
        mMap = googleMap;
        setupMap();
    }

    void setupMap() {
        if (mMap != null) {
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setBuildingsEnabled(true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerDragListener(this);
            mMap.setOnCameraMoveListener(this);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);

            /*if (isSelectedOrder.getPickUpRestaurant() == 0) {
                if (isSelectedOrder.getAddress() != null) {
                    //Map
                    String url = getUrl(isSelectedOrder.getAddress().getLatitude(), isSelectedOrder.getAddress().getLongitude()
                            , isSelectedOrder.getShop().getLatitude(), isSelectedOrder.getShop().getLongitude());
                    FetchUrl fetchUrl = new FetchUrl();
                    fetchUrl.execute(url);
                }
            } else {*/
                /*if (currentLocation != null) {
                    //Map
                    String url = getUrl(currentLocation.getLatitude(), currentLocation.getLongitude()
                            , isSelectedOrder.getShop().getLatitude(), isSelectedOrder.getShop().getLongitude());
                    FetchUrl fetchUrl = new FetchUrl();
                    fetchUrl.execute(url);
                } else {*/
            //if (currentLocation != null) {

            if (isSelectedFoodOrder.getChef()!=null) {
                if (isSelectedFoodOrder.getCustomerAddress() != null) {
                    String url = getUrl(isSelectedFoodOrder.getCustomerAddress().getLatitude(), isSelectedFoodOrder.getCustomerAddress().getLongitude()
                            , isSelectedFoodOrder.getChef().getLatitude(), isSelectedFoodOrder.getChef().getLongitude());
                    FetchUrl fetchUrl = new FetchUrl();
                    fetchUrl.execute(url);
                } else {
                    if (currentLocation != null) {
                        String url = getUrl(currentLocation.getLatitude(), currentLocation.getLongitude()
                                , isSelectedFoodOrder.getChef().getLatitude(), isSelectedFoodOrder.getChef().getLongitude());
                        FetchUrl fetchUrl = new FetchUrl();
                        fetchUrl.execute(url);
                    }
                }
            }

            if (isSelectedFoodOrder.getCustomerAddress() != null) {
                //Map
                    destLatLng = new LatLng(isSelectedFoodOrder.getCustomerAddress().getLatitude(), isSelectedFoodOrder.getCustomerAddress().getLongitude());
                    if (destinationMarker != null)
                        destinationMarker.remove();
                    MarkerOptions destMarker = new MarkerOptions()
                            .position(destLatLng).title("Destination").draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker));
                    destinationMarker = mMap.addMarker(destMarker);
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(destLatLng, 14);
                    mMap.moveCamera(cu);
                }
//            }
        }
    }

    public void showImage() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            final FrameLayout frameView = new FrameLayout(this);
            builder.setView(frameView);

            final AlertDialog alertDialog = builder.create();
            LayoutInflater inflater = alertDialog.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.image_popup, frameView);
            alertDialog.show();

            ImageView feedbackImage = dialogView.findViewById(R.id.cooked_image);
            if (isSelectedFoodOrder != null) {
                Glide.with(context)
                        .load(BASE_URL +isSelectedFoodOrder.getPreparedImage())
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.ic_banner)
                                .error(R.drawable.ic_banner))
                        .into(feedbackImage);
            }
            Button feedbackSubmit = dialogView.findViewById(R.id.feedback_confirm);
            feedbackSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if (GlobalData.isSelectedOrder != null && GlobalData.isSelectedOrder.getId() != null) {
                    if (isSelectedFoodOrder != null && GlobalData.isSelectedFoodOrder.getId() != null) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("_method", "PUT");
                        map.put("status", "COMPLETED");
                        updateOrder(map);
                        alertDialog.dismiss();
                    }
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result);
                if (!jsonObj.optString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                    ParserTask parserTask = new ParserTask();
                    // Invokes the thread for parsing the JSON data
                    parserTask.execute(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data);
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0]);
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());
            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            if (result != null) {
                // Traversing through all the routes
                if (result.size() > 0) {
                    for (int i = 0; i < result.size(); i++) {
                        points = new ArrayList<>();
                        lineOptions = new PolylineOptions();

                        // Fetching i-th route
                        List<HashMap<String, String>> path = result.get(i);

                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);
                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
                            points.add(position);
                        }
                        if (isSelectedFoodOrder.getCustomerAddress() != null) {
                            LatLng location = new LatLng(isSelectedFoodOrder.getCustomerAddress().getLatitude(), isSelectedFoodOrder.getCustomerAddress().getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(location).title("Home").draggable(true)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_marker));
                            sourceMarker = mMap.addMarker(markerOptions);

                            destLatLng = new LatLng(isSelectedFoodOrder.getChef().getLatitude(), isSelectedFoodOrder.getChef().getLongitude());
                            if (destinationMarker != null)
                                destinationMarker.remove();
                            MarkerOptions destMarker = new MarkerOptions()
                                    .position(destLatLng).title("Chef").draggable(true)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker));
                            destinationMarker = mMap.addMarker(destMarker);
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(sourceMarker.getPosition());
                            builder.include(destinationMarker.getPosition());
                            LatLngBounds bounds = builder.build();
                            final int width = getResources().getDisplayMetrics().widthPixels;
                            final int height = getResources().getDisplayMetrics().heightPixels;
                            final int padding = (int) (width * 0.20); // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 120);
                            mMap.moveCamera(cu);
                            // Adding all the points in the route to LineOptions
                            lineOptions.addAll(points);
                            lineOptions.width(5);
                            lineOptions.color(Color.BLACK);
                            Log.d("onPostExecute", "onPostExecute lineoptions decoded");
                        } else {
                            LatLng location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(location).title("Source").draggable(true)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_marker));
                            sourceMarker = mMap.addMarker(markerOptions);

                            destLatLng = new LatLng(isSelectedFoodOrder.getChef().getLatitude(), isSelectedFoodOrder.getChef().getLongitude());
                            if (destinationMarker != null)
                                destinationMarker.remove();
                            MarkerOptions destMarker = new MarkerOptions()
                                    .position(destLatLng).title("Chef").draggable(true)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker));
                            destinationMarker = mMap.addMarker(destMarker);
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(sourceMarker.getPosition());
                            builder.include(destinationMarker.getPosition());
                            LatLngBounds bounds = builder.build();
                            final int width = getResources().getDisplayMetrics().widthPixels;
                            final int height = getResources().getDisplayMetrics().heightPixels;
                            final int padding = (int) (width * 0.20); // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 120);
                            mMap.moveCamera(cu);
                            // Adding all the points in the route to LineOptions
                            lineOptions.addAll(points);
                            lineOptions.width(5);
                            lineOptions.color(Color.BLACK);
                            Log.d("onPostExecute", "onPostExecute lineoptions decoded");
                        }
                    }
                } else {
                    mMap.clear();
                }
            }
            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                List<PatternItem> pattern = Arrays.asList(new Dash(30), new Gap(20));
                mMap.addPolyline(lineOptions).setPattern(pattern);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    private String getUrl(double source_latitude, double source_longitude, double dest_latitude, double dest_longitude) {
        // Origin of route
        String str_origin = "origin=" + source_latitude + "," + source_longitude;

        // Destination of route
        String str_dest = "destination=" + dest_latitude + "," + dest_longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
    }

    private void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.order_cancel_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = dialogView.findViewById(R.id.reason_edit);
        dialogBuilder.setTitle(orderIdTxt.getText().toString());
        dialogBuilder.setMessage(getResources().getString(R.string.alert_areyou_cancel_order));
        dialogBuilder.setPositiveButton(R.string.submit, null);
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edt.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(context, R.string.pleasse_enter_reason, Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            cancelOrder(edt.getText().toString());
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    private void cancelOrder(String reason) {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        Call<Order> call = apiInterface.cancelOrder(isSelectedOrder.getId(), reason);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                if (response.isSuccessful()) {
                    clearNotification();
                    onBackPressed();
                } else {
                    customDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(CurrentOrderDetailActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isOrderPage) {
            finish();
        } else {
            startActivity(new Intent(CurrentOrderDetailActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
        }
    }

    private void getParticularOrders(int order_id) {
        Call<List<FoodOrder>> call = apiInterface.getParticularOrders(order_id);
        call.enqueue(new Callback<List<FoodOrder>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<List<FoodOrder>> call, @NonNull Response<List<FoodOrder>> response) {
                if (response.isSuccessful()) {
                    if (GlobalData.isSelectedFoodOrder == null) {
                        isSelectedFoodOrder = response.body().get(0);
                        updateOrderDetail();
                    } else isSelectedFoodOrder = response.body().get(0);

                    if (GlobalData.isSelectedFoodOrder.getStatus().equalsIgnoreCase("PREPARED")){
                        if (isImageOpened){
                            ((CurrentOrderDetailActivity) context).showImage();
                            isImageOpened = false;
                        }
                    }

                    if (GlobalData.isSelectedFoodOrder.getStatus().equalsIgnoreCase("COMPLETED")){
                        if (GlobalData.isSelectedFoodOrder.getRating().isEmpty())
                        if (isRateOpened){
                            ((CurrentOrderDetailActivity) context).rate();
                            isRateOpened = false;
                        }
                    }

                    Log.i("isSelectedFoodOrder : ", isSelectedFoodOrder.toString());

//                    order_eta.setText(" : " + isSelectedOrder.getEta() + getResources().getString(R.string.order_minits));

                    /*if (isSelectedOrder.getStatus().equalsIgnoreCase("received")) {
                        order_eta.setText(getString(R.string.eta) + " : " + isSelectedOrder.getOrderReadyTime() + " " + getResources().getString(R.string.order_minits));
                    } else {
                        order_eta.setText(getString(R.string.eta) + " : " + isSelectedOrder.getShop().getEstimatedDeliveryTime() + " " + getResources().getString(R.string.order_minits));
                    }*/

                    /*if (isSelectedOrder.getEta() != null) {
                        order_eta.setText(getString(R.string.eta) + " : " + isSelectedOrder.getEta() + " " + getResources().getString(R.string.order_minits));
                    } else {
                        if (isSelectedOrder.getStatus().equalsIgnoreCase("received")) {
                            order_eta.setText(getString(R.string.eta) + " : " + isSelectedOrder.getOrderReadyTime() + " " + getResources().getString(R.string.order_minits));
                        } else {
                            order_eta.setText(getString(R.string.eta) + " : " + isSelectedOrder.getShop().getEstimatedDeliveryTime() + " " + getResources().getString(R.string.order_minits));
                        }
                    }*/

                    /*if (isSelectedOrder.getTransporter() != null && context != null) {
                        transporter_details.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(isSelectedOrder.getTransporter().getAvatar())
                                .apply(new RequestOptions()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .placeholder(R.drawable.man)
                                        .error(R.drawable.man))
                                .into(transporter_image);
                        transporterName.setText(isSelectedOrder.getTransporter().getName());
                        TransporterNumber = isSelectedOrder.getTransporter().getPhone();
                    } else {
                        transporter_details.setVisibility(View.GONE);
                    }*/
                    if (isSelectedFoodOrder.getStatus().equals("PICKEDUP") ||
                            isSelectedFoodOrder.getStatus().equals("ARRIVED") || isSelectedFoodOrder.getStatus().equals("ASSIGNED")) {
                        liveNavigation(isSelectedFoodOrder.getChef().getLatitude(),
                                isSelectedFoodOrder.getChef().getLongitude());
                    }
                    if (!isSelectedFoodOrder.getStatus().equalsIgnoreCase(previousStatus)) {
                        previousStatus = isSelectedFoodOrder.getStatus();
                        adapter.notifyDataSetChanged();
                    }
                    if (isSelectedFoodOrder.getStatus().equals("CANCELLED")) {
                        orderStatusLayout.setVisibility(View.VISIBLE);
                        orderFlowRv.setVisibility(View.GONE);
                        orderStatusTxt.setText(getResources().getString(R.string.order_cancelled));
                        order_succeess_image.setImageResource(R.drawable.order_cancelled_img);
//                        dotLineImg.setBackgroundResource(R.drawable.order_cancelled_line);
                        orderStatusTxt.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                    } else {
                        orderStatusLayout.setVisibility(View.GONE);
                        orderFlowRv.setVisibility(View.VISIBLE);
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (!jObjError.optString("error").equals(""))
                            Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                        if (!jObjError.optString("message").equals(""))
                            Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FoodOrder>> call, @NonNull Throwable t) {

            }
        });
    }

    public void liveNavigation(Double lat, Double lng) {
        Log.e("Livenavigation", "ProLat" + lat + " ProLng" + lng);
        if (lat != null && lng != null) {
            Location targetLocation = new Location("providerlocation");//provider name is unnecessary
            targetLocation.setLatitude(lat);//your coords of course
            targetLocation.setLongitude(lng);
            float rotation = 0.0f;
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .rotation(rotation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver_marker));
            if (providerMarker != null) {
                animateMarker(targetLocation, providerMarker);
            } else {
                try {
                    providerMarker = mMap.addMarker(markerOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //car Motion Animation
    public static void animateMarker(final Location destination, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());
            final float startRotation = marker.getRotation();
            if (prevLoc == null) {
                prevLoc = destination;
            }
            final float bearing = prevLoc.bearingTo(destination);

            prevLoc = destination;

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1000); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
//                        marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));
                        marker.setRotation(bearing);
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });
            valueAnimator.start();
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    private interface LatLngInterpolator {

        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }

    public float getBearing(LatLng oldPosition, LatLng newPosition) {
        double deltaLongitude = newPosition.longitude - oldPosition.longitude;
        double deltaLatitude = newPosition.latitude - oldPosition.latitude;
        double angle = (Math.PI * .5f) - Math.atan(deltaLatitude / deltaLongitude);

        if (deltaLongitude > 0) {
            return (float) angle;
        } else if (deltaLongitude < 0) {
            return (float) (angle + Math.PI);
        } else if (deltaLatitude < 0) {
            return (float) Math.PI;
        }

        return 0.0f;
    }

    private String getTimeFromString(String time) {
        System.out.println("Time : " + time);
        String value = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

            if (time != null) {
                Date date = df.parse(time);
                value = sdf.format(date);
            }

        } catch (ParseException e) {
            e.printStackTrace();

        }
        return value;
    }

    private void rateTransporter(int Id,HashMap<String, String> map) {
        System.out.println(map.toString());
        Call<Message> call = apiInterface.chefRate(Id,map);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.errorBody() != null) {
                    finish();
                } else if (response.isSuccessful()) {
                    Message message = response.body();
                    Toast.makeText(context, "Rating submitted successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Toast.makeText(context, "Something wrong - rateTransporter", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context, OrdersActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goToCall();
                } else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void rate() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            final FrameLayout frameView = new FrameLayout(this);
            builder.setView(frameView);

            final AlertDialog alertDialog = builder.create();
            LayoutInflater inflater = alertDialog.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.feedback_popup, frameView);
            alertDialog.show();

            final RadioGroup rateRadioGroup = dialogView.findViewById(R.id.rate_radiogroup);
            ((RadioButton) rateRadioGroup.getChildAt(4)).setChecked(true);
            rateRadioGroup.setOnCheckedChangeListener(null);
            rateRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (i == R.id.one) {
                        //do work when radioButton1 is active
                        rating = 1;
                    } else if (i == R.id.two) {
                        //do work when radioButton2 is active
                        rating = 2;
                    } else if (i == R.id.three) {
                        //do work when radioButton3 is active
                        rating = 3;
                    } else if (i == R.id.four) {
                        //do work when radioButton3 is active
                        rating = 4;
                    } else if (i == R.id.five) {
                        //do work when radioButton3 is active
                        rating = 5;
                    }
                    Log.d("gfgfgf", "onCheckedChanged: " + rating);
                }
            });
            final EditText comment = dialogView.findViewById(R.id.comment);
            Button feedbackSubmit = dialogView.findViewById(R.id.feedback_submit);
            feedbackSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if (GlobalData.isSelectedOrder != null && GlobalData.isSelectedOrder.getId() != null) {
                    if (isSelectedFoodOrder != null && GlobalData.isSelectedFoodOrder.getId() != null) {
                        HashMap<String, String> map = new HashMap<>();
                        //map.put("order_id", String.valueOf(GlobalData.isSelectedFoodOrder.getId()));
                        map.put("rating", String.valueOf(rating));
                        map.put("comment", comment.getText().toString());
                        //map.put("type", Constants.SHOP_RATING);
                        rateTransporter(GlobalData.isSelectedFoodOrder.getId(),map);
                        alertDialog.dismiss();
                    }
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(orderStatusRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(orderStatusRunnable, 500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(orderStatusRunnable);
    }
}