package com.example.neighbormaterebuild.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.example.neighbormaterebuild.config.Constants;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.network.HttpHeaders;
import com.example.neighbormaterebuild.service.RegisterService;
import com.example.neighbormaterebuild.utils.CShowProgress;
import com.example.neighbormaterebuild.utils.FileDeviceID;
import com.example.neighbormaterebuild.utils.FileService;

import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterController {

    private RegisterService registerService;
    private String baseUrl;
    private Context context;
    private FileService fileService;
    private FileDeviceID fileDeviceID;
    private LoginController loginController;

    public RegisterController(String baseUrl, Context context) {
        this.baseUrl = baseUrl;
        this.context = context;
        registerService = Client.getClient(baseUrl).create(RegisterService.class);
        fileService = new FileService(context);
        fileDeviceID = new FileDeviceID(context);
        loginController = new LoginController(baseUrl, context);
    }

    public void registerUser(final User user) {
        //Log.e("User:", user.toString());
        CShowProgress.getInstance().showProgress(context);
        Call<User.UserResponse> userCall = registerService.createUser(user.getDisplayname(), user.getPassword(),
                user.getSex(), user.getAge(), user.getArea(), user.getCity_id(),
                HttpHeaders.getInstance().getCarrierName(), HttpHeaders.getInstance().getCarrierCode());
        userCall.enqueue(new Callback<User.UserResponse>() {
            @Override
            public void onResponse(Call<User.UserResponse> call, Response<User.UserResponse> response) {
                //Log.d("Call request", call.request().toString());
                //Log.d("Call request header", call.request().headers().toString());
                //Log.d("Response raw header", response.headers().toString());
                //Log.d("Response raw", String.valueOf(response.raw().body()));
                //Log.d("Response code", String.valueOf(response.code()));
                if (response.isSuccessful()) {
                    AdjustEvent adjustEvent = new AdjustEvent(Constants.ADJUST_EVENT_REGISTER);
                    Adjust.trackEvent(adjustEvent);

                    SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS_NAME", Context.MODE_PRIVATE).edit();
                    editor.putString("token", response.body().getUser().getTokken());
                    editor.apply();
                    fileDeviceID.writeDeviceID();
                    try {
                        User userRegis = response.body().getUser();
                        userRegis = response.body().getUser();
                        userRegis.setPassword(user.getPassword());

                        fileDeviceID.writeDeviceID();
                        loginController.checkLogin(userRegis);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    //Log.d("Response errorBody", String.valueOf(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<User.UserResponse> call, Throwable t) {
                t.printStackTrace();
                CShowProgress.getInstance().hideProgress();
            }
        });
    }
}
