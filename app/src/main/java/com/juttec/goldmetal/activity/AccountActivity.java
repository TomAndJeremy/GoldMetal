package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.UserInfoBean;
import com.juttec.goldmetal.dialog.MyAlertDialog;
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

/**
 * 用户基本信息界面
 */
public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private static int NICKNAME = 1;
    private int NAME = 2;
    private int MOBILE = 3;
    private int QQ = 4;



    private RelativeLayout head;//头部布局

    private Button bt_nickname, bt_name, bt_phone, bt_qq;

    private TextView tv_nickname, tv_name, tv_phone, tv_qq;

    private int countDown = 60 * 1000;//倒计时的时间  默认为60秒
    private TimeCount timeCount;//用于倒计时
    private EditText et_phone;//手机号
    private EditText et_code ;//验证码
    private Button btn_code ;//获取验证码按钮

    private String phone_back;//返回的手机号
    private String code_back;//返回的验证码



    private TextView tv_goldId;//掌金ID

    private Button btn_exit;//退出当前账号
    private TextView tv_change_pwd;//修改密码

    private MyAlertDialog dialog,phoneDialog;//对话框

    private String result;//修改后的结果

    private MyApplication app;
    private UserInfoBean userInfoBean;//用户信息实体类

    private MyProgressDialog dialog_progress;//正在加载 进度框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        app = (MyApplication) getApplication();
        userInfoBean = app.getUserInfoBean();


        dialog = new MyAlertDialog(AccountActivity.this);

        dialog_progress = new MyProgressDialog(this);

        initView();

        initData();

    }

    //初始化控件
    private void initView() {

        head = (RelativeLayout) this.findViewById(R.id.head_layout);

        TextView lefttext = (TextView) head.findViewById(R.id.left_text);
        //联系我们
        lefttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, ContactUsActivity.class));
            }
        });

        bt_nickname = (Button) this.findViewById(R.id.account_change_nickname);
        bt_name = (Button) this.findViewById(R.id.account_change_name);
        bt_phone = (Button) this.findViewById(R.id.account_change_phone);
        bt_qq = (Button) this.findViewById(R.id.account_change_qq);

        tv_change_pwd = (TextView) findViewById(R.id.tv_change_pwd);
        tv_goldId = (TextView) findViewById(R.id.tv_ID);

        btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_exit.setSelected(true);
        btn_exit.setOnClickListener(this);


        tv_nickname = (TextView) this.findViewById(R.id.account_tv_nickname);
        tv_name = (TextView) this.findViewById(R.id.account_tv_name);
        tv_phone = (TextView) this.findViewById(R.id.account_tv_phone);
        tv_qq = (TextView) this.findViewById(R.id.account_tv_qq);



        bt_nickname.setOnClickListener(this);
        bt_name.setOnClickListener(this);
        bt_phone.setOnClickListener(this);
        bt_qq.setOnClickListener(this);
        tv_change_pwd.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
    }


    //初始化数据
    private void initData(){
        tv_goldId.setText(userInfoBean.getGoldMetalId());
        tv_nickname.setText(userInfoBean.getUserNickName());
        tv_name.setText(userInfoBean.getUserName());
        tv_phone.setText(userInfoBean.getMobile());
        tv_qq.setText(userInfoBean.getUserQQ());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.account_change_nickname:
                showDialog("昵称修改","请输入昵称",tv_nickname,NICKNAME);

                break;

            case R.id.account_change_name:
                showDialog("名字修改","请输入姓名",tv_name,NAME);

                break;

            case R.id.account_change_phone:
                phoneDialog = new MyAlertDialog(AccountActivity.this);
                timeCount = new TimeCount(countDown, 1000);

                View view  = LayoutInflater.from(this).inflate(R.layout.view_phone,null);
                 et_phone = (EditText) view.findViewById(R.id.et_phone);
                 et_code = (EditText) view.findViewById(R.id.et_code);
                 btn_code = (Button) view.findViewById(R.id.btn_code);

                String phoneContent = et_phone.getText().toString();
                String codeContent = et_code.getText().toString();

                //手机号的监听事件   更改获取验证码按钮的背景
                et_phone.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String temp = et_phone.getText().toString().trim();
                        if (temp != null && !"".equals(temp) && temp.length() == 11) {
                            btn_code.setSelected(true);
                        }
                    }
                });


                //获取验证码按钮 的 点击事件
                btn_code.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(phoneVerification(et_phone.getText().toString().trim())){
                            //
                            getCode();
                        }

                    }
                });


                phoneDialog.builder().setTitle("手机号修改")
                        .setView(view).setCancelableOnTouchOutside(false)
                        .setSingleButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!phoneVerification(et_phone.getText().toString().trim())){
                                    //
                                    return;
                                }
                                if (checkCode(et_code.getText().toString().trim())) {
                                    if (!et_phone.getText().toString().trim().equals(phone_back)) {
                                        ToastUtil.showShort(AccountActivity.this, "验证码错误");
                                        return;
                                    } else {
                                        phoneDialog.dismiss();
                                        timeCount.cancel();
                                        //修改手机号
                                        editUserInfo(QQ, tv_qq, et_phone.getText().toString().trim());

                                    }
                                } else {
                                    return;
                                }

                            }
                        }).show();

                break;

            case R.id.account_change_qq:
                showDialog("QQ号码修改","请输入QQ号码",tv_qq,QQ);
                break;

            case R.id.tv_change_pwd:
                //修改密码
                startActivity(new Intent(AccountActivity.this, ChangePWDActivity.class));
                break;

            case R.id.btn_exit:
                //退出当前账号
                SharedPreferencesUtil.clearParam(AccountActivity.this, "pwd");
                //先跳转到MainActivity 接收后判断 在跳转到LoginActivity
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","AccountActivity");
                startActivity(intent);
                break;
        }
    }



    //监听返回键   取消倒计时
    @Override
    public void onBackPressed() {
        LogUtil.d("-------------------------------");
        super.onBackPressed();
        if(phoneDialog!=null&&phoneDialog.isShowing()){
            timeCount.cancel();
            LogUtil.d("KEYCODE_BACK--------------");
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
//            LogUtil.d("-------------------------------");
//            if(phoneDialog!=null&&phoneDialog.isShowing()){
//                timeCount.onTick(0);
//                timeCount.cancel();
//                timeCount.onFinish();
//                LogUtil.d("KEYCODE_BACK--------------");
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }



    //判断手机号 是否符合规范
    private boolean phoneVerification(String temp) {
        if (temp == null || "".equals(temp)) {
            ToastUtil.showShort(AccountActivity.this, "请先输入手机号");
            return false;
        }
        Pattern pattern = Pattern.compile("^(1)\\d{10}$");
        Matcher matcher = pattern.matcher(temp);
        if (!matcher.find()) {
            ToastUtil.showShort(AccountActivity.this, "手机号码有误");
            return false;
        }
        return true;
    }


    //判断验证码是否正确
    private boolean  checkCode( String code){
        if (code == null || "".equals(code)) {
            ToastUtil.showShort(this, "请输入验证码");
            return false;
        }else if(code_back==null) {
            ToastUtil.showShort(this, "请获取验证码");
            return false;
        }else{
            if(!code.equals(code_back)){
                ToastUtil.showShort(this, "验证码错误");
                return false;
            }
        }
        return true;
    }



    /**
     * 获取验证码 接口
     */
    private void getCode(){
        if (phoneVerification(et_phone.getText().toString().trim())) {
            timeCount.start();
            RequestParams params = new RequestParams();
            params.addBodyParameter("userMobile", et_phone.getText().toString().trim());
            new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getSendMessageUrl(), params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {

                    try {
                        JSONObject jsonObject = new JSONObject(responseInfo.result.toString());
                        LogUtil.d("获取验证码接口--------------" + responseInfo.result.toString());
                        String status = jsonObject.getString("status");
                        String promptInfor = jsonObject.getString("promptInfor");


                        if ("1".equals(status)) {
                            ToastUtil.showShort(AccountActivity.this, "验证码已发送，请注意查收");

                            phone_back = jsonObject.getString("message1");
                            code_back = jsonObject.getString("message2");
                        } else {
                            btn_code.setText("重新获取");
                            btn_code.setClickable(true);
                            SnackbarUtil.showShort(getApplicationContext(), promptInfor);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                    }

                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    btn_code.setText("重新获取");
                    btn_code.setClickable(true);
                    NetWorkUtils.showMsg(AccountActivity.this);
                }
            });
        }
    }



    /**
     * 倒计时
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发

            btn_code.setText("重新获取");
            btn_code.setSelected(true);
            btn_code.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            btn_code.setClickable(false);
            btn_code.setText(millisUntilFinished / 1000 + "s");
        }
    }




    public  void showDialog(String title,String edittext, final TextView tv, final int type){

        dialog.builder()
                .setTitle(title).setEditText(edittext)
                .setSingleButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (type==QQ) {
                            //设置EditText只能输数字
                            dialog.setEditType();
                        }
                        result = dialog.getResult();
                        if (result == null || TextUtils.isEmpty(result)) {
                            ToastUtil.showShort(AccountActivity.this, "修改的内容不能为空");
                        } else {
                            // tv.setText(dialog.getResult());

                            editUserInfo(type, tv, result);
                        }

                        dialog.dismiss();
                    }
                }).show();
    }


    /**
     * 修改用户信息接口
     * @param type
     * @param tv
     * @param result
     */
    private void editUserInfo(final int type,final TextView tv,final String result){
        dialog_progress.builder().setMessage("请稍等~").show();

       // userNickName  userName userMobile  userQQ
        RequestParams params = new RequestParams();
        params.addBodyParameter("userId",userInfoBean.getUserId());

        if(type==NICKNAME){
            params.addBodyParameter("userNickName",result);
        }else if(type==NAME){
            params.addBodyParameter("userName",result);
        }else if(type==QQ){
            params.addBodyParameter("userQQ",result);
        }

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.getEditUserInforUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog_progress.dismiss();

                JSONObject object = null;
                        try {
                            object = new JSONObject(responseInfo.result.toString());
                            String status = object.getString("status");
                            String promptInfor = object.getString("promptInfor");
                            if("1".equals(status)){

                                tv.setText(result);

                                if(type==NICKNAME){
                                    userInfoBean.setUserNickName(result);
                                }else if(type==NAME){
                                    userInfoBean.setUserName(result);
                                }else if(type==QQ){
                                    userInfoBean.setUserQQ(result);
                                }
                            }else{

                            }

                            ToastUtil.showShort(AccountActivity.this,promptInfor);

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog_progress.dismiss();
                NetWorkUtils.showMsg(AccountActivity.this);
            }
        });

    }



}
