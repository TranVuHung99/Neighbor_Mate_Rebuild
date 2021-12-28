package com.example.neighbormaterebuild.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class DeviceInfo {
    public static String getVersion(Context context) {
        PackageInfo packageInfo = null;
        {
            try {
                packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return packageInfo.versionName;
    }
}
