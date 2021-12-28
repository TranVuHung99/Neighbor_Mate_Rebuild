package com.example.neighbormaterebuild.network;

import static java.lang.System.currentTimeMillis;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import com.example.neighbormaterebuild.config.Config;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Random;

public class HttpHeaders {
    private static final String TAG = "HttpHeaders";
    private static HttpHeaders instance;

    private HttpHeaders() {

    }

    public static HttpHeaders getInstance() {
        if (instance == null) {
            synchronized (HttpHeaders.class) {
                if (null == instance) {
                    instance = new HttpHeaders();
                }
            }
        }
        return instance;
    }

    private String deviceID;
    private String appVersion;
    private String pushToken;
    private String carrierName;
    private String carrierCode;

    private static String appName;

    public void init(Context context) {
        if (deviceID == null) {
            String androidID = "";
            try {
                androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

                Log.e("Android ID :: ", androidID);

            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }
            deviceID = androidID;
            if (deviceID == null || deviceID.isEmpty()) {
                Random r = new Random();
                char c = (char) (r.nextInt(26) + 'a');
                deviceID = c + "" + currentTimeMillis() + "";
            }
        }

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
            appVersion = packageInfo.versionName;
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (manager != null) {
                carrierName =  Base64.encodeToString(manager.getSimOperatorName().getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
                carrierCode = manager.getSimOperator();
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public HashMap<String, String> headers() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("X-DEVICE-ID", deviceID);
        hashMap.put("X-OS-TYPE", Config.OS_TYPE + "");
        hashMap.put("X-OS-VERSION", Config.OS_VERSION + "");
        hashMap.put("X-APP-VERSION", appVersion + "");
        hashMap.put("X-API-ID", Config.API_ID + "");
        hashMap.put("X-API-KEY", Config.API_KEY + "");
        if (pushToken != null) hashMap.put("X-PUSH-TOKEN", pushToken + "");
        if (carrierName != null) hashMap.put("X-CARRIER-NAME", carrierName + "");
        if (carrierCode != null) hashMap.put("X-CARRIER-CODE", carrierCode + "");

        Log.e(TAG, " " + Config.OS_TYPE + " " +Config.OS_VERSION + " " + appVersion + " "
                + carrierName + " " + carrierCode + " ");
        return hashMap;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        HttpHeaders.appName = appName;
    }
}

