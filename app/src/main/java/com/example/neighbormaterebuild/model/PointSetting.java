package com.example.neighbormaterebuild.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PointSetting {
    @SerializedName("name")
    private String name;

    @SerializedName("slug")
    private String slug;

    @SerializedName("point")
    private int point;

    @SerializedName("special_point")
    private String special_point;

    public PointSetting(String name, String slug, int point, String special_point, int from_time, String to_time, String expires, String created_at) {
        this.name = name;
        this.slug = slug;
        this.point = point;
        this.special_point = special_point;
    }

    @Override
    public String toString() {
        return "PointSetting{" +
                "name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", point=" + point +
                ", special_point='" + special_point + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public int getPoint() {
        return point;
    }

    public String getSpecial_point() {
        return special_point;
    }

    public static class Response {
        @SerializedName("code")
        private String code;

        @SerializedName("stattus")
        private String status;

        @SerializedName("message")
        private String message;


        @SerializedName("data")
        private Data data;

        public Response(String code, String status, Data data) {
            this.code = code;
            this.status = status;
            this.data = data;
        }

        @Override
        public String toString() {
            return "PointResponse{" +
                    "code='" + code + '\'' +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    ", data=" + data +
                    '}';
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public static class Data {
            @SerializedName("result")
            private List<PointSetting> pointList;

            @SerializedName("balance")
            private int totalPoint;

            @SerializedName("free_chat")
            private String free_chat;

            @SerializedName("unlimit_point")
            private String unlimit_point;

            @SerializedName("point_unlimit")
            private String point_unlimit;

            @SerializedName("limit_release_period")
            private String limit_release_period;

            @SerializedName("unlimit_description")
            private String unlimit_description;

            @SerializedName("unlimit_payment_text")
            private String unlimit_payment_text;

            public Data(List<PointSetting> pointList, int totalPoint, String free_chat, String unlimit_point, String point_unlimit, String limit_release_period, String unlimit_description, String unlimit_payment_text) {
                this.pointList = pointList;
                this.totalPoint = totalPoint;
                this.free_chat = free_chat;
                this.unlimit_point = unlimit_point;
                this.point_unlimit = point_unlimit;
                this.limit_release_period = limit_release_period;
                this.unlimit_description = unlimit_description;
                this.unlimit_payment_text = unlimit_payment_text;
            }

            @Override
            public String toString() {
                return "Data{" +
                        "pointList=" + pointList +
                        "totalPoint" + totalPoint +
                        '}';
            }

            public int getTotalPoint() {
                return totalPoint;
            }

            public void setTotalPoint(int totalPoint) {
                this.totalPoint = totalPoint;
            }

            public List<PointSetting> getPointList() {
                return pointList;
            }

            public void setPointList(List<PointSetting> pointList) {
                this.pointList = pointList;
            }
        }
    }

    public static class Response2 {
        @SerializedName("code")
        private String code;

        @SerializedName("stattus")
        private String status;

        @SerializedName("message")
        private String message;


        @SerializedName("data")
        private List<PointSetting> data;

        public Response2(String code, String status, List<PointSetting> data) {
            this.code = code;
            this.status = status;
            this.data = data;
        }

        @Override
        public String toString() {
            return "PointResponse{" +
                    "code='" + code + '\'' +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    ", data=" + data +
                    '}';
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<PointSetting> getData() {
            return data;
        }

        public void setData(List<PointSetting> data) {
            this.data = data;
        }

    }
}
