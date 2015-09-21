package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;

/**
 * Created by Jeremy on 2015/9/15.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        initView();

    }

    private void initView() {
        RelativeLayout head = (RelativeLayout) this.findViewById(R.id.head_layout);

        Button btLogin = (Button) findViewById(R.id.login_btn);
        btLogin.setOnClickListener(this);

        Button btFergotPWD = (Button) findViewById(R.id.login_btn);
        btFergotPWD.setOnClickListener(this);


        TextView tvRegister = (TextView) head.findViewById(R.id.right_text);
        tvRegister.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                break;
            case R.id.login_tv_fergotpwd:
                startActivity(new Intent(LoginActivity.this, FindBcakPWDActivity.class));
                break;
            case R.id.right_text:
                break;
        }
    }
}
