package com.juttec.goldmetal.bean;

/**
 * Created by Administrator on 2015/10/8.
 *
 * 消息实体类
 */
public class MessageBean {

    private String MsgId;  //消息编号ID
    private String MsgUserId;  //用户编号ID
    private String MsgUserPhoto;  //发消息人头像
    private String MsgDyId;  //动态编号ID
    private String MsgCommentId;  //评论编号ID   msgType=1
    private String MsgReplyId;  //回复编号ID     msgType=2
    private String MsgReplyerId;  //回复人ID
    private String MsgReplyerName;  //回复人昵称
    private String MsgBirefTitle;  //简要标题
    private String MsgBriefContent;  //简要内容
    private String MsgType;  //消息类别(0：赞 1：评论 2：回复)
    private String MsgAddTime;  //消息时间


    public String getMsgId() {
        return MsgId;
    }

    public void setMsgId(String msgId) {
        MsgId = msgId;
    }

    public String getMsgUserId() {
        return MsgUserId;
    }

    public void setMsgUserId(String msgUserId) {
        MsgUserId = msgUserId;
    }

    public String getMsgUserPhoto() {
        return MsgUserPhoto;
    }

    public void setMsgUserPhoto(String msgUserPhoto) {
        MsgUserPhoto = msgUserPhoto;
    }

    public String getMsgDyId() {
        return MsgDyId;
    }

    public void setMsgDyId(String msgDyId) {
        MsgDyId = msgDyId;
    }

    public String getMsgCommentId() {
        return MsgCommentId;
    }

    public void setMsgCommentId(String msgCommentId) {
        MsgCommentId = msgCommentId;
    }

    public String getMsgReplyId() {
        return MsgReplyId;
    }

    public void setMsgReplyId(String msgReplyId) {
        MsgReplyId = msgReplyId;
    }

    public String getMsgReplyerId() {
        return MsgReplyerId;
    }

    public void setMsgReplyerId(String msgReplyerId) {
        MsgReplyerId = msgReplyerId;
    }

    public String getMsgReplyerName() {
        return MsgReplyerName;
    }

    public void setMsgReplyerName(String msgReplyerName) {
        MsgReplyerName = msgReplyerName;
    }

    public String getMsgBirefTitle() {
        return MsgBirefTitle;
    }

    public void setMsgBirefTitle(String msgBirefTitle) {
        MsgBirefTitle = msgBirefTitle;
    }

    public String getMsgBriefContent() {
        return MsgBriefContent;
    }

    public void setMsgBriefContent(String msgBriefContent) {
        MsgBriefContent = msgBriefContent;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public String getMsgAddTime() {
        return MsgAddTime;
    }

    public void setMsgAddTime(String msgAddTime) {
        MsgAddTime = msgAddTime;
    }
}
