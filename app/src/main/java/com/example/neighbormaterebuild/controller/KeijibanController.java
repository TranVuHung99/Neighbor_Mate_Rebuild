package com.example.neighbormaterebuild.controller;

import com.example.neighbormaterebuild.model.BaseResponse;
import com.example.neighbormaterebuild.model.Keijiban;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.KeijibanService;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class KeijibanController implements KeijibanService {
    private String baseUrl;

    private KeijibanService keijibanService;

    public KeijibanController(String baseUrl) {
        this.baseUrl = baseUrl;
        keijibanService = Client.getClient(baseUrl).create(KeijibanService.class);
    }

    @Override
    public Call<Keijiban.KeijibanResponse> getKeijibanList(String token, int page, int limit) {
        Call<Keijiban.KeijibanResponse> keijibanResponseCall = keijibanService.getKeijibanList(token, page, limit);
        return keijibanResponseCall;
    }

    @Override
    public Call<Keijiban.KeijibanResponse> getKeijibanListFind(String token, HashMap<String, String> fills, int page, int limit) {
        return keijibanService.getKeijibanListFind(token, fills, page, limit);
    }

    @Override
    public Call<Keijiban.KeijibanPost> postKeijiban(MultipartBody.Part image, Map<String, RequestBody> map) {
        Call<Keijiban.KeijibanPost> call = keijibanService.postKeijiban(image, map);
        return call;
    }

    @Override
    public Call<Keijiban.KeijibanDetail> getKeijibanDetail(String token, String keijiban_id) {
        return keijibanService.getKeijibanDetail(token, keijiban_id);
    }

    @Override
    public Call<BaseResponse> viewImageKeijiban(String token, String user_id) {
        return keijibanService.viewImageKeijiban(token, user_id);
    }
}
