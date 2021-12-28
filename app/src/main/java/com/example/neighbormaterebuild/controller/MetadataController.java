package com.example.neighbormaterebuild.controller;

import android.content.Context;

import com.example.neighbormaterebuild.model.Metadata;
import com.example.neighbormaterebuild.network.Client;
import com.example.neighbormaterebuild.service.MetadataService;

import retrofit2.Call;

public class MetadataController implements MetadataService {

    private String TAG = "KIEMTRA_METADATA_API";
    private MetadataService metadataService;

    private String baseUrl;

    public MetadataController(final String baseUrl) {
        this.baseUrl = baseUrl;
        metadataService = Client.getClient(baseUrl).create(MetadataService.class);
    }

    @Override
    public Call<Metadata> getResultMetadata() {
        Call<Metadata> metadataCall = metadataService.getResultMetadata();
        return metadataCall;
    }
}
