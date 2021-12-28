package com.example.neighbormaterebuild.controller;

import android.content.Context;

import com.example.neighbormaterebuild.model.Point;
import com.example.neighbormaterebuild.model.PointSetting;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.PointService;

import retrofit2.Call;

public class PointController implements PointService {
    private PointService pointService;

    private String baseUrl;

    public PointController(final String baseUrl) {
        this.baseUrl = baseUrl;
        pointService = Client.getClient(baseUrl).create(PointService.class);
    }

    @Override
    public Call<Point.PointResponse> getResultPointPackage(String token, String type) {
        return pointService.getResultPointPackage(token, type);
    }

    @Override
    public Call<PointSetting.Response> getResultPointSetting(String token) {
        return pointService.getResultPointSetting(token);
    }

    @Override
    public Call<PointSetting.Response2> getPointSetting(String token) {

        return pointService.getPointSetting(token);
    }

    @Override
    public Call<Point.PointResponse> postUnLimit(String token, String friend_id, String friend_code) {
        return pointService.postUnLimit(token, friend_id, friend_code);
    }
}
