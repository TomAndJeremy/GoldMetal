package com.juttec.goldmetal.bean;

import java.util.List;

/**
 * Created by Jeremy on 2015/9/24.
 *
 * 评论的实体类
 */
public class DyCommentReplyBean {
    private String id; //评论编号ID
    private String discussantId;//评论人ID
    private String discussantName;//评论人姓名
    private String commentContent;//评论内容
    private List<DyReplyInfoBean> dyReply;


    public DyCommentReplyBean(){

    }

    public DyCommentReplyBean(String discussantId,String discussantName,String commentContent,List<DyReplyInfoBean> dyReply){

        this.discussantId =discussantId;
        this.discussantName = discussantName;
        this.commentContent = commentContent;
        this.dyReply = dyReply;

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDiscussantId(String discussantId) {
        this.discussantId = discussantId;
    }

    public void setDiscussantName(String discussantName) {
        this.discussantName = discussantName;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public void setDyReply(List<DyReplyInfoBean> dyReply) {
        this.dyReply = dyReply;
    }

    public String getId() {

        return id;
    }

    public String getDiscussantId() {
        return discussantId;
    }

    public String getDiscussantName() {
        return discussantName;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public List<DyReplyInfoBean> getDyReply() {
        return dyReply;
    }
}
