package com.juttec.goldmetal.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.utils.SnackbarUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.juttec.goldmetal.R.id.back;
import static com.juttec.goldmetal.R.id.register_bt_ok;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText phone;
    EditText identifyCode;
    EditText password;
    EditText pwdConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        phone = (EditText) this.findViewById(R.id.register_et_phone);
        identifyCode = (EditText) this.findViewById(R.id.register_et_identifying_code);
        password = (EditText) this.findViewById(R.id.register_et_pwd);
        pwdConfig = (EditText) this.findViewById(R.id.register_et_pwd_config);

        Button getCode = (Button) this.findViewById(R.id.register_bt_identifying_code);
        Button register = (Button) this.findViewById(register_bt_ok);


        getCode.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_bt_identifying_code:
                break;
            case register_bt_ok:
                break;
        }
    }

    private boolean phoneVerification() {
        String temp = phone.getText().toString().trim();
        if (temp==null){
            SnackbarUtil.showShort(this,"请先输入手机号");
            return false;
        }
        Pattern pattern = Pattern.compile("^(1)\\d{10}$");
        Matcher matcher = pattern.matcher(temp);
       if(!matcher.find()){
           SnackbarUtil.showShort(this,"请检查手机号码是否正确");
           return false;
       }
        return true;
    }

}
