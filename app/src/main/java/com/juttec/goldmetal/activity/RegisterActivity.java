package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.customview.HeadLayout;
import com.juttec.goldmetal.dialog.MyProgressDialog;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.juttec.goldmetal.utils.SharedPreferencesUtil;
import com.juttec.goldmetal.utils.SnackbarUtil;
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

import static com.juttec.goldmetal.R.id.register_bt_ok;

/**
 * 注册界面   和 忘记密码界面   同一个界面
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText phone;//手机号
    private EditText identifyCode;//验证码
    private EditText password;//密码
    private EditText pwdConfig;//确认密码

    private Button getCode;//获取验证码

    private Button register;//注册 按钮
    private TextView tv_mark;//手机注册

    private String toActivity;//  ForgetPwdActivity  RegisterActivity

    private TimeCount timeCount;

    private MyApplication app;

    private String phone_back;//返回的手机号
    private String code_back;//返回的验证码

    private MyProgressDialog dialog;//正在加载的  进度框


    private int countDown = 60 * 1000;//倒计时的时间  默认为60秒


    private long firstTime = 0;
    private long lastTime = 0;
    private int codeTime = 3 * 60 * 1000;//验证码有效期  默认3分钟内有效


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        app = (MyApplication) getApplication();
        dialog = new MyProgressDialog(this);

        //
        toActivity = getIntent().getStringExtra("ToActivity");

        initView();
    }

    private void initView() {

        timeCount = new TimeCount(countDown, 1000);
        tv_mark = (TextView) findViewById(R.id.tv_mark);

        phone = (EditText) this.findViewById(R.id.register_et_phone);
        identifyCode = (EditText) this.findViewById(R.id.register_et_identifying_code);
        password = (EditText) this.findViewById(R.id.register_et_pwd);
        pwdConfig = (EditText) this.findViewById(R.id.register_et_pwd_config);

        getCode = (Button) this.findViewById(R.id.register_bt_identifying_code);

        register = (Button) this.findViewById(register_bt_ok);
        register.setSelected(true);


        if (toActivity.equals("RegisterActivity")) {
            //注册


        } else {
            //忘记密码
            tv_mark.setVisibility(View.GONE);
            register.setText("确定");
            HeadLayout headLayout = (HeadLayout) findViewById(R.id.head_layout);
            headLayout.setHeadTitle("找回密码");

        }


        getCode.setOnClickListener(this);
        register.setOnClickListener(this);

        //手机号的监听事件   更改获取验证码按钮的背景
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = phone.getText().toString().trim();
                if (temp != null && !"".equals(temp) && temp.length() == 11) {
                    getCode.setSelected(true);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_bt_identifying_code:
                //获取验证码
                getCode();
                break;
            case register_bt_ok:
                //进行注册
                if (phoneVerification()) {
                    if (checkCode()) {
                        if (checkPwd()) {
                            if (checkIsOutTime()) {
                                //注册用户
                                registerUser();
                            }
                        }
                    }
                }

                break;
        }
    }

    //判断手机号 是否符合规范
    private boolean phoneVerification() {
        String temp = phone.getText().toString().trim();
        if (temp == null || "".equals(temp)) {
            SnackbarUtil.showShort(this, "请先输入手机号");
            return false;
        }
        Pattern pattern = Pattern.compile("^(1)\\d{10}$");
        Matcher matcher = pattern.matcher(temp);
        if (!matcher.find()) {
            SnackbarUtil.showShort(this, "请检查手机号码是否正确");
            return false;
        }
        return true;
    }


    //判断验证码是否正确
    private boolean checkCode() {
        String code = identifyCode.getText().toString().trim();
        if (code == null || "".equals(code)) {
            SnackbarUtil.showShort(this, "请输入验证码");
            return false;
        } else if (code_back == null) {
            SnackbarUtil.showShort(this, "请获取验证码");
            return false;
        } else {
            if (!code.equals(code_back)) {
                SnackbarUtil.showShort(this, "验证码错误");
                return false;
            }
        }
        return true;
    }


    // 检查密码 是否符合规则
    private boolean checkPwd() {
        String pwd = password.getText().toString().trim();
        String confirmPwd = pwdConfig.getText().toString().trim();

        if (pwd == null || "".equals(pwd)) {
            SnackbarUtil.showShort(this, "请输入密码");
            return false;
        } else {
            // 判断密码是否符合规则
            Pattern pattern1 = Pattern.compile("^\\w{6,15}$");
            Matcher matcher1 = pattern1.matcher(pwd);
            boolean isMatcher1 = matcher1.find();
            if (!isMatcher1) {
                SnackbarUtil.showShort(this, "密码必须6到15位");
                return false;
            } else {
                // 判断确认密码是否为空
                if (confirmPwd == null || "".equals(confirmPwd)) {
                    SnackbarUtil.showShort(this, "请确认密码");
                    return false;
                } else {

                    if (pwd.equals(confirmPwd)) {
                        return true;
                    } else {
                        SnackbarUtil.showShort(this, "两次密码输入不一致,请重新输入");
                        return false;
                    }
                }
            }

        }
    }

    //判断验证码是否过期
    private boolean checkIsOutTime() {
        lastTime = System.currentTimeMillis();
        if (lastTime - firstTime > codeTime) {
            SnackbarUtil.showShort(this, "验证码已过期,请重新获取");
            return false;
        } else if (!phone.getText().toString().trim().equals(phone_back)) {
            SnackbarUtil.showShort(this, "验证码错误");
            return false;
        }
        return true;
    }


    /**
     * 获取验证码 接口
     */
    private void getCode() {

        if (phoneVerification()) {
            timeCount.start();
            RequestParams params = new RequestParams();
            params.addBodyParameter("userMobile", phone.getText().toString().trim());
            new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getSendMessageUrl(), params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {

                    try {
                        JSONObject jsonObject = new JSONObject(responseInfo.result.toString());
                        LogUtil.d("获取验证码接口--------------" + responseInfo.result.toString());
                        String status = jsonObject.getString("status");
                        String promptInfor = jsonObject.getString("promptInfor");


                        if ("1".equals(status)) {
                            SnackbarUtil.showShort(RegisterActivity.this, "验证码已发送，请注意查收");
                            firstTime = System.currentTimeMillis();

                            phone_back = jsonObject.getString("message1");
                            code_back = jsonObject.getString("message2");
                        } else {
                            getCode.setText("重新获取");
                            getCode.setClickable(true);
                            SnackbarUtil.showShort(getApplicationContext(), promptInfor);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                    }

                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    getCode.setText("重新获取");
                    getCode.setClickable(true);
                    NetWorkUtils.showMsg(RegisterActivity.this);
                }
            });
        }
    }


    /**
     * 注册接口      忘记密码接口
     */
    private void registerUser() {
        dialog.builder().setMessage("请稍等~").show();
        RequestParams params = new RequestParams();
        params.addBodyParameter("userMobile", phone_back);
        params.addBodyParameter("password", password.getText().toString().trim());
        String url;
        if (toActivity.equals("RegisterActivity")) {
            //注册
            url = app.getUserRegisterUrl();
        } else {
            //忘记密码
            url = app.getForgetPasswordUrl();
        }
        new HttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog.dismiss();
                LogUtil.d(responseInfo.result.toString());

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseInfo.result.toString());
                    String status = jsonObject.getString("status");
                    String promptInfor = jsonObject.getString("promptInfor");
                    ToastUtil.showShort(RegisterActivity.this, promptInfor);
                    if ("1".equals(status)) {
                        SharedPreferencesUtil.setParam(RegisterActivity.this, "username", phone_back);
                        SharedPreferencesUtil.clearParam(RegisterActivity.this, "pwd");
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("from", "RegisterActivity");
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog.dismiss();
                NetWorkUtils.showMsg(RegisterActivity.this);
            }
        });
    }


    /**
     * 倒计时
     */
    class TimeCount extends CountDownTimer {
        private int leftTime;

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            leftTime = 0;
            getCode.setText("重新获取");
            getCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            getCode.setClickable(false);
            leftTime = (int) (millisUntilFinished / 1000);
            getCode.setText(millisUntilFinished / 1000 + "s");
        }
    }


}
