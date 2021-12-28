package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.BaseResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MapViewService {

    @GET("media/stream_map")
    Call<BaseResponse> openMapView(@Query("token") String token,
                                   @Query("sender_id") String uID,
                                   @Query("seq_id") String msgID);

}
