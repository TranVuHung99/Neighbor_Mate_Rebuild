package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.BaseResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PushTokenService {

    @GET("metadata/push_token")
    Call<BaseResponse> pushToken();
}
