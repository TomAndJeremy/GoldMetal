package com.juttec.goldmetal.activity.CreateAccount;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.ToastUtil;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.dialog.MyProgressDialog;
import com.juttec.goldmetal.utils.NetWorkUtils;
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
 * 开户基本信息界面
 */
public class InputYourMessage extends AppCompatActivity {

    private TextView tv_type;//开户类别
    private EditText et_name,et_phone;//姓名和 手机号

    private RadioGroup mRadioGroup;
    private RadioButton radio1,radio2,radio3;

    private EditText et_remark;//备注信息

    private Button btn_next;//下一步 按钮

    private String  money;//投资资金
    private MyProgressDialog dialog_progress;//正在加载 进度框
    private MyApplication app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputmessage);
        app = (MyApplication) getApplication();
        dialog_progress = new MyProgressDialog(this);


        initView();



        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == radio1.getId()){
                    money = radio1.getText().toString();
                }else  if(checkedId == radio2.getId()){
                    money = radio2.getText().toString();
                } if(checkedId == radio3.getId()){
                    money = radio3.getText().toString();
                }
            }
        });



        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInformation()){
                    //提交开户信息
                    submitInfo(); }
            }
        });
    }


    private boolean checkInformation(){
        String nameContent = et_name.getText().toString();
        if(TextUtils.isEmpty(nameContent) || "".equals(nameContent) || nameContent.trim().length() <= 0){
            ToastUtil.showShort(this,"请填写姓名");
            return false;
        }else{
            String phoneContent = et_phone.getText().toString();
            if(TextUtils.isEmpty(phoneContent) || "".equals(phoneContent) || phoneContent.trim().length() <= 0){
                ToastUtil.showShort(this,"请填写手机号");
                return false;
            }else{
                if(checkMobile(phoneContent)){
                    if(money==null){
                        ToastUtil.showShort(this,"请选择投资资金");
                        return false;
                    }else{
                        return true;
                    }
                }
                return false;
            }
        }
    }


    //检查手机号 是否正确
    private boolean checkMobile(String phoneNum){
        if(!TextUtils.isEmpty(phoneNum)){
            Pattern pattern = Pattern.compile("^(1)\\d{10}$");
            Matcher matcher = pattern.matcher(phoneNum);
            boolean isMatcher = matcher.find();
            if (!isMatcher) {
                Toast.makeText(InputYourMessage.this, "手机号有错误", Toast.LENGTH_SHORT)
                        .show();
                return false;
            }

        }else{
            Toast.makeText(InputYourMessage.this, "请填写手机号", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }




    //初始化控件
    private void initView(){
        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_type.setText(getIntent().getStringExtra("type").toString());

        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);

        mRadioGroup = (RadioGroup) findViewById(R.id.id_radiogroup);
        radio1 = (RadioButton) findViewById(R.id.radio1);
        radio2 = (RadioButton) findViewById(R.id.radio2);
        radio3 = (RadioButton) findViewById(R.id.radio3);

        et_remark = (EditText) findViewById(R.id.et_remark);

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setSelected(true);
    }
    //提交开户信息接口
    private void submitInfo(){
        dialog_progress.builder().setMessage("正在提交信息~").show();
        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", app.getUserInfoBean().getUserId());
        params.addBodyParameter("openAccountType",tv_type.getText().toString());
        params.addBodyParameter("openAccountName",et_name.getText().toString());
        params.addBodyParameter("openAccountPhone",et_phone.getText().toString());
        params.addBodyParameter("investmentFund",money);
        params.addBodyParameter("remarks",et_remark.getText().toString());
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.getSubmitOpenAccountInfor(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog_progress.dismiss();
                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    LogUtil.e("111111  " + responseInfo.result.toString());
                    String status = object.getString("status");
                    String promptInfor = object.getString("promptInfor");
                    if ("1".equals(status)) {
                        startActivity(new Intent(InputYourMessage.this,AccountFinishActivity.class));
                    } else {
                    }

                    ToastUtil.showShort(InputYourMessage.this, promptInfor);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog_progress.dismiss();
                NetWorkUtils.showMsg(InputYourMessage.this);
            }
        });
    }


}
