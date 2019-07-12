package com.pakupaku.user;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.pakupaku.user.utils.LocaleUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import com.facebook.stetho.Stetho;

/**
 * Created by santhosh@appoets.com on 28-08-2017.
 */

public class MyApplication extends Application {

    private static Context context;

    public static String commonAccess = "";
    public static String promoCodeSelect = "";
    public static String currency = "";

    public static Object getContext() {
        return MyApplication.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();

        Fabric.with(this, new Crashlytics());
        Stetho.initializeWithDefaults(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Nunito-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

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
//        super.attachBaseContext(LocaleUtils.onAttach(base, "en"));

        MultiDex.install(this);
    }

    //Number format not working N currency not found so changed text to decimal format --suresh
    public static String retrunTwoDecimal(double actualVaue) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(actualVaue);

    }

}
