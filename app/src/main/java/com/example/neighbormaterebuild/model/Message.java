package com.example.neighbormaterebuild.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.neighbormaterebuild.utils.FormatUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class Message implements Parcelable {

    public static final String text = "text";
    public static final String location = "location";
    public static final String image = "image";

    @SerializedName("msg_id")
    private String msgID;

    @SerializedName("msg")
    private String msg;

    @SerializedName("is_read")
    private int isRead;

    @SerializedName("received")
    private int received;

    @SerializedName("u_id")
    private int uID;

    @SerializedName("r_id")
    private int rID;

    @SerializedName("showed")
    private int showed;

    @SerializedName("is_delete")
    private int isDelete;

    @SerializedName("time")
    private String timeNotConvert;

    @SerializedName("type")
    private String type;

    @SerializedName("chat_center")
    private String chatCenter;

    @SerializedName("keijiban_id")
    private String keijibanID;

    @SerializedName("param")
    Map param;

    public Message(String msgID, String msg, int isRead, int received, int uID, int rID, int showed, int isDelete, String timeNotConvert, String type, String chatCenter, String keijibanID, Map param) {
        this.msgID = msgID;
        this.msg = msg;
        this.isRead = isRead;
        this.received = received;
        this.uID = uID;
        this.rID = rID;
        this.showed = showed;
        this.isDelete = isDelete;
        this.timeNotConvert = timeNotConvert;
        this.type = type;
        this.chatCenter = chatCenter;
        this.keijibanID = keijibanID;
        this.param = param;
    }

    public Message(Parcel in) {
        this.msgID = in.readString();
        this.msg = in.readString();
        this.isRead = in.readInt();
        this.received = in.readInt();
        this.uID = in.readInt();
        this.rID = in.readInt();
        this.showed = in.readInt();
        this.isDelete = in.readInt();
        this.timeNotConvert = in.readString();
        this.type = in.readString();
        this.chatCenter = in.readString();
        this.keijibanID = in.readString();
        //this.param = in.readHashMap();
    }

    public Message() {

    }

    @Override
    public String toString() {
        return "Message{" +
                "msgID='" + msgID + '\'' +
                ", msg='" + msg + '\'' +
                ", isRead=" + isRead +
                ", received=" + received +
                ", uID=" + uID +
                ", rID=" + rID +
                ", showed=" + showed +
                ", isDelete=" + isDelete +
                ", timeNotConvert='" + timeNotConvert + '\'' +
                ", type='" + type + '\'' +
                ", chatCenter='" + chatCenter + '\'' +
                ", keijibanID='" + keijibanID + '\'' +
                ", param=" + param +
                '}';
    }

    public String time() {
        return FormatUtil.convertTime(timeNotConvert);
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }

    public int getuID() {
        return uID;
    }

    public void setuID(int uID) {
        this.uID = uID;
    }

    public int getrID() {
        return rID;
    }

    public void setrID(int rID) {
        this.rID = rID;
    }

    public int getShowed() {
        return showed;
    }

    public void setShowed(int showed) {
        this.showed = showed;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public String getTimeNotConvert() {
        return timeNotConvert;
    }

    public void setTimeNotConvert(String timeNotConvert) {
        this.timeNotConvert = timeNotConvert;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChatCenter() {
        return chatCenter;
    }

    public void setChatCenter(String chatCenter) {
        this.chatCenter = chatCenter;
    }

    public String getKeijibanID() {
        return keijibanID;
    }

    public void setKeijibanID(String keijibanID) {
        this.keijibanID = keijibanID;
    }

    public Map getParam() {
        return param;
    }

    public void setParam(Map param) {
        this.param = param;
    }


    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(msgID);
        parcel.writeString(msg);
        parcel.writeInt(isRead);
        parcel.writeInt(received);
        parcel.writeInt(rID);
        parcel.writeInt(uID);
        parcel.writeInt(showed);
        parcel.writeInt(isDelete);
        parcel.writeString(timeNotConvert);
        parcel.writeString(type);
        parcel.writeString(chatCenter);
        parcel.writeString(keijibanID);
    }

    public  static class MessageResponse {
        @SerializedName("code")
        private String code;

        @SerializedName("status")
        private String status;

        @SerializedName("data")
        private Data data;

        public MessageResponse(String code, String status, Data data) {
            this.code = code;
            this.status = status;
            this.data = data;
        }

        @Override
        public String toString() {
            return "MessageResponse{" +
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

        public static class Data {
            private int page = 0;
            private int totalResults = 0;
            private int totalPages = 0;

            @SerializedName("result")
            @Expose
            List<Message> messageList;

            public Data(int page, int totalResults, int totalPages, List<Message> messageList) {
                this.page = page;
                this.totalResults = totalResults;
                this.totalPages = totalPages;
                this.messageList = messageList;
            }

            @Override
            public String toString() {
                return "Data{" +
                        "page=" + page +
                        ", totalResults=" + totalResults +
                        ", totalPages=" + totalPages +
                        ", messageList=" + messageList +
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

            public List<Message> getMessageList() {
                return messageList;
            }

            public void setMessageList(List<Message> messageList) {
                this.messageList = messageList;
            }
        }
    }
}
