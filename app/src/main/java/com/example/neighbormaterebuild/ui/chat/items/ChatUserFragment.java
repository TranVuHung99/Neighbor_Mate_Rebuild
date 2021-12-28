package com.example.neighbormaterebuild.ui.chat.items;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.controller.ChatController;
import com.example.neighbormaterebuild.helper.RecyclerTouchListener;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.Report;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.network.SocketClient;
import com.example.neighbormaterebuild.store.MessageStore;
import com.example.neighbormaterebuild.ui.MainActivity;
import com.example.neighbormaterebuild.ui.chat.ChatFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatUserFragment extends Fragment implements Toolbar.OnMenuItemClickListener  {

    private RecyclerView recycleItemChatContainer;
    private ChatUserAdapter adapter;
    private List<User> userModelList;
    private RecyclerView.LayoutManager layoutManager;
    private ChatController chatController;
    private ConstraintLayout itemPushSetting;
    private TextView tvPushTitle, tvPushContent, tvEmpty;
    private ImageView imgPushIcon;
    private ProgressBar progressLoading;
    private SwipeRefreshLayout refresh;

    String token;

    private int page = 0;
    private boolean mIsLoading = false;
    private boolean isMore = false;
    private static final int limit = 10;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    private RecyclerTouchListener touchListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        token = sharedPref.getString("token", "");

        setHasOptionsMenu(true);

        refresh = view.findViewById(R.id.refresh);
        refresh.setColorSchemeColors(requireActivity().getColor(R.color.colorMain));
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mIsLoading) {
                    getDataWithoutLoading(true);
                } else refresh.setRefreshing(false);
            }
        });

        recycleItemChatContainer = view.findViewById(R.id.recycleItemChatContainer);
        progressLoading = view.findViewById(R.id.progressLoading);
        itemPushSetting = view.findViewById(R.id.itemPushSetting);
        itemPushSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().getPackageName());
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", requireActivity().getPackageName());
                    intent.putExtra("app_uid", requireActivity().getApplicationInfo().uid);
                } else {
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
                }
                requireActivity().startActivity(intent);
            }
        });
        tvPushTitle = view.findViewById(R.id.tvPushTitle);
        tvPushContent = view.findViewById(R.id.tvPushContent);

        String pushTitle = Metadata.getInstance().getData().getPush_off_title();
        String pushContent = Metadata.getInstance().getData().getPush_off_content();
        if (pushTitle != null) tvPushTitle.setText(pushTitle);
        if (pushContent != null) tvPushContent.setText(pushContent);
        imgPushIcon = view.findViewById(R.id.imgPushIcon);

        try {
            Glide.with(requireActivity())
                    .load(Metadata.getInstance().getData().getPush_off_image())
                    .apply(new RequestOptions().error(R.drawable.ic_image_solid_svg))
                    .into(imgPushIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        chatController = new ChatController(Config.API_URL);

        tvEmpty = view.findViewById(R.id.txtEmpty);
        if (getActivity() == null) {
            return;
        }

        setupListView();

        onListenerSocket();
    }

    private void onListenerSocket() {
        SocketClient.getInstance().addListener(new SocketClient.OnSocketListener() {
            @Override
            public void onReconnect(Object... args) {
            }

            @Override
            public void onDisconnect(Object... args) {

            }

            @Override
            public void onChatSendStatus(Object... args) {
            }

            @Override
            public void onChatReceive(Object... args) {
                if (MainActivity.isActivityVisible() && getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getChatList(true);
                        }
                    });
            }

            @Override
            public void onChatReadStatus(Object... args) {
            }

            @Override
            public void onChatTyping(Object... args) {
            }
        });
    }

    private void getChatList(final boolean isFirstPage) {
        progressLoading.setVisibility(View.VISIBLE);

        getDataWithoutLoading(isFirstPage);
    }

    private void getDataWithoutLoading(final boolean isFirstPage) {
        mIsLoading = true;
        if (isFirstPage) page = 0;
        else ++page;

        //Log.e("getDataWithoutLoading: ", " " + page + ": " + limit);

        chatController.getUserChat(token, page, limit).enqueue(new Callback<User.UserListResponse>() {
            @Override
            public void onResponse(Call<User.UserListResponse> call, final Response<User.UserListResponse> response) {
//                Log.e("Roster", call.request().toString());
//                Log.e("Roster", response.body().toString());

                refresh.setRefreshing(false);
                mIsLoading = false;
                progressLoading.setVisibility(View.GONE);
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                        if (response.body().getData().getUserList() == null || isFirstPage
                                && response.body().getData().getUserList().size() == 0)
                            tvEmpty.setVisibility(View.VISIBLE);
                        else tvEmpty.setVisibility(View.GONE);

                        final User.UserListResponse.Data result = response.body().getData();
                        if (result == null) return;
                        else if (!isFirstPage) adapter.addAll(result.getUserList());
                        else adapter.setList(result.getUserList());

                        if (result.getUserList() != null)
                            isMore = result.getUserList().size() >= limit;


                        if (isFirstPage) {
                            MessageStore.getInstance().unreadAllSubject.onNext(response.body().getData().getTotal_unread_all());
                            MessageStore.getInstance().unreadMessageSubject.onNext(response.body().getData().getTotal_unread_message());
                            MessageStore.getInstance().unreadNoticeSubject.onNext(response.body().getData().getTotal_unread_notice());
                            MessageStore.getInstance().unreadCampaignSubject.onNext(response.body().getData().getTotal_unread_campaign());
                        }

                    } else {
                        if (adapter.userModelList == null || adapter.userModelList.isEmpty())
                            tvEmpty.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<User.UserListResponse> call, Throwable t) {
                mIsLoading = false;
                refresh.setRefreshing(false);
                progressLoading.setVisibility(View.GONE);
                if (adapter.userModelList == null || adapter.userModelList.isEmpty())
                    tvEmpty.setVisibility(View.VISIBLE);
                t.printStackTrace();
            }
        });
    }

    public void setupListView() {
        layoutManager = new LinearLayoutManager(getActivity());
        recycleItemChatContainer.setLayoutManager(layoutManager);
        userModelList = new ArrayList<>();
        adapter = new ChatUserAdapter(getActivity(), userModelList);
        recycleItemChatContainer.setAdapter(adapter);
        recycleItemChatContainer.setNestedScrollingEnabled(false);
        recycleItemChatContainer.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            getChatList(false);

                            loading = true;
                        }
                    }
                }
            }
        });
        areNotificationsEnabled();

        touchListener = new RecyclerTouchListener(getActivity(), recycleItemChatContainer);
        touchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {
                    }
                })
                .setSwipeOptionViews(R.id.delete_roster, R.id.pin_roster)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID) {
                            case R.id.delete_roster:
                                try {
                                    if (!adapter.userModelList.isEmpty() &&
                                            position < adapter.userModelList.size())
                                        chatController.postDeleteRoster(token, adapter.userModelList.get(position).getUser_id()).enqueue(new Callback<Report>() {
                                            @Override
                                            public void onResponse(Call<Report> call, Response<Report> response) {
                                                if (response.isSuccessful()) {
                                                    getChatList(true);
                                                }
                                                //Log.e("kiemtraDelete", response.code() + " " + response.message());
                                            }

                                            @Override
                                            public void onFailure(Call<Report> call, Throwable t) {

                                            }
                                        });
                                } catch (Exception ignored) {
                                }
                                break;
                            case R.id.pin_roster:
                                try {
                                    if (!adapter.userModelList.isEmpty() &&
                                            position < adapter.userModelList.size())
                                        if (adapter.userModelList.get(position).getIs_pin_chat() == 0) {
                                            chatController.postPinRoster(token, adapter.userModelList.get(position).getUser_id()).enqueue(new Callback<Report>() {
                                                @Override
                                                public void onResponse(Call<Report> call, Response<Report> response) {
                                                    if (response.isSuccessful()) {
                                                        getChatList(true);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Report> call, Throwable t) {

                                                }
                                            });
                                            break;
                                        } else {
                                            chatController.postPinRoster(token, adapter.userModelList.get(position).getUser_id(), "true").enqueue(new Callback<Report>() {
                                                @Override
                                                public void onResponse(Call<Report> call, Response<Report> response) {
                                                    if (response.isSuccessful()) {
                                                        getChatList(true);
                                                    }
                                                    //Log.e("kiemtraPin", response.code() + " " + response.message());
                                                }

                                                @Override
                                                public void onFailure(Call<Report> call, Throwable t) {

                                                }
                                            });
                                            break;
                                        }
                                } catch (Exception ignored) {
                                }
                                break;

                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        recycleItemChatContainer.addOnItemTouchListener(touchListener);
        getChatList(true);
        onListenerSocket();
        areNotificationsEnabled();
        MessageStore.getInstance().listener = new ChatFragment.OnRefreshTab() {
            @Override
            public void clickPosition() {
                recycleItemChatContainer.addOnItemTouchListener(touchListener);
                getChatList(true);
                onListenerSocket();
                areNotificationsEnabled();
            }
        };
    }

    public boolean areNotificationsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (!manager.areNotificationsEnabled()) {
                itemPushSetting.setVisibility(View.VISIBLE);
                return false;
            }
            List<NotificationChannel> channels = manager.getNotificationChannels();
            for (NotificationChannel channel : channels) {
                if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                    itemPushSetting.setVisibility(View.VISIBLE);
                    return false;
                }
            }
            itemPushSetting.setVisibility(View.GONE);
            return true;
        } else {
            boolean notifiSetting = NotificationManagerCompat.from(getContext()).areNotificationsEnabled();
            if (notifiSetting) itemPushSetting.setVisibility(View.GONE);
            else
                itemPushSetting.setVisibility(View.VISIBLE);
            return notifiSetting;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.view_hon) {
            getChatList(true);
            return true;
        }
        return false;
    }

    public static ChatUserFragment newInstance() {
        return new ChatUserFragment();
    }


}
