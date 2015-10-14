package com.juttec.goldmetal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.customview.HeadLayout;

/**
 * 关于我们界面
 */
public class AboutActivity extends AppCompatActivity implements View.OnClickListener{


    //头部布局
    private HeadLayout mTopbar;
    //头部布局中的  标题
    private TextView mTitle;

    private TextView tv_title;
    private TextView tv_content;

    private String fromActivity;//判断要展示哪个activit ：免责声明 、 关于我们、

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        fromActivity = getIntent().getStringExtra("fromActivity");

        initView();
    }


    /**
     * 初始化控件
     */
    private void initView() {

        mTopbar = (HeadLayout) findViewById(R.id.head_layout);
        mTitle = (TextView) mTopbar.findViewById(R.id.head_title);
        tv_title= (TextView) mTopbar.findViewById(R.id.tv_title);
        tv_content= (TextView) mTopbar.findViewById(R.id.tv_content);

        mTitle.setText(fromActivity);
        tv_title.setText(fromActivity);

        if("关于我们".equals(fromActivity)){

        }else if("免责声明".equals(fromActivity)){

        }else if("使用说明".equals(fromActivity)){

        }



    }



    @Override
    public void onClick(View v) {
            switch (v.getId()){


            }
    }
}
