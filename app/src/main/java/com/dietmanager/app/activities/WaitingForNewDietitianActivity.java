package com.dietmanager.app.activities;

import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dietmanager.app.OyolaApplication;
import com.dietmanager.app.R;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.SharedHelper;
import com.dietmanager.app.models.User;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaitingForNewDietitianActivity extends AppCompatActivity {
    ScheduledExecutorService scheduler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_new_dietitian);
        ButterKnife.bind(this);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                getProfileAPI();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void onDestroy() {
        super.onDestroy();
        scheduler.shutdown();
    }
    private ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

    public void getProfileAPI() {
        String device_id = Settings.Secure.getString(OyolaApplication.getAppInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
        String device_type = "Android";
        String device_token = SharedHelper.getKey(OyolaApplication.getAppInstance(), "device_token");
        HashMap<String, String> params = new HashMap<>();
        params.put("device_id", device_id);
        params.put("device_type", device_type);
        params.put("device_token", device_token);
        Call<User> call = apiInterface.getProfile(params);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    if(response.body().getInvites()==1){
                        if (!isNewIncomingRequestShowing) {
                            isNewIncomingRequestShowing =true;
                            newIncomingDietitianPopup();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
            }
        });
    }

    private void newIncomingDietitianPopup() {
    }

    private boolean isNewIncomingRequestShowing =false;
}
