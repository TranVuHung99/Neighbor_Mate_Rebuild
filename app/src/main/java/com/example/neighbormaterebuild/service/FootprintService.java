package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FootprintService {
    @GET("footprint/index/list")
    Call<User.UserListResponse> getList(@Query("token") String token,
                                        @Query("page") int page,
                                        @Query("limit") int limit);
}
