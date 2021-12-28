package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.BaseResponse;
import com.example.neighbormaterebuild.model.Keijiban;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface KeijibanService {

    @GET("keijiban/post")
    Call<Keijiban.KeijibanResponse> getKeijibanList(@Query("token") String token,
                                                    @Query("page") int page,
                                                    @Query("limit") int limit);

    @GET("keijiban/post")
    Call<Keijiban.KeijibanResponse> getKeijibanListFind(@Query("token") String token,
                                                        @QueryMap HashMap<String, String> fills,
                                                        @Query("page") int page,
                                                        @Query("limit") int limit);

    @Multipart
    @POST("keijiban/post/create")
    Call<Keijiban.KeijibanPost> postKeijiban(@Part MultipartBody.Part image,
                                             @PartMap Map<String, RequestBody> map);

    @GET("keijiban/show_keijiban_detail")
    Call<Keijiban.KeijibanDetail> getKeijibanDetail(@Query("token") String token,
                                                    @Query("keijiban_id") String keijiban_id);

    @GET("keijiban/view_keijiban_image")
    Call<BaseResponse> viewImageKeijiban(@Query("token") String token, @Query("user_id") String user_id );
}
