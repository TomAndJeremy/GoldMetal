package com.juttec.goldmetal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.customview.HeadLayout;

/**
 * 关于我们界面
 */
public class AboutActivity extends AppCompatActivity implements View.OnClickListener{


    //头部布局
    private HeadLayout mTopbar;
    //头部布局中的  返回按钮
    private ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initView();
    }


    /**
     * 初始化控件
     */
    private void initView() {

        mTopbar = (HeadLayout) findViewById(R.id.head_layout);
        iv_back = (ImageView) mTopbar.findViewById(R.id.left_img);
        iv_back.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.left_img:
                    //返回按钮
                    finish();
                    break;
            }
    }
}
