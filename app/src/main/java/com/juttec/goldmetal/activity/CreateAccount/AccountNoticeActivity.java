package com.juttec.goldmetal.activity.CreateAccount;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.utils.ToastUtil;

/**
 * 开户须知界面
 */


public class AccountNoticeActivity extends AppCompatActivity {

    private TextView tv_notice;//开户须知内容
    private CheckBox ck_account;//选择框

    private Button btn_next;//下一步按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_notice);

        Intent intent = getIntent();
        if(intent.getFlags()==Intent.FLAG_ACTIVITY_CLEAR_TOP){
            finish();
        }

            initView();
    }

    //初始化控件
    private void initView(){
        tv_notice = (TextView) findViewById(R.id.tv_notice);
        ck_account = (CheckBox) findViewById(R.id.ck_account);
        btn_next = (Button) findViewById(R.id.btn_next);


        ck_account.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    btn_next.setSelected(true);
                }else{
                    btn_next.setSelected(false);
                }
            }
        });


        //下一步的点击事件
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ck_account.isChecked()) {
                    startActivity(new Intent(AccountNoticeActivity.this, SelectType.class));
                } else {
                    ToastUtil.showShort(AccountNoticeActivity.this, "请仔细阅读开户须知");
                }
            }
        });
    }



}
