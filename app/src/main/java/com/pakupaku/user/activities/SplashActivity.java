package com.pakupaku.user.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pakupaku.user.BuildConfig;
import com.pakupaku.user.HomeActivity;
import com.pakupaku.user.R;
import com.pakupaku.user.SmsRetrive.AppSignatureHelper;
import com.pakupaku.user.build.api.ApiClient;
import com.pakupaku.user.build.api.ApiInterface;
import com.pakupaku.user.helper.ConnectionHelper;
import com.pakupaku.user.helper.GlobalData;
import com.pakupaku.user.helper.SharedHelper;
import com.pakupaku.user.models.AddCart;
import com.pakupaku.user.models.AddressList;
import com.pakupaku.user.models.User;
import com.pakupaku.user.utils.Utils;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.pakupaku.user.helper.GlobalData.addCart;
import static com.pakupaku.user.helper.GlobalData.profileModel;

public class SplashActivity extends AppCompatActivity {

    int retryCount = 0;
    Context context;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    ConnectionHelper connectionHelper;
    String device_token, device_UDID;
    Utils utils = new Utils();
    String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        context = SplashActivity.this;
        connectionHelper = new ConnectionHelper(context);
        getDeviceToken();
        // OTP REtriver
        List<String> list = new AppSignatureHelper(this).getAppSignatures();
        Log.d(TAG, "HASH " + list.toString());
        GlobalData.hashcode = list.get(0);
        /*SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully started retriever");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                Log.d(TAG, " started retriever failed");
            }
        });*/

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 3000ms
                if (SharedHelper.getKey(context, "logged").equalsIgnoreCase("true") && SharedHelper.getKey(context, "logged") != null) {
                    GlobalData.accessToken = SharedHelper.getKey(context, "access_token");
                    if (connectionHelper.isConnectingToInternet()) {
                        getDeviceToken();
                        getProfile();
                    }
                    else displayMessage(getString(R.string.oops_connect_your_internet));
                } else {

                    startActivity(new Intent(SplashActivity.this, WelcomeScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }

            }
        }, 3000);

        getHashKey();
    }

    private void getHashKey() {
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    BuildConfig.APPLICATION_ID,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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
        retryCount++;

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
                    GlobalData.currencySymbol = profileModel.getCurrency();
                    GlobalData.currency = profileModel.getCurrency_code();
                    GlobalData.terms = profileModel.getTerms();
                    GlobalData.privacy = profileModel.getPrivacy();

                    addCart = new AddCart();
                    addCart.setProductList(response.body().getCart());
                    GlobalData.addressList = new AddressList();
                    GlobalData.addressList.setAddresses(response.body().getAddresses());
                    if (addCart.getProductList() != null && addCart.getProductList().size() != 0)
                        GlobalData.addCartShopId = addCart.getProductList().get(0).getProduct().getShopId();
                    checkActivty();

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
                if (retryCount < 5) {
                    getProfile();
                }

            }
        });
    }

    private void checkActivty() {

        if (getIntent().getSerializableExtra("customdata") != null &&
                getIntent().getStringExtra("order_staus").equalsIgnoreCase("ongoing")) {

            startActivity(new Intent(SplashActivity.this, CurrentOrderDetailActivity.class)
                    .putExtra("customdata", getIntent().getSerializableExtra("customdata")));
            finish();
        } else if (getIntent().getStringExtra("order_staus") != null && getIntent().getStringExtra("order_staus").equalsIgnoreCase("dispute")) {
            startActivity(new Intent(SplashActivity.this, OrdersActivity.class)
                    .putExtra("customdata", getIntent().getSerializableExtra("customdata")));
            finish();
        } else {
            startActivity(new Intent(context, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }

    }

    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}