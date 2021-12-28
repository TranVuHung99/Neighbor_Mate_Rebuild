package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BlockService {
    @GET("user/lock/list")
    Call<User.UserListResponse> getList(@Query("token") String token,
                                        @Query("page") int page,
                                        @Query("limit") int limit);

    @FormUrlEncoded
    @POST("user/lock")
    Call<User.UserResponse> unLock(@Field("token") String token,
                                   @Field("id") String id,
                                   @Field("lock_user_code") String user_code,
                                   @Field("type") String type);
}
