package com.example.neighbormaterebuild.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class Metadata implements Parcelable{

    public static Metadata instance;

    @SerializedName("code")
    private String code;

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Data data;

    public Metadata(String code, String status, Data data) {
        this.code = code;
        this.status = status;
        this.data = data;
    }

    public Metadata() {

    }

    protected Metadata(Parcel in) {
        code = in.readString();
        status = in.readString();
        data = in.readParcelable(Data.class.getClassLoader());
    }

    public static final Parcelable.Creator<Metadata> CREATOR = new Parcelable.Creator<Metadata>() {
        @Override
        public Metadata createFromParcel(Parcel in) {
            return new Metadata(in);
        }

        @Override
        public Metadata[] newArray(int size) {
            return new Metadata[size];
        }
    };

    public static void setInstance(Metadata instance) {
        Metadata.instance = instance;
    }

    public static Metadata getInstance() {
        if (instance == null) {
            synchronized (Metadata.class) {
                if (null == instance) {
                    instance = new Metadata();
                }
            }
        }
        return instance;
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "code='" + code + '\'' +
                ", status='" + status + '\'' +
                ", data=" + data +
                '}';
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(code);
        parcel.writeString(status);
        parcel.writeParcelable(data, i);
    }

    public static class Data implements Parcelable {

        @SerializedName("user_profile_list")
        private UserProfileList userProfileList;

        @SerializedName("profile_not_set")
        private String profile_not_set;

        @SerializedName("push_off_title")
        private String push_off_title;

        @SerializedName("push_off_content")
        private String push_off_content;

        @SerializedName("push_off_image")
        private String push_off_image;

        @SerializedName("function_off")
        private FunctionOff function_off;

        @SerializedName("supporter")
        private Supporter supporter;

        @SerializedName("ng_words_replace")
        private String ng_words_replace;

        @SerializedName("ng_words")
        private NGWord ngWord;

        public Data(UserProfileList userProfileList, String profile_not_set, String push_off_title, String push_off_content, String push_off_image, FunctionOff function_off, Supporter supporter, String ng_words_replace, NGWord ngWord) {
            this.userProfileList = userProfileList;
            this.profile_not_set = profile_not_set;
            this.push_off_title = push_off_title;
            this.push_off_content = push_off_content;
            this.push_off_image = push_off_image;
            this.function_off = function_off;
            this.supporter = supporter;
            this.ng_words_replace = ng_words_replace;
            this.ngWord = ngWord;
        }

        public Data(UserProfileList userProfileList) {
            this.userProfileList = userProfileList;
        }

        protected Data(Parcel in) {
            userProfileList = in.readParcelable(UserProfileList.class.getClassLoader());
            profile_not_set = in.readString();
            push_off_title = in.readString();
            push_off_content = in.readString();
            push_off_image = in.readString();
            function_off = in.readParcelable(FunctionOff.class.getClassLoader());
            supporter = in.readParcelable(Supporter.class.getClassLoader());
            ng_words_replace = in.readString();
            ngWord = in.readParcelable(NGWord.class.getClassLoader());
        }

        public static final Creator<Data> CREATOR = new Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        @Override
        public String toString() {
            return "Data{" +
                    "userProfileList=" + userProfileList +
                    ", profile_not_set='" + profile_not_set + '\'' +
                    ", push_off_title='" + push_off_title + '\'' +
                    ", push_off_content='" + push_off_content + '\'' +
                    ", push_off_image='" + push_off_image + '\'' +
                    ", supporter='" + supporter + '\'' +
                    ", ng_words='" + ngWord + '\'' +
                    ", ng_words_replace='" + ng_words_replace + '\'' +
                    '}';
        }

        public String getProfile_not_set() {
            return profile_not_set;
        }

        public void setProfile_not_set(String profile_not_set) {
            this.profile_not_set = profile_not_set;
        }

        public UserProfileList getUserProfileList() {
            return userProfileList;
        }

        public void setUserProfileList(UserProfileList userProfileList) {
            this.userProfileList = userProfileList;
        }

        public String getPush_off_title() {
            return push_off_title;
        }

        public void setPush_off_title(String push_off_title) {
            this.push_off_title = push_off_title;
        }

        public String getPush_off_content() {
            return push_off_content;
        }

        public void setPush_off_content(String push_off_content) {
            this.push_off_content = push_off_content;
        }

        public String getPush_off_image() {
            return push_off_image;
        }

        public void setPush_off_image(String push_off_image) {
            this.push_off_image = push_off_image;
        }

        public FunctionOff getFunction_off() {
            return function_off;
        }

        public void setFunction_off(FunctionOff function_off) {
            this.function_off = function_off;
        }

        public Supporter getSupporter() {
            return supporter;
        }

        public void setSupporter(Supporter supporter) {
            this.supporter = supporter;
        }

        public String getNg_words_replace() {
            return ng_words_replace;
        }

        public void setNg_words_replace(String ng_words_replace) {
            this.ng_words_replace = ng_words_replace;
        }

        public NGWord getNgWord() {
            return ngWord;
        }

        public void setNgWord(NGWord ngWord) {
            this.ngWord = ngWord;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(userProfileList, i);
            parcel.writeString(profile_not_set);
            parcel.writeString(push_off_title);
            parcel.writeString(push_off_content);
            parcel.writeString(push_off_image);
            parcel.writeParcelable(function_off, i);
            parcel.writeParcelable(supporter, i);
            parcel.writeString(ng_words_replace);
            parcel.writeParcelable(ngWord, i);
        }
    }

    public static class UserProfileList implements Parcelable {

        @SerializedName("display_name")
        private String displayname;

        @SerializedName("password")
        private String password;

        @SerializedName("sex")
        private List<Util> sex;

        @SerializedName("age")
        private List<Util> age;

        @SerializedName("area")
        @Expose
        private HashMap<Integer, Util> areaHashMap;

        @SerializedName("income")
        private List<Util> income;

        @SerializedName("height")
        private List<Util> height;

        @SerializedName("job")
        private List<Util> job;

        @SerializedName("style")
        private List<Util> style;

        public UserProfileList() {
        }

        public UserProfileList(String displayname, String password, List<Util> sex, List<Util> age, HashMap<Integer, Util> areaHashMap, List<Util> income, List<Util> height, List<Util> job, List<Util> style) {
            this.displayname = displayname;
            this.password = password;
            this.sex = sex;
            this.age = age;
            this.areaHashMap = areaHashMap;
            this.income = income;
            this.height = height;
            this.job = job;
            this.style = style;
        }

        protected UserProfileList(Parcel in) {
            displayname = in.readString();
            password = in.readString();
        }

        public static final Creator<UserProfileList> CREATOR = new Creator<UserProfileList>() {
            @Override
            public UserProfileList createFromParcel(Parcel in) {
                return new UserProfileList(in);
            }

            @Override
            public UserProfileList[] newArray(int size) {
                return new UserProfileList[size];
            }
        };

        @Override
        public String toString() {
            return "UserProfileList{" +
                    "displayname='" + displayname + '\'' +
                    ", password='" + password + '\'' +
                    ", sex=" + sex +
                    ", age=" + age +
                    ", areaHashMap=" + areaHashMap +
                    ", income=" + income +
                    ", height=" + height +
                    ", job=" + job +
                    ", style=" + style +
                    '}';
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

        public List<Util> getSex() {
            return sex;
        }

        public void setSex(List<Util> sex) {
            this.sex = sex;
        }

        public List<Util> getAge() {
            return age;
        }

        public void setAge(List<Util> age) {
            this.age = age;
        }

        public HashMap<Integer, Util> getAreaHashMap() {
            return areaHashMap;
        }

        public void setAreaHashMap(HashMap<Integer, Util> areaHashMap) {
            this.areaHashMap = areaHashMap;
        }

        public List<Util> getIncome() {
            return income;
        }

        public void setIncome(List<Util> income) {
            this.income = income;
        }

        public List<Util> getHeight() {
            return height;
        }

        public void setHeight(List<Util> height) {
            this.height = height;
        }

        public List<Util> getJob() {
            return job;
        }

        public void setJob(List<Util> job) {
            this.job = job;
        }

        public List<Util> getStyle() {
            return style;
        }

        public void setStyle(List<Util> style) {
            this.style = style;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(displayname);
            parcel.writeString(password);
        }
    }

    public static class Supporter implements Parcelable{
        @SerializedName("id")
        private String id;

        @SerializedName("displayname")
        private String displayname;

        @SerializedName("user_code")
        private String user_code;

        public Supporter(String id, String displayname, String user_code) {
            this.id = id;
            this.displayname = displayname;
            this.user_code = user_code;
        }

        protected Supporter(Parcel in) {
            id = in.readString();
            displayname = in.readString();
            user_code = in.readString();
        }

        public static final Creator<Supporter> CREATOR = new Creator<Supporter>() {
            @Override
            public Supporter createFromParcel(Parcel in) {
                return new Supporter(in);
            }

            @Override
            public Supporter[] newArray(int size) {
                return new Supporter[size];
            }
        };

        @Override
        public String toString() {
            return "Supporter{" +
                    ", id=" + id + "\n" +
                    "displayname='" + displayname + '\'' +
                    ", user_code='" + user_code + '\'' +
                    '}';
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDisplayname() {
            return displayname;
        }

        public void setDisplayname(String displayname) {
            this.displayname = displayname;
        }

        public String getUser_code() {
            return user_code;
        }

        public void setUser_code(String user_code) {
            this.user_code = user_code;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(id);
            parcel.writeString(displayname);
            parcel.writeString(user_code);
        }
    }

    public static class FunctionOff implements Parcelable {
        @SerializedName("version_appstore_review")
        private String version_appstore_review;

        @SerializedName("new_version")
        private String new_version;

        @SerializedName("new_version_url")
        private String new_version_url;

        @SerializedName("new_version_title")
        private String new_version_title;

        @SerializedName("new_version_description")
        private String new_version_description;

        @SerializedName("force_update_version")
        private String force_update_version;

        @SerializedName("force_update_title")
        private String force_update_title;

        @SerializedName("force_update_description")
        private String force_update_description;

        public FunctionOff(String version_appstore_review, String new_version, String new_version_url, String new_version_title, String new_version_description, String force_update_version, String force_update_title, String force_update_description) {
            this.version_appstore_review = version_appstore_review;
            this.new_version = new_version;
            this.new_version_url = new_version_url;
            this.new_version_title = new_version_title;
            this.new_version_description = new_version_description;
            this.force_update_version = force_update_version;
            this.force_update_title = force_update_title;
            this.force_update_description = force_update_description;
        }

        protected FunctionOff(Parcel in) {
            version_appstore_review = in.readString();
            new_version = in.readString();
            new_version_url = in.readString();
            new_version_title = in.readString();
            new_version_description = in.readString();
            force_update_version = in.readString();
            force_update_title = in.readString();
            force_update_description = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<FunctionOff> CREATOR = new Creator<FunctionOff>() {
            @Override
            public FunctionOff createFromParcel(Parcel in) {
                return new FunctionOff(in);
            }

            @Override
            public FunctionOff[] newArray(int size) {
                return new FunctionOff[size];
            }
        };

        public String getVersion_appstore_review() {
            return version_appstore_review;
        }

        public void setVersion_appstore_review(String version_appstore_review) {
            this.version_appstore_review = version_appstore_review;
        }

        public String getNew_version() {
            return new_version;
        }

        public void setNew_version(String new_version) {
            this.new_version = new_version;
        }

        public String getNew_version_url() {
            return new_version_url;
        }

        public void setNew_version_url(String new_version_url) {
            this.new_version_url = new_version_url;
        }

        public String getNew_version_title() {
            return new_version_title;
        }

        public void setNew_version_title(String new_version_title) {
            this.new_version_title = new_version_title;
        }

        public String getNew_version_description() {
            return new_version_description;
        }

        public void setNew_version_description(String new_version_description) {
            this.new_version_description = new_version_description;
        }

        public String getForce_update_version() {
            return force_update_version;
        }

        public void setForce_update_version(String force_update_version) {
            this.force_update_version = force_update_version;
        }

        public String getForce_update_title() {
            return force_update_title;
        }

        public void setForce_update_title(String force_update_title) {
            this.force_update_title = force_update_title;
        }

        public String getForce_update_description() {
            return force_update_description;
        }

        public void setForce_update_description(String force_update_description) {
            this.force_update_description = force_update_description;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(version_appstore_review);
            parcel.writeString(new_version);
            parcel.writeString(new_version_url);
            parcel.writeString(new_version_title);
            parcel.writeString(new_version_description);
            parcel.writeString(force_update_version);
            parcel.writeString(force_update_title);
            parcel.writeString(force_update_description);
        }
    }

    public static class NGWord implements Parcelable {
        @SerializedName("result")
        private List<String> result;

        @SerializedName("total")
        private int total;

        public NGWord(List<String> result, int total) {
            this.result = result;
            this.total = total;
        }

        protected NGWord(Parcel in) {
            result = in.createStringArrayList();
            total = in.readInt();
        }

        public static final Creator<NGWord> CREATOR = new Creator<NGWord>() {
            @Override
            public NGWord createFromParcel(Parcel in) {
                return new NGWord(in);
            }

            @Override
            public NGWord[] newArray(int size) {
                return new NGWord[size];
            }
        };

        public List<String> getResult() {
            return result;
        }

        public void setResult(List<String> result) {
            this.result = result;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public static String replaceRanges(String text) {
            for (String replacement : Metadata.getInstance().getData().getNgWord().getResult()) {
                text = text.replaceAll(replacement,
                        Metadata.getInstance().getData().getNg_words_replace());
            }
            return text;
        }

        public static boolean test(String text) {
            for (String replacement : Metadata.getInstance().getData().getNgWord().getResult()) {
                if (text.contains(replacement)) return true;
            }
            return false;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeStringList(result);
            parcel.writeInt(total);
        }
    }
}
