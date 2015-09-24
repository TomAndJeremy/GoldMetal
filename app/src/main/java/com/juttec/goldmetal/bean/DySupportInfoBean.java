package com.juttec.goldmetal.bean;

/**
 * Created by Jeremy on 2015/9/24.
 */
public class DySupportInfoBean {
    private String id; //点赞编号ID
    private String userId; //点赞人ID

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    private String userName;// 点赞人姓名
}
