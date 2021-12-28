package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.Campaign;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CampaignService {

    @GET("campaign/lists")
    Call<Campaign.Response> getList(@Query("token") String token,
                                    @Query("page") int page,
                                    @Query("limit") int limit);

    @GET("campaign/get_detail")
    Call<Campaign.ResponseDetail> getDetail(@Query("token") String token,
                                            @Query("campaign_id") String campaign_id);
}
