package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.igexin.sdk.PushManager;
import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.UserInfoBean;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.SharedPreferencesUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 启动页
 */
public class SplashActivity extends AppCompatActivity {

    private String mUserName;//用户名
    private String mPwd;//密码
    private String mCID;//推送用的cid

    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        app = (MyApplication) getApplication();

        //初始化个推SDK
        PushManager.getInstance().initialize(SplashActivity.this.getApplicationContext());

        float width = getResources().getDisplayMetrics().widthPixels;
        float height = getResources().getDisplayMetrics().heightPixels;
        LogUtil.d(width+"--------------"+height);


        mUserName = (String) SharedPreferencesUtil.getParam(this, "username", "");
        mPwd = (String) SharedPreferencesUtil.getParam(this, "pwd", "");
        mCID = (String) SharedPreferencesUtil.getParam(this, "CID", "");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Handler handler = new Handler();
        if ("".equals(mUserName) || "".equals(mPwd) || "".equals(mCID)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enterMain();
                }
            }, 2000);
        } else {
            //执行登录
            login();
            LogUtil.d("login------------------------" + mUserName + mPwd + mCID);
        }
    }

    //登录接口
    private void login() {
        final long timeStart = System.currentTimeMillis();
        RequestParams params = new RequestParams();

        params.addBodyParameter("userMobile", mUserName);
        params.addBodyParameter("password", mPwd);
        params.addBodyParameter("cId", mCID);
        params.addBodyParameter("systemMark", "Android");//系统标识

        HttpUtils httpUtils = new HttpUtils(2000);//设置 2秒超时
        httpUtils.send(HttpRequest.HttpMethod.POST, app.getUserLoginUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                LogUtil.d(responseInfo.result.toString());

                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    String status = object.getString("status");
                    String promptInfor = object.getString("promptInfor");
                    if ("1".equals(status)) {
                        JSONObject userObject = object.getJSONObject("entityList");
                        UserInfoBean userInfoBean = new UserInfoBean();
                        userInfoBean.setUserId(userObject.getString("id"));
                        userInfoBean.setMobile(userObject.getString("userMobile"));
                        userInfoBean.setGoldMetalId(userObject.getString("goldMetalId"));
                        userInfoBean.setUserName(userObject.getString("userName"));
                        userInfoBean.setUserNickName(userObject.getString("userNickName"));
                        userInfoBean.setUserQQ(userObject.getString("userQQ"));
                        userInfoBean.setUserPhoto(userObject.getString("userPhoto"));
                        userInfoBean.setNoteWarn(userObject.getString("noteWarn"));
                        app.setUserInfoBean(userInfoBean);

                        //开启推送
                        PushManager.getInstance().turnOnPush(SplashActivity.this);
                        //个推 绑定用户别名
                        boolean isSuccdess = PushManager.getInstance().bindAlias(SplashActivity.this,mUserName);
                        if(isSuccdess){
                            LogUtil.d("SplashActivity 个推别名绑定成功---------------");
                        }
                    } else {
                    }

                    long timeEnd = System.currentTimeMillis();
                    if(timeEnd-timeStart<2000){
                        new Handler().postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        enterMain();
                                    }
                                },(2000-(timeEnd-timeStart))
                        );
                    }else{
                        enterMain();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                enterMain();
            }
        });
    }


    //跳转到主界面
    private void enterMain(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
