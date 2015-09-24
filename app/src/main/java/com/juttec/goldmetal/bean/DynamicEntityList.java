package com.juttec.goldmetal.bean;

import java.util.ArrayList;

/**
 * Created by Jeremy on 2015/9/24.
 */
public class DynamicEntityList {
    private String id;  //动态ID
    private String userId;//用户ID
    private String userName;//用户昵称
    private String addTime; //发表时间
    private String dyContent; //动态内容
    private ArrayList<String> dyPhoto;//图片
    private ArrayList<DySupportInfoBean> dySupport;
    private ArrayList<DyCommentReplyBean> dyCommentReply;

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public void setDyContent(String dyContent) {
        this.dyContent = dyContent;
    }

    public void setDyPhoto(ArrayList<String> dyPhoto) {
        this.dyPhoto = dyPhoto;
    }

    public void setDySupport(ArrayList<DySupportInfoBean> dySupport) {
        this.dySupport = dySupport;
    }

    public void setDyCommentReply(ArrayList<DyCommentReplyBean> dyCommentReply) {
        this.dyCommentReply = dyCommentReply;
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

    public String getAddTime() {
        return addTime;
    }

    public String getDyContent() {
        return dyContent;
    }

    public ArrayList<String> getDyPhoto() {
        return dyPhoto;
    }

    public ArrayList<DySupportInfoBean> getDySupport() {
        return dySupport;
    }

    public ArrayList<DyCommentReplyBean> getDyCommentReply() {
        return dyCommentReply;
    }

    @Override
    public String toString() {
        return "DynamicEntityList{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", addTime='" + addTime + '\'' +
                ", dyContent='" + dyContent + '\'' +
                ", dyPhoto=" + dyPhoto +
                ", dySupport=" + dySupport +
                ", dyCommentReply=" + dyCommentReply +
                '}';
    }
}

