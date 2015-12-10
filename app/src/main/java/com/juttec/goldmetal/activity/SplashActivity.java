package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

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


        mUserName = (String) SharedPreferencesUtil.getParam(this, "username", "");
        mPwd = (String) SharedPreferencesUtil.getParam(this, "pwd", "");
        mCID = (String) SharedPreferencesUtil.getParam(this, "CID", "");


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ("".equals(mUserName) || "".equals(mPwd) || "".equals(mCID)) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    //执行登录
                    login();
                    LogUtil.d("login------------------------" + mUserName + mPwd + mCID);
                }
            }
        }, 500);

    }


    //登录接口
    private void login() {

        RequestParams params = new RequestParams();

        params.addBodyParameter("userMobile", mUserName);
        params.addBodyParameter("password", mPwd);
        params.addBodyParameter("cId", mCID);

        HttpUtils httpUtils = new HttpUtils(3000);//设置 3秒超时
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
                        app.setUserInfoBean(userInfoBean);

                    } else {
                    }

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
