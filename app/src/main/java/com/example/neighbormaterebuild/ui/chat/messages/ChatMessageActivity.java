package com.example.neighbormaterebuild.ui.chat.messages;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.comm.ExampleBottomSheetDialog;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.config.Constants;
import com.example.neighbormaterebuild.config.PointFee;
import com.example.neighbormaterebuild.controller.FavoriteController;
import com.example.neighbormaterebuild.controller.MessageController;
import com.example.neighbormaterebuild.controller.PointController;
import com.example.neighbormaterebuild.controller.UserListController;
import com.example.neighbormaterebuild.helper.NetworkReceiver;
import com.example.neighbormaterebuild.helper.OnSingleClickListener;
import com.example.neighbormaterebuild.model.Block;
import com.example.neighbormaterebuild.model.Favorites;
import com.example.neighbormaterebuild.model.MediaUpload;
import com.example.neighbormaterebuild.model.Message;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.Point;
import com.example.neighbormaterebuild.model.PointSetting;
import com.example.neighbormaterebuild.model.Report;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.network.HttpHeaders;
import com.example.neighbormaterebuild.network.SocketClient;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.example.neighbormaterebuild.ui.SplashActivity;
import com.example.neighbormaterebuild.utils.CShowProgress;
import com.example.neighbormaterebuild.utils.ImageUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatMessageActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener, NetworkReceiver.ConnectivityReceiverListener,
        ExampleBottomSheetDialog.BottomSheetListener {

    private static final String TAG = "ChatMessageActivity";
    private static final int SELECT_PHOTO = 1;
    private static final int RC_LOCATION = 100;
    private static final int RC_PHOTO = 101;

    private User user;

    private Toolbar toolbarMessage;
    private ImageButton imgbtnRefresh;
    private MessageListAdapter messageListAdapter;
    private TextView toolbar_text_fullname;
    private List<Message> messageList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recycleChatMessage;
    private LinearLayout layoutIsConnected;

    private LinearLayout menuContainer, btnSendImage, btnSendLocation, btnUnlimit, btnAddFavorite, btnReport;

    private ImageView imgUnLimit;
    private TextView tvUnLimit;
    private ImageButton imgbtn_bottom_sheet;
    private EditText edtMessage;
    private Button btnSend;
    private ProgressBar progressLoading;

    private int i = 0;
    private MessageController messageController;
    private UserListController userListController;
    private PointController pointController;
    private FavoriteController favoriteController;
    private CShowProgress cShowProgress;
    private ProgressBar progressBarLocation;



    private int page = 1;
    private final static int limit = 30;
    private boolean isMoreData = false;
    // initialise loading state
    private boolean mIsLoading = true;
    private boolean mIsLastPage = false;
    // amount of items you want to load per page
    private int pageSize = 20;

    public static final int REQUEST_PERMISSION_LOCATION = 100;


    private String token;

    private boolean isLoading, totallyLoaded;

    private static boolean activityVisible;
    private Message lastMessage;

    private BroadcastReceiver mNetworkReceiver;

    private FusedLocationProviderClient fusedLocationClient;

    private LocationCallback locationCallback;
    private Location mLocation;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ProfileStore.getInstance().getUserLogin() == null || Metadata.getInstance().getData() == null) {
            Intent mStartActivity = new Intent(ChatMessageActivity.this, SplashActivity.class);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mStartActivity);
            finish();
            Runtime.getRuntime().exit(0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        user = getIntent().getParcelableExtra("user");
        User.setInstance(user);
        if (user == null) finish();
        //Log.e("kiemtra", User.getInstance() + "");
        SharedPreferences sharedPref = getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE);
        token = sharedPref.getString("token", "");
        cShowProgress = new CShowProgress();

        initView();
        onListenerSocket();
        if (user != null)
            getProfile();
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterNetworkChanges();
        super.onDestroy();
    }

    private void initView() {
        mNetworkReceiver = new NetworkReceiver();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        toolbar_text_fullname = findViewById(R.id.toolbar_text_fullname);
        recycleChatMessage = findViewById(R.id.recycleChatMessage);
        imgbtn_bottom_sheet = findViewById(R.id.imgbtn_bottom_sheet);
        layoutIsConnected = findViewById(R.id.layoutIsConnected);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        imgbtnRefresh = findViewById(R.id.imgbtnRefresh);
        toolbarMessage = findViewById(R.id.toolbarMessage);
        menuContainer = findViewById(R.id.menuContainer);
        btnSendImage = findViewById(R.id.btnSendImage);
        btnSendLocation = findViewById(R.id.btnSendLocation);
        btnUnlimit = findViewById(R.id.btnUnlimit);
        btnAddFavorite = findViewById(R.id.btnAddFavorite);
        btnReport = findViewById(R.id.btnReport);


        imgUnLimit = findViewById(R.id.imgUnLimit);
        tvUnLimit = findViewById(R.id.tvUnLimit);

        toolbarMessage.setLogo(R.drawable.ic_back_svg);
        toolbarMessage.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgbtnRefresh.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                cShowProgress.showProgress(ChatMessageActivity.this);
                recycleChatMessage.setHasFixedSize(true);
                final NpaLinearLayoutManager linearLayoutManager = new NpaLinearLayoutManager(ChatMessageActivity.this);
                linearLayoutManager.setStackFromEnd(true);
                recycleChatMessage.setLayoutManager(linearLayoutManager);
                messageList = new ArrayList<>();
                messageListAdapter = new MessageListAdapter(ChatMessageActivity.this, messageList, User.getInstance());
                recycleChatMessage.setAdapter(messageListAdapter);
                getMessageList();
            }
        });


        progressLoading = findViewById(R.id.progressLoading);
        progressBarLocation = findViewById(R.id.progressBarLocation);

        messageController = new MessageController(Config.API_URL);
        userListController = new UserListController(Config.API_URL);
        pointController = new PointController(Config.API_URL);
        favoriteController = new FavoriteController(Config.API_URL);

        recycleChatMessage.setHasFixedSize(true);
        final NpaLinearLayoutManager linearLayoutManager = new NpaLinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recycleChatMessage.setLayoutManager(linearLayoutManager);
        messageList = new ArrayList<>();
        messageListAdapter = new MessageListAdapter(ChatMessageActivity.this, messageList, User.getInstance());
        recycleChatMessage.setAdapter(messageListAdapter);

        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        imgbtn_bottom_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i += 1;
                if (i % 2 == 0) {
                    imgbtn_bottom_sheet.setImageResource(R.drawable.ic_plus_svg);
                    menuContainer.setVisibility(View.GONE);
                } else {
                    imgbtn_bottom_sheet.setImageResource(R.drawable.ic_close_svg);
                    menuContainer.setVisibility(View.VISIBLE);
                }
            }
        });

        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                sendTyping(editable.toString());
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        btnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PointStore.getInstance().isUserEnoughPoint(PointFee.sendImage)) {
                    PointStore.getInstance().showDialogUserNotEnoughPoint(ChatMessageActivity.this);
                    return;
                }
                getImage();
            }
        });

        btnSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PointStore.getInstance().isUserEnoughPoint(PointFee.sendLocation)) {
                    PointStore.getInstance().showDialogUserNotEnoughPoint(ChatMessageActivity.this);
                    return;
                }
                sendLocation();
            }
        });

        btnUnlimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getUnlimit_point() != 1)
                    if (PointStore.getInstance().getPointSettings() != null && PointStore.getInstance().isUserEnoughPoint(PointFee.unlimitPoint)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ChatMessageActivity.this);
                        alertBuilder.setMessage(PointStore.getInstance().getPointUnLimit().getPoint() +
                                "pt でお相手へのメッセージ送信が永久無料で行えます。");
                        alertBuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                String userID = user.getId();
                                if (user.getId() == null || user.getId().isEmpty())
                                    userID = user.getUser_id();
                                pointController.postUnLimit(token, userID, user.getUser_code()).enqueue(new Callback<Point.PointResponse>() {
                                    @Override
                                    public void onResponse(Call<Point.PointResponse> call, Response<Point.PointResponse> response) {
                                        //Log.e(TAG, call.request().toString());
                                        //Log.e(TAG, response.body().toString());

                                        if (response.isSuccessful()) {
                                            PointStore.getInstance().consumePoint(PointFee.unlimitPoint);
                                            user.setUnlimit_point(1);
                                            imgUnLimit.setImageResource(R.drawable.ic_unlock_svg);
                                            tvUnLimit.setText("リミット解除中");

                                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ChatMessageActivity.this);
                                            alertBuilder.setTitle(response.body().getMessage());
                                            alertBuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            alertBuilder.show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Point.PointResponse> call, Throwable t) {

                                    }
                                });
                            }
                        });
                        alertBuilder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();
                            }
                        });
                        alertBuilder.show();
                    } else {
                        PointStore.showDialogUnlimit(ChatMessageActivity.this);
                    }
            }
        });

        btnAddFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null && (user.getFavorite_status().equals("0"))) {
                    String userID = user.getId();
                    if (user.getId() == null || user.getId().isEmpty()) userID = user.getUser_id();
                    favoriteController.postFavorite(token, user.getUser_code(), userID, 1).enqueue(new Callback<Favorites>() {
                        @Override
                        public void onResponse(Call<Favorites> call, Response<Favorites> response) {
                            if (response.isSuccessful()) {

                                user.setFavorite_status("1");
                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ChatMessageActivity.this);
                                alertBuilder.setMessage(response.body().getData());
                                alertBuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alertBuilder.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Favorites> call, Throwable t) {

                        }
                    });
                } else {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ChatMessageActivity.this);
                    alertBuilder.setMessage("お気に入りに追加しました");
                    alertBuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                }
            }
        });

        final ExampleBottomSheetDialog bottomSheet = new ExampleBottomSheetDialog(ChatMessageActivity.this);


        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ExampleBottomSheetDialog(ChatMessageActivity.this).show(getSupportFragmentManager(), null);
            }
        });
    }

    private void isLoading(boolean loading) {
        if (!this.isFinishing()) {
            mIsLoading = loading;
            if (loading) cShowProgress.showProgress(ChatMessageActivity.this);
            else cShowProgress.hideProgress();
        }
        btnSend.setEnabled(!loading);
        imgbtn_bottom_sheet.setEnabled(!loading);
        btnSendImage.setEnabled(!loading);
        btnSendLocation.setEnabled(!loading);
        btnUnlimit.setEnabled(!loading);
    }


    private void onListenerSocket() {
        SocketClient.getInstance().addListener(new SocketClient.OnSocketListener() {
            @Override
            public void onReconnect(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isLoading(false);
                    }
                });
            }

            @Override
            public void onDisconnect(Object... args) {
                if (isActivityVisible())
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isLoading(true);
                        }
                    });
            }

            @Override
            public void onChatSendStatus(Object... args) {

                final Message newMessage = new Gson().fromJson(args[0].toString(), Message.class);
                lastMessage = newMessage;
                lastMessage.setIsRead(lastMessage.getReceived());
                switch (newMessage.getType()) {
                    case Message.text:
                        newMessage.setMsg(Metadata.NGWord.replaceRanges(newMessage.getMsg()));
                        if (user.getUnlimit_point() != 1 && user.getFree_chat() != 1)
                            PointStore.getInstance().consumePoint(PointFee.sendMessage);
                        break;
                    case Message.image:
                        PointStore.getInstance().consumePoint(PointFee.sendImage);
                        break;
                    case Message.location:
                        PointStore.getInstance().consumePoint(PointFee.sendLocation);
                        break;
                }
                final int lastIndex = messageList.size();
                messageList.add(newMessage);
                if (isActivityVisible())
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageListAdapter.notifyItemRangeInserted(lastIndex, messageList.size());
                            recycleChatMessage.smoothScrollToPosition(messageList.size());
                        }
                    });
            }

            @Override
            public void onChatReceive(Object... args) {
                final Message newMessage = new Gson().fromJson(args[0].toString(), Message.class);
                if (newMessage.getType().equals(Message.text))
                    newMessage.setMsg(Metadata.NGWord.replaceRanges(newMessage.getMsg()));

                if (newMessage.getuID() == getUserID()) {
                    lastMessage = newMessage;
                    final int lastIndex = messageList.size();
                    messageList.add(newMessage);
                    if (isActivityVisible()) {
                        SocketClient.getInstance().sendReadMessage(lastMessage.getuID(), lastMessage.getTimeNotConvert());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageListAdapter.notifyItemRangeInserted(lastIndex, messageList.size());
                                recycleChatMessage.smoothScrollToPosition(messageList.size());
                            }
                        });
                    }
                }
            }

            @Override
            public void onChatReadStatus(Object... args) {
                final Message newMessage = new Gson().fromJson(args[0].toString(), Message.class);

                if (newMessage.getrID() == getUserID())
                    for (int i = messageList.size() - 1; i >= 0; i--) {
                        final Message item = messageList.get(i);
                        if (ProfileStore.getInstance().getUserLogin().getId().equals("" + item.getuID())) {
                            if (item.getIsRead() != 0) item.setIsRead(0);
                            else break;
                            //Log.e("EVENT_CHAT_IS_READ", "" + item);
                        }
                    }

                if (isActivityVisible())
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageListAdapter.notifyDataSetChanged();
                        }
                    });
            }

            @Override
            public void onChatTyping(Object... args) {
                //TODO: typing
            }
        });
    }

    private int getUserID() {
        String userID = user.getId();
        if (user.getId() == null || user.getId().isEmpty()) userID = user.getUser_id();
        return Integer.parseInt(userID);
    }

    private void getProfile() {
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
                    Log.e("[Log] user::: ", user.toString());
                    if (user == null) finish();
                    toolbar_text_fullname.setText(String.format("%s", user.getDisplayname()));
                    if (user.getUnlimit_point() == 1) {
                        imgUnLimit.setImageResource(R.drawable.ic_unlock_svg);
                        tvUnLimit.setText("リミット解除中");
                    } else {
                        imgUnLimit.setImageResource(R.drawable.ic_lock_svg);
                        tvUnLimit.setText("リミット解除");
                    }
                    getMessageList();

                }
            }

            @Override
            public void onFailure(Call<PointSetting.Response> call, Throwable t) {

            }
        });
    }

    private void getMessageList() {
        mIsLoading = true;
        page = 0;
        messageController.getMessage(token, user.getUser_code(), String.valueOf(getUserID()), page, limit).enqueue(new Callback<Message.MessageResponse>() {
            @Override
            public void onResponse(Call<Message.MessageResponse> call, Response<Message.MessageResponse> response) {
                if (response.isSuccessful()) {
                    //Log.e(TAG, response.body() + " ");
                    progressLoading.setVisibility(View.GONE);

                    messageList.addAll(0, response.body().getData().getMessageList());
                    Collections.reverse(messageList);
                    messageListAdapter.notifyItemRangeInserted(0, messageList.size());
                    if (!response.body().getData().getMessageList().isEmpty()) {
                        lastMessage = response.body().getData().getMessageList().get(0);
                        SocketClient.getInstance().sendReadMessage(lastMessage.getuID(), lastMessage.getTimeNotConvert());
                        isMoreData = response.body().getData().getMessageList().size() >= limit;
                    }
                    cShowProgress.hideProgress();
                }
                //Log.e("kiemtra", response.message() + " " + response.code());
                mIsLoading = false;
            }

            @Override
            public void onFailure(Call<Message.MessageResponse> call, Throwable t) {
                t.printStackTrace();
                mIsLoading = false;
            }
        });
    }

    private void getMessageListLoad() {
        mIsLoading = true;
        page += 1;
        final Message message = messageList.get(0);
        //Log.e("mesageID", message.getMsgID());
        messageController.getMessage(token, user.getUser_code(), String.valueOf(getUserID()), page, limit, message.getMsgID()).enqueue(new Callback<Message.MessageResponse>() {
            @Override
            public void onResponse(Call<Message.MessageResponse> call, Response<Message.MessageResponse> response) {
                if (response.isSuccessful()) {
                    if (!response.body().getData().getMessageList().isEmpty()) {
                        List<Message> newData = response.body().getData().getMessageList();
                        Collections.reverse(newData);
                        messageList.addAll(0, newData);
                        recycleChatMessage.getRecycledViewPool().clear();
                        messageListAdapter.notifyDataSetChanged();
                        isMoreData = newData.size() >= limit;
                    }

                }
                mIsLoading = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Message.MessageResponse> call, Throwable t) {
                t.printStackTrace();
                mIsLoading = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void sendMessage() {
        String userID = user.getId();
        if (user.getId() == null || user.getId().isEmpty()) userID = user.getUser_id();
        String message = edtMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            if (PointStore.getInstance().isUserEnoughPoint(PointFee.sendMessage) ||
                    user.getFree_chat() == 1 ||
                    user.getUnlimit_point() == 1 ||
                    Metadata.getInstance().getData().getSupporter().getId().equals((userID))
            ) {
                SocketClient.getInstance().sendMessage(message, userID, "chatting");
                edtMessage.getText().clear();
            } else {
                PointStore.getInstance().showDialogUserNotEnoughPoint(ChatMessageActivity.this);
            }

        }
    }

    private void getImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_PHOTO);
    }

    private void sendImage(File file) {

        isLoading(true);


        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/" + ImageUtil.getFileType(file.getPath())), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file",
                file.getAbsolutePath(),
                fileReqBody);

        RequestBody tokenBody = RequestBody.create(MediaType.parse("multipart/form-data"), token);
        RequestBody typeBody = RequestBody.create(MediaType.parse("multipart/form-data"), "1");
        RequestBody sectorBody = RequestBody.create(MediaType.parse("multipart/form-data"), "chat");

        messageController.sendImage(body, tokenBody, typeBody, sectorBody)
                .enqueue(new Callback<MediaUpload.MediaUploadResponse>() {
                    @Override
                    public void onResponse(Call<MediaUpload.MediaUploadResponse> call,
                                           Response<MediaUpload.MediaUploadResponse> response) {
                        isLoading(false);


                        if (response.body() != null && response.body().getData() != null) {
                            MediaUpload fileUpload = response.body().getData();
                            JSONObject file = new JSONObject();
                            try {
                                file.put("type", fileUpload.getType());
                                file.put("file_id", fileUpload.getMediaID());
                                file.put("file_name", fileUpload.getName());
                                file.put("ext", fileUpload.getExt());
                                file.put("size", fileUpload.getSize());
                                file.put("mime", fileUpload.getMime());

                                Log.e(TAG, "file: " + file.toString());
                            } catch (Exception e) {

                            }
                            String userID = user.getId();
                            if (user.getId() == null || user.getId().isEmpty())
                                userID = user.getUser_id();
                            SocketClient.getInstance().sendImage(file, userID, "chatting");
                        } else {
                            if (ChatMessageActivity.this.hasWindowFocus())
                                new AlertDialog.Builder(ChatMessageActivity.this)
                                        .setMessage("このファイルタイプは送信できません。")
                                        .setPositiveButton("はい", null)
                                        .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MediaUpload.MediaUploadResponse> call, Throwable t) {
                        isLoading(false);
                        t.printStackTrace();
                    }
                });
    }

    private void sendLocation() {
        if (checkLocationPermission())
            if (mLocation == null) {
                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBarLocation.setVisibility(View.GONE);
                            }
                        });
                        //Log.e("Location Update:", locationResult + "");
                        onLocationChanged(locationResult.getLastLocation());
                        stopLocationUpdates();
                    }
                };
                if (fusedLocationClient == null)
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(ChatMessageActivity.this);
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(ChatMessageActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                //Log.e(TAG, "Location: " + location);
                                if (location != null) {
                                    mLocation = location;
                                    JSONObject data = new JSONObject();
                                    try {
                                        data.put("lat", location.getLatitude());
                                        data.put("long", location.getLongitude());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String userID = user.getId();
                                    if (user.getId() == null || user.getId().isEmpty())
                                        userID = user.getUser_id();
                                    SocketClient.getInstance().sendLocation(data, userID, "chatting");
                                } else {
                                    startLocationUpdates();
                                }
                            }
                        });
            } else {
                JSONObject data = new JSONObject();
                try {
                    data.put("lat", mLocation.getLatitude());
                    data.put("long", mLocation.getLongitude());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String userID = user.getId();
                if (user.getId() == null || user.getId().isEmpty())
                    userID = user.getUser_id();
                SocketClient.getInstance().sendLocation(data, userID, "chatting");
            }
    }

    private boolean _typing = false;
    private CountDownTimer _timer;

    private void sendTyping(String text) {
        if (_timer == null)
            _timer = new CountDownTimer(6000, 1000) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    _typing = false;
                    String userID = user.getId();
                    if (user.getId() == null || user.getId().isEmpty()) userID = user.getUser_id();
                    SocketClient.getInstance().sendTyping(userID, 0);
                }
            };

        if (_typing == false) {
            _typing = true;
            String userID = user.getId();
            if (user.getId() == null || user.getId().isEmpty()) userID = user.getUser_id();
            SocketClient.getInstance().sendTyping(userID, 1);
            _timer.start();
        } else {
            if (text.isEmpty()) {
                _typing = false;
                _timer.cancel();
                String userID = user.getId();
                if (user.getId() == null || user.getId().isEmpty()) userID = user.getUser_id();
                SocketClient.getInstance().sendTyping(userID, 0);
            }
        }
    }

    @Override
    public void onButtonClickedLock(UserListController userListController) {
        String userID = user.getId();
        if (user.getId() == null || user.getId().isEmpty()) userID = user.getUser_id();
        userListController.userLock(token, userID, user.getUser_code(), 1).enqueue(new Callback<Block>() {
            @Override
            public void onResponse(Call<Block> call, Response<Block> response) {
                if (response.isSuccessful()) {
                    try {
                        new AlertDialog.Builder(ChatMessageActivity.this)
                                .setMessage(response.body().getMessage())
                                .setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    } catch (Exception e) {
                    }

                }
            }

            @Override
            public void onFailure(Call<Block> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onButtonClickedReport(UserListController userListController, EditText edtReport, final Dialog dialog) {
        String userID = user.getId();
        if (user.getId() == null || user.getId().isEmpty()) userID = user.getUser_id();
        userListController.userReport(token, userID, user.getUser_code(), edtReport.getText().toString().trim(), "chatting").enqueue(new Callback<Report>() {
            @Override
            public void onResponse(Call<Report> call, Response<Report> response) {
                if (response.isSuccessful()) {
                    dialog.cancel();
                    try {
                        new AlertDialog.Builder(ChatMessageActivity.this)
                                .setMessage(response.body().getMessage())
                                .setPositiveButton("はい", null)
                                .show();
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Report> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            //Log.e(TAG, "ImageUri: " + imageUri);

            final File file_resize = ImageUtil.resizeImageFile(this, imageUri);

            if (file_resize != null) {
                sendImage(file_resize);
            } else {
                Toast.makeText(this,
                        "このファイルタイプは送信できません。",
                        Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == Constants.GPS_REQUEST) {
            switch (resultCode) {
                case RESULT_OK:
                    // All required changes were successfully made
                    startLocationUpdates();
                    break;
                case RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(this, "位置情報サービスがオフです", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (fusedLocationClient == null)
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(ChatMessageActivity.this);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(ChatMessageActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                //Log.e(TAG, "Location: " + location);
                                if (location != null) {
                                    mLocation = location;
                                    JSONObject data = new JSONObject();
                                    try {
                                        data.put("lat", location.getLatitude());
                                        data.put("long", location.getLongitude());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String userID = user.getId();
                                    if (user.getId() == null || user.getId().isEmpty())
                                        userID = user.getUser_id();
                                    SocketClient.getInstance().sendLocation(data, userID, "chatting");
                                } else {
                                    startLocationUpdates();
                                }
                            }
                        });
                return;
            } else {
                //Denied
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityPaused();
        if (locationCallback != null) stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityResumed();
        //Log.e("Chatting:::::", " 1 " + isActivityVisible());
        if (lastMessage != null) {
            SocketClient.getInstance().sendReadMessage(lastMessage.getuID(), lastMessage.getTimeNotConvert());
        }
        if (messageListAdapter != null) {
            messageListAdapter.notifyDataSetChanged();
        }
        if (recycleChatMessage != null && messageList != null) {
            recycleChatMessage.smoothScrollToPosition(messageList.size());
        }
        NetworkReceiver.connectivityReceiverListener = this;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(ChatMessageActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ChatMessageActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ChatMessageActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Log.e(TAG, "PERMISSION_DENIED_NEVER_ASK");

                new AlertDialog.Builder(ChatMessageActivity.this)
                        .setTitle("位置情報サービスがオフです")
                        .setMessage("位置情報サービスが無効です。設定から" + HttpHeaders.getInstance().getAppName() + "を開き、" +
                                "「位置情報」を「次回確認」または「このAppの使用中のみ許可」に変更することで、" +
                                "GPS情報の送受信ができるようになります。")
                        .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .create()
                        .show();
            } else {
                //Log.e(TAG, "PERMISSION_DENIED");
                ActivityCompat.requestPermissions(ChatMessageActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        RC_LOCATION);
            }
            return false;
        } else {
            //Log.e(TAG, "PERMISSION_GRANTED");
            return true;
        }
    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarLocation.setVisibility(View.VISIBLE);
                }
            });
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(10 * 1000);
            mLocationRequest.setFastestInterval(2000);

            // Create LocationSettingsRequest object using location request
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();

            // Check whether location settings are satisfied
            // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
            SettingsClient settingsClient = LocationServices.getSettingsClient(this);
            settingsClient.checkLocationSettings(locationSettingsRequest).addOnFailureListener(ChatMessageActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(ChatMessageActivity.this, Constants.GPS_REQUEST);
                            } catch (IntentSender.SendIntentException sie) {
                                //Log.i(TAG, "PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "位置情報サービスがオフです。設定を修正してください。";
                            //Log.e(TAG, errorMessage);
                            Toast.makeText(ChatMessageActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
            if (fusedLocationClient != null)
                fusedLocationClient.requestLocationUpdates(mLocationRequest,
                        locationCallback,
                        Looper.getMainLooper());
        } else {
            new AlertDialog.Builder(ChatMessageActivity.this)
                    .setTitle("位置情報サービスがオフです")
                    .setMessage("位置情報サービスが無効です。設定から" + HttpHeaders.getInstance().getAppName() + "を開き、" +
                            "「位置情報」を「次回確認」または「このAppの使用中のみ許可」に変更することで、" +
                            "GPS情報の送受信ができるようになります。")
                    .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .create()
                    .show();
        }
    }

    public void onLocationChanged(Location location) {
        this.mLocation = location;
        JSONObject data = new JSONObject();
        try {
            data.put("lat", location.getLatitude());
            data.put("long", location.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SocketClient.getInstance().sendLocation(data, getUserID() + "", "chatting");
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null)
            fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRefresh() {
        if (isMoreData && !mIsLoading) {
            getMessageListLoad();
        } else
            mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onNetworkConnectionChanged(Boolean isConnected) {
        if (isConnected) {
            SocketClient.getInstance().connectSocket();
            cShowProgress.hideProgress();
            btnSend.setEnabled(true);
            imgbtn_bottom_sheet.setEnabled(true);
            btnSendImage.setEnabled(true);
            btnSendLocation.setEnabled(true);
            btnUnlimit.setEnabled(true);
            layoutIsConnected.setVisibility(View.GONE);
        } else {
            SocketClient.getInstance().disconnectSocket();
            layoutIsConnected.setVisibility(View.VISIBLE);
        }
    }
}
