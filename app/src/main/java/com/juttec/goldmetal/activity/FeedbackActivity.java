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
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.customview.HeadLayout;
import com.juttec.goldmetal.dialog.MyProgressDialog;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener{


    //头部布局
    private HeadLayout mTopbar;
    //头部布局中的  返回按钮
    private ImageView iv_back;

    private Button btn_send;//发送按钮
    private EditText edit_feedback;//输入反馈意见


    private MyProgressDialog dialog_progress;//正在加载 进度框
    private MyApplication app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        app = (MyApplication) getApplication();
        dialog_progress = new MyProgressDialog(this);

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
                    String content = edit_feedback.getText().toString();
                    if (TextUtils.isEmpty(content) || "".equals(content) || content.trim().length() <= 0) {
                        ToastUtil.showShort(FeedbackActivity.this,"反馈内容不能为空！");
                    }else{
                        submitAdvice(content);
                    }
                    break;
                case R.id.left_img:
                    //返回按钮
                    finish();
                    break;
            }
    }



    //提交反馈意见
    private void submitAdvice(String content){
        dialog_progress.builder().setMessage("正在提交信息~").show();
        RequestParams params = new RequestParams();
//        params.addBodyParameter("userId", app.getUserInfoBean().getUserId());
        params.addBodyParameter("feedbackInfor",content);

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.getSubmitAdvice(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog_progress.dismiss();
                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    String status = object.getString("status");
                    String promptInfor = object.getString("promptInfor");
                    if ("1".equals(status)) {
                        finish();
                    } else {
                    }

                    ToastUtil.showShort(FeedbackActivity.this, promptInfor);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog_progress.dismiss();
                NetWorkUtils.showMsg(FeedbackActivity.this);
            }
        });

    }


}
