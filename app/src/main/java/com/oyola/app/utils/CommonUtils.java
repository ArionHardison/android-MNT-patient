package com.oyola.app.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.oyola.app.R;

public class CommonUtils {

    private static boolean isRatingNotEmpty(Double rating) {
        return rating != null && rating != 0.0;
    }

    public static String getRating(Double ratingValue) {
        if (isRatingNotEmpty(ratingValue)) {
            return String.valueOf(ratingValue.intValue());
        }
        return "5";
    }

    public static void showToast(Context context, String input) {
        Toast.makeText(context, input, Toast.LENGTH_SHORT).show();
    }

    public static AlertDialog sessionExpiredAlert(Context context,
                                                  DialogInterface.OnClickListener okClickListener,
                                                  DialogInterface.OnClickListener cancelClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.session_expired_alert_title);
        builder.setMessage(R.string.session_expired_alert_message);
        builder.setPositiveButton(R.string.session_expired_button_yes, okClickListener);
        builder.setNegativeButton(R.string.session_expired_button_no, cancelClickListener);
        return builder.create();
    }

}
