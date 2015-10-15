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

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.utils.ToastUtil;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputmessage);


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

                    startActivity(new Intent(InputYourMessage.this,AccountFinishActivity.class));
                }
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
                if(money==null){
                    ToastUtil.showShort(this,"请选择投资资金");
                    return false;
                }else{
                    return true;
                }
            }
        }
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
}
