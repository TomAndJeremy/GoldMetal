package com.juttec.goldmetal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
 * 修改密码 界面
 */
public class ChangePWDActivity extends AppCompatActivity {

    private EditText et_oldPwd,et_newPwd,et_confirmPwd;//旧密码  新密码 确认密码
    private Button btn_ok;//确定按钮


    private MyApplication app;
    private UserInfoBean userInfoBean;//用户信息实体类

    private MyProgressDialog dialog_progress;//正在加载 进度框


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);

        app = (MyApplication) getApplication();
        userInfoBean = app.getUserInfoBean();
        dialog_progress = new MyProgressDialog(this);

        initView();
    }

    private void initView(){
        et_oldPwd = (EditText) findViewById(R.id.et_oldpwd);
        et_newPwd = (EditText) findViewById(R.id.et_newpwd);
        et_confirmPwd = (EditText) findViewById(R.id.et_confirmpwd);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setSelected(true);


        //确定按钮的 点击事件
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPwd()) {
                    //调修改密码的接口
                    changePwd();
                }
            }
        });
    }


    //检查密码
    private boolean checkPwd(){

        String oldPwd = et_oldPwd.getText().toString();
        if (TextUtils.isEmpty(oldPwd) || "".equals(oldPwd) || oldPwd.trim().length() <= 0) {
            ToastUtil.showShort(ChangePWDActivity.this,"请输入旧密码");
            return false;
        }else {
            String newPwd = et_newPwd.getText().toString();
            if (TextUtils.isEmpty(newPwd) || "".equals(newPwd) || newPwd.trim().length() <= 0) {
                ToastUtil.showShort(ChangePWDActivity.this,"请输入新密码");
                return false;
            }else{
                Pattern pattern = Pattern.compile("^\\w{6,15}$");
                Matcher matcher = pattern.matcher(newPwd);
                boolean isMatcher = matcher.find();
                if (!isMatcher) {
                    ToastUtil.showShort(ChangePWDActivity.this, "密码必须6到15位");
                    return false;
                }else{
                    String confirmPwd = et_confirmPwd.getText().toString();
                    if (TextUtils.isEmpty(confirmPwd) || "".equals(confirmPwd) || confirmPwd.trim().length() <= 0) {
                        ToastUtil.showShort(ChangePWDActivity.this,"请输入确认密码");
                        return false;
                    }else if(!confirmPwd.equals(newPwd)){
                        ToastUtil.showShort(ChangePWDActivity.this,"两次输入的密码不一致");
                        return false;
                    }
                }
            }

        }
        return true;
    }



    //调接口 修改密码
    private  void changePwd(){
        dialog_progress.builder().setMessage("正在修改密码~").show();
        RequestParams params = new RequestParams();
        params.addBodyParameter("userId",userInfoBean.getUserId());
        params.addBodyParameter("oldPassword",et_oldPwd.getText().toString());
        params.addBodyParameter("newPassword",et_newPwd.getText().toString());
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.getEditPassword(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog_progress.dismiss();

                JSONObject object = null;
                try {
                    LogUtil.d("changePwd---------------"+responseInfo.result.toString());
                    object = new JSONObject(responseInfo.result.toString());
                    String status = object.getString("status");
                    String promptInfor = object.getString("promptInfor");
                    if("1".equals(status)){
                        //将修改后的密码 存起来
                        SharedPreferencesUtil.setParam(ChangePWDActivity.this, "pwd", et_newPwd.getText().toString());
                        finish();
                    }
                    ToastUtil.showShort(ChangePWDActivity.this,promptInfor);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog_progress.dismiss();
                NetWorkUtils.showMsg(ChangePWDActivity.this);
            }
        });


    }

}
