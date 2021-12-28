package com.example.neighbormaterebuild.controller;

import android.content.Context;

import com.example.neighbormaterebuild.model.Region;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.RegionService;

import retrofit2.Call;

public class RegionController implements RegionService {

    private RegionService regionService;

    private String baseUrl;
    private Context context;

    public RegionController(final String baseUrl, final Context context) {
        this.baseUrl = baseUrl;
        this.context = context;
        regionService = Client.getClient(baseUrl).create(RegionService.class);
    }


    @Override
    public Call<Region> getRegionList(String id) {
        Call<Region> observable = regionService.getRegionList(id);
        return observable;
    }
}
