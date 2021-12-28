package com.example.neighbormaterebuild.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.neighbormaterebuild.network.HttpHeaders;

public class FileDeviceID {

    private static final String TAG = "FileDeviceID";

    private SharedPreferences pref;

    public FileDeviceID(Context context) {

        pref = PreferenceManager
                .getDefaultSharedPreferences(context);
    }

    public void writeDeviceID() {

        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = pref.edit();
        editor.putString("API-KEY-NEIGHBORMATE-LIVE-MS", HttpHeaders.getInstance().getDeviceID());
        editor.apply();
    }

    public String readDeviceID() {

        String deviceId = pref.getString("API-KEY-NEIGHBORMATE-LIVE-MS", null);
//        Log.e(TAG, " ttt" + deviceId);
        return deviceId;
    }
}
