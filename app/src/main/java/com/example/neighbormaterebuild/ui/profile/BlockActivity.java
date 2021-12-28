package com.example.neighbormaterebuild.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.controller.BlockController;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.example.neighbormaterebuild.ui.SplashActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockActivity extends AppCompatActivity {
    private Toolbar toolbarFootPrint;
    private RecyclerView recycleFootPrint;
    private RecyclerView.LayoutManager layoutManager;
    private BlockAdapter adapter;

    private ProgressBar progressLoading;
    private TextView txtEmpty, tvTitle;

    private BlockController controller;
    private String token;

    private List<User> userList;

    private int page = 0;
    // initialise loading state
    private boolean mIsLoading = false;
    private boolean mIsLastPage = false;
    // amount of items you want to load per page
    private int pageSize = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ProfileStore.getInstance().getUserLogin() == null || Metadata.getInstance().getData() == null) {
            Intent mStartActivity = new Intent(BlockActivity.this, SplashActivity.class);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mStartActivity);
            finish();
            Runtime.getRuntime().exit(0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);

        toolbarFootPrint = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.toolbar_text_fullname);
        recycleFootPrint = findViewById(R.id.recycleFootPrint);
        progressLoading = findViewById(R.id.progressLoading);
        txtEmpty = findViewById(R.id.txtEmpty);

        tvTitle.setText("ブロック済みユーザー一覧");

        txtEmpty.setText("ブロックリストはありません。");

        toolbarFootPrint.setLogo(R.drawable.ic_back_svg);
        View view = toolbarFootPrint.getChildAt(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        controller = new BlockController(Config.API_URL, this);
        userList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        recycleFootPrint.setLayoutManager(layoutManager);
        adapter = new BlockAdapter(BlockActivity.this, userList, controller);
        recycleFootPrint.setAdapter(adapter);

        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        token = sharedPref.getString("token", "");

        loadmoreList();
    }

    private void loadmoreList() {
        recycleFootPrint.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // number of visible items
                int visibleItemCount = layoutManager.getChildCount();
                // number of items in layout
                int totalItemCount = layoutManager.getItemCount();
                // the position of first visible item
                int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                boolean isNotLoadingAndNotLastPage = !mIsLoading && !mIsLastPage;
                // flag if number of visible items is at the last
                boolean isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount;
                // validate non negative values
                boolean isValidFirstItem = firstVisibleItemPosition >= 0;
                // validate total items are more than possible visible items
                boolean totalIsMoreThanVisible = totalItemCount >= pageSize;
                // flag to know whether to load more
                boolean shouldLoadMore = isValidFirstItem && isAtLastItem && totalIsMoreThanVisible && isNotLoadingAndNotLastPage;

                if (shouldLoadMore) {
                    getList(false);
                }
            }
        });
        getList(true);
    }

    private void getList(final boolean isFirstPage) {
        mIsLoading = isFirstPage;
        if (isFirstPage) page = 1;
        else ++page;

        controller.getList(token, page, pageSize).enqueue(new Callback<User.UserListResponse>() {
            @Override
            public void onResponse(Call<User.UserListResponse> call, Response<User.UserListResponse> response) {
//                Log.e("Block", call.request().toString());
//                Log.e("Block", response.code() + "");
//                Log.e("Block", response.message());

                progressLoading.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    User.UserListResponse.Data result = response.body().getData();

                    if (result == null || result.getUserList() == null) {
                        txtEmpty.setVisibility(View.VISIBLE);
                        return;
                    } else if (!isFirstPage) adapter.addAll(result.getUserList());
                    else adapter.setList(result.getUserList());

                    if (result.getUserList().isEmpty()) txtEmpty.setVisibility(View.VISIBLE);
                    else txtEmpty.setVisibility(View.GONE);
                    mIsLoading = false;
                    mIsLastPage = page == result.getTotalPages();
                } else
                    txtEmpty.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<User.UserListResponse> call, Throwable t) {
                progressLoading.setVisibility(View.GONE);
                txtEmpty.setVisibility(View.VISIBLE);
                //Log.e("Block", call.request().toString());
                t.printStackTrace();
            }
        });
    }
}
