package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.Region;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RegionService {

    @GET("region/cities")
    Call<Region> getRegionList(@Query("area_id") String id);
}
