package com.dietmanager.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.google.android.material.snackbar.Snackbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.dietmanager.app.R;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.GlobalData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Tamil on 10/14/2017.
 */

public class Utils {
    public static boolean showLog = true;

    Retrofit retrofit;
    ApiInterface apiInterface;
    public static String address = "";

    public static void displayMessage(Activity activity, Context context, String toastString) {
        try {
            Snackbar.make(activity.getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    public static void print(String tag, String message) {
        if (showLog) {
            Log.v(tag, message);
        }
    }

    public static boolean isShopChanged(int shopId) {
        if (GlobalData.addCart != null && !GlobalData.addCart.getProductList().isEmpty()) {
            if (!GlobalData.addCart.getProductList().get(0).getProduct().getShopId().equals(shopId)) {
                return true;
            }
        }
        return false;

    }
    public static boolean isNullOrEmpty(List list) {
        return list == null || list.isEmpty();
    }
    public String getAddress(final Context context, final double latitude, final double longitude) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getResponse(latitude + "," + longitude,
                context.getResources().getString(R.string.google_maps_key));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("sUCESS", "SUCESS" + response.body());
                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.e("sUCESS", "bodyString" + bodyString);
                        JSONObject jsonObj = new JSONObject(bodyString);
                        JSONArray jsonArray = jsonObj.optJSONArray("results");
                        if (jsonArray.length() > 0) {
                            address = jsonArray.optJSONObject(0).optString("formatted_address");
                            Log.v("Formatted Address", "" + GlobalData.addressHeader);
                        } else {
                            address = "" + latitude + "" + longitude;

                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    address = "" + latitude + "" + longitude;
                }
                //BroadCast Listner
                Intent intent = new Intent("location");
                // You can also include some extra data.
                intent.putExtra("message", "This is my message!");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure", "onFailure" + call.request().url());
                address = "" + latitude + "" + longitude;

            }
        });
        return address;

    }

    public static String getNewNumberFormat(double d) {
        //  String text = Double.toString(Math.abs(d));
        String text = Double.toString(d);
        int integerPlaces = text.indexOf('.');
        int decimalPlaces = text.length() - integerPlaces - 1;
        if (decimalPlaces == 2) return text;
        else if (decimalPlaces == 1) return text + "0";
        else if (decimalPlaces == 0) return text + ".00";
        else if (decimalPlaces > 2) {
            String converted = String.valueOf((double) Math.round(d * 100) / 100);
            int convertedIntegers = converted.indexOf('.');
            int convertedDecimals = converted.length() - convertedIntegers - 1;
            if (convertedDecimals == 2) return converted;
            else if (convertedDecimals == 1) return converted + "0";
            else if (convertedDecimals == 0) return converted + ".00";
            else return converted;
        } else return text;
    }
    public static String getDayAndTimeFormat(Date dateObj) {
        String time = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(dateObj);
        SimpleDateFormat fmtOutFull =
                new SimpleDateFormat("d MMM yyyy, EEE", Locale.getDefault());
        return String.format("%s %s", fmtOutFull.format(dateObj.getTime()), time);

    }

}
