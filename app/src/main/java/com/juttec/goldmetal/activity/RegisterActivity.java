package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
   private  EditText phone;
   private  EditText identifyCode;
    private EditText password;
   private  EditText pwdConfig;

   private  Button getCode;

    private TimeCount timeCount;

    private MyApplication app;

    private String phone_back;
    private String code_back;

    private MyProgressDialog dialog;//正在加载的  进度框


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        app = (MyApplication) getApplication();
        dialog = new MyProgressDialog(this);
        initView();
    }

    private void initView() {

        timeCount = new TimeCount(60 * 1000, 1000);

        phone = (EditText) this.findViewById(R.id.register_et_phone);
        identifyCode = (EditText) this.findViewById(R.id.register_et_identifying_code);
        password = (EditText) this.findViewById(R.id.register_et_pwd);
        pwdConfig = (EditText) this.findViewById(R.id.register_et_pwd_config);

        getCode = (Button) this.findViewById(R.id.register_bt_identifying_code);

        Button register = (Button) this.findViewById(register_bt_ok);


        getCode.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.register_bt_identifying_code:
                if (phoneVerification()) {
                    timeCount.start();
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("userMobile", phone.getText().toString().trim());

                    new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getSendMessageUrl(), new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {

                            try {
                                JSONObject jsonObject = new JSONObject(responseInfo.result.toString());
                                String status = jsonObject.getString("status");
                                String promptInfor = jsonObject.getString("promptInfor");


                                if ("1".equals(status)) {
                                    SnackbarUtil.showShort(getApplicationContext(), "验证码已发送，请注意查收");
                                    phone_back = jsonObject.getString("message1");
                                    code_back = jsonObject.getString("message2");
                                }else{
                                    SnackbarUtil.showShort(getApplicationContext(), promptInfor);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }finally {
                            }

                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            NetWorkUtils.showMsg(RegisterActivity.this);
                        }
                    });
                }
                break;
            case register_bt_ok:
                if (infoVerification()) {
                    dialog.builder().setMessage("正在注册~").show();
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("userMobile", phone.getText().toString().trim());
                    params.addBodyParameter("password", password.getText().toString().trim());
                    new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getUserRegisterUrl(), params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            dialog.dismiss();
                            LogUtil.d(responseInfo.result.toString());

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(responseInfo.result.toString());
                                String status = jsonObject.getString("status");
                                String promptInfor = jsonObject.getString("promptInfor");
                               // SnackbarUtil.showShort(getApplicationContext(), promptInfor);

                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                                intent.putExtra("phone", phone.getText().toString().trim());
                                SharedPreferencesUtil.setParam(RegisterActivity.this, "username", phone.getText().toString().trim());
                                if ("1".equals(status)) {
                                    ToastUtil.showShort(RegisterActivity.this,"注册成功");
                                    startActivity(intent);
                                    finish();
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


                break;
        }
    }

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

    private boolean infoVerification() {
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
        if (matcher == null) {

        }
        return true;
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
