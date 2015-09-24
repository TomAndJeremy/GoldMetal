package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.UserInfoBean;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jeremy on 2015/9/15.
 * 登录界面
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener,OnCheckedChangeListener{

    private EditText mUserName;//用户名
    private EditText mPwd;//密码

    private CheckBox cb_remember;//记住密码
    private TextView tv_forget;//忘记密码

    private Button btn_login;//登录

    private TextView tvRegister;//注册

    private MyApplication app ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        app = (MyApplication) getApplication();

        //初始化个推服务
        PushManager.getInstance().initialize(this.getApplicationContext());

        initView();

    }




    /**
     * 初始化控件
     */
    private void initView(){

        RelativeLayout head = (RelativeLayout) this.findViewById(R.id.head_layout);
        tvRegister = (TextView) head.findViewById(R.id.right_text);
        tvRegister.setOnClickListener(this);

        mUserName = (EditText) findViewById(R.id.et_name);
        mUserName.setOnClickListener(this);

        Intent intent = getIntent();
        if(intent.getStringExtra("phone")!=null){
            mUserName.setText(intent.getStringExtra("phone"));
        }

        mPwd = (EditText) findViewById(R.id.et_pwd);//密码
        mPwd.setOnClickListener(this);

        cb_remember = (CheckBox) findViewById(R.id.cb_remember);
        cb_remember.setOnCheckedChangeListener(this);


        tv_forget = (TextView) findViewById(R.id.login_tv_fergotpwd);
        tv_forget.setOnClickListener(this);

        btn_login = (Button) findViewById(R.id.login_btn);
        btn_login.setOnClickListener(this);
    }


    //检查手机号是否符合规范
    private boolean checkMobile(String num){
        if(TextUtils.isEmpty(num)||num==null){
            ToastUtil.showShort(this,"请输入用户名");
            return false;
        }else{
            Pattern pattern = Pattern.compile("^(1)\\d{10}$");
            Matcher matcher = pattern.matcher(num);
            boolean isMatcher = matcher.find();
            if (!isMatcher) {
                ToastUtil.showShort(LoginActivity.this, "用户名不符合规范");
                return false;
            }
        }

        return true;
    }


    //检查密码是否符合规范
    private boolean checkPwd(String pwd){
        if(TextUtils.isEmpty(pwd)||pwd==null){
            ToastUtil.showShort(this,"请输入密码");
            return false;
        }else{
            Pattern pattern = Pattern.compile("^\\w{6,15}$");
            Matcher matcher = pattern.matcher(pwd);
            boolean isMatcher = matcher.find();
            if (!isMatcher) {
                ToastUtil.showShort(LoginActivity.this, "密码必须6到15位");
                return false;
            }
        }
        return true;
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:

                if(checkMobile(mUserName.getText().toString())){
                    if(checkPwd(mPwd.getText().toString())){
                        if(app.getCID()==null){
                            ToastUtil.showShort(this,"请检查网络状态是否良好");
                            return;
                        }
                    }else{
                        return;
                    }
                }else{
                    return;
                }


                RequestParams params = new RequestParams();
                params.addBodyParameter("userMobile", mUserName.getText().toString());
                params.addBodyParameter("password",mPwd.getText().toString());
                params.addBodyParameter("cId", app.getCID());

                HttpUtils httpUtils = new HttpUtils();
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

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                ToastUtil.showShort(LoginActivity.this,"登录成功");
                                finish();
                            }else{
                                ToastUtil.showShort(LoginActivity.this,promptInfor);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        NetWorkUtils.showMsg(LoginActivity.this);

                    }
                });

                break;



            case R.id.login_tv_fergotpwd:
                Intent intent_forget = new Intent(LoginActivity.this, FindBcakPWDActivity.class);
                startActivity(intent_forget);
                break;

            case R.id.right_text:
                Intent intent_register = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent_register);
                finish();
                break;
        }

    }




    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }




}
