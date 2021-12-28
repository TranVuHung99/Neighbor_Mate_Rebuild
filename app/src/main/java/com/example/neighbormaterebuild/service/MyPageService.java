package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.User;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MyPageService {

    @GET("user")
    Call<User.UserResponse> getUserProfile(@Query("token") String token);

    @GET("user/show/")
    Observable<User.UserResponse> getUserProfileByID(@Query("token") String token,
                                                     @Query("id") String id);

    @FormUrlEncoded
    @POST("user/device/transfer")
    Call<User.UserTranfer> postUserDeviceTransfer(@Field("email") String email,
                                                  @Field("password") String passworrd);

    @FormUrlEncoded
    @POST("user/device/prepare")
    Call<User.UserTranfer> postUserDevicePrepare(@Field("email") String email,
                                                 @Field("password") String passworrd,
                                                 @Field("device_password") String device_password,
                                                 @Field("token") String token);
}
