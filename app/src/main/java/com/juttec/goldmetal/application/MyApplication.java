package com.juttec.goldmetal.application;


import android.app.Application;
import android.content.Context;

import com.juttec.goldmetal.bean.UserInfoBean;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MyApplication extends Application {

    public static final String IMAGECACHE = "/imageCache/";// 图片缓存目录
    private static final String BASEURL = "http://192.168.1.35:8155/App_Areas/";






    private String CID;//用于  推送的  CID
    private UserInfoBean userInfoBean;//用户实体类


    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public UserInfoBean getUserInfoBean() {
        return userInfoBean;
    }

    public void setUserInfoBean(UserInfoBean userInfoBean) {
        this.userInfoBean = userInfoBean;
    }






    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
    }


    //初始化ImageLoader
    void initImageLoader(Context context) {

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());

    }


    //接口访问地址
    public  String getSendMessageUrl() {
        return BASEURL + "App_Contact/SendMessage";
    }

    public  String getUserRegisterUrl() {
        return BASEURL + "App_User/UserRegister";
    }

    public  String getUserLoginUrl() {
        return BASEURL + "App_User/UserLogin";
    }

    public  String getForgetPasswordUrl() {
        return BASEURL + "App_User/ForgetPassword";
    }

    public  String getEditUserInforUrl() {
        return BASEURL + "App_User/EditUserInfor";
    }

    public  String getContactUsUrl() {
        return BASEURL + "App_User/ContactUs";
    }

    public  String getUploadUserPhotoUrl() {
        return BASEURL + "App_User/UploadUserPhoto";
    }

    public  String getGetUserPhotoUrl() {
        return BASEURL + "App_User/GetUserPhoto";
    }

    public  String getGetDynamicUrl() {
        return BASEURL + "App_Contact/GetDynamic";
    }

    public  String PostDynamicUrl() {
        return BASEURL + "App_Contact/PostDynamic";
    }

    public  String getUploadPhotoUrl() {
        return BASEURL + "App_Contact/GetDynamic";
    }

    public  String getAddOrCancelAttentionUrl() {
        return BASEURL + "App_Contact/GetDynamic";
    }

    public  String getAddOrCancelSupportUrl() {
        return BASEURL + "App_Contact/GetDynamic";
    }

    public  String getCommentUrl() {
        return BASEURL + "App_Contact/Comment";
    }

    public  String getReplyUrl() {
        return BASEURL + "App_Contact/Reply";
    }

    public  String getGetMyMessageUrl() {
        return BASEURL + "App_Contact/GetMyMessage";
    }

    public  String getDelMessageUrl() {
        return BASEURL + "App_Contact/DelMessage";
    }

    public  String getGetMsgDetailsUrl() {
        return BASEURL + "App_Contact/GetMsgDetails";
    }
}
