package com.example.neighbormaterebuild.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.config.Constants;
import com.example.neighbormaterebuild.controller.LoginController;
import com.example.neighbormaterebuild.helper.NetworkReceiver;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.network.FirebaseService;
import com.example.neighbormaterebuild.network.HttpHeaders;
import com.example.neighbormaterebuild.utils.FileDeviceID;
import com.example.neighbormaterebuild.utils.FileService;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;

public class SplashActivity extends AppCompatActivity implements NetworkReceiver.ConnectivityReceiverListener {

    private static final String TAG = "SplashActivity";
    private static final String CHECK_INSTALL = "check_install";

    private FileService fileService;
    private FileDeviceID fileDeviceID;
    private User user;
    private LoginController loginController;
    private BroadcastReceiver mNetworkReceiver;

    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkInstallApp();

        mNetworkReceiver = new NetworkReceiver();

        HttpHeaders.getInstance().init(SplashActivity.this);
        new FirebaseService().getToken();
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.spin_kit);
        Sprite threeBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(threeBounce);

    }


    @Override
    protected void onStart() {
        super.onStart();
        ActivityCompat.requestPermissions(SplashActivity.this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkLoginRegister(final Activity context) {
        HttpHeaders.getInstance().init(context);
        Log.e("HttpHeaders DeviceID", HttpHeaders.getInstance().getDeviceID()+"");
        user = new User();
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            ////Log.e("kiemtra", "false");
        } else {
            try {
                fileService = new FileService(context);
                fileDeviceID = new FileDeviceID(context);
                String text = fileDeviceID.readDeviceID();
                Log.e(TAG, "DeviceID" + text);

                if (text != null && !text.isEmpty()) {
//                    String[] lines = text.split("\\n");
                    user = new User();
                    HttpHeaders.getInstance().setDeviceID(text);
                    user.setUser_code("aaa");
                    user.setPassword("aaa");
                } else {
                    Log.e(TAG, "Else login");

                    user = new User();
                    user.setUser_code("aaa");
                    user.setPassword("aaa");
                }
                loginController = new LoginController(Config.API_URL, SplashActivity.this);

                loginController.checkLogin(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkInstallApp() {
        //Log.e(TAG, "Check Install App???");
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        boolean isNewInstall = sharedPref.getBoolean(CHECK_INSTALL, true);

        if (isNewInstall) {
            AdjustEvent adjustEvent = new AdjustEvent(Constants.ADJUST_EVENT_INSTALL);
            Adjust.trackEvent(adjustEvent);

            sharedPref.edit().putBoolean(CHECK_INSTALL, false).apply();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLoginRegister(SplashActivity.this);
                registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            } else {
                System.exit(0);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        NetworkReceiver.connectivityReceiverListener = this;
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    /*
     * Kiểm tra xem device có bộ nhớ ngoài không
     */
    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        unregisterNetworkChanges();
        super.onDestroy();
    }

    @Override
    public void onNetworkConnectionChanged(Boolean isConnected) {
    }


}