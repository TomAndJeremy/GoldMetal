package com.juttec.goldmetal.application;


import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MyApplication extends Application {

    public static final String IMAGECACHE = "/imageCache/";// 图片缓存目录
    private static final String BASEURL = "http://localhost:4444/App_Areas/";


    public  String getSendMessageUrl() {
        return BASEURL + "App_One/SendMessage";
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
        return BASEURL + "App_One/GetDynamic";
    }

    public  String PostDynamicUrl() {
        return BASEURL + "App_One/PostDynamic";
    }


    public  String getUploadPhotoUrl() {
        return BASEURL + "App_One/GetDynamic";
    }

    public  String getAddOrCancelAttentionUrl() {
        return BASEURL + "App_One/GetDynamic";
    }

    public  String getAddOrCancelSupportUrl() {
        return BASEURL + "App_One/GetDynamic";
    }

    public  String getCommentUrl() {
        return BASEURL + "App_One/Comment";
    }

    public  String getReplyUrl() {
        return BASEURL + "App_One/Reply";
    }

    public  String getGetMyMessageUrl() {
        return BASEURL + "App_One/GetMyMessage";
    }

    public  String getDelMessageUrl() {
        return BASEURL + "App_One/DelMessage";
    }

    public  String getGetMsgDetailsUrl() {
        return BASEURL + "App_One/GetMsgDetails";
    }



    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
    }


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


}
