package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.MediaUpload;
import com.example.neighbormaterebuild.model.Message;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface MessageService {

    @GET("user/message_history")
    Call<Message.MessageResponse> getMessage(@Query("token") String token,
                                             @Query("user_code") String userCode,
                                             @Query("user_id") String userId,
                                             @Query("page") int page,
                                             @Query("limit") int limit,
                                             @Query("seq") String messageId);

    @GET("user/message_history")
    Call<Message.MessageResponse> getMessage(@Query("token") String token,
                                             @Query("user_code") String userCode,
                                             @Query("user_id") String userId,
                                             @Query("page") int page,
                                             @Query("limit") int limit);

    @Multipart
    @POST("media/upload")
    Call<MediaUpload.MediaUploadResponse> sendImage(@Part MultipartBody.Part file,
                                                    @Part("token") RequestBody token,
                                                    @Part("type") RequestBody type,
                                                    @Part("sector") RequestBody sector);
}
