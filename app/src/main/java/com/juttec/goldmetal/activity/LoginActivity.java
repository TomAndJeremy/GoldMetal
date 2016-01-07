package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.juttec.goldmetal.dialog.MyProgressDialog;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.juttec.goldmetal.utils.SharedPreferencesUtil;
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

    private MyProgressDialog dialog;//加载时的 进度框





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        app = (MyApplication) getApplication();
        dialog = new MyProgressDialog(this);

        initView();

        if((Boolean) SharedPreferencesUtil.getParam(this,"remember",true)&&!"".equals(mPwd.getText().toString())){
            login();
        }

    }




    /**
     * 初始化控件
     */
    private void initView(){
        RelativeLayout head = (RelativeLayout) this.findViewById(R.id.head_layout);
        tvRegister = (TextView) head.findViewById(R.id.right_text);
        tvRegister.setOnClickListener(this);

        mUserName = (EditText) findViewById(R.id.et_name);
        mPwd = (EditText) findViewById(R.id.et_pwd);//密码
        btn_login = (Button) findViewById(R.id.login_btn);
        btn_login.setOnClickListener(this);


        mUserName.addTextChangedListener(textWatcher);
        mPwd.addTextChangedListener(textWatcher);

        mUserName.setText((String) SharedPreferencesUtil.getParam(this, "username", ""));


//        Intent intent = getIntent();
//        if(intent.getStringExtra("phone")!=null){
//            mUserName.setText(intent.getStringExtra("phone"));
//        }


        if((Boolean) SharedPreferencesUtil.getParam(this,"remember",false)){
            mPwd.setText((String) SharedPreferencesUtil.getParam(this,"pwd",""));
        }


        cb_remember = (CheckBox) findViewById(R.id.cb_remember);
        cb_remember.setOnCheckedChangeListener(this);
        cb_remember.setChecked((Boolean) SharedPreferencesUtil.getParam(this, "remember", true));



        tv_forget = (TextView) findViewById(R.id.login_tv_fergotpwd);
        tv_forget.setOnClickListener(this);
    }


    //edittext 的内容监听  更改登陆按钮的背景
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String nameContent = mUserName.getText().toString();
            String pwdContent = mPwd.getText().toString();
            if (TextUtils.isEmpty(nameContent) || "".equals(nameContent) || nameContent.trim().length() <= 0) {
                btn_login.setSelected(false);
            } else if(TextUtils.isEmpty(pwdContent) || "".equals(pwdContent) || pwdContent.trim().length() <= 0){
                btn_login.setSelected(false);
            }else{
                btn_login.setSelected(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
                        if(app.getCID()==null&&"".equals((String)SharedPreferencesUtil.getParam(LoginActivity.this, "CID", ""))){
                            ToastUtil.showShort(this,"请检查网络状态是否良好");
                            return;
                        }
                    }else{
                        return;
                    }
                }else{
                    return;
                }
                if(app.getCID()!=null){
                    SharedPreferencesUtil.setParam(this,"CID",app.getCID());
                }

                login();

                break;

            case R.id.login_tv_fergotpwd:
                Intent intent_forget = new Intent(LoginActivity.this, RegisterActivity.class);
                intent_forget.putExtra("ToActivity","ForgetPwdActivity");
                startActivity(intent_forget);
                break;

            case R.id.right_text:
                //注册
                Intent intent_register = new Intent(LoginActivity.this,RegisterActivity.class);
                intent_register.putExtra("ToActivity","RegisterActivity");
                startActivity(intent_register);

                break;
        }

    }




    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(isChecked){
            SharedPreferencesUtil.setParam(this,"remember",true);
        }else{
            SharedPreferencesUtil.setParam(this,"remember",false);
            SharedPreferencesUtil.setParam(this,"pwd","");
        }
    }


    //登录接口
    private  void login(){
        dialog.builder().setMessage("正在努力登录~").show();
        SharedPreferencesUtil.setParam(this, "username", mUserName.getText().toString());
        if(cb_remember.isChecked()){
            SharedPreferencesUtil.setParam(this, "pwd", mPwd.getText().toString());
        }


        RequestParams params = new RequestParams();

        params.addBodyParameter("userMobile", mUserName.getText().toString());
        params.addBodyParameter("password",mPwd.getText().toString());
        params.addBodyParameter("cId", (String)SharedPreferencesUtil.getParam(LoginActivity.this, "CID", ""));
        params.addBodyParameter("systemMark", "Android");//系统标识

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.getUserLoginUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog.dismiss();
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

                        //开启推送
                        PushManager.getInstance().turnOnPush(LoginActivity.this);

                        //个推 绑定用户别名
                        boolean isSuccdess = PushManager.getInstance().bindAlias(LoginActivity.this,mUserName.getText().toString().trim());
                        if(isSuccdess){
                            LogUtil.d("LoginActivity 个推别名绑定成功---------------");
                        }else{
                            LogUtil.d("LoginActivity 个推别名绑定失败---------------");
                        }

                        ToastUtil.showShort(LoginActivity.this, "登录成功");
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
                dialog.dismiss();
                NetWorkUtils.showMsg(LoginActivity.this);

            }
        });
    }




}
