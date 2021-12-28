package com.example.neighbormaterebuild.controller;

import android.content.Context;

import com.example.neighbormaterebuild.model.Notice;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.NoticeService;

import retrofit2.Call;

public class NoticeController implements NoticeService {

    private String baseUrl;

    private NoticeService noticeService;

    public NoticeController(String baseUrl) {
        this.baseUrl = baseUrl;
        noticeService = Client.getClient(baseUrl).create(NoticeService.class);
    }

    @Override
    public Call<Notice.Response> getList(String token, int page, int limit) {
        return noticeService.getList(token, page, limit);
    }

    @Override
    public Call<Notice.ResponseDetail> getDetail(String token, String noticeID, String noticeDoneID, String scheduleSendID) {
        return noticeService.getDetail(token, noticeID, noticeDoneID, scheduleSendID);
    }
}
