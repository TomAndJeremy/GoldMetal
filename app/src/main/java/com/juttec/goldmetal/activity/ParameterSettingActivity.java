package com.juttec.goldmetal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.customview.HeadLayout;

/**
 * 指标参数设置界面
 */
public class ParameterSettingActivity extends AppCompatActivity implements View.OnClickListener{


    private RelativeLayout rl_macd;//MACD指标
    private RelativeLayout rl_boll;//BOLL指标
    private RelativeLayout rl_kdj;//KDJ指标
    private RelativeLayout rl_rsi;//RSI指标
    private RelativeLayout rl_sma;//SMA指标
    private RelativeLayout rl_ema;//EMA指标
    private RelativeLayout rl_env;//ENV指标

    //头部布局
    private HeadLayout mTopbar;
    //头部布局中的  返回按钮
    private ImageView iv_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametersetting);

        initView();
    }


    /**
     * 初始化控件
     */
    private void initView(){

        mTopbar = (HeadLayout) findViewById(R.id.head_layout);
        iv_back = (ImageView) mTopbar.findViewById(R.id.left_img);
        iv_back.setOnClickListener(this);


        rl_macd = (RelativeLayout)findViewById(R.id.rl_macd);
        rl_macd.setOnClickListener(this);

        rl_boll = (RelativeLayout)findViewById(R.id.rl_boll);
        rl_boll.setOnClickListener(this);

        rl_kdj = (RelativeLayout)findViewById(R.id.rl_kdj);
        rl_kdj.setOnClickListener(this);

        rl_rsi = (RelativeLayout)findViewById(R.id.rl_rsi);
        rl_rsi.setOnClickListener(this);

        rl_sma = (RelativeLayout)findViewById(R.id.rl_sma);
        rl_sma.setOnClickListener(this);

        rl_ema = (RelativeLayout)findViewById(R.id.rl_ema);
        rl_ema.setOnClickListener(this);

        rl_env = (RelativeLayout)findViewById(R.id.rl_env);
        rl_env.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_macd:

                break;
            case R.id.rl_boll:

                break;
            case R.id.rl_kdj:

                break;
            case R.id.rl_rsi:

                break;
            case R.id.rl_sma:

                break;
            case R.id.rl_ema:

                break;
            case R.id.rl_env:

                break;
            case R.id.left_img:
                //返回按钮
                finish();
                break;
        }
    }
}