package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.Favorites;
import com.example.neighbormaterebuild.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FavoriteService {

    @FormUrlEncoded
    @POST("favorites/index")
    Call<Favorites> postFavorite(@Field("token") String token,
                                 @Field("favorites_user_code") String favorites_user_code,
                                 @Field("favorites_id") String favorites_id,
                                 @Field("type") int type);

    @GET("favorites/index/lists")
    Call<User.UserListResponse> getList(@Query("token") String token,
                                        @Query("page") int page,
                                        @Query("limit") int limit);
}
