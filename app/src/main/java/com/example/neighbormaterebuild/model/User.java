package com.example.neighbormaterebuild.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User implements Parcelable {

    public static User instance;

    public static void setInstance(User instance) {
        User.instance = instance;
    }

    public static User getInstance() {
        if (instance == null) {
            synchronized (User.class) {
                if (null == instance) {
                    instance = new User();
                }
            }
        }
        return instance;
    }

    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("user_code")
    private String user_code;

    @SerializedName("displayname")
    private String displayname;

    @SerializedName("password")
    private String password;

    @SerializedName("sex")
    private String sex;

    @SerializedName("age")
    private String age;

    @SerializedName("area_id")
    @Expose
    private String area;

    @SerializedName("token")
    private String tokken;

    @SerializedName("socket_jwt")
    private String socket_jwt;

    @SerializedName("user_status")
    private String user_status;

    @SerializedName("area_name")
    private String area_name;

    @SerializedName("job")
    private String job;

    @SerializedName("income")
    private String income;

    @SerializedName("style")
    private String style;

    @SerializedName("height")
    private String height;

    @SerializedName("avatar_url")
    private String avatar_url;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("point")
    private String point;

    @SerializedName("image")
    private String image;

    @SerializedName("city_id")
    private String city_id;

    @SerializedName("city_name")
    private String city_name;

    @SerializedName("msg_text")
    private String msg_text;

    @SerializedName("is_read")
    private int is_read;

    @SerializedName("unread_cnt")
    private int unread_cnt;

    @SerializedName("send_at")
    private String send_at;

    @SerializedName("device_password")
    private String device_password;

    @SerializedName("favorite_status")
    private String favorite_status;

    @SerializedName("is_bonus")
    private int is_bonus;

    @SerializedName("bonus_title")
    private String bonus_title;

    @SerializedName("bonus_body")
    private String bonus_body;

    private String pickerList;

    @SerializedName("user_status_tmp")
    private String user_status_tmp;

    @SerializedName("image_avata")
    private String image_avata;

    @SerializedName("unlimit_point")
    private int unlimit_point;

    @SerializedName("free_chat")
    private int free_chat;

    @SerializedName("is_pin_chat")
    private int is_pin_chat;

    public User() {
    }


    protected User(Parcel in) {
        id = in.readString();
        user_id = in.readString();
        user_code = in.readString();
        displayname = in.readString();
        password = in.readString();
        sex = in.readString();
        age = in.readString();
        area = in.readString();
        tokken = in.readString();
        socket_jwt = in.readString();
        user_status = in.readString();
        area_name = in.readString();
        job = in.readString();
        income = in.readString();
        style = in.readString();
        height = in.readString();
        avatar_url = in.readString();
        created_at = in.readString();
        point = in.readString();
        image = in.readString();
        city_id = in.readString();
        city_name = in.readString();
        msg_text = in.readString();
        is_read = in.readInt();
        unread_cnt = in.readInt();
        send_at = in.readString();
        device_password = in.readString();
        favorite_status = in.readString();
        is_bonus = in.readInt();
        bonus_title = in.readString();
        bonus_body = in.readString();
        pickerList = in.readString();
        user_status_tmp = in.readString();
        image_avata = in.readString();
        unlimit_point = in.readInt();
        free_chat = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_code='" + user_code + '\'' +
                ", displayname='" + displayname + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", area='" + area + '\'' +
                ", tokken='" + tokken + '\'' +
                ", socket_jwt='" + socket_jwt + '\'' +
                ", user_status='" + user_status + '\'' +
                ", area_name='" + area_name + '\'' +
                ", job='" + job + '\'' +
                ", income='" + income + '\'' +
                ", style='" + style + '\'' +
                ", height='" + height + '\'' +
                ", avatar_url='" + avatar_url + '\'' +
                ", created_at='" + created_at + '\'' +
                ", point='" + point + '\'' +
                ", image='" + image + '\'' +
                ", city_id='" + city_id + '\'' +
                ", city_name='" + city_name + '\'' +
                ", msg_text='" + msg_text + '\'' +
                ", is_read=" + is_read +
                ", unread_cnt=" + unread_cnt +
                ", send_at='" + send_at + '\'' +
                ", device_password='" + device_password + '\'' +
                ", favorite_status='" + favorite_status + '\'' +
                ", is_bonus=" + is_bonus +
                ", bonus_title='" + bonus_title + '\'' +
                ", bonus_body='" + bonus_body + '\'' +
                ", pickerList='" + pickerList + '\'' +
                ", user_status_tmp='" + user_status_tmp + '\'' +
                ", image_avata='" + image_avata + '\'' +
                ", unlimit_point=" + unlimit_point +
                ", free_chat=" + free_chat +
                ", is_pin_chat='" + is_pin_chat + '\'' +
                '}';
    }

    public int getIs_pin_chat() {
        return is_pin_chat;
    }

    public void setIs_pin_chat(int is_pin_chat) {
        this.is_pin_chat = is_pin_chat;
    }

    public String getUser_status_tmp() {
        return user_status_tmp;
    }

    public void setUser_status_tmp(String user_status_tmp) {
        this.user_status_tmp = user_status_tmp;
    }

    public AvatarUrl[] getImageList() {
        try {
            Gson g = new Gson();
            AvatarUrl[] a = g.fromJson(this.image, AvatarUrl[].class);
            return a;
        } catch (Exception e) {
            return null;
        }
    }

    public String getImageFootprint() {
        try {
            Gson g = new Gson();
            AvatarUrl[] a = g.fromJson(this.image_avata, AvatarUrl[].class);
            return a[0].getPath();
        } catch (Exception e) {
            return "";
        }
    }

    public String getUser_id() {
        return user_id;
    }

    public String getPickerList() {
        return pickerList;
    }

    public void setPickerList(String pickerList) {
        this.pickerList = pickerList;
    }

    public int getIs_bonus() {
        return is_bonus;
    }

    public void setIs_bonus(int is_bonus) {
        this.is_bonus = is_bonus;
    }

    public String getBonus_title() {
        return bonus_title;
    }

    public void setBonus_title(String bonus_title) {
        this.bonus_title = bonus_title;
    }

    public String getBonus_body() {
        return bonus_body;
    }

    public void setBonus_body(String bonus_body) {
        this.bonus_body = bonus_body;
    }

    public String getFavorite_status() {
        return favorite_status;
    }

    public void setFavorite_status(String favorite_status) {
        this.favorite_status = favorite_status;
    }

    public String getDevice_password() {
        return device_password;
    }

    public void setDevice_password(String device_password) {
        this.device_password = device_password;
    }

    public String getSend_at() {
        return send_at;
    }

    public void setSend_at(String send_at) {
        this.send_at = send_at;
    }

    public String getMsg_text() {
        return msg_text;
    }

    public void setMsg_text(String msg_text) {
        this.msg_text = msg_text;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public int getUnread_cnt() {
        return unread_cnt;
    }

    public void setUnread_cnt(int unread_cnt) {
        this.unread_cnt = unread_cnt;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getTokken() {
        return tokken;
    }

    public void setTokken(String tokken) {
        this.tokken = tokken;
    }

    public String getSocket_jwt() {
        return socket_jwt;
    }

    public void setSocket_jwt(String socket_jwt) {
        this.socket_jwt = socket_jwt;
    }

    public String getUser_status() {
        if (user_status == null) return "";
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

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getAvatar_url() {
        if (avatar_url != null && !avatar_url.isEmpty())
            return avatar_url;
        else if (getImageList() != null) {
            return getImageList()[0].getPath();
        } else return null;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_avata() {
        return image_avata;
    }

    public void setImage_avata(String image_avata) {
        this.image_avata = image_avata;
    }

    public int getUnlimit_point() {
        return unlimit_point;
    }

    public void setUnlimit_point(int unlimit_point) {
        this.unlimit_point = unlimit_point;
    }

    public int getFree_chat() {
        return free_chat;
    }

    public void setFree_chat(int free_chat) {
        this.free_chat = free_chat;
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
        dest.writeString(password);
        dest.writeString(sex);
        dest.writeString(age);
        dest.writeString(area);
        dest.writeString(tokken);
        dest.writeString(socket_jwt);
        dest.writeString(user_status);
        dest.writeString(area_name);
        dest.writeString(job);
        dest.writeString(income);
        dest.writeString(style);
        dest.writeString(height);
        dest.writeString(avatar_url);
        dest.writeString(created_at);
        dest.writeString(point);
        dest.writeString(image);
        dest.writeString(city_id);
        dest.writeString(city_name);
        dest.writeString(msg_text);
        dest.writeInt(is_read);
        dest.writeInt(unread_cnt);
        dest.writeString(send_at);
        dest.writeString(device_password);
        dest.writeString(favorite_status);
        dest.writeInt(is_bonus);
        dest.writeString(bonus_title);
        dest.writeString(bonus_body);
        dest.writeString(pickerList);
        dest.writeString(user_status_tmp);
        dest.writeString(image_avata);
        dest.writeInt(unlimit_point);
        dest.writeInt(free_chat);
    }


    public static class UserTranfer {
        @SerializedName("code")
        @Expose
        private int code;

        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("message")
        @Expose
        private String message;

        public UserTranfer(int code, String status, String message, Tranfer tranfer) {
            this.code = code;
            this.status = status;
            this.message = message;
            this.tranfer = tranfer;
        }

        @Override
        public String toString() {
            return "UserTranfer{" +
                    "code=" + code +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    ", tranfer=" + tranfer +
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

        public Tranfer getTranfer() {
            return tranfer;
        }

        public void setTranfer(Tranfer tranfer) {
            this.tranfer = tranfer;
        }

        @SerializedName("data")
        @Expose
        private Tranfer tranfer;

        public static class Tranfer {
            @SerializedName("status")
            private String status;

            @SerializedName("user_info")
            private User user;

            public Tranfer(String status, User user) {
                this.status = status;
                this.user = user;
            }

            @Override
            public String toString() {
                return "Tranfer{" +
                        "status='" + status + '\'' +
                        ", user=" + user +
                        '}';
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public User getUser() {
                return user;
            }

            public void setUser(User user) {
                this.user = user;
            }
        }
    }


    public static class UserResponse {
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
        private User user;

        public UserResponse(int code, String status, String message, User user) {
            this.code = code;
            this.status = status;
            this.message = message;
            this.user = user;
        }

        @Override
        public String toString() {
            return "UserResponse{" +
                    "code=" + code +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    ", user=" + user +
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

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    public static class UserListResponse {
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

        public UserListResponse(int code, String status, String message, Data data) {
            this.code = code;
            this.status = status;
            this.message = message;
            this.data = data;
        }

        @Override
        public String toString() {
            return "UserListResponse{" +
                    "code=" + code +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    ", data=" + data +
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

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public static class Data {
            private int page = 0;
            private int totalResults = 0;
            private int totalPages = 0;

            public static Data instance;

            public Data() {

            }

            public static void setInstance(Data instance) {
                Data.instance = instance;
            }

            public static Data getInstance() {
                if (instance == null) {
                    synchronized (Data.class) {
                        if (null == instance) {
                            instance = new Data();
                        }
                    }
                }
                return instance;
            }

            @SerializedName("result")
            List<User> userList;

            @SerializedName("total_unread_message")
            private int total_unread_message;

            @SerializedName("total_unread_notice")
            private int total_unread_notice;

            @SerializedName("total_unread_campaign")
            private int total_unread_campaign;

            @SerializedName("total_unread_all")
            private int total_unread_all;

            public Data(int page, int totalResults, int totalPages, List<User> userList, int total_unread_message, int total_unread_notice, int total_unread_campaign, int total_unread_all) {
                this.page = page;
                this.totalResults = totalResults;
                this.totalPages = totalPages;
                this.userList = userList;
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
                        ", userList=" + userList +
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

            public List<User> getUserList() {
                return userList;
            }

            public void setUserList(List<User> userList) {
                this.userList = userList;
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
}