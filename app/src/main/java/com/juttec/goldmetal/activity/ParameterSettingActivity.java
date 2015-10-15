package com.juttec.goldmetal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.customview.HeadLayout;

/**
 * 指标参数设置界面
 */
public class ParameterSettingActivity extends AppCompatActivity implements View.OnClickListener{


    private TextView tv_macd;//MACD指标
    private TextView tv_boll;//BOLL指标
    private TextView tv_kdj;//KDJ指标
    private TextView tv_rsi;//RSI指标
    private TextView tv_sma;//SMA指标
    private TextView tv_ema;//EMA指标
    private TextView tv_env;//ENV指标

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


        tv_macd = (TextView)findViewById(R.id.rl_macd);
        tv_macd.setOnClickListener(this);

        tv_boll = (TextView)findViewById(R.id.rl_boll);
        tv_boll.setOnClickListener(this);

        tv_kdj = (TextView)findViewById(R.id.rl_kdj);
        tv_kdj.setOnClickListener(this);

        tv_rsi = (TextView)findViewById(R.id.rl_rsi);
        tv_rsi.setOnClickListener(this);

        tv_sma = (TextView)findViewById(R.id.rl_sma);
        tv_sma.setOnClickListener(this);

        tv_ema = (TextView)findViewById(R.id.rl_ema);
        tv_ema.setOnClickListener(this);

        tv_env = (TextView)findViewById(R.id.rl_env);
        tv_env.setOnClickListener(this);

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