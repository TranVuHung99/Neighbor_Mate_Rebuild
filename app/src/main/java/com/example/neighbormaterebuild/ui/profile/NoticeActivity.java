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
import com.example.neighbormaterebuild.controller.NoticeController;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.Notice;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.example.neighbormaterebuild.ui.SplashActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeActivity extends AppCompatActivity {

    private Toolbar toolbarFootPrint;
    private RecyclerView recycleChatNotifiContainer;
    private List<Notice> itemChatList;
    private NoticeAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressLoading;
    private TextView txtEmpty;

    private NoticeController noticeController;

    private int page = 0;
    private final int limit = 10;
    private boolean mIsLoading = false;
    private boolean mIsLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ProfileStore.getInstance().getUserLogin() == null || Metadata.getInstance().getData() == null) {
            Intent mStartActivity = new Intent(NoticeActivity.this, SplashActivity.class);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mStartActivity);
            finish();
            Runtime.getRuntime().exit(0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        toolbarFootPrint = findViewById(R.id.toolbar);
        toolbarFootPrint.setLogo(R.drawable.ic_back_svg);
        View view = toolbarFootPrint.getChildAt(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressLoading = findViewById(R.id.progressLoading);
        txtEmpty = findViewById(R.id.txtEmpty);
        txtEmpty.setText("お知らせはありません。");

        recycleChatNotifiContainer = findViewById(R.id.recycleChatNotifiContainer);
        itemChatList = new ArrayList<>();

        adapter = new NoticeAdapter(this, itemChatList);
        layoutManager = new LinearLayoutManager(this);
        recycleChatNotifiContainer.setLayoutManager(layoutManager);
        recycleChatNotifiContainer.setAdapter(adapter);

        noticeController = new NoticeController(Config.API_URL);

        getList(true);
    }

    private void getList(final boolean isFirstPage) {
        mIsLoading = isFirstPage;
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        noticeController.getList(token, page, limit).enqueue(new Callback<Notice.Response>() {
            @Override
            public void onResponse(Call<Notice.Response> call, Response<Notice.Response> response) {
                //Log.e("Notice", call.request().toString());
                //Log.e("Notice", response.body().toString());
                progressLoading.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Notice> result = response.body().getData().getList();
                    if (result == null) return;
                    else if (!isFirstPage) adapter.addAll(result);
                    else adapter.setList(result);

                    mIsLoading = false;
                    mIsLastPage = page == result.size();
                } else txtEmpty.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<Notice.Response> call, Throwable t) {
                progressLoading.setVisibility(View.GONE);
                txtEmpty.setVisibility(View.VISIBLE);
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
