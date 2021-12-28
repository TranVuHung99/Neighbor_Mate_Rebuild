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
import com.example.neighbormaterebuild.controller.FavoriteController;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.example.neighbormaterebuild.ui.SplashActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteActivity extends AppCompatActivity {
    private Toolbar toolbarFootPrint;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FavoriteAdapter adapter;
    private ProgressBar progressLoading;
    private TextView txtEmpty, tvTitle;

    private List<User> userList;

    private FavoriteController controller;
    private String token;

    private int page = 1;
    // initialise loading state
    private boolean mIsLastPage = false;
    // amount of items you want to load per page
    private int pageSize = 10;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ProfileStore.getInstance().getUserLogin() == null || Metadata.getInstance().getData() == null) {
            Intent mStartActivity = new Intent(FavoriteActivity.this, SplashActivity.class);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mStartActivity);
            finish();
            Runtime.getRuntime().exit(0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_favorite);

        toolbarFootPrint = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.toolbar_text_fullname);
        recyclerView = findViewById(R.id.recycleFootPrint);
        progressLoading = findViewById(R.id.progressLoading);
        txtEmpty = findViewById(R.id.txtEmpty);

        tvTitle.setText("お気に入り");

        toolbarFootPrint.setLogo(R.drawable.ic_back_svg);
        View view = toolbarFootPrint.getChildAt(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FavoriteAdapter(this, userList);
        recyclerView.setAdapter(adapter);

        controller = new FavoriteController(Config.API_URL);
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        token = sharedPref.getString("token", "");

        getList(true);

        loadmoreList();
    }

    private void loadmoreList() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { //check for scroll down
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && mIsLastPage) {
                            progressLoading.setVisibility(View.VISIBLE);
                            mIsLastPage = false;
                            loading = false;
                            getList(false);

                            loading = true;
                        }
                    }
                }
            }
        });
    }

    private void getList(final boolean isFirstPage) {
        if (isFirstPage) page = 1;
        else ++page;

        controller.getList(token, page, pageSize).enqueue(new Callback<User.UserListResponse>() {
            @Override
            public void onResponse(Call<User.UserListResponse> call, Response<User.UserListResponse> response) {
                //Log.e("Favorite", call.request().toString());
                //Log.e("Favorite", response.body().toString());
                progressLoading.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    txtEmpty.setVisibility(View.GONE);
                    User.UserListResponse.Data result = response.body().getData();
                    if (result.getUserList().size() == 0 && adapter.itemChatList.size() == 0) {
                        txtEmpty.setVisibility(View.VISIBLE);
                    }
                    if (result == null) return;
                    else if (!isFirstPage) adapter.addAll(result.getUserList());
                    else adapter.setList(result.getUserList());

                    mIsLastPage = result.getUserList().size() >= pageSize ;
                } else
                    txtEmpty.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<User.UserListResponse> call, Throwable t) {
                progressLoading.setVisibility(View.GONE);
                txtEmpty.setVisibility(View.VISIBLE);
                //Log.e("Favorite", call.request().toString());
                t.printStackTrace();
            }
        });
    }
}
