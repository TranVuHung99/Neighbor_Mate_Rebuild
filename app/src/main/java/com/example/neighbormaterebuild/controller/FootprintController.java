package com.example.neighbormaterebuild.controller;

import android.content.Context;

import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.FootprintService;

import retrofit2.Call;

public class FootprintController implements FootprintService {
    private String baseUrl;

    private FootprintService sevice;

    public FootprintController(String baseUrl) {
        this.baseUrl = baseUrl;
        sevice = Client.getClient(baseUrl).create(FootprintService.class);
    }

    @Override
    public Call<User.UserListResponse> getList(String token, int page, int limit) {
        return sevice.getList(token, page, limit);
    }
}
