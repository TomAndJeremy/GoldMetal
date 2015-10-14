package com.juttec.goldmetal.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jeremy on 2015/9/24.
 *
 * 回复的 实体类
 */
public class DyReplyInfoBean implements Parcelable {
    private String id;//回复编号ID
    private String userId;//回复人ID
    private String userName;//回复人姓名
    private String repliedId;//被回复人ID
    private String repliedName;//被回复人姓名
    private String replyContent;//回复内容



    public DyReplyInfoBean(){

    }

    public DyReplyInfoBean(String userId,String userName,String repliedId,String repliedName,String replyContent){
        this.userId = userId;
        this.userName = userName;
        this.repliedId = repliedId;
        this.repliedName = repliedName;
        this.replyContent = replyContent;
    }


    protected DyReplyInfoBean(Parcel in) {
        id = in.readString();
        userId = in.readString();
        userName = in.readString();
        repliedId = in.readString();
        repliedName = in.readString();
        replyContent = in.readString();
    }

    public static final Creator<DyReplyInfoBean> CREATOR = new Creator<DyReplyInfoBean>() {
        @Override
        public DyReplyInfoBean createFromParcel(Parcel in) {
            return new DyReplyInfoBean(in);
        }

        @Override
        public DyReplyInfoBean[] newArray(int size) {
            return new DyReplyInfoBean[size];
        }
    };

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRepliedId(String repliedId) {
        this.repliedId = repliedId;
    }

    public void setRepliedName(String repliedName) {
        this.repliedName = repliedName;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getId() {

        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getRepliedId() {
        return repliedId;
    }

    public String getRepliedName() {
        return repliedName;
    }

    public String getReplyContent() {
        return replyContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(repliedId);
        dest.writeString(repliedName);
        dest.writeString(replyContent);
    }
}
