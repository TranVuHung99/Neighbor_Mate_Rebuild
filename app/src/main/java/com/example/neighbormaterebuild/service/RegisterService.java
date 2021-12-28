package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterService {

    @FormUrlEncoded
    @POST("register/index")
    Call<User.UserResponse> createUser(@Field("displayname") String displayname,
                                       @Field("password") String password,
                                       @Field("sex") String sex,
                                       @Field("age") String age,
                                       @Field("area") String area,
                                       @Field("city") String city,
                                       @Field("carriername") String caririername,
                                       @Field("mobilenetworkcode") String mobilenetworkcode
    );
}
