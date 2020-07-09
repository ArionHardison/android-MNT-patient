package com.oyola.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oyola.app.R;
import com.oyola.app.adapter.WalletHistoryAdapter;
import com.oyola.app.build.api.ApiClient;
import com.oyola.app.build.api.ApiInterface;
import com.oyola.app.helper.CustomDialog;
import com.oyola.app.helper.GlobalData;
import com.oyola.app.helper.SharedHelper;
import com.oyola.app.models.AddCart;
import com.oyola.app.models.AddressList;
import com.oyola.app.models.User;
import com.oyola.app.models.WalletHistory;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.oyola.app.helper.GlobalData.addCart;
import static com.oyola.app.helper.GlobalData.currencySymbol;

public class WalletActivity extends AppCompatActivity {

    String TAG = "WalletActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.add_btn)
    Button addBtn;
    @BindView(R.id.wallet_amount_txt)
    TextView walletAmountTxt;
    @BindView(R.id.wallet_history_recycler_view)
    RecyclerView walletHistoryRecyclerView;
    String device_token, device_UDID;


    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    Context context = WalletActivity.this;
    CustomDialog customDialog;

    List<WalletHistory> walletHistoryHistoryList;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;

    NumberFormat numberFormat = GlobalData.getNumberFormat();
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;

    WalletHistoryAdapter walletHistoryAdapter;
    String walletMoney = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ButterKnife.bind(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        customDialog = new CustomDialog(context);


        title.setText(context.getResources().getString(R.string.walletHistory));
        walletHistoryHistoryList = new ArrayList<>();
        walletHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        walletHistoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        walletHistoryRecyclerView.setHasFixedSize(true);
        walletHistoryAdapter = new WalletHistoryAdapter(walletHistoryHistoryList);
        walletHistoryRecyclerView.setAdapter(walletHistoryAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*String walletMoney = GlobalData.profileModel.getWalletBalance();
        walletAmountTxt.setText(currencySymbol + " " + String.valueOf(walletMoney));*/

        getDeviceToken();
        getWalletHistory();
        getProfile();
    }

    public void getDeviceToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Log.d(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "" + FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(context, "device_token", "" + FirebaseInstanceId.getInstance().getToken());
                Log.d(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Log.d(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }

    private void getProfile() {
//        retryCount++;

        HashMap<String, String> map = new HashMap<>();
        map.put("device_type", "android");
        map.put("device_id", device_UDID);
        map.put("device_token", device_token);
        Call<User> getprofile = apiInterface.getProfile(map);
        getprofile.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    SharedHelper.putKey(context, "logged", "true");
                    GlobalData.profileModel = response.body();
                    addCart = new AddCart();
                    addCart.setProductList(response.body().getCart());
                    GlobalData.addressList = new AddressList();
                    GlobalData.addressList.setAddresses(response.body().getAddresses());
                    if (addCart.getProductList() != null && addCart.getProductList().size() != 0)
                        GlobalData.addCartShopId = addCart.getProductList().get(0).getProduct().getShopId();

                    if (GlobalData.profileModel.getWalletBalance() != null) {
                        walletMoney = GlobalData.profileModel.getWalletBalance();
                    }
                    walletAmountTxt.setText(currencySymbol + " " + String.valueOf(walletMoney));

//                    checkActivty();

                } else {
                    if (response.code() == 401) {
                        Toast.makeText(context, "UnAuthenticated", Toast.LENGTH_LONG).show();
                        SharedHelper.putKey(context, "logged", "false");
                        startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().toString());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }


            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {

            }
        });
    }

    private void getWalletHistory() {
        customDialog.show();
        Call<List<WalletHistory>> call = apiInterface.getWalletHistory();
        call.enqueue(new Callback<List<WalletHistory>>() {
            @Override
            public void onResponse(@NonNull Call<List<WalletHistory>> call, @NonNull Response<List<WalletHistory>> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        walletHistoryHistoryList.addAll(response.body());
                        walletHistoryRecyclerView.getAdapter().notifyDataSetChanged();
                        errorLayout.setVisibility(View.GONE);
                    } else {
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WalletHistory>> call, @NonNull Throwable t) {
                customDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, AccountPaymentActivity.class).putExtra("is_show_wallet", true).putExtra("is_show_cash", false));
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }

    @OnClick(R.id.add_btn)
    public void onViewClicked() {
        startActivity(new Intent(this, AddMoneyActivity.class));
        finish();
    }

    @OnClick(R.id.back)
    public void onBackClicked() {
        onBackPressed();
    }

}
