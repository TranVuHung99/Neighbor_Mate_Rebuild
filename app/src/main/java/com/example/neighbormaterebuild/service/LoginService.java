package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginService {

    @FormUrlEncoded
    @POST("login/index")
    Call<User.UserResponse> checkLogin(@Field("user_code") String user_code,
                                       @Field("password") String password);
}
