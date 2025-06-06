package com.dietmanager.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dietmanager.app.HomeActivity;
import com.dietmanager.app.R;
import com.dietmanager.app.adapter.FoodsOrdersAdapter;
import com.dietmanager.app.adapter.OrdersAdapter;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.ConnectionHelper;
import com.dietmanager.app.helper.CustomDialog;
import com.dietmanager.app.models.FoodOrder;
import com.dietmanager.app.models.FoodOrderModel;
import com.dietmanager.app.models.Order;
import com.dietmanager.app.models.OrderModel;
import com.dietmanager.app.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dietmanager.app.helper.GlobalData.onGoingOrderList;
import static com.dietmanager.app.helper.GlobalData.pastOrderList;

public class OrdersActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.orders_rv)
    RecyclerView ordersRv;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;

    //private OrdersAdapter adapter;
    private FoodsOrdersAdapter adapter;
    Activity activity = OrdersActivity.this;
    private List<FoodOrderModel> modelListReference = new ArrayList<>();

    Context context;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    List<FoodOrderModel> modelList = new ArrayList<>();
    ConnectionHelper connectionHelper;
    CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        context = OrdersActivity.this;
        customDialog = new CustomDialog(context);
        ButterKnife.bind(this);
        connectionHelper = new ConnectionHelper(context);
        setSupportActionBar(toolbar);
        onGoingOrderList = new ArrayList<>();
        pastOrderList = new ArrayList<>();
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setPadding(16, 16, 16, 16);//for tab otherwise give space in tab
        toolbar.setContentInsetsAbsolute(0, 0);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        ordersRv.setLayoutManager(manager);
        modelListReference.clear();
//        adapter = new OrdersAdapter(this, activity, modelListReference);
        adapter = new FoodsOrdersAdapter(this, activity, modelListReference);
        ordersRv.setAdapter(adapter);
        ordersRv.setHasFixedSize(false);
    }

    /*private void getPastOrders() {
        Call<List<Order>> call = apiInterface.getPastOders();
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    pastOrderList.clear();
                    pastOrderList = response.body();
                    Collections.sort(pastOrderList, new Comparator<Order>() {
                        @Override
                        public int compare(Order lhs, Order rhs) {
                            return rhs.getCreatedAt().compareTo(lhs.getCreatedAt());
                        }
                    });
                    OrderModel model = new OrderModel();
                    model.setHeader(getString(R.string.past_orders));
                    model.setOrders(pastOrderList);
                    modelList.add(model);
                    modelListReference.clear();
                    modelListReference.addAll(modelList);
                    adapter.notifyDataSetChanged();
                    if (onGoingOrderList.size() == 0 && pastOrderList.size() == 0) {
                        errorLayout.setVisibility(View.VISIBLE);
                    } else
                        errorLayout.setVisibility(View.GONE);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }
                customDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(OrdersActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    /*private void getOngoingOrders() {
        Call<List<Order>> call = apiInterface.getOngoingOrders();
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    onGoingOrderList.clear();
                    modelListReference.clear();
                    onGoingOrderList = response.body();
                    Collections.sort(onGoingOrderList, new Comparator<Order>() {
                        @Override
                        public int compare(Order lhs, Order rhs) {
                            return rhs.getCreatedAt().compareTo(lhs.getCreatedAt());
                        }
                    });
                    modelList.clear();
                    OrderModel model = new OrderModel();
                    model.setHeader("Current Orders");
                    model.setOrders(onGoingOrderList);
                    modelList.add(model);
                    modelListReference.addAll(modelList);
                    getPastOrders();
                } else {
                    getPastOrders();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                Toast.makeText(OrdersActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                getPastOrders();
                customDialog.dismiss();
            }
        });
    }*/

    private void getOngoingFoodOrders() {
        Call<List<FoodOrder>> call = apiInterface.getOngoingFoodOrders();
        call.enqueue(new Callback<List<FoodOrder>>() {
            @Override
            public void onResponse(@NonNull Call<List<FoodOrder>> call, @NonNull Response<List<FoodOrder>> response) {
                if (response.isSuccessful()) {
                    onGoingOrderList.clear();
                    modelListReference.clear();
                    onGoingOrderList = response.body();
                    /*Collections.sort(onGoingOrderList, new Comparator<Order>() {
                        @Override
                        public int compare(Order lhs, Order rhs) {
                            return rhs.getCreatedAt().compareTo(lhs.getCreatedAt());
                        }
                    });*/
                    //Collections.sort(onGoingOrderList);
                    modelList.clear();
                    FoodOrderModel model = new FoodOrderModel();
                    model.setHeader("Current Orders");
                    model.setOrders(onGoingOrderList);
                    modelList.add(model);
                    modelListReference.addAll(modelList);
                    adapter.notifyDataSetChanged();
                    if (onGoingOrderList.size() == 0) {
                        errorLayout.setVisibility(View.VISIBLE);
                    } else
                        errorLayout.setVisibility(View.GONE);
                    customDialog.dismiss();
//                    getPastOrders();
                } else {
//                    getPastOrders();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                    customDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FoodOrder>> call, @NonNull Throwable t) {
                Toast.makeText(OrdersActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
//                getPastOrders();
                customDialog.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        modelList.clear();
        //Get Ongoing Order list
        if (connectionHelper.isConnectingToInternet()) {
            customDialog.show();
            //getOngoingOrders();
            getOngoingFoodOrders();
        } else {
            Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(OrdersActivity.this, HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
        finish();
    }

}