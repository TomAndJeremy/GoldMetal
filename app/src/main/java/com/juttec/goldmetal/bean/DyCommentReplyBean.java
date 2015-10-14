package com.juttec.goldmetal.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Jeremy on 2015/9/24.
 *
 * 评论的实体类
 */
public class DyCommentReplyBean implements Parcelable {
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

    protected DyCommentReplyBean(Parcel in) {
        id = in.readString();
        discussantId = in.readString();
        discussantName = in.readString();
        commentContent = in.readString();
        dyReply = in.createTypedArrayList(DyReplyInfoBean.CREATOR);
    }

    public static final Creator<DyCommentReplyBean> CREATOR = new Creator<DyCommentReplyBean>() {
        @Override
        public DyCommentReplyBean createFromParcel(Parcel in) {
            return new DyCommentReplyBean(in);
        }

        @Override
        public DyCommentReplyBean[] newArray(int size) {
            return new DyCommentReplyBean[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(discussantId);
        dest.writeString(discussantName);
        dest.writeString(commentContent);
        dest.writeTypedList(dyReply);
    }
}
