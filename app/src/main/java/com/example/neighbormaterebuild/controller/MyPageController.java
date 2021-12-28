package com.example.neighbormaterebuild.controller;

import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.MyPageService;

import io.reactivex.Observable;
import retrofit2.Call;

public class MyPageController implements MyPageService {

    private String baseUrl;

    private MyPageService myPageService;

    public MyPageController(String baseUrl) {
        this.baseUrl = baseUrl;
        myPageService = Client.getClient(baseUrl).create(MyPageService.class);
    }

    @Override
    public Call<User.UserResponse> getUserProfile(String token) {
        Call<User.UserResponse> userResponseCall = myPageService.getUserProfile(token);
        return userResponseCall;
    }

    @Override
    public Observable<User.UserResponse> getUserProfileByID(String token, String id) {
        return myPageService.getUserProfileByID(token, id);
    }

    @Override
    public Call<User.UserTranfer> postUserDeviceTransfer(String email, String passworrd) {
        return myPageService.postUserDeviceTransfer(email, passworrd);
    }

    @Override
    public Call<User.UserTranfer> postUserDevicePrepare(String email, String passworrd, String device_password, String token) {
        return myPageService.postUserDevicePrepare(email, passworrd, device_password, token);
    }
}
