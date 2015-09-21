package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.dialog.MyAlertDialog;

/**
 * 用户基本信息界面
 */
public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout head;//头部布局

    private Button bt_nickname, bt_name, bt_phone, bt_qq;

    private TextView tv_nickname, tv_name, tv_phone, tv_qq;

    private Button btn_exit;//退出当前账号
    private TextView tv_change_pwd;//修改密码

    private MyAlertDialog dialog;//对话框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);



        initView();
        dialog = new MyAlertDialog(AccountActivity.this);
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.account_change_nickname:
                showDialog("昵称修改","请输入昵称",tv_nickname);

                break;

            case R.id.account_change_name:
                showDialog("名字修改","请输入姓名",tv_name);

                break;

            case R.id.account_change_phone:

                break;

            case R.id.account_change_qq:
                showDialog("QQ号码修改","请输入QQ号码",tv_qq);
                break;

            case R.id.tv_change_pwd:
                startActivity(new Intent(AccountActivity.this,ChangePWDActivity.class));
                break;
        }
    }


    public  void showDialog(String title,String edittext, final TextView tv){

        dialog.builder()
                .setTitle(title).setEditText(edittext)
                .setSingleButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog.getResult()!=null) {
                            tv.setText(dialog.getResult());
                        }

                        dialog.dismiss();
                    }
                }).show();
    }
}
