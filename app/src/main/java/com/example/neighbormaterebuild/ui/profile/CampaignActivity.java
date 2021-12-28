package com.example.neighbormaterebuild.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.controller.CampaignController;
import com.example.neighbormaterebuild.model.Campaign;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.example.neighbormaterebuild.ui.SplashActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CampaignActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title, txtEmpty;
    private RecyclerView recyclerView;
    private List<Campaign> itemChatList;
    private CampaignAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressLoading;

    private CampaignController controller;

    private int page = 1;
    private final int limit = 20;
    private boolean mIsLoading = false;
    private boolean mIsLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ProfileStore.getInstance().getUserLogin() == null || Metadata.getInstance().getData() == null) {
            Intent mStartActivity = new Intent(CampaignActivity.this, SplashActivity.class);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mStartActivity);
            finish();
            Runtime.getRuntime().exit(0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.toolbar_title);
        title.setText("キャンペーン");
        toolbar.setLogo(R.drawable.ic_back_svg);
        View view = toolbar.getChildAt(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressLoading = findViewById(R.id.progressLoading);
        txtEmpty = findViewById(R.id.txtEmpty);
        txtEmpty.setText("開催中のキャンペーンはありません。");

        recyclerView = findViewById(R.id.recycleChatNotifiContainer);
        itemChatList = new ArrayList<>();

        adapter = new CampaignAdapter(this, itemChatList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        controller = new CampaignController(Config.API_URL);

        getList(true);
    }

    private void getList(final boolean isFirstPage) {
        mIsLoading = isFirstPage;
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        controller.getList(token, page, limit).enqueue(new Callback<Campaign.Response>() {
            @Override
            public void onResponse(Call<Campaign.Response> call, Response<Campaign.Response> response) {
//                Log.e("Notice", call.request().toString());
//                Log.e("Notice", response.body().toString());
                progressLoading.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    List<Campaign> result = response.body().getData().getList();
                    if (result == null) return;
                    else if (!isFirstPage) adapter.addAll(result);
                    else adapter.setList(result);

                    mIsLoading = false;
                    mIsLastPage = page == result.size();
                } else txtEmpty.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<Campaign.Response> call, Throwable t) {
                txtEmpty.setVisibility(View.VISIBLE);
                progressLoading.setVisibility(View.GONE);
                t.printStackTrace();
                //Log.e("Notice Fail", call.request().toString());
            }
        });
        page = page + 1;
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 1;
    }
}
