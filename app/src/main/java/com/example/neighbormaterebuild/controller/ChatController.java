package com.example.neighbormaterebuild.controller;

import android.content.Context;

import com.example.neighbormaterebuild.model.BaseResponse;
import com.example.neighbormaterebuild.model.Report;
import com.example.neighbormaterebuild.model.User;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.ChatService;

import retrofit2.Call;

public class ChatController implements ChatService {

    private String baseUrl;

    private ChatService chatService;

    public ChatController(String baseUrl) {
        this.baseUrl = baseUrl;
        chatService = Client.getClient(baseUrl).create(ChatService.class);
    }

    @Override
    public Call<User.UserListResponse> getUserChat(String token, int page, int limit) {
        return chatService.getUserChat(token, page, limit);
    }

    @Override
    public Call<Report> postPinRoster(String token, String userId) {
        return chatService.postPinRoster(token, userId);
    }

    @Override
    public Call<Report> postPinRoster(String token, String userId, String is_unpin) {
        return chatService.postPinRoster(token, userId, is_unpin);
    }

    @Override
    public Call<Report> postDeleteRoster(String token, String userId) {
        return chatService.postDeleteRoster(token, userId);
    }

    @Override
    public Call<BaseResponse> viewRoster(String token, String karaId, int isRead) {
        return chatService.viewRoster(token, karaId, isRead);
    }
}
