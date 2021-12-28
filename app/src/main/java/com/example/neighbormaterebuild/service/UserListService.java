package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.BaseResponse;
import com.example.neighbormaterebuild.model.Block;
import com.example.neighbormaterebuild.model.Report;
import com.example.neighbormaterebuild.model.User;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface UserListService {

    @GET("user/find")
    Call<User.UserListResponse> getUserList(@Query("token") String token, @Query("page") int page, @Query("limit") int limit);

    @GET("user/find")
    Call<User.UserListResponse> getUserListFind(@Query("token") String token,
                                                @QueryMap HashMap<String, String> fills,
                                                @Query("page") int page,
                                                @Query("limit") int limit);

    @GET("user/show/")
    Call<User.UserResponse> getUserProfile(@Query("token") String token, @Query("id") String id);

    @FormUrlEncoded
    @POST("user/lock")
    Call<Block> userLock(@Field("token") String token,
                         @Field("id") String id,
                         @Field("lock_user_code") String lockUserCode,
                         @Field("type") int type);

    @FormUrlEncoded
    @POST("user/report")
    Call<Report> userReport(@Field("token") String token,
                            @Field("id") String id,
                            @Field("user_code") String lockUserCode,
                            @Field("comment") String comment,
                            @Field("screen") String screen);

    @FormUrlEncoded
    @POST("keijiban/lock")
    Call<Block> keijibanLock(@Field("token") String token,
                             @Field("id") String id,
                             @Field("lock_user_code") String lockUserCode,
                             @Field("type") int type);

    @FormUrlEncoded
    @POST("keijiban/report")
    Call<Report> keijibanReport(@Field("token") String token,
                                @Field("id") String id,
                                @Field("user_code") String lockUserCode,
                                @Field("comment") String comment,
                                @Field("screen") String screen);

    @FormUrlEncoded
    @POST("user/update")
    Call<User.UserResponse> userUpdateProfile(@Field("token") String token,
                                              @FieldMap HashMap<String, String> result);

    @Multipart
    @POST("user/images/update")
    Call<Report> userUpdateImage(@PartMap HashMap<String, RequestBody> result,
                                 @Part MultipartBody.Part images);



    @GET("user/profile_image")
    Call<BaseResponse> viewImageProfile(@Query("my_user_id") String userId,
                                        @Query("user_id_footprint") String karaId,
                                        @Query("img_index") int imgIndex);
}

