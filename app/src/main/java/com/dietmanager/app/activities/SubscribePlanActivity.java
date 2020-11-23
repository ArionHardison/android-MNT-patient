package com.dietmanager.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dietmanager.app.HomeActivity;
import com.dietmanager.app.R;
import com.dietmanager.app.adapter.SubscribtionListAdapter;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.ConnectionHelper;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.helper.SharedHelper;
import com.dietmanager.app.models.AddCart;
import com.dietmanager.app.models.AddressList;
import com.dietmanager.app.models.Dietitian;
import com.dietmanager.app.models.Otp;
import com.dietmanager.app.models.SubscriptionList;
import com.dietmanager.app.models.User;
import com.dietmanager.app.utils.Utils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dietmanager.app.build.configure.BuildConfigure.BASE_URL;
import static com.dietmanager.app.fragments.HomeDietFragment.isFilterApplied;


public class SubscribePlanActivity extends AppCompatActivity implements SubscribtionListAdapter.OnSelectedListener {

    ConnectionHelper connectionHelper;
    Activity activity;
    Context context;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    @BindView(R.id.rv_subscribtion)
    RecyclerView rvsubscribtion;
    SubscribtionListAdapter SubscribtionAdapter;
    private List<SubscriptionList> SubscribtionList = new ArrayList<>();
    Dietitian dietitian;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_city)
    TextView tv_city;
    @BindView(R.id.diet_img)
    ImageView diet_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_plan);
        ButterKnife.bind(this);
        ((TextView)findViewById(R.id.toolbar).findViewById(R.id.title)).setText(getString(R.string.subscribe));
        context = SubscribePlanActivity.this;
        activity = SubscribePlanActivity.this;
        connectionHelper = new ConnectionHelper(context);


        // Subscribtion Adapter
        SubscribtionAdapter = new SubscribtionListAdapter(SubscribtionList,this);
        rvsubscribtion.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvsubscribtion.setAdapter(SubscribtionAdapter);

        //



        findViewById(R.id.subscribe_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (dietitian!=null)
                   postsubscribe();

            }
        });
        findViewById(R.id.toolbar).findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                alertDialog();
            }
        });

        if (connectionHelper.isConnectingToInternet()) {
            getsubscription();
        } else {
            Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
        }
    }
    public void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getResources().getString(R.string.alert_logout))
                .setPositiveButton(getResources().getString(R.string.logout), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        logout();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(context, R.color.theme));
        nbutton.setTypeface(nbutton.getTypeface(), Typeface.BOLD);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(context, R.color.theme));
        pbutton.setTypeface(pbutton.getTypeface(), Typeface.BOLD);
    }
    protected void logout() {
        if (SharedHelper.getKey(context, "login_by").equals("facebook"))
            LoginManager.getInstance().logOut();
        if (SharedHelper.getKey(context, "login_by").equals("google"))
            signOutFromGoogle();
        SharedHelper.clearSharedPreferences(context);
        SharedHelper.putKey(context, "logged", "false");
        isFilterApplied = false;
        startActivity(new Intent(context, WelcomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        GlobalData.profileModel = null;
        GlobalData.addCart = null;
        GlobalData.address = "";
        GlobalData.selectedAddress = null;
        GlobalData.notificationCount = 0;
        activity.finish();
    }
    private void postsubscribe() {
        HashMap<String, String> map = new HashMap<>();
        map.put("plan_id", String.valueOf(SubscribtionListAdapter.selectedposition));
        map.put("dietitian_id", String.valueOf(dietitian.getId()));
        Call<Otp> postsubscribe = apiInterface.postsubscribe(map);
        postsubscribe.enqueue(new Callback<Otp>() {
            @Override
            public void onResponse(@NonNull Call<Otp> call, @NonNull Response<Otp> response) {
                if (response.isSuccessful()) {
                    Otp data=response.body();
                    Toast.makeText(context, data.getMessagenew(), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SubscribePlanActivity.this, SplashActivity.class));
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(context, "UnAuthenticated", Toast.LENGTH_LONG).show();
                        SharedHelper.putKey(context, "logged", "false");
                        startActivity(new Intent(context, MobileNumberActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("Message"))
                        Toast.makeText(context, jObjError.optString("Message"), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<Otp> call, @NonNull Throwable t) {

            }
        });
    }
    private void getsubscription() {
        Call<List<SubscriptionList>> call = apiInterface.getsubscription();
        call.enqueue(new Callback<List<SubscriptionList>>() {
            @Override
            public void onResponse(Call<List<SubscriptionList>> call, Response<List<SubscriptionList>> response) {
                if (!response.isSuccessful() && response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(activity, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(activity, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    List<SubscriptionList> SubscriptionList = response.body();
                    if (SubscriptionList != null && !Utils.isNullOrEmpty(SubscriptionList)) {
                        dietitian = SubscriptionList.get(0).getDietitian();
                        init();
                        SubscribtionList.clear();
                        SubscribtionList.addAll(SubscriptionList);
                        SubscribtionAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SubscriptionList>> call, Throwable t) {
                Toast.makeText(activity, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init() {
        tv_name.setText(dietitian.getName());
        tv_city.setText(dietitian.getEmail());
        if (dietitian.getAvatar()!=null)
        Glide.with(context)
                .load(BASE_URL+dietitian.getAvatar())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.man)
                        .error(R.drawable.man))
                .into(diet_img);
    }

    @Override
    public void onSelected(SubscriptionList data) {

    }

    private void signOutFromGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //taken from google api console (Web api client id)
//                .requestIdToken("795253286119-p5b084skjnl7sll3s24ha310iotin5k4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
//                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d("MainAct", "Google User Logged out");
                               /* Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();*/
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("MAin", "Google API Client Connection Suspended");
            }
        });
    }
}
