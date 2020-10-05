package com.dietmanager.app.utils;

import com.dietmanager.app.BuildConfig;

public class Constants {

    public static final int RC_PLACES_AUTOCOMPLETE_ACTIVITY = 300;

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    private static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;

    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT = PACKAGE_NAME + ".RESULT";
    public static final String LATITUDE = PACKAGE_NAME + ".LATITUDE";
    public static final String LONGITUDE = PACKAGE_NAME + ".LONGITUDE";
    public static final String ADDRESS = PACKAGE_NAME + ".ADDRESS";
    public static final String ENABLE_PLACES_AUTOCOMPLETE = PACKAGE_NAME + ".ENABLE_PLACES_AUTOCOMPLETE";
    public static final String SHOP_RATING = "shop";
}
