package com.example.neighbormaterebuild.ui.search;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.controller.PointController;
import com.example.neighbormaterebuild.controller.UserListController;
import com.example.neighbormaterebuild.helper.OnSingleClickListener;
import com.example.neighbormaterebuild.model.PointSetting;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.store.ProfileStore;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFragment extends Fragment implements Toolbar.OnMenuItemClickListener {

    //init view
    private Toolbar toolbarFragmentSearch;
    private RecyclerView recycleSearchContainer;
    private SearchAdapter adapter;
    private List<User> userList;
    private RecyclerView.LayoutManager layoutManager;
    private UserListController userListController;
    private ProgressBar progressLoadmore;
    private ImageButton imgbtnRefresh;
    private PointController pointController;

    private String token;
    private int page = 1;
    // initialise loading state
    private boolean mIsLoading = false;
    private boolean isMore = false;
    private static final int pageSize = 30;

    private static int checkNewDate = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final User userLogin = ProfileStore.getInstance().getUserLogin();
        if (userLogin != null && userLogin.getIs_bonus() == 1 && checkNewDate == 0) {
            checkNewDate += 1;
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
            alertBuilder.setTitle(userLogin.getBonus_title());
            alertBuilder.setMessage(userLogin.getBonus_body());
            alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertBuilder.show();
        }

        pointController = new PointController(Config.API_URL);
        userListController = new UserListController(Config.API_URL);

        userList = new ArrayList<>();
        recycleSearchContainer = view.findViewById(R.id.recycleSearchContainer);
        imgbtnRefresh = view.findViewById(R.id.imgbtnRefresh);
        layoutManager = new GridLayoutManager(getActivity(), 4);
        recycleSearchContainer.setLayoutManager(layoutManager);
        adapter = new SearchAdapter(getActivity(), userList, 0);
        recycleSearchContainer.setAdapter(adapter);

        progressLoadmore = view.findViewById(R.id.progressLoadmore);

        toolbarFragmentSearch = view.findViewById(R.id.toolbarFragmentSearch);
        setIconColor( //set default color of horizontal_grid Icon
                toolbarFragmentSearch.getMenu().findItem(R.id.view_hon)
                , getResources().getColor(R.color.colorMain)
        );


        imgbtnRefresh.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                userList = new ArrayList<>();
                layoutManager = new GridLayoutManager(getActivity(), 4);
                recycleSearchContainer.setLayoutManager(layoutManager);
                adapter = new SearchAdapter(getActivity(), userList, 0);
                recycleSearchContainer.setAdapter(adapter);
                loadmoreList();
            }
        });
        setHasOptionsMenu(true);
        toolbarFragmentSearch.setOnMenuItemClickListener((Toolbar.OnMenuItemClickListener) this);
        toolbarFragmentSearch.post(new Runnable() {
            @Override
            public void run() {
                View view_hon = toolbarFragmentSearch.findViewById(R.id.view_hon);
                if (view_hon instanceof TextView) {
                }
            }
        });
        SharedPreferences sharedPref = getActivity().getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        token = sharedPref.getString("token", "");

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
                boolean shouldLoadMore = isValidFirstItem && isAtLastItem && totalIsMoreThanVisible
                        && isNotLoadingAndNotLastPage;

                if (shouldLoadMore) {
                    getUserList(false);
                }
            }
        });
        getUserList(true);
    }

    private void getUserList(final boolean isFirstPage) {
        mIsLoading = true;
        if (isFirstPage) page = 1;
        else page++;
        progressLoadmore.setVisibility(View.VISIBLE);
        userListController.getUserList(token, page, pageSize).enqueue(new Callback<User.UserListResponse>() {
            @Override
            public void onResponse(Call<User.UserListResponse> call, Response<User.UserListResponse> response) {
                progressLoadmore.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    User.UserListResponse.Data result = response.body().getData();
                    if (result == null) return;
                    else if (!isFirstPage) adapter.addAll(result.getUserList());
                    else adapter.setList(result.getUserList());

                    mIsLoading = false;
                    if (result.getUserList() != null)
                        isMore = result.getUserList().size() >= pageSize;
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

    private boolean isGridView = true;

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        try {
            Drawable gridIcon = (Drawable) menu.findItem(R.id.view_hon).getIcon();
            Drawable listIcon = (Drawable) menu.findItem(R.id.view_ver).getIcon();
            if (isGridView == true) {
                gridIcon.mutate().setColorFilter(getResources().getColor(R.color.colorMain), PorterDuff.Mode.SRC_IN);
                //
                listIcon.mutate().setColorFilter(getResources().getColor(R.color.colorGrey), PorterDuff.Mode.SRC_IN);
            } else {
                gridIcon.mutate().setColorFilter(getResources().getColor(R.color.colorGrey), PorterDuff.Mode.SRC_IN);
                //
                listIcon.mutate().setColorFilter(getResources().getColor(R.color.colorMain), PorterDuff.Mode.SRC_IN);
            }
            menu.findItem(R.id.view_hon).setIcon(gridIcon);
            menu.findItem(R.id.view_ver).setIcon(listIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onPrepareOptionsMenu(menu);
    }

    private void setIconColor(MenuItem item, int color) {
        Drawable icon = item.getIcon();
        icon.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        item.setIcon(icon);
    }

    private void refeshMenuColor() {
        int refeshColor = getResources().getColor(R.color.colorGrey);
        MenuItem itemHon = toolbarFragmentSearch.getMenu().findItem(R.id.view_hon);
        setIconColor(itemHon, refeshColor);
        MenuItem itemVer = toolbarFragmentSearch.getMenu().findItem(R.id.view_ver);
        setIconColor(itemVer, refeshColor);
        MenuItem itemSearch = toolbarFragmentSearch.getMenu().findItem(R.id.search);
        setIconColor(itemSearch, refeshColor);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int targetColor = getResources().getColor(R.color.colorMain);

        switch (item.getItemId()) {
            case R.id.view_hon:
                isGridView = true;
                refeshMenuColor();
                setIconColor(item, targetColor);
                progressLoadmore.setVisibility(View.VISIBLE);
                userList = new ArrayList<>();
                layoutManager = new GridLayoutManager(getActivity(), 4);
                recycleSearchContainer.setLayoutManager(layoutManager);
                adapter = new SearchAdapter(getActivity(), userList, 0);
                recycleSearchContainer.setAdapter(adapter);
                loadmoreList();
                break;
            case R.id.view_ver:
                isGridView = false;
                refeshMenuColor();
                setIconColor(item, targetColor);
                progressLoadmore.setVisibility(View.VISIBLE);
                userList = new ArrayList<>();
                layoutManager = new LinearLayoutManager(getActivity());
                recycleSearchContainer.setLayoutManager(layoutManager);
                adapter = new SearchAdapter(getActivity(), userList, 1);
                recycleSearchContainer.setAdapter(adapter);
                loadmoreList();
                break;
            case R.id.search:
                setIconColor(item, targetColor);
                Intent i = new Intent(getActivity(), SearchActivity.class);
                startActivity(i);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        refeshMenuColor();
        setIconColor( //set default color of horizontal_grid Icon
                toolbarFragmentSearch.getMenu().findItem(R.id.view_hon)
                , getResources().getColor(R.color.colorMain)
        );
        isGridView = true;
        userList = new ArrayList<>();
        layoutManager = new GridLayoutManager(getActivity(), 4);
        recycleSearchContainer.setLayoutManager(layoutManager);
        adapter = new SearchAdapter(requireActivity(), userList, 0);
        recycleSearchContainer.setAdapter(adapter);
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
                loadmoreList();
            }

            @Override
            public void onFailure(Call<PointSetting.Response> call, Throwable t) {

            }
        });
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }
}