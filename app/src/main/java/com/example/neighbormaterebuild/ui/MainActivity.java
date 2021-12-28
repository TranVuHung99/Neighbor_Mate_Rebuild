package com.example.neighbormaterebuild.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.helper.NetworkReceiver;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.network.FirebaseService;
import com.example.neighbormaterebuild.network.HttpHeaders;
import com.example.neighbormaterebuild.store.MessageStore;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.example.neighbormaterebuild.ui.chat.ChatFragment;
import com.example.neighbormaterebuild.ui.keijiban.KeijibanFragment;
import com.example.neighbormaterebuild.ui.point.PointFragment;
import com.example.neighbormaterebuild.ui.profile.ProfileFragment;
import com.example.neighbormaterebuild.ui.search.SearchFragment;
import com.example.neighbormaterebuild.utils.Navigator;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements NetworkReceiver.ConnectivityReceiverListener {

    private static int i = 0;
    BadgeDrawable badge;
    LinearLayout layoutIsConnected;
    private BroadcastReceiver mNetworkReceiver;
    private BroadcastReceiver onMessageReceived;
    BottomNavigationView navView;
    private static boolean activityVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ProfileStore.getInstance().getUserLogin() == null || Metadata.getInstance().getData() == null) {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);
        layoutIsConnected = findViewById(R.id.layoutIsDisconnected);

        badge = navView.getOrCreateBadge(R.id.navigation_chat);
        try {
            badge.setBackgroundColor(ContextCompat.getColor(this, R.color.badgeBackground));
        } catch (Exception ignored) {
        }
        badge.setVisible(false);




        mNetworkReceiver = new NetworkReceiver();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        MessageStore.getInstance().unreadAllSubject.hide().subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                if (integer > 0) {
                    badge.setVisible(true);
                    badge.setNumber(integer);
                } else {
                    badge.clearNumber();
                    badge.setVisible(false);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        onMessageReceived = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    int badge = intent.getIntExtra("badge", 0);
                    if (MessageStore.getInstance() != null) {
                        MessageStore.getInstance().unreadAllSubject.onNext(badge);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        bottomNavOnClick();

        mNetworkReceiver = new NetworkReceiver();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));




    }
    private void bottomNavOnClick() {
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_search:
                        Navigator.loadFragment(getSupportFragmentManager(), R.id.nav_host_fragment, SearchFragment.newInstance(), SearchFragment.class.getSimpleName());
                        break;
                    case R.id.navigation_chat:
                        Navigator.loadFragment(getSupportFragmentManager(), R.id.nav_host_fragment, ChatFragment.newInstance(), ChatFragment.class.getSimpleName());
                        break;
                    case R.id.navigation_notification:
                        Navigator.loadFragment(getSupportFragmentManager(), R.id.nav_host_fragment, KeijibanFragment.newInstance(), KeijibanFragment.class.getSimpleName());
                        break;
                    case R.id.navigation_point:
                        Navigator.loadFragment(getSupportFragmentManager(), R.id.nav_host_fragment, PointFragment.newInstance(), PointFragment.class.getSimpleName());
                        break;
                    case R.id.navigation_profile:
                        Navigator.loadFragment(getSupportFragmentManager(), R.id.nav_host_fragment, ProfileFragment.newInstance(), ProfileFragment.class.getSimpleName());
                        break;
                }

                return true;
            }
        });
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void updateBadge() {
        if (isActivityVisible())
            if (MessageStore.getInstance().unreadAllSubject.getValue() != null)
                if (MessageStore.getInstance().unreadAllSubject.getValue() > 0) {
                    badge.setVisible(true);
                    badge.setNumber(MessageStore.getInstance().unreadAllSubject.getValue());
                } else {
                    badge.clearNumber();
                    badge.setVisible(false);
                }
    }

    @Override
    protected void onDestroy() {
        unregisterNetworkChanges();
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(onMessageReceived);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityResumed();
        updateBadge();

        NetworkReceiver.connectivityReceiverListener = this;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    @Override
    public void onNetworkConnectionChanged(Boolean isConnected) {// Change status according to boolean value
        if (isConnected) {
            layoutIsConnected.setVisibility(View.GONE);
            bottomNavOnClick();
        } else {
            layoutIsConnected.setVisibility(View.VISIBLE);
            navView.setOnNavigationItemSelectedListener(null);
        }
    }
}