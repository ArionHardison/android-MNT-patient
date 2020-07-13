package com.oyola.app.utils;

import android.content.Context;
import android.widget.Toast;

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

}
