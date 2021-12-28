package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.Point;
import com.example.neighbormaterebuild.model.PointSetting;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PointService {

    @GET("point/package")
    Call<Point.PointResponse> getResultPointPackage(@Query("token") String token, @Query("type") String type);

    @GET("point/fee/")
    Call<PointSetting.Response> getResultPointSetting(@Query("token") String token);

    @GET("point/point_setting")
    Call<PointSetting.Response2> getPointSetting(@Query("token") String token);

    @FormUrlEncoded
    @POST("point/unlimit")
    Call<Point.PointResponse> postUnLimit(@Field("token") String token, @Field("friend_id") String friend_id,
                                          @Field("friend_code") String friend_code);
}
