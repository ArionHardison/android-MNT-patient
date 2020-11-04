package com.dietmanager.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dietmanager.app.R;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.ConnectionHelper;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.Cuisine;
import com.dietmanager.app.models.SubscriptionList;
import com.dietmanager.app.models.User;
import com.dietmanager.app.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dietmanager.app.helper.GlobalData.cuisineList;
import static com.dietmanager.app.helper.GlobalData.subscriptionList;

public class SubscribePlanActivity extends AppCompatActivity {

    ConnectionHelper connectionHelper;
    Activity activity;
    Context context;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_plan);
        ButterKnife.bind(this);
        ((TextView)findViewById(R.id.toolbar).findViewById(R.id.title)).setText(getString(R.string.subscribe));
        context = SubscribePlanActivity.this;
        activity = SubscribePlanActivity.this;
        connectionHelper = new ConnectionHelper(context);
        if (connectionHelper.isConnectingToInternet()) {
            getsubscription();
        } else {
            Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
        }
        findViewById(R.id.subscribe_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubscribePlanActivity.this, MenuActivity.class));
            }
        });
        findViewById(R.id.toolbar).findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                    subscriptionList = new ArrayList<>();
                    subscriptionList.addAll(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<SubscriptionList>> call, Throwable t) {

            }
        });
    }
}
