package com.datingapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kavya227 on 9/3/18.
 */

public class MessageDTO {

    @SerializedName("tot_unread")
    @Expose
    private String unread_msg_count;

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("username")
    @Expose
    private String name;

    @SerializedName("profile_image")
    @Expose
    private String filename;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("time")
    @Expose
    private String created_at;

    @SerializedName("chatonline")
    @Expose
    private String chatonline;

    @SerializedName("to_user_id")
    @Expose
    private String toUserId;
    @SerializedName("is_online")
    @Expose
    private String isOnline;

    @SerializedName("msg_type")
    private String msg_type;


    public String getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(String msg_type) {
        this.msg_type = msg_type;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public String getOnlineHalfHour() {
        return onlineHalfHour;
    }

    public void setOnlineHalfHour(String onlineHalfHour) {
        this.onlineHalfHour = onlineHalfHour;
    }

    @SerializedName("online_half_hour")
    @Expose

    private String onlineHalfHour;

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getChatonline() {
        return chatonline;
    }

    public void setChatonline(String chatonline) {
        this.chatonline = chatonline;
    }

    public String getUnread_msg_count() {
        return unread_msg_count;
    }

    public void setUnread_msg_count(String unread_msg_count) {
        this.unread_msg_count = unread_msg_count;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
