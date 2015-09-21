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

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout head;

    Button bt_nickname, bt_name, bt_phone, bt_qq;
    TextView tv_nickname, tv_name, tv_phone, tv_qq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        head = (RelativeLayout) this.findViewById(R.id.head_layout);
        TextView lefttext = (TextView) head.findViewById(R.id.left_text);
        lefttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, ContactUsActivity.class));
            }
        });


        initView();
    }

    private void initView() {
        bt_nickname = (Button) this.findViewById(R.id.account_change_nickname);
        bt_name = (Button) this.findViewById(R.id.account_change_name);
        bt_phone = (Button) this.findViewById(R.id.account_change_phone);
        bt_qq = (Button) this.findViewById(R.id.account_change_qq);


        tv_nickname = (TextView) this.findViewById(R.id.account_tv_nickname);
        tv_name = (TextView) this.findViewById(R.id.account_tv_name);
        tv_phone = (TextView) this.findViewById(R.id.account_tv_phone);
        tv_qq = (TextView) this.findViewById(R.id.account_tv_qq);

        bt_nickname.setOnClickListener(this);
        bt_name.setOnClickListener(this);
        bt_phone.setOnClickListener(this);
        bt_qq.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.account_change_nickname:


                break;
            case R.id.account_change_name:
                break;
            case R.id.account_change_phone:
                break;
            case R.id.account_change_qq:
                break;
        }
    }
    public  void showDialog(String title,String edittext){
        final MyAlertDialog dialog = new MyAlertDialog(AccountActivity.this);
        dialog.builder()
                .setTitle("昵称修改").setEditText("请输入昵称")
                .setSingleButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
