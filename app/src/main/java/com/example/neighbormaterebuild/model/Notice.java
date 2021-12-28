package com.example.neighbormaterebuild.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Notice extends User implements Parcelable {

    @SerializedName("notice_title")
    private String notice_title;

    @SerializedName("schedule_title")
    private String schedule_title;

    @SerializedName("notice_content")
    private String notice_content;

    @SerializedName("schedule_content")
    private String schedule_content;

    @SerializedName("schedule_notice_id")
    private String schedule_notice_id;

    @SerializedName("notice_user_id")
    private String notice_user_id;

    @SerializedName("notice_done_id")
    private String notice_done_id;

    @SerializedName("schedule_send_id")
    private String schedule_send_id;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("posted_at")
    private String posted_at;

    @SerializedName("images")
    private String images;


    protected Notice(Parcel in) {
        super(in);
        notice_title = in.readString();
        schedule_title = in.readString();
        notice_content = in.readString();
        schedule_content = in.readString();
        schedule_notice_id = in.readString();
        notice_user_id = in.readString();
        notice_done_id = in.readString();
        schedule_send_id = in.readString();
        title = in.readString();
        content = in.readString();
        posted_at = in.readString();
        images = in.readString();
    }

    public static final Parcelable.Creator<Notice> CREATOR = new Parcelable.Creator<Notice>() {
        @Override
        public Notice createFromParcel(Parcel in) {
            return new Notice(in);
        }

        @Override
        public Notice[] newArray(int size) {
            return new Notice[size];
        }
    };

    public void setNotice_title(String notice_title) {
        this.notice_title = notice_title;
    }

    public void setSchedule_title(String schedule_title) {
        this.schedule_title = schedule_title;
    }

    public void setNotice_content(String notice_content) {
        this.notice_content = notice_content;
    }

    public void setSchedule_content(String schedule_content) {
        this.schedule_content = schedule_content;
    }

    public void setSchedule_notice_id(String schedule_notice_id) {
        this.schedule_notice_id = schedule_notice_id;
    }

    public void setNotice_user_id(String notice_user_id) {
        this.notice_user_id = notice_user_id;
    }

    public void setNotice_done_id(String notice_done_id) {
        this.notice_done_id = notice_done_id;
    }

    public void setSchedule_send_id(String schedule_send_id) {
        this.schedule_send_id = schedule_send_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPosted_at(String posted_at) {
        this.posted_at = posted_at;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }


    public AvatarUrl[] getImagesPath() {
        try {
            Gson g = new Gson();
            AvatarUrl[] a = g.fromJson(getImages(), AvatarUrl[].class);
            return a;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(notice_title);
        dest.writeString(schedule_title);
        dest.writeString(notice_content);
        dest.writeString(schedule_content);
        dest.writeString(schedule_notice_id);
        dest.writeString(notice_user_id);
        dest.writeString(notice_done_id);
        dest.writeString(schedule_send_id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(posted_at);
        dest.writeString(images);
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

        public Response(int code, String status, String message, Data notice) {
            this.code = code;
            this.status = status;
            this.message = message;
            this.data = notice;
        }

        @Override
        public String toString() {
            return "Notice{" +
                    "code=" + code +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    ", notice=" + data +
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

    public String getTitle() {
        return title;
    }

    public String getSchedule_title() {
        return schedule_title;
    }

    public String getNotice_content() {
        return notice_content;
    }

    public String getSchedule_content() {
        return schedule_content;
    }

    public String getSchedule_notice_id() {
        return schedule_notice_id;
    }

    public String getNotice_user_id() {
        return notice_user_id;
    }

    public String getNotice_done_id() {
        return notice_done_id;
    }

    public String getSchedule_send_id() {
        return schedule_send_id;
    }

    public String getNotice_title() {
        return notice_title;
    }

    public String getContent() {
        return content;
    }

    public String getPosted_at() {
        return posted_at;
    }

    public static class Data {
        private int page = 0;
        private int totalResults = 0;
        private int totalPages = 0;

        @SerializedName("result")
        List<Notice> list;

        @SerializedName("total_unread_message")
        private int total_unread_message;

        @SerializedName("total_unread_notice")
        private int total_unread_notice;

        @SerializedName("total_unread_campaign")
        private int total_unread_campaign;

        @SerializedName("total_unread_all")
        private int total_unread_all;

        public Data(int page, int totalResults, int totalPages, List<Notice> list, int total_unread_message, int total_unread_notice, int total_unread_campaign, int total_unread_all) {
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

        public List<Notice> getList() {
            return list;
        }

        public void setList(List<Notice> list) {
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
        private DataDetail data;

        public ResponseDetail(int code, String status, String message, DataDetail notice) {
            this.code = code;
            this.status = status;
            this.message = message;
            this.data = notice;
        }

        @Override
        public String toString() {
            return "ResponseDetail{" +
                    "code=" + code +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    ", notice=" + data +
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

        public DataDetail getData() {
            return data;
        }
    }

    public static class DataDetail {
        @SerializedName("type")
        private String type;

        @SerializedName("is_read")
        private int is_read;

        @SerializedName("notice")
        private Notice notice;

        public DataDetail(String type, int is_read, Notice notice) {
            this.type = type;
            this.is_read = is_read;
            this.notice = notice;
        }

        @Override
        public String toString() {
            return "DataDetail{" +
                    "type=" + type +
                    ", is_read=" + is_read +
                    ", notice=" + notice +
                    '}';
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getIs_read() {
            return is_read;
        }

        public void setIs_read(int is_read) {
            this.is_read = is_read;
        }

        public Notice getNotice() {
            notice.setIs_read(this.is_read);
            return notice;
        }

        public void setNotice(Notice notice) {
            this.notice = notice;
        }
    }
}
