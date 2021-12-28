package com.example.neighbormaterebuild.controller;

import android.content.Context;

import com.example.neighbormaterebuild.model.Campaign;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.CampaignService;

import retrofit2.Call;

public class CampaignController implements CampaignService {

    private String baseUrl;

    private CampaignService campaignService;

    public CampaignController(String baseUrl) {
        this.baseUrl = baseUrl;
        campaignService = Client.getClient(baseUrl).create(CampaignService.class);
    }

    @Override
    public Call<Campaign.Response> getList(String token, int page, int limit) {
        return campaignService.getList(token, page, limit);
    }

    @Override
    public Call<Campaign.ResponseDetail> getDetail(String token, String campaign_id) {
        return campaignService.getDetail(token, campaign_id);
    }
}
