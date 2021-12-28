package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.Metadata;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MetadataService {

    @GET("metadata")
    Call<Metadata> getResultMetadata();

}
