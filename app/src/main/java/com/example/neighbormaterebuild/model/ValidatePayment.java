package com.example.neighbormaterebuild.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ValidatePayment {

    @SerializedName("code")
    @Expose
    private int code;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    private Data data;


    public ValidatePayment(int code, String status, String message, User user) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {

        @SerializedName("balance")
        private int totalPoint;

        @SerializedName("point")
        private int point;

        public Data(int point, int totalPoint) {
            this.point = point;
            this.totalPoint = totalPoint;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "point=" + point +
                    "totalPoint" + totalPoint +
                    '}';
        }

        public int getTotalPoint() {
            return totalPoint;
        }

        public int getPoint() {
            return point;
        }
    }

}
