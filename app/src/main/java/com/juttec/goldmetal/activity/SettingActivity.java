package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.customview.HeadLayout;

/**
 * 系统设置界面
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout rl_setting_parameter;//指标参数设置
    private SwitchCompat iv_screen_light;//设置屏幕常亮
    private SwitchCompat iv_display_refresh;//是否显示刷新时间
    private ImageView iv_refresh_time_reduce;//刷新行情时间间隔  减号
    private ImageView iv_refresh_time_plus;//刷新行情时间间隔  加号

    //头部布局
    private HeadLayout mTopbar;
    //头部布局中的  返回按钮
    private ImageView iv_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
    }


    /**
     * 初始化控件
     */
    private void initView(){

        mTopbar = (HeadLayout) findViewById(R.id.head_layout);
        iv_back = (ImageView) mTopbar.findViewById(R.id.left_img);
        iv_back.setOnClickListener(this);

        rl_setting_parameter = (RelativeLayout)findViewById(R.id.setting_parameter);
        rl_setting_parameter.setOnClickListener(this);


        iv_screen_light = (SwitchCompat)findViewById(R.id.switch_screen_light);
//        iv_screen_light.setOnClickListener(this);

        iv_display_refresh = (SwitchCompat)findViewById(R.id.switch_display_refresh);
//        iv_display_refresh.setOnClickListener(this);

        iv_refresh_time_reduce = (ImageView)findViewById(R.id.iv_refresh_time_reduce);
        iv_refresh_time_reduce.setOnClickListener(this);

        iv_refresh_time_plus = (ImageView)findViewById(R.id.iv_refresh_time_plus);
        iv_refresh_time_plus.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.iv_screen_light:
//
//                break;
            case R.id.iv_refresh_time_reduce:

                break;
            case R.id.iv_refresh_time_plus:

                break;
//            case R.id.iv_display_refresh:
//
//                break;
            case R.id.setting_parameter:
                //跳转到指标参数设置界面
                Intent intent = new Intent(SettingActivity.this,ParameterSettingActivity.class);
                startActivity(intent);

                break;
            case R.id.left_img:
                //返回按钮
                finish();
                break;
        }
    }
}
