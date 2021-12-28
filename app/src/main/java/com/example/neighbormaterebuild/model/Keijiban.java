package com.example.neighbormaterebuild.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Keijiban implements Parcelable {

    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("user_code")
    private String user_code;

    @SerializedName("displayname")
    private String displayname;

    @SerializedName("sex")
    private String sex;

    @SerializedName("age")
    private String age;

    @SerializedName("area_id")
    @Expose
    private String area;
    @SerializedName("user_status")
    private String user_status;

    @SerializedName("area_name")
    private String area_name;

    @SerializedName("avatar_url")
    private String avatar_url;

    @SerializedName("content")
    private String content;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("origin")
    private Thumb thumb;

    @SerializedName("unlimit_point")
    private int unlimit_point;

    @SerializedName("favorite_status")
    private String favorite_status;

    @SerializedName("is_pin_chat")
    private String is_pin_chat;


    protected Keijiban(Parcel in) {
        id = in.readString();
        user_id = in.readString();
        user_code = in.readString();
        displayname = in.readString();
        sex = in.readString();
        age = in.readString();
        area = in.readString();
        user_status = in.readString();
        area_name = in.readString();
        avatar_url = in.readString();
        content = in.readString();
        created_at = in.readString();
        thumb = in.readParcelable(Thumb.class.getClassLoader());
        unlimit_point= in.readInt();
    }

    public static final Creator<Keijiban> CREATOR = new Creator<Keijiban>() {
        @Override
        public Keijiban createFromParcel(Parcel in) {
            return new Keijiban(in);
        }

        @Override
        public Keijiban[] newArray(int size) {
            return new Keijiban[size];
        }
    };

    @Override
    public String toString() {
        return "Keijiban{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_code='" + user_code + '\'' +
                ", displayname='" + displayname + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", area='" + area + '\'' +
                ", user_status='" + user_status + '\'' +
                ", area_name='" + area_name + '\'' +
                ", avatar_url='" + avatar_url + '\'' +
                ", content='" + content + '\'' +
                ", created_at='" + created_at + '\'' +
                ", thumb=" + thumb +
                ", unlimit_point=" + unlimit_point +
                ", favorite_status='" + favorite_status + '\'' +
                ", is_pin_chat='" + is_pin_chat + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Thumb getThumb() {
        return thumb;
    }

    public void setThumb(Thumb thumb) {
        this.thumb = thumb;
    }

    public int getUnlimit_point() {
        return unlimit_point;
    }

    public void setUnlimit_point(int unlimit_point) {
        this.unlimit_point = unlimit_point;
    }

    public String getFavorite_status() {
        return favorite_status;
    }

    public void setFavorite_status(String favorite_status) {
        this.favorite_status = favorite_status;
    }

    public String getIs_pin_chat() {
        return is_pin_chat;
    }

    public void setIs_pin_chat(String is_pin_chat) {
        this.is_pin_chat = is_pin_chat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user_id);
        dest.writeString(user_code);
        dest.writeString(displayname);
        dest.writeString(sex);
        dest.writeString(age);
        dest.writeString(area);
        dest.writeString(user_status);
        dest.writeString(area_name);
        dest.writeString(avatar_url);
        dest.writeString(content);
        dest.writeString(created_at);
        dest.writeParcelable(thumb, flags);
        dest.writeInt(unlimit_point);
    }

    public static class Thumb implements Parcelable {
        @SerializedName("status")
        private String status;

        @SerializedName("image_id")
        private String image_id;

        @SerializedName("path")
        private String path;

        protected Thumb(Parcel in) {
            status = in.readString();
            image_id = in.readString();
            path = in.readString();
        }

        public static final Creator<Thumb> CREATOR = new Creator<Thumb>() {
            @Override
            public Thumb createFromParcel(Parcel in) {
                return new Thumb(in);
            }

            @Override
            public Thumb[] newArray(int size) {
                return new Thumb[size];
            }
        };

        @Override
        public String toString() {
            return "Thumb{" +
                    "status='" + status + '\'' +
                    ", image_id='" + image_id + '\'' +
                    ", path='" + path + '\'' +
                    '}';
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getImage_id() {
            return image_id;
        }

        public void setImage_id(String image_id) {
            this.image_id = image_id;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(status);
            parcel.writeString(image_id);
            parcel.writeString(path);
        }
    }

    public static class KeijibanPost {
        @SerializedName("code")
        @Expose
        private int code;

        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("message")
        @Expose
        private String message;

        public KeijibanPost(int code, String status, String message) {
            this.code = code;
            this.status = status;
            this.message = message;
        }

        @Override
        public String toString() {
            return "KeijibanPost{" +
                    "code=" + code +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class KeijibanResponse {
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
        private Data result;

        public KeijibanResponse(int code, String status, String message, Data result) {
            this.code = code;
            this.status = status;
            this.message = message;
            this.result = result;
        }

        @Override
        public String toString() {
            return "KeijibanResponse{" +
                    "code=" + code +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    ", result=" + result +
                    '}';
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Data getResult() {
            return result;
        }

        public void setResult(Data result) {
            this.result = result;
        }

        public static class Data {
            private int page = 0;
            private int totalResults = 0;
            private int totalPages = 0;

            @SerializedName("result")
            private List<Keijiban> keijiban;


            public Data(int page, List<Keijiban> keijiban, int limit, int totalResults) {
                this.page = page;
                this.keijiban = keijiban;
                this.totalResults = totalResults;
                this.totalPages = totalPages;
            }

            @Override
            public String toString() {
                return "Data{" +
                        "page=" + page +
                        ", limit=" + totalResults +
                        ", totalPages=" + totalPages +
                        ", keijiban=" + keijiban +
                        '}';
            }

            public int getPage() {
                return page;
            }

            public void setPage(int page) {
                this.page = page;
            }

            public List<Keijiban> getKeijiban() {
                return keijiban;
            }

            public void setKeijiban(List<Keijiban> keijiban) {
                this.keijiban = keijiban;
            }

            public int getLimit() {
                return totalResults;
            }

            public void setLimit(int totalResults) {
                this.totalResults = totalResults;
            }

            public int getTotalPages() {
                return totalPages;
            }

            public void setTotalPages(int totalPages) {
                this.totalPages = totalPages;
            }
        }
    }

    public static class KeijibanDetail {
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
        private Keijiban keijiban;

        public static KeijibanDetail instance;

        public static void setInstance(KeijibanDetail instance) {
            KeijibanDetail.instance = instance;
        }

        public static KeijibanDetail getInstance() {
            if (instance == null) {
                synchronized (KeijibanDetail.class) {
                    if (null == instance) {
                        instance = new KeijibanDetail();
                    }
                }
            }
            return instance;
        }

        @Override
        public String toString() {
            return "KeijibanDetail{" +
                    "code=" + code +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    ", keijiban=" + keijiban +
                    '}';
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Keijiban getKeijiban() {
            return keijiban;
        }

        public void setKeijiban(Keijiban keijiban) {
            this.keijiban = keijiban;
        }
    }

}

