package com.example.neighbormaterebuild.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Campaign {

    @SerializedName("id")
    private String id;

    @SerializedName("header")
    private String header;

    @SerializedName("title")
    private String title;

    @SerializedName("is_read")
    private String is_read;

    @SerializedName("received_time")
    private String received_time;

    @SerializedName("avatar_url")
    private String avatar_url;

    public Campaign(String id, String header, String title, String is_read, String received_time, String avatar_url) {
        this.id = id;
        this.header = header;
        this.title = title;
        this.is_read = is_read;
        this.received_time = received_time;
        this.avatar_url = avatar_url;
    }

    public static class Response {
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
        @Expose
        private Data data;

        public Response(int code, String status, String message, Data data) {
            this.code = code;
            this.status = status;
            this.message = message;
            this.data = data;
        }

        @Override
        public String toString() {
            return "Notice{" +
                    "code=" + code +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    ", data=" + data +
                    '}';
        }

        public int getCode() {
            return code;
        }


        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }


        public Data getData() {
            return data;
        }
    }

    public static class ResponseDetail {
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
        @Expose
        private String url;

        public ResponseDetail(int code, String status, String message, String url) {
            this.code = code;
            this.status = status;
            this.message = message;
            this.url = url;
        }

        @Override
        public String toString() {
            return "Notice{" +
                    "code=" + code +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    ", data=" + url +
                    '}';
        }

        public int getCode() {
            return code;
        }


        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getUrl() {
            return url;
        }
    }

    public String getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    public String getTitle() {
        return title;
    }

    public String getIs_read() {
        return is_read;
    }

    public String getReceived_time() {
        return received_time;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }

    public static class Data {
        private int page = 0;
        private int totalResults = 0;
        private int totalPages = 0;

        @SerializedName("result")
        List<Campaign> list;

        @SerializedName("total_unread_message")
        private int total_unread_message;

        @SerializedName("total_unread_notice")
        private int total_unread_notice;

        @SerializedName("total_unread_campaign")
        private int total_unread_campaign;

        @SerializedName("total_unread_all")
        private int total_unread_all;

        public Data(int page, int totalResults, int totalPages, List<Campaign> list, int total_unread_message, int total_unread_notice, int total_unread_campaign, int total_unread_all) {
            this.page = page;
            this.totalResults = totalResults;
            this.totalPages = totalPages;
            this.list = list;
            this.total_unread_message = total_unread_message;
            this.total_unread_notice = total_unread_notice;
            this.total_unread_campaign = total_unread_campaign;
            this.total_unread_all = total_unread_all;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "page=" + page +
                    ", totalResults=" + totalResults +
                    ", totalPages=" + totalPages +
                    ", list=" + list +
                    ", total_unread_message=" + total_unread_message +
                    ", total_unread_notice=" + total_unread_notice +
                    ", total_unread_campaign=" + total_unread_campaign +
                    ", total_unread_all=" + total_unread_all +
                    '}';
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(int totalResults) {
            this.totalResults = totalResults;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public List<Campaign> getList() {
            return list;
        }

        public void setList(List<Campaign> list) {
            this.list = list;
        }

        public int getTotal_unread_message() {
            return total_unread_message;
        }

        public void setTotal_unread_message(int total_unread_message) {
            this.total_unread_message = total_unread_message;
        }

        public int getTotal_unread_notice() {
            return total_unread_notice;
        }

        public void setTotal_unread_notice(int total_unread_notice) {
            this.total_unread_notice = total_unread_notice;
        }

        public int getTotal_unread_campaign() {
            return total_unread_campaign;
        }

        public void setTotal_unread_campaign(int total_unread_campaign) {
            this.total_unread_campaign = total_unread_campaign;
        }

        public int getTotal_unread_all() {
            return total_unread_all;
        }

        public void setTotal_unread_all(int total_unread_all) {
            this.total_unread_all = total_unread_all;
        }
    }
}
