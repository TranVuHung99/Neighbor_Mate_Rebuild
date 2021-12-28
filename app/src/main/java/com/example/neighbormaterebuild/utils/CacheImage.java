package com.example.neighbormaterebuild.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheImage {
    public static String create(Context context, String urlImage) {
//        Log.e("AVATAR URL ORIGIN", urlImage + "");
        if (urlImage == null || urlImage.isEmpty()) return null;

        String cacheVariable = "?time=";
        SharedPreferences prefs = context.getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        String url = urlImage;
        if (url.contains(cacheVariable)) {
            String[] arraySplit = urlImage.split("\\?time=");
            prefsEdit.putString(arraySplit[0], arraySplit[1]);
            prefsEdit.apply();
            //Log.e("AVATAR URL", arraySplit[1]);
        } else {
            String cacheValue = prefs.getString(urlImage, null);
            if (cacheValue != null) {
                urlImage = urlImage + cacheVariable + cacheValue;
            }else {
//                long now = new Date().getTime();
//                urlImage = urlImage + cacheVariable + now;
            }
        }
//        Log.e("AVATAR URL", urlImage + "");
        return urlImage;
    }
}
