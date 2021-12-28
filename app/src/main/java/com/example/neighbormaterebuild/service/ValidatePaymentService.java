package com.example.neighbormaterebuild.service;

import com.example.neighbormaterebuild.model.ValidatePayment;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ValidatePaymentService {

    @FormUrlEncoded
    @POST("payment/validate_receipt")
    Call<ValidatePayment> validate(@Field("token") String token,
                                   @Field("receipt-data") String receiptData);
}
