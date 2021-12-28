package com.example.neighbormaterebuild.controller;

import com.example.neighbormaterebuild.model.BaseResponse;
import com.example.neighbormaterebuild.model.Block;
import com.example.neighbormaterebuild.model.Report;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.UserListService;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class UserListController implements UserListService {

    private String baseUrl;

    private UserListService userListService;

    public UserListController(String baseUrl) {
        this.baseUrl = baseUrl;
        userListService = Client.getClient(baseUrl).create(UserListService.class);
    }

    @Override
    public Call<User.UserListResponse> getUserList(String token, int page, int limit) {
        Call<User.UserListResponse> userListResponseCall = userListService.getUserList(token, page, limit);
        return userListResponseCall;
    }

    @Override
    public Call<User.UserListResponse> getUserListFind(String token, HashMap<String, String> fills, int page, int limit) {
        Call<User.UserListResponse> userListResponseCall = userListService.getUserListFind(token, fills, page, limit);
        return userListResponseCall;
    }

    @Override
    public Call<User.UserResponse> getUserProfile(String token, String id) {
        Call<User.UserResponse> userResponseCall = userListService.getUserProfile(token, id);
        return userResponseCall;
    }

    @Override
    public Call<Block> userLock(String token, String id, String lockUserCode, int type) {
        return userListService.userLock(token, id, lockUserCode, type);
    }


    @Override
    public Call<Report> userReport(String token, String id, String lockUserCode, String comment, String screen) {
        return userListService.userReport(token, id, lockUserCode, comment, screen);

    }

    @Override
    public Call<Block> keijibanLock(String token, String id, String lockUserCode, int type) {
        return userListService.keijibanLock(token, id, lockUserCode, type);
    }

    @Override
    public Call<Report> keijibanReport(String token, String id, String lockUserCode, String comment, String screen) {
        return userListService.keijibanReport(token, id, lockUserCode, comment, screen);
    }

    @Override
    public Call<User.UserResponse> userUpdateProfile(String token, HashMap<String, String> result) {
        return userListService.userUpdateProfile(token, result);
    }

    @Override
    public Call<Report> userUpdateImage(HashMap<String, RequestBody> result, MultipartBody.Part images) {
        return userListService.userUpdateImage(result, images);
    }

    @Override
    public Call<BaseResponse> viewImageProfile(String userId, String karaId, int imgIndex) {
        return userListService.viewImageProfile(userId, karaId, imgIndex);
    }
}

