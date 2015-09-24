package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.UserInfoBean;
import com.juttec.goldmetal.dialog.MyAlertDialog;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

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

    private TextView tv_goldId;//掌金ID

    private Button btn_exit;//退出当前账号
    private TextView tv_change_pwd;//修改密码

    private MyAlertDialog dialog;//对话框

    private String result;//修改后的结果

    private MyApplication app;
    private UserInfoBean userInfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        app = (MyApplication) getApplication();
        userInfoBean = app.getUserInfoBean();


        dialog = new MyAlertDialog(AccountActivity.this);
        initView();

        initData();

    }

    //初始化控件
    private void initView() {

        head = (RelativeLayout) this.findViewById(R.id.head_layout);

        TextView lefttext = (TextView) head.findViewById(R.id.left_text);
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

                break;

            case R.id.account_change_qq:
                showDialog("QQ号码修改","请输入QQ号码",tv_qq,QQ);
                break;

            case R.id.tv_change_pwd:
                startActivity(new Intent(AccountActivity.this,ChangePWDActivity.class));
                break;
        }
    }


    public  void showDialog(String title,String edittext, final TextView tv, final int type){

        dialog.builder()
                .setTitle(title).setEditText(edittext)
                .setSingleButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        result = dialog.getResult();
                        if (result == null || TextUtils.isEmpty(result)) {
                            ToastUtil.showShort(AccountActivity.this,"修改的内容不能为空");
                        } else {
                           // tv.setText(dialog.getResult());

                            editUserInfo(type,tv,result);
                        }

                        dialog.dismiss();
                    }
                }).show();
    }



    private void editUserInfo(final int type,final TextView tv,final String result){

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

            }
        });

    }



}
