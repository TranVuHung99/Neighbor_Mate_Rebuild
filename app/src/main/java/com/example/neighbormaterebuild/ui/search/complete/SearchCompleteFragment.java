package com.example.neighbormaterebuild.ui.search.complete;

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
import com.example.neighbormaterebuild.controller.UserListController;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.ui.search.SearchAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchCompleteFragment extends Fragment {

    private RecyclerView recycleSearchContainer;
    private SearchAdapter adapter;
    private List<User> userList;
    private RecyclerView.LayoutManager layoutManager;
    private UserListController userListController;
    private String sex, age, job, height, income, style, sort_by;
    private TextView tvResultEmptyFind;
    private ProgressBar progressLoadmore;

    private int page = 1;
    private static int checkIsEmpty = 0;
    // initialise loading state
    private boolean mIsLoading = false;
    private boolean isMore = false;
    private static final int pageSize = 30;

    HashMap<String, String> fills;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_complete, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userListController = new UserListController(Config.API_URL);
        userList = new ArrayList<>();
        recycleSearchContainer = view.findViewById(R.id.recycleSearchContainer);
        tvResultEmptyFind = view.findViewById(R.id.tvResultEmptyFind);
        progressLoadmore = view.findViewById(R.id.progressLoadmore);

        userList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        recycleSearchContainer.setLayoutManager(layoutManager);
        adapter = new SearchAdapter(getActivity(), userList, 1);
        recycleSearchContainer.setAdapter(adapter);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sex = bundle.getString("sex", "");
            age = bundle.getString("age", "");
            job = bundle.getString("job", "");
            height = bundle.getString("height", "");
            income = bundle.getString("income", "");
            style = bundle.getString("style", "");
            sort_by = bundle.getString("sort_by", "0");
            getUserList(sex, age, job, height, income, style, sort_by);
        }
    }

    private void getUserList(String sex, String age, String job, String height, String income, String style, String sort_by) {
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
//                    progressLoadmore.setVisibility(View.VISIBLE);
                    getUserList(false);
                }
            }
        });
        getUserList(true);
    }

    private void getUserList(final boolean isFirstPage) {
        if (isFirstPage) page = 1;
        else page++;
        SharedPreferences sharedPref = getActivity().getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");

        progressLoadmore.setVisibility(View.VISIBLE);
        //Log.e("kiemtra",  " " + fills);
        mIsLoading = true;
        userListController.getUserListFind(token, fills, page, pageSize).enqueue(new Callback<User.UserListResponse>() {
            @Override
            public void onResponse(Call<User.UserListResponse> call, Response<User.UserListResponse> response) {
                progressLoadmore.setVisibility(View.GONE);
                mIsLoading = false;
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    if (response.body().getData().getUserList().isEmpty() && checkIsEmpty == 0) {
                        tvResultEmptyFind.setVisibility(View.VISIBLE);
                    } else {
                        checkIsEmpty += 1;
                        User.UserListResponse.Data result = response.body().getData();
                        if (result == null) return;
                        else if (!isFirstPage) adapter.addAll(result.getUserList());
                        else adapter.setList(result.getUserList());

                        if (result.getUserList() != null)
                            isMore = result.getUserList().size() >= pageSize;
                    }
                }else {
                    tvResultEmptyFind.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<User.UserListResponse> call, Throwable t) {
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