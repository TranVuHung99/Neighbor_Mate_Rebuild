package com.example.neighbormaterebuild.controller;

import android.content.Context;

import com.example.neighbormaterebuild.model.BaseResponse;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.MapViewService;

import retrofit2.Call;

public class MapViewController implements MapViewService {

    private MapViewService service;

    private String baseUrl;

    public MapViewController(final String baseUrl) {
        this.baseUrl = baseUrl;
        service = Client.getClient(baseUrl).create(MapViewService.class);
    }

    @Override
    public Call<BaseResponse> openMapView(String token, String uID, String msgID) {
        return service.openMapView(token, uID, msgID);
    }
}
