package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.BaseResponse;
import com.example.neighbormaterebuild.model.Report;
import com.example.neighbormaterebuild.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatService {

    @GET("roster/get")
    Call<User.UserListResponse> getUserChat(@Query("token") String token,
                                            @Query("page") int page,
                                            @Query("limit") int limit);

    @FormUrlEncoded
    @POST("roster/pin_chat")
    Call<Report> postPinRoster(@Field("token") String token,
                               @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("roster/pin_chat")
    Call<Report> postPinRoster(@Field("token") String token,
                               @Field("user_id") String userId,
                               @Field("is_unpin") String is_unpin);

    @FormUrlEncoded
    @POST("roster/delete_chat")
    Call<Report> postDeleteRoster(@Field("token") String token,
                                  @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("roster/view_roster")
    Call<BaseResponse> viewRoster(@Field("token") String token,
                                  @Field("kara_id") String karaId,
                                  @Field("is_read") int isRead);
}
