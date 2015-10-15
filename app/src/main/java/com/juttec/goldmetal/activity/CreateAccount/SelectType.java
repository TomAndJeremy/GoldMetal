package com.juttec.goldmetal.activity.CreateAccount;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.utils.ToastUtil;

/**
 * 选择开户品种界面
 */

public class SelectType extends AppCompatActivity {

    private Button btn_next;//下一步按钮

    private RadioGroup mRadioGroup;

    private RadioButton radio1,radio2,radio3,radio4;


    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecttype);

        initView();
    }

    //初始化控件
    private void initView(){
        btn_next = (Button) findViewById(R.id.btn_next);

        mRadioGroup = (RadioGroup) findViewById(R.id.id_radiogroup);
        radio1 = (RadioButton) findViewById(R.id.radio1);
        radio2 = (RadioButton) findViewById(R.id.radio2);
        radio3 = (RadioButton) findViewById(R.id.radio3);
        radio4 = (RadioButton) findViewById(R.id.radio4);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                btn_next.setSelected(true);

               if(checkedId == radio1.getId()){
                   type = radio1.getText().toString();

               }else if(checkedId == radio2.getId()){
                   type = radio2.getText().toString();

               }else if(checkedId == radio3.getId()){
                   type = radio3.getText().toString();

               }else if(checkedId == radio4.getId()){
                   type = radio4.getText().toString();

               }

            }
        });


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radio1.isChecked()||radio2.isChecked()||radio3.isChecked()||radio4.isChecked()){
                    Intent intent = new Intent(SelectType.this,InputYourMessage.class);
                    intent.putExtra("type",type);
                    startActivity(intent);
                }else{
                    ToastUtil.showShort(SelectType.this,"请选择开户品种");
                }
            }
        });
    }
}
