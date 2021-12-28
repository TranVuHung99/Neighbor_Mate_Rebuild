package com.example.neighbormaterebuild.ui.chat.items;

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
import com.example.neighbormaterebuild.controller.CampaignController;
import com.example.neighbormaterebuild.model.Campaign;
import com.example.neighbormaterebuild.store.MessageStore;
import com.example.neighbormaterebuild.ui.chat.ChatFragment;
import com.example.neighbormaterebuild.ui.profile.CampaignAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CampaignFragment extends Fragment {

    private RecyclerView recycleChatCampaignContainer;
    private List<Campaign> list;
    private CampaignAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private CampaignController campaignController;
    private ProgressBar progressLoading;
    private TextView tvEmpty;

    private int page = 0;
    private final int limit = 10;
    private boolean mIsLoading = false;
    private boolean isMore = false;

    private boolean isViewShown = false;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_campaign, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recycleChatCampaignContainer = view.findViewById(R.id.recycleChatCampaignContainer);
        progressLoading = view.findViewById(R.id.progressLoading);
        tvEmpty = view.findViewById(R.id.txtEmpty);
    }

    private void load() {
        list = new ArrayList<>();

        adapter = new CampaignAdapter(getActivity(), list);
        layoutManager = new LinearLayoutManager(getActivity());
        recycleChatCampaignContainer.setLayoutManager(layoutManager);
        recycleChatCampaignContainer.setAdapter(adapter);

        campaignController = new CampaignController(Config.API_URL);


        recycleChatCampaignContainer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) { //check for scroll down
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && isMore) {
                            progressLoading.setVisibility(View.VISIBLE);
                            isMore = false;
                            loading = false;
                            getList(false);

                            loading = true;
                        }
                    }
                }
            }
        });
        getList(true);
    }


    private void getList(final boolean isFirstPage) {
        progressLoading.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        mIsLoading = true;
        if (isFirstPage) page = 0;
        else ++page;
        SharedPreferences sharedPref = getActivity().getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        campaignController.getList(token, page, limit).enqueue(new Callback<Campaign.Response>() {
            @Override
            public void onResponse(Call<Campaign.Response> call, Response<Campaign.Response> response) {
//                Log.e("Campaign", call.request().toString());
//                Log.e("Campaign", response.body().toString());

                mIsLoading = false;
                progressLoading.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body().getData().getList() == null || (isFirstPage
                            && response.body().getData().getList().isEmpty())) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else tvEmpty.setVisibility(View.GONE);

                    List<Campaign> result = response.body().getData().getList();
                    if (result == null) return;
                    else if (!isFirstPage) adapter.addAll(result);
                    else adapter.setList(result);

                    isMore = result.size() >= limit;

                    if (isFirstPage){
                        MessageStore.getInstance().unreadAllSubject.onNext(response.body().getData().getTotal_unread_all());
                        MessageStore.getInstance().unreadMessageSubject.onNext(response.body().getData().getTotal_unread_message());
                        MessageStore.getInstance().unreadNoticeSubject.onNext(response.body().getData().getTotal_unread_notice());
                        MessageStore.getInstance().unreadCampaignSubject.onNext(response.body().getData().getTotal_unread_campaign());
                    }

                } else {
                    if (adapter.getList() == null || adapter.getList().isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else tvEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Campaign.Response> call, Throwable t) {
                mIsLoading = false;
                progressLoading.setVisibility(View.GONE);
                if (adapter.getList() == null || adapter.getList().isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else tvEmpty.setVisibility(View.GONE);
                t.printStackTrace();
                //Log.e("Campaign Fail", call.request().toString());
            }
        });
    }

    @Override
    public void onResume() {
        if (!isViewShown) {
            load();
        }
        super.onResume();
        MessageStore.getInstance().listener = new ChatFragment.OnRefreshTab() {
            @Override
            public void clickPosition() {
                if (!isViewShown) {
                    load();
                }
            }
        };
    }
}
