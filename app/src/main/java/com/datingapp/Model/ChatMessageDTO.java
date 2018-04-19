package com.datingapp.Model;

import com.datingapp.util.Constant;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

/**
 * Created by kavya227 on 9/3/18.
 */

public class ChatMessageDTO {

    @SerializedName("chat_id")
    @Expose
    private String id;

    @SerializedName("user_id")
    @Expose
    private String sender_id;

    @SerializedName("to_user_id")
    @Expose
    private String receiver_id;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("file_name")
    @Expose
    private String file;

    @SerializedName("orig_name")
    private String orig_name = "";

    private File srcFile = null;

    @SerializedName("msg_type")
    @Expose
    private String type = "";

    @SerializedName("read_status")
    @Expose
    private String read_status;

    @SerializedName("time")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    @SerializedName("deleted_at")
    @Expose
    private String deleted_at;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("messageseen")
    @Expose
    private String messageseen;

    @SerializedName("msg_id")
    private String msg_id;

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public boolean isPlay = false;

    private String state = Constant.play;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    private int seekPercentage = -1;
    private int duration = -1;

    private Boolean isUploading = false;

    public String getOrig_name() {
        return orig_name;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setOrig_name(String orig_name) {
        this.orig_name = orig_name;
    }

    public Boolean getUploading() {
        return isUploading;
    }

    public void setUploading(Boolean uploading) {
        isUploading = uploading;
    }

    public File getSrcFile() {
        return srcFile;
    }

    public void setSrcFile(File srcFile) {
        this.srcFile = srcFile;
    }

    public String getMessageseen() {
        return messageseen;
    }

    public int getSeekPercentage() {
        return seekPercentage;
    }

    public void setSeekPercentage(int seekPercentage) {
        this.seekPercentage = seekPercentage;
    }

    public void setMessageseen(String messageseen) {
        this.messageseen = messageseen;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRead_status() {
        return read_status;
    }

    public void setRead_status(String read_status) {
        this.read_status = read_status;
    }

    public String getCreated_at() {
        //  Log.e("created_At",created_at);
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }
}
