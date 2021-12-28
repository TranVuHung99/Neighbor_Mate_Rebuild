package com.example.neighbormaterebuild.ui.keijiban.complete;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.controller.KeijibanController;
import com.example.neighbormaterebuild.controller.PointController;
import com.example.neighbormaterebuild.model.Keijiban;
import com.example.neighbormaterebuild.model.PointSetting;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.ui.keijiban.KeijibanAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeijibanSearchCompleteFragment extends Fragment {

    private RecyclerView recycleSearchContainer;
    private KeijibanAdapter adapter;
    private List<Keijiban> keijibanList;
    private RecyclerView.LayoutManager layoutManager;
    private KeijibanController keijibanController;
    private String sex, age, job, height, income, style, sort_by;
    private TextView tvResultEmptyFind;
    private ProgressBar progressLoadmore;

    private int page = 1;
    private static int checkIsEmpty = 0;
    private boolean mIsLoading = false;
    private boolean isMore = false;
    private int pageSize = 30;

    HashMap<String, String> fills;

    private PointController pointController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_keijiban_search_complete, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        keijibanController = new KeijibanController(Config.API_URL);
        keijibanList = new ArrayList<>();
        recycleSearchContainer = view.findViewById(R.id.recycleSearchContainer);
        tvResultEmptyFind = view.findViewById(R.id.tvResultEmptyFind);
        progressLoadmore = view.findViewById(R.id.progressLoadmore);

        keijibanList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        recycleSearchContainer.setLayoutManager(layoutManager);
        adapter = new KeijibanAdapter(getActivity(), keijibanList);
        recycleSearchContainer.setAdapter(adapter);

        pointController = new PointController(Config.API_URL);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sex = bundle.getString("sex", "");
            age = bundle.getString("age", "");
            job = bundle.getString("job", "");
            height = bundle.getString("height", "");
            income = bundle.getString("income", "");
            style = bundle.getString("style", "");
            sort_by = bundle.getString("sort_by", "0");
            getKeijibanList(sex, age, job, height, income, style, sort_by);
        }
    }

    private void getKeijibanList(String sex, String age, String job, String height, String income, String style, String sort_by) {
        fills = new HashMap<String, String>();

        if (!sex.equals("指定なし")) {
            fills.put("sex", sex);
        }
        if (!age.equals("指定なし")) {
            fills.put("age", age);
        }
        if (!height.equals("指定なし")) {
            fills.put("height", height);
        }
        if (!job.equals("指定なし")) {
            fills.put("job", job);
        }
        if (!sex.equals("指定なし")) {
            fills.put("sex", sex);
        }
        if (!style.equals("指定なし")) {
            fills.put("style", style);
        }
        if (!income.equals("指定なし")) {
            fills.put("income", income);
        }
        if (Integer.parseInt(sort_by) != 0) {
            fills.put("sort_by", sort_by);
        }
        loadmoreList();
    }

    private void loadmoreList() {
        recycleSearchContainer.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                if (shouldLoadMore) {
                    getKeijibanList(false);
                }
            }
        });
        getKeijibanList(true);
    }

    private void getKeijibanList(final boolean isFirstPage) {
        mIsLoading = true;
        if (isFirstPage) page = 1;
        else page++;
        progressLoadmore.setVisibility(View.VISIBLE);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        pointController.getResultPointSetting(token).enqueue(new Callback<PointSetting.Response>() {
            @Override
            public void onResponse(Call<PointSetting.Response> call, Response<PointSetting.Response> response) {
                if (response.isSuccessful() && response.body() != null)
                    try {
                        PointStore.getInstance().setTotalPoint(response.body().getData().getTotalPoint());
                        PointStore.getInstance().setPointSettings(response.body().getData().getPointList());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void onFailure(Call<PointSetting.Response> call, Throwable t) {

            }
        });
        //Log.e("kiemtra", fills + " " + fills);
        keijibanController.getKeijibanListFind(token, fills, page, pageSize).enqueue(new Callback<Keijiban.KeijibanResponse>() {
            @Override
            public void onResponse(Call<Keijiban.KeijibanResponse> call, Response<Keijiban.KeijibanResponse> response) {
                progressLoadmore.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body().getResult().getKeijiban().isEmpty() && checkIsEmpty == 0) {
                        tvResultEmptyFind.setVisibility(View.VISIBLE);
                    } else {
                        checkIsEmpty += 1;
                        Keijiban.KeijibanResponse.Data result = response.body().getResult();
                        if (result == null) return;
                        else if (!isFirstPage) adapter.addAll(result.getKeijiban());
                        else adapter.setList(result.getKeijiban());

                        mIsLoading = false;
                        if (response.body().getResult() != null && response.body().getResult().getKeijiban() != null)
                            isMore = response.body().getResult().getKeijiban().size() >= pageSize;
                    }
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

    @Override
    public void onStart() {
        super.onStart();
        checkIsEmpty = 0;
    }
}
