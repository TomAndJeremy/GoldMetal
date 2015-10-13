package com.juttec.goldmetal.bean;

/**
 * Created by Jeremy on 2015/9/24.、
 * <p/>
 * 用户点赞实体类
 */
public class DySupportInfoBean {
    private String id; //点赞编号ID
    private String userId; //点赞人ID
    private String userName;// 点赞人姓名

    public DySupportInfoBean(){

    }

    public DySupportInfoBean(String userId,String userName){
        this.userId = userId;
        this.userName = userName;
    }

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


}
