package com.juttec.goldmetal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.customview.HeadLayout;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener{


    //头部布局
    private HeadLayout mTopbar;
    //头部布局中的  返回按钮
    private ImageView iv_back;

    private Button btn_send;//发送按钮
    private EditText edit_feedback;//输入反馈意见


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initView();
    }



    /**
     * 初始化控件
     */
    private void initView() {

        mTopbar = (HeadLayout) findViewById(R.id.head_layout);
        iv_back = (ImageView) mTopbar.findViewById(R.id.left_img);
        iv_back.setOnClickListener(this);

        edit_feedback = (EditText) findViewById(R.id.et_feedback);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);


        //edittext监听内容变化   更改发送按钮的背景
        edit_feedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = edit_feedback.getText().toString();
                if (TextUtils.isEmpty(content) || "".equals(content) || content.trim().length() <= 0) {
                    btn_send.setSelected(false);
                } else {
                    btn_send.setSelected(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }



        @Override
    public void onClick(View v) {
            switch (v.getId()){

                case R.id.btn_send:
                    //发表按钮

                    break;
                case R.id.left_img:
                    //返回按钮
                    finish();
                    break;
            }
    }
}
