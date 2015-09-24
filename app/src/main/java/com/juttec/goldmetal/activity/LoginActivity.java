package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;

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

        mPwd = (EditText) findViewById(R.id.et_pwd);//密码
        mPwd.setOnClickListener(this);

        cb_remember = (CheckBox) findViewById(R.id.cb_remember);
        cb_remember.setOnCheckedChangeListener(this);


        tv_forget = (TextView) findViewById(R.id.login_tv_fergotpwd);
        tv_forget.setOnClickListener(this);

        btn_login = (Button) findViewById(R.id.login_btn);
        btn_login.setOnClickListener(this);
    }





    @Override
    public void onClick(View v) {
        Intent intent ;

        switch (v.getId()){
            case R.id.login_btn:
                intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;



            case R.id.login_tv_fergotpwd:
                intent = new Intent(LoginActivity.this, FindBcakPWDActivity.class);
                startActivity(intent);
                break;

            case R.id.right_text:
                intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
        }

    }




    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }


}
