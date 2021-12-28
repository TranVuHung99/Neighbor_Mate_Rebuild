package com.example.neighbormaterebuild.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.controller.PointController;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.PointSetting;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.example.neighbormaterebuild.ui.SplashActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointSettingActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView listView;
    private PointSettingAdapter adapter;

    private ProgressBar progressLoading;

    private List<PointSetting> listData;
    private PointController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ProfileStore.getInstance().getUserLogin() == null || Metadata.getInstance().getData() == null) {
            Intent mStartActivity = new Intent(PointSettingActivity.this, SplashActivity.class);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mStartActivity);
            finish();
            Runtime.getRuntime().exit(0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_setting);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_back_svg);
        View view = toolbar.getChildAt(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressLoading = findViewById(R.id.progressLoading);

        listView = findViewById(R.id.listView);
        listData = new ArrayList<>();
        adapter = new PointSettingAdapter(this, listData);
        listView.setAdapter(adapter);

        controller = new PointController(Config.API_URL);

        /////////
        getData();
    }

    private void getData() {
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");

        controller.getResultPointSetting(token).enqueue(new Callback<PointSetting.Response>() {
            @Override
            public void onResponse(Call<PointSetting.Response> call, Response<PointSetting.Response> response) {

                progressLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        PointStore.getInstance().setTotalPoint(response.body().getData().getTotalPoint());
                        PointStore.getInstance().setPointSettings(response.body().getData().getPointList());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<PointSetting.Response> call, Throwable t) {
                progressLoading.setVisibility(View.VISIBLE);
                //Log.e("PointSetting", call.request().toString());
                t.printStackTrace();
            }
        });

        controller.getPointSetting(token).enqueue(new Callback<PointSetting.Response2>() {
            @Override
            public void onResponse(Call<PointSetting.Response2> call, Response<PointSetting.Response2> response) {

                progressLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    try {
//                       PointStore.getInstance().setPointSettings(response.body().getData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    listData = response.body().getData();
                    Log.e("kiemtra", listData + "");
                    adapter.setList(listData);
                }
            }

            @Override
            public void onFailure(Call<PointSetting.Response2> call, Throwable t) {
                progressLoading.setVisibility(View.VISIBLE);
                t.printStackTrace();
            }
        });
    }
}
