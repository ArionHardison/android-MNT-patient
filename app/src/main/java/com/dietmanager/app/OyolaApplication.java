package com.dietmanager.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.dietmanager.app.helper.SharedHelper;
import com.dietmanager.app.utils.LocaleUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by santhosh@appoets.com on 28-08-2017.
 */

public class OyolaApplication extends Application {

    private static OyolaApplication appInstance;

    public static String commonAccess = "";
    public static String promoCodeSelect = "";
    public static String currency = "";
    private Gson gson;

    public static OyolaApplication getAppInstance(){
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        Stetho.initializeWithDefaults(this);
        gson = new GsonBuilder().serializeNulls().setLenient().create();
    }

    public Gson getGson() {
        return gson;
    }

    public void fetchDeviceToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
                instanceIdResult -> {
                    String newToken = instanceIdResult.getToken();
                    SharedHelper.putKey(getApplicationContext(), "device_token", "" + newToken);
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("FireBaseToken", "onFailure : " + e.toString());
            }
        });
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
        MultiDex.install(this);
    }

    //Number format not working N currency not found so changed text to decimal format --suresh
    public static String retrunTwoDecimal(double actualVaue) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(actualVaue);

    }

}
