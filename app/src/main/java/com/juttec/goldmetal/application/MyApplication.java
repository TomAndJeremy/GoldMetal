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

    public static final String BASEURL = "http://117.132.8.93:9988/App_Areas/";
    public static final String ImgBASEURL = "http://117.132.8.93:9988";


//    public static final String BASEURL = "http://192.168.1.5:8899/App_Areas/";
//    public static final String ImgBASEURL = "http://192.168.1.5:8899";




    public static final String DYNAMIC_TYPE_ALL = "all";
    public static final String DYNAMIC_TYPE_ATTENTION = "attention";
    public static final String DYNAMIC_TYPE_PERSONAL = "personal";


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

    //图片的基地址
    public String getImgBaseUrl() {
        return ImgBASEURL;
    }

    //短信接口
    public String getSendMessageUrl() {
        return BASEURL + "App_User/SendMessage";
    }

    //用户注册接口
    public String getUserRegisterUrl() {
        return BASEURL + "App_User/UserRegister";
    }

    //用户登录接口
    public String getUserLoginUrl() {
        return BASEURL + "App_User/UserLogin";
    }

    //忘记密码接口
    public String getForgetPasswordUrl() {
        return BASEURL + "App_User/ForgetPassword";
    }

    //修改用户信息接口
    public String getEditUserInforUrl() {
        return BASEURL + "App_User/EditUserInfor";
    }

    //修改密码接口
    public String getEditPassword() {
        return BASEURL + "App_User/EditPassword";
    }

    //联系我们接口
    public String getContactUsUrl() {
        return BASEURL + "App_User/ContactUs";
    }

    //上传用户头像接口
    public String getUploadUserPhotoUrl() {
        return BASEURL + "App_User/UploadUserPhoto";
    }

    //获取用户头像接口
    public String getGetUserPhotoUrl() {
        return BASEURL + "App_User/GetUserPhoto";
    }

    //获取动态接口
    public String getGetDynamicUrl() {
        return BASEURL + "App_Contact/GetDynamic";
    }

    //发布动态接口
    public String PostDynamicUrl() {
        return BASEURL + "App_Contact/PostDynamic";
    }

    //上传图片接口
    public String getUploadPhotoUrl() {
        return BASEURL + "App_Contact/UploadPhoto";
    }


    //添加 或 取消关注 接口
    public String getAddOrCancelAttentionUrl() {
        return BASEURL + "App_Contact/AddOrCancelAttention";
    }

    //点赞或者取消赞 的 接口
    public String getAddOrCancelSupportUrl() {
        return BASEURL + "App_Contact/AddOrCancelSupport";
    }

    //评论接口
    public String getCommentUrl() {
        return BASEURL + "App_Contact/Comment";
    }

    //回复接口
    public String getReplyUrl() {
        return BASEURL + "App_Contact/Reply";
    }

    //获取消息接口
    public String getGetMyMessageUrl() {
        return BASEURL + "App_Contact/GetMyMessage";
    }

    //删除消息接口
    public String getDelMessageUrl() {
        return BASEURL + "App_Contact/DelMessage";
    }

    //获取消息详情 接口
    public String getGetMsgDetailsUrl() {
        return BASEURL + "App_Contact/GetMsgDetails";
    }

    //获取机构评论
    public String getGetOrgReviewUrl() {
        return BASEURL + "App_Consulting/GetOrgReview";
    }

    //获取机构详情
    public String getGetOrgReviewDetailsUrl() {
        return BASEURL + "App_Consulting/GetOrgReviewDetails";
    }

    //获取深度解析
    public String getGetDepthAnalysisUrl() {
        return BASEURL + "App_Consulting/GetDepthAnalysis";
    }

    //获取深度解析详情
    public String getGetDepthAnalysisDetailsUrl() {
        return BASEURL + "App_Consulting/GetDepthAnalysisDetails";
    }
    //获取投资机构

    public String getGetInvestmentOrgUrl() {
        return BASEURL + "App_Consulting/GetInvestmentOrg";
    }

    //获取投资机构详情
    public String getGetInvestmentOrgDetailsUrl() {
        return BASEURL + "App_Consulting/GetInvestmentOrgDetails";
    }
    //获取交易所信息

    public String getGetExchangeInforUrl() {
        return BASEURL + "App_Consulting/GetExchangeInfor";
    }

    //获取交易所公告
    public String getGetExchangeNoticeUrl() {
        return BASEURL + "App_Consulting/GetExchangeNotice";
    }

    //获取公告信息
    public String getGetNoticeManageUrl() {
        return BASEURL + "App_Consulting/GetNoticeManage";
    }

    //获取交易机构信息
    public String getGetTradeOrgUrl() {

        return BASEURL + "App_Consulting/GetTradeOrg";
    }

    //获取交易规则
    public String getGetTradeRuleUrl() {

        return BASEURL + "App_Consulting/GetTradeRule";
    }

   //获取今日策略
    public String getGetTodayStrategyUrl() {

        return BASEURL + "App_Consulting/GetTodayStrategy";
    }


    //提交开户信息
    public String getSubmitOpenAccountInfor() {

        return BASEURL + "App_Consulting/SubmitOpenAccountInfor";
    }

    //提交反馈信息
    public String getSubmitAdvice() {

        return BASEURL + "App_User/SubmitAdvice";
    }
//提交反馈信息
    public String getGetExchangeRateUrl() {
        return BASEURL + "App_Consulting/GetExchangeRate";
    }


}
