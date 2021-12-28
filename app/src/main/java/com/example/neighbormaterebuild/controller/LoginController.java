package com.example.neighbormaterebuild.controller;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.CountDownTimer;

import androidx.appcompat.app.AlertDialog;

import com.example.neighbormaterebuild.network.HttpHeaders;
import com.example.neighbormaterebuild.network.SocketClient;
import com.example.neighbormaterebuild.store.MessageStore;
import com.example.neighbormaterebuild.store.PointStore;
import com.example.neighbormaterebuild.store.ProfileStore;
import com.example.neighbormaterebuild.ui.MainActivity;
import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.model.BaseResponse;
import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.model.PointSetting;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.LoginService;
import com.example.neighbormaterebuild.service.PointService;
import com.example.neighbormaterebuild.service.PushTokenService;
import com.example.neighbormaterebuild.ui.RegisterActivity;
import com.example.neighbormaterebuild.utils.FileDeviceID;
import com.example.neighbormaterebuild.utils.FileService;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginController {

    private static final String TAG = "LoginController";

    private String baseUrl;
    private Context context;
    private MetadataController metadataController;
    private ChatController chatController;
    private PointService pointService;

    private LoginService loginService;

    private FileService fileService;
    private FileDeviceID fileDeviceID;

    public LoginController(String baseUrl, Context context) {
        this.baseUrl = baseUrl;
        this.context = context;
        metadataController = new MetadataController(baseUrl);
        loginService = Client.getClient(baseUrl).create(LoginService.class);
        chatController = new ChatController(baseUrl);

        fileService = new FileService(context);
        fileDeviceID = new FileDeviceID(context);

    }

    public void checkLogin(final User user) throws ExecutionException, InterruptedException {
        reCallMetadata(user);
    }

    public void reCallMetadata(final User user) {

        final String password;
//        Log.e(TAG, "Device_password" + user.getDevice_password());
//        Log.e(TAG, "Password" + user.getPassword());

        if (user.getDevice_password() != null) {
            password = user.getDevice_password();
        } else {
            password = user.getPassword();
        }
        metadataController.getResultMetadata().enqueue(new Callback<Metadata>() {
            @Override
            public void onResponse(Call<Metadata> call, Response<Metadata> response) {
                Metadata.setInstance(response.body());
                if (Metadata.getInstance().getData() != null) {
                    if (Metadata.getInstance().getData().getFunction_off().getForce_update_version() != null
                            && !Metadata.getInstance().getData().getFunction_off().getForce_update_version().isEmpty()) {
                        if(!((Activity) context).isFinishing())
                            new AlertDialog.Builder(context)
                                    .setTitle(Metadata.getInstance().getData().getFunction_off().getForce_update_title())
                                    .setMessage(Metadata.getInstance().getData().getFunction_off().getForce_update_description())
                                    .setPositiveButton("アップデート", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                            browserIntent.setData(Uri.parse(Metadata.getInstance().getData().getFunction_off().getNew_version_url()));
                                            if (browserIntent.resolveActivity(context.getPackageManager()) != null) {
                                                context.startActivity(browserIntent);
                                            }
                                        }
                                    })
                                    .setCancelable(false)
                                    .create()
                                    .show();
                        return;
                    }

                    Call<User.UserResponse> userResponseCall = loginService.checkLogin(user.getUser_code(), password);

                    userResponseCall.enqueue(new Callback<User.UserResponse>() {
                        @Override
                        public void onResponse(Call<User.UserResponse> call, Response<User.UserResponse> response) {
//                            Log.e("Login controller:", call.request().toString());
//                            Log.e("Login controller:", response.code() + "");
//                            Log.e("Login controller:", response.message());

                            if (response.code() == 200) {

                                pointService = Client.getClient(Config.API_URL).create(PointService.class);
                                Call<PointSetting.Response> getResultPointSetting = pointService.getResultPointSetting(response.body().getUser().getTokken());
                                getResultPointSetting.enqueue(new Callback<PointSetting.Response>() {
                                    @Override
                                    public void onResponse(Call<PointSetting.Response> call, Response<PointSetting.Response> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            try {
                                                PointStore.getInstance().setTotalPoint(response.body().getData().getTotalPoint());
                                                PointStore.getInstance().setPointSettings(response.body().getData().getPointList());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<PointSetting.Response> call, Throwable t) {

                                    }
                                });

                                pushToken();
                                SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE).edit();
                                editor.putString("token", response.body().getUser().getTokken());
                                editor.putString("user", response.body().getUser().getId());
                                editor.putString("userCode", response.body().getUser().getUser_code());
                                editor.apply();

                                // TODO: write user login
                                String isDataLogin = fileDeviceID.readDeviceID();
                                if(isDataLogin == null) {
                                    fileService.writeFile(HttpHeaders.getInstance().getDeviceID(), response.body().getUser().getUser_code(), password);
                                    fileDeviceID.writeDeviceID();
                                }

                                ProfileStore.getInstance().setUserLogin(response.body().getUser());

                                if (SocketClient.getInstance().isNull())
                                    SocketClient.getInstance().create(response.body().getUser().getSocket_jwt());

                                chatController.getUserChat(response.body().getUser().getTokken(), 0, 10)
                                        .enqueue(new Callback<User.UserListResponse>() {
                                            @Override
                                            public void onResponse(Call<User.UserListResponse> call, Response<User.UserListResponse> response) {
                                                if (response.isSuccessful())
                                                    try {
                                                        User.UserListResponse.Data.setInstance(response.body().getData());

                                                        MessageStore.getInstance().unreadAllSubject.onNext(response.body().getData().getTotal_unread_all());
                                                        MessageStore.getInstance().unreadMessageSubject.onNext(response.body().getData().getTotal_unread_message());
                                                        MessageStore.getInstance().unreadNoticeSubject.onNext(response.body().getData().getTotal_unread_notice());
                                                        MessageStore.getInstance().unreadCampaignSubject.onNext(response.body().getData().getTotal_unread_campaign());
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                            }

                                            @Override
                                            public void onFailure(Call<User.UserListResponse> call, Throwable t) {

                                            }
                                        });

                                //Log.e("User::::::", response.body().getUser().toString());
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.putExtra("userLogin", response.body().getUser());
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            } else if (response.code() == 400) {
                                Intent intent = new Intent(context, RegisterActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            } else if (response.code() == 401) {
                                String message = "ログインが正しくありません";
                                try {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<User.UserResponse>() {
                                    }.getType();
                                    User.UserResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                                    if (errorResponse != null && errorResponse.getMessage() != null)
                                        message = errorResponse.getMessage();
                                } catch (Exception ignored) {
                                }
                                try {
                                    if (((Activity) context).hasWindowFocus())
                                        new AlertDialog.Builder(context)
                                                .setCancelable(false)
                                                .setMessage(message)
                                                .setPositiveButton("閉じる", null)
                                                .show();
                                } catch (Exception ignored) {
                                }
                            } else if (response.code() == 500) {
                                try {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<User.UserResponse>() {
                                    }.getType();
                                    User.UserResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                                    android.text.Html.fromHtml(errorResponse.getMessage()).toString();
                                    String[] lines = android.text.Html.fromHtml(errorResponse.getMessage()).toString().split("\\n");

                                    final String[] mails = errorResponse.getMessage().split("href='mailto:")[1].split("subject=")[0].substring(0,
                                            errorResponse.getMessage().split("href='mailto:")[1].split("subject=")[0].length() - 1).split(",");
                                    final String subject = errorResponse.getMessage().split("subject=")[1].split("&body")[0];
                                    final String body = errorResponse.getMessage().split("&body=")[1].split("'>")[0];

                                    if (((Activity) context).hasWindowFocus())
                                        new AlertDialog.Builder(context)
                                                .setMessage(lines[0] + "\n" + lines[1] + "\n" + lines[3])
                                                .setNegativeButton("メールを送信する", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                                                        intent.setData(Uri.parse("mailto:"));
                                                        intent.putExtra(Intent.EXTRA_EMAIL, mails);
                                                        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                                        intent.putExtra(Intent.EXTRA_TEXT, body);
                                                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                                                            context.startActivity(Intent.createChooser(intent, "Send mail"));
                                                        }
                                                    }
                                                })
                                                .setCancelable(false)
                                                .setPositiveButton("閉じる", null)
                                                .create()
                                                .show();
                                } catch (Exception e) {
                                    //e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<User.UserResponse> call, Throwable t) {
                            FirebaseCrashlytics.getInstance().recordException(t);
                            t.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void onFailure(final Call<Metadata> call, Throwable t) {
                new CountDownTimer(5000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        reCallMetadata(user);
                    }
                }.start();
                FirebaseCrashlytics.getInstance().recordException(t);
            }
        });
    }

    public void pushToken() {
        Client.getClient(Config.API_URL).create(PushTokenService.class)
                .pushToken().enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                //Log.e(TAG, "Header:" + HttpHeaders.getInstance().headers());
                //Log.e(TAG, "Push token: " + response.code() + ": " + response.message());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                //Log.e(TAG, "Push token to server fail" + t);
                //Log.e(TAG, "Push token to server fail" + call.request());
                t.printStackTrace();
            }
        });
    }
}
