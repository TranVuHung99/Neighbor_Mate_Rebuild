package com.example.neighbormaterebuild.controller;

import android.content.Context;

import com.example.neighbormaterebuild.model.MediaUpload;
import com.example.neighbormaterebuild.model.Message;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.MessageService;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class MessageController implements MessageService {

    private String baseUrl;

    private MessageService messageService;

    public MessageController(String baseUrl) {
        this.baseUrl = baseUrl;
        messageService = Client.getClient(baseUrl).create(MessageService.class);
    }

    @Override
    public Call<Message.MessageResponse> getMessage(String token, String userCode, String userId, int page, int limit, String messageID) {
        Call<Message.MessageResponse> call = messageService.getMessage(token, userCode, userId, page, limit, messageID);
        return call;
    }

    @Override
    public Call<Message.MessageResponse> getMessage(String token, String userCode, String userId, int page, int limit) {
        Call<Message.MessageResponse> call = messageService.getMessage(token, userCode, userId, page,limit);
        return call;
    }

    @Override
    public Call<MediaUpload.MediaUploadResponse> sendImage(MultipartBody.Part file, RequestBody token, RequestBody type, RequestBody sector) {
        Call<MediaUpload.MediaUploadResponse> call = messageService.sendImage(file, token, type, sector);
        return call;
    }
}
