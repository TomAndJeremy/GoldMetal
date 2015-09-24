package com.juttec.goldmetal.bean;

/**
 * Created by Administrator on 2015/9/23.
 * 用户个人信息实体类
 */
public class UserInfoBean {

    private String userId;//用户id
    private String mobile;//用户手机号
    private String goldMetalId;//掌金ID
    private String userName;//用户姓名
    private String userNickName;//用户昵称
    private String userQQ;//用户QQ
    private String userPhoto;//用户头像(地址)
    private String userCid;//用户 个推需要 的  Cid


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGoldMetalId() {
        return goldMetalId;
    }

    public void setGoldMetalId(String goldMetalId) {
        this.goldMetalId = goldMetalId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserQQ() {
        return userQQ;
    }

    public void setUserQQ(String userQQ) {
        this.userQQ = userQQ;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserCid() {
        return userCid;
    }

    public void setUserCid(String userCid) {
        this.userCid = userCid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
