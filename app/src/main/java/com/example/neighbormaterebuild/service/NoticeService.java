package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.Notice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NoticeService {

    @GET("discuss/post/notice_list")
    Call<Notice.Response> getList(@Query("token") String token,
                                  @Query("page") int page,
                                  @Query("limit") int limit);

    @GET("discuss/notice_user_detail")
    Call<Notice.ResponseDetail> getDetail(@Query("token") String token,
                                          @Query("notice_id") String noticeID,
                                          @Query("notice_done_id") String noticeDoneID,
                                          @Query("schedule_send_id") String scheduleSendID);
}
