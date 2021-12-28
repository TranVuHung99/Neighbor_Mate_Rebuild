package com.example.neighbormaterebuild.controller;

import android.content.Context;

import com.example.neighbormaterebuild.model.Favorites;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.FavoriteService;

import retrofit2.Call;

public class FavoriteController implements FavoriteService {
    private String baseUrl;

    private FavoriteService service;

    public FavoriteController(String baseUrl) {
        this.baseUrl = baseUrl;
        service = Client.getClient(baseUrl).create(FavoriteService.class);
    }

    @Override
    public Call<Favorites> postFavorite(String token, String favorites_user_code, String favorites_id, int type) {
        return service.postFavorite(token, favorites_user_code, favorites_id, type);
    }

    @Override
    public Call<User.UserListResponse> getList(String token, int page, int limit) {
        return service.getList(token, page, limit);
    }
}
