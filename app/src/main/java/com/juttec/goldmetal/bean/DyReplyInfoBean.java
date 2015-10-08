package com.juttec.goldmetal.bean;

/**
 * Created by Jeremy on 2015/9/24.
 *
 * 回复的 实体类
 */
public class DyReplyInfoBean {
    private String id;//回复编号ID
    private String userId;//回复人ID
    private String userName;//回复人姓名
    private String repliedId;//被回复人ID
    private String repliedName;//被回复人姓名
    private String replyContent;//回复内容

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
}
