package com.example.neighbormaterebuild.network;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    public static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        final HashMap<String, String> hashMap = HttpHeaders.getInstance().headers();
        //Log.e("Header::::", "" + hashMap);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient
                .connectTimeout(220, TimeUnit.SECONDS)
                .readTimeout(220, TimeUnit.SECONDS)
                .writeTimeout(220, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        Request.Builder request = chain.request().newBuilder().addHeader("Content-Type", "application/json; charset=utf-8")
                                .addHeader("Accept", "application/json");
                        Iterator it = hashMap.entrySet().iterator();
                        while (it.hasNext()) {
                            HashMap.Entry pair = (HashMap.Entry)it.next();
                            if(pair.getKey() != null)
                                if(pair.getValue() != null)
                                    request.addHeader(pair.getKey().toString(), pair.getValue().toString());
                                else request.addHeader(pair.getKey().toString(), "");
                        }
                        return chain.proceed(request.build());
                    }
                });

        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}