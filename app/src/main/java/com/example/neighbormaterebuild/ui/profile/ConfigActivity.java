package com.example.neighbormaterebuild.ui.profile;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.utils.DeviceInfo;

import java.util.List;

public class ConfigActivity extends AppCompatActivity {
    private TextView tvVersion, tvBlock;
    private Toolbar toolbarConfig;
    private Switch swSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        initView();
    }

    private void initView() {
        toolbarConfig = findViewById(R.id.toolbarConfig);
        toolbarConfig.setLogo(R.drawable.ic_back_svg);
        swSetting = findViewById(R.id.swSetting);

        swSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);
                } else {
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                }
                startActivity(intent);
            }
        });

        View viewType = toolbarConfig.getChildAt(1);
        viewType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvBlock = findViewById(R.id.tvBlock);
        tvBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), BlockActivity.class);
                startActivity(intent);
            }
        });

        tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText(DeviceInfo.getVersion(this));

        areNotificationsEnabled();
    }

    @Override
    public void onResume() {
        super.onResume();
        areNotificationsEnabled();
    }

    public boolean areNotificationsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (!manager.areNotificationsEnabled()) {
                swSetting.setChecked(false);
                return false;
            }
            List<NotificationChannel> channels = manager.getNotificationChannels();
            for (NotificationChannel channel : channels) {
                if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                    swSetting.setChecked(false);
                    return false;
                }
            }
            swSetting.setChecked(true);
            return true;
        } else {
            boolean notifiSetting = NotificationManagerCompat.from(this).areNotificationsEnabled();
            swSetting.setChecked(notifiSetting);
            return notifiSetting;
        }
    }
}
