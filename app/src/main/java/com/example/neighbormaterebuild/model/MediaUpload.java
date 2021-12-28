package com.example.neighbormaterebuild.model;

import com.google.gson.annotations.SerializedName;

public class MediaUpload {

    @SerializedName("user_id")
    private String userID;

    @SerializedName("media_id")
    private String mediaID;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("sector")
    private String sector;

    @SerializedName("ext")
    private String ext;

    @SerializedName("mime")
    private String mime;

    @SerializedName("size")
    private String size;

    @SerializedName("width")
    private String width;

    @SerializedName("height")
    private String height;

    public MediaUpload(String userID, String mediaID, String name, String type, String sector, String ext, String mime, String size, String width, String height) {
        this.userID = userID;
        this.mediaID = mediaID;
        this.name = name;
        this.type = type;
        this.sector = sector;
        this.ext = ext;
        this.mime = mime;
        this.size = size;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "MediaUpload{" +
                "userID='" + userID + '\'' +
                ", mediaID='" + mediaID + '\'' +
                ", name=" + name +
                ", type=" + type +
                ", sector=" + sector +
                ", ext=" + ext +
                ", mime=" + mime +
                ", size='" + size + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                '}';
    }

    public String getUserID() {
        return userID;
    }

    public String getMediaID() {
        return mediaID;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSector() {
        return sector;
    }

    public String getExt() {
        return ext;
    }

    public String getMime() {
        return mime;
    }

    public String getSize() {
        return size;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public  static class MediaUploadResponse {
        @SerializedName("code")
        private String code;

        @SerializedName("status")
        private String status;

        @SerializedName("message")
        private String message;

        @SerializedName("data")
        private MediaUpload data;

        public MediaUploadResponse(String code, String status, String message, MediaUpload data) {
            this.code = code;
            this.status = status;
            this.message = message;
            this.data = data;
        }

        @Override
        public String toString() {
            return "MediaUploadResponse{" +
                    "code='" + code + '\'' +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    ", data=" + data +
                    '}';
        }

        public String getCode() {
            return code;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public MediaUpload getData() {
            return data;
        }
    }


}
