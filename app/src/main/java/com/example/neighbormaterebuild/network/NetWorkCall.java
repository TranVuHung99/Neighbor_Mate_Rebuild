package com.example.neighbormaterebuild.network;

import android.os.AsyncTask;

import com.example.neighbormaterebuild.model.Metadata;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class NetWorkCall extends AsyncTask<Call, Void, Metadata> {

    @Override
    protected Metadata doInBackground(Call... calls) {
        try {
            Call<Metadata> call = calls[0];
            Response<Metadata> response = call.execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
