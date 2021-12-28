package com.example.neighbormaterebuild.ui.keijiban;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.controller.KeijibanController;
import com.example.neighbormaterebuild.controller.PointController;
import com.example.neighbormaterebuild.controller.UserListController;
import com.example.neighbormaterebuild.helper.OnSingleClickListener;
import com.example.neighbormaterebuild.model.Keijiban;
import com.example.neighbormaterebuild.model.PointSetting;
import com.example.neighbormaterebuild.store.PointStore;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeijibanFragment extends Fragment {

    public static KeijibanFragment newInstance() {
        return new KeijibanFragment();
    }

    private RecyclerView recycleKejibanContainer;
    private Button btnfab;
    private KeijibanController keijibanController;
    private List<Keijiban> keijibanList;
    private RecyclerView.LayoutManager layoutManager;
    private KeijibanAdapter keijibanAdapter;
    private Toolbar toolbarKeijiban;
    private ProgressBar progressLoadmore;
    private ImageButton imgbtnRefresh;

    private int page = 1;
    private boolean mIsLoading = false;
    private boolean isMore = false;
    private int pageSize = 30;

    private PointController pointController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_keijiban, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycleKejibanContainer = view.findViewById(R.id.recycleKejibanContainer);
        imgbtnRefresh = view.findViewById(R.id.imgbtnRefresh);
        progressLoadmore = view.findViewById(R.id.progressLoadmore);
        toolbarKeijiban = view.findViewById(R.id.toolbarKeijiban);
        btnfab = view.findViewById(R.id.btnfab);

        pointController = new PointController(Config.API_URL);

        btnfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), KeijibanPostActivity.class);
                startActivity(i);
            }
        });

        imgbtnRefresh.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Load();
            }
        });

        toolbarKeijiban.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.keijibanSearch:
                        Intent intent = new Intent(getContext(), KeijibanSearchActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

    private void getKeijibanList(final boolean isFirstPage) {
        mIsLoading = true;
        if (isFirstPage) page = 1;
        else page++;
        progressLoadmore.setVisibility(View.VISIBLE);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        final String token = sharedPref.getString("token", "");
        pointController.getResultPointSetting(token).enqueue(new Callback<PointSetting.Response>() {
            @Override
            public void onResponse(Call<PointSetting.Response> call, Response<PointSetting.Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        PointStore.getInstance().setTotalPoint(response.body().getData().getTotalPoint());
                        PointStore.getInstance().setPointSettings(response.body().getData().getPointList());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    keijibanController.getKeijibanList(token, page, pageSize).enqueue(new Callback<Keijiban.KeijibanResponse>() {
                        @Override
                        public void onResponse(Call<Keijiban.KeijibanResponse> call, Response<Keijiban.KeijibanResponse> response) {
                            progressLoadmore.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                //Log.e("kiemtra", page + "");
                                Keijiban.KeijibanResponse.Data result = response.body().getResult();
                                if (result == null) return;
                                else if (!isFirstPage) keijibanAdapter.addAll(result.getKeijiban());
                                else keijibanAdapter.setList(result.getKeijiban());

                                mIsLoading = false;
                                if (response.body().getResult() != null && response.body().getResult().getKeijiban() != null)
                                    isMore = response.body().getResult().getKeijiban().size() >= pageSize;
                            }
                        }

                        @Override
                        public void onFailure(Call<Keijiban.KeijibanResponse> call, Throwable t) {
                            mIsLoading = false;
                            progressLoadmore.setVisibility(View.GONE);
                            t.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<PointSetting.Response> call, Throwable t) {

            }
        });
    }

    private void Load() {
        keijibanController = new KeijibanController(Config.API_URL);
        keijibanList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        recycleKejibanContainer.setLayoutManager(layoutManager);
        keijibanAdapter = new KeijibanAdapter(getActivity(), keijibanList);
        recycleKejibanContainer.setAdapter(keijibanAdapter);
        recycleKejibanContainer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // number of visible items
                int visibleItemCount = layoutManager.getChildCount();
                // number of items in layout
                int totalItemCount = layoutManager.getItemCount();
                // the position of first visible item
                int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                boolean isNotLoadingAndNotLastPage = !mIsLoading && isMore;
                // flag if number of visible items is at the last
                boolean isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount;
                // validate non negative values
                boolean isValidFirstItem = firstVisibleItemPosition >= 0;
                // validate total items are more than possible visible items
                boolean totalIsMoreThanVisible = totalItemCount >= pageSize;
                // flag to know whether to load more
                boolean shouldLoadMore = isValidFirstItem && isAtLastItem && totalIsMoreThanVisible && isNotLoadingAndNotLastPage;

                if (shouldLoadMore) getKeijibanList(false);
            }
        });
        getKeijibanList(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Load();
    }
}