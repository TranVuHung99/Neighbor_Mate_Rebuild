package com.example.neighbormaterebuild.controller;

import android.content.Context;

import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.BlockService;

import retrofit2.Call;

public class BlockController implements BlockService {
    private String baseUrl;
    private Context context;

    private BlockService service;

    public BlockController(String baseUrl, Context context) {
        this.baseUrl = baseUrl;
        this.context = context;
        service = Client.getClient(baseUrl).create(BlockService.class);
    }

    @Override
    public Call<User.UserListResponse> getList(String token, int page, int limit) {
        return service.getList(token, page, limit);
    }

    @Override
    public Call<User.UserResponse> unLock(String token, String id, String user_code, String type) {
        Call<User.UserResponse> response = service.unLock(token, id, user_code, "0");
        return response;
    }
}
