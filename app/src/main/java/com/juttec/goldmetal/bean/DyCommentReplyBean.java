package com.juttec.goldmetal.bean;

import java.util.List;

/**
 * Created by Jeremy on 2015/9/24.
 */
public class DyCommentReplyBean {
    private String id; //评论编号ID
    private String discussantId;//评论人ID
    private String discussantName;//评论人姓名
    private String commentContent;//评论内容
    private List<DyReplyInfoBean> replys;

}
