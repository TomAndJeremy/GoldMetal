package com.juttec.goldmetal.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jeremy on 2015/9/24.、
 * <p/>
 * 用户点赞实体类
 */
public class DySupportInfoBean implements Parcelable {
    private String id; //点赞编号ID
    private String userId; //点赞人ID
    private String userName;// 点赞人姓名

    public DySupportInfoBean(){

    }

    public DySupportInfoBean(String userId,String userName){
        this.userId = userId;
        this.userName = userName;
    }

    protected DySupportInfoBean(Parcel in) {
        id = in.readString();
        userId = in.readString();
        userName = in.readString();
    }

    public static final Creator<DySupportInfoBean> CREATOR = new Creator<DySupportInfoBean>() {
        @Override
        public DySupportInfoBean createFromParcel(Parcel in) {
            return new DySupportInfoBean(in);
        }

        @Override
        public DySupportInfoBean[] newArray(int size) {
            return new DySupportInfoBean[size];
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

    public String getId() {

        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
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
    }
}
