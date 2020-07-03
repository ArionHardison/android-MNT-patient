package com.oyola.app.utils;

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

}
