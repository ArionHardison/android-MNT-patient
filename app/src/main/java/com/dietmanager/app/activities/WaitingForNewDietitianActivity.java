package com.dietmanager.app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dietmanager.app.HomeActivity;
import com.dietmanager.app.OyolaApplication;
import com.dietmanager.app.R;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.CustomDialog;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.helper.SharedHelper;
import com.dietmanager.app.models.Dietitian_;
import com.dietmanager.app.models.Message;
import com.dietmanager.app.models.User;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dietmanager.app.helper.GlobalData.profileModel;

public class WaitingForNewDietitianActivity extends AppCompatActivity {
    ScheduledExecutorService scheduler;
private CustomDialog customDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_new_dietitian);
        ButterKnife.bind(this);
        customDialog=new CustomDialog(this);
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
                    GlobalData.currencySymbol = response.body().getCurrency();
                    if(response.body().getInvites()==1){
                        if (!isNewIncomingRequestShowing) {
                            newIncomingDietitianPopup(response.body().getDietitian());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
            }
        });
    }

    private void newIncomingDietitianPopup(Dietitian_ dietitian_) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(WaitingForNewDietitianActivity.this);
            final FrameLayout frameView = new FrameLayout(WaitingForNewDietitianActivity.this);
            builder.setView(frameView);
            final AlertDialog incomingDialog = builder.create();
            incomingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            incomingDialog.setCancelable(false);
            LayoutInflater inflater = incomingDialog.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.new_dietitian_request_popup, frameView);
            ((TextView) dialogView.findViewById(R.id.tvName)).setText(dietitian_.getDietitian().getName());
            ((TextView) dialogView.findViewById(R.id.tvAddress)).setText(dietitian_.getDietitian().getAddress());
            Button acceptBtn = dialogView.findViewById(R.id.accept_btn);
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    incomingDialog.dismiss();
                    acceptOrRejectDietitian(true,1);
                }
            });
            Button reject_btn = dialogView.findViewById(R.id.reject_btn);
            reject_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    incomingDialog.dismiss();
                    acceptOrRejectDietitian(false,0);
                }
            });
            incomingDialog.show();
            isNewIncomingRequestShowing =true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void acceptOrRejectDietitian(boolean isAccept, int id) {
        customDialog.show();
        Call<Message> call = apiInterface.acceptOrRejectDietitian(id);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                customDialog.dismiss();
                isNewIncomingRequestShowing=false;
                if (response.isSuccessful()) {
                    if(isAccept){
                        Intent intent = new Intent(WaitingForNewDietitianActivity.this, SubscribePlanActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                    else {
                        Toast.makeText(WaitingForNewDietitianActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(WaitingForNewDietitianActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean isNewIncomingRequestShowing =false;
}
