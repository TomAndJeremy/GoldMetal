package com.juttec.goldmetal.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.customview.HeadLayout;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.SharedPreferencesUtil;
import com.juttec.goldmetal.utils.ToastUtil;

/**
 * 系统设置界面
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int MIN_REFRESH_TIME = 5; //刷新时间  最低限制：5s
    private static final int MAX_REFRESH_TIME = 3*60*60;//最大限制  3小时


    private TextView rl_setting_parameter;//指标参数设置
    private SwitchCompat iv_screen_light;//设置屏幕常亮
    private SwitchCompat iv_display_refresh;//是否显示刷新时间
    private ImageView iv_refresh_time_reduce;//刷新行情时间间隔  减号
    private ImageView iv_refresh_time_plus;//刷新行情时间间隔  加号
    private EditText et_refresh_time;//刷新行情的时间间隔

    //头部布局
    private HeadLayout mTopbar;
    //头部布局中的  返回按钮
    private ImageView iv_back;

    private PowerManager.WakeLock  mWakeLock;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //MyTag可以随便写,可以写应用名称等
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
        //在释放之前，屏幕一直亮着（有可能会变暗,但是还可以看到屏幕内容,换成PowerManager.SCREEN_BRIGHT_WAKE_LOCK不会变暗）

        initView();
    }


    /**
     * 初始化控件
     */
    private void initView(){

        mTopbar = (HeadLayout) findViewById(R.id.head_layout);
        iv_back = (ImageView) mTopbar.findViewById(R.id.left_img);
        iv_back.setOnClickListener(this);

        rl_setting_parameter = (TextView)findViewById(R.id.setting_parameter);
        rl_setting_parameter.setOnClickListener(this);


        iv_screen_light = (SwitchCompat)findViewById(R.id.switch_screen_light);
        iv_screen_light.setChecked((Boolean) SharedPreferencesUtil.getParam(SettingActivity.this, "isScreenLight", true));
        iv_display_refresh = (SwitchCompat)findViewById(R.id.switch_display_refresh);
        iv_display_refresh.setChecked((Boolean) SharedPreferencesUtil.getParam(SettingActivity.this, "isShowRefreshTiem",true));



        et_refresh_time = (EditText) findViewById(R.id.et_refresh_time);
        et_refresh_time.setText((Integer) SharedPreferencesUtil.getParam(this, "refreshTime", MIN_REFRESH_TIME) + "");
        //输入框的监听
        et_refresh_time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                LogUtil.d("et_refresh_time-------"+et_refresh_time.getText().toString());
                if("".equals(et_refresh_time.getText().toString().trim())){
                    et_refresh_time.setText((Integer) SharedPreferencesUtil.getParam(SettingActivity.this,"refreshTime",MIN_REFRESH_TIME)+"");

                }else{
                    int time = Integer.parseInt(et_refresh_time.getText().toString());
                    if(time<MIN_REFRESH_TIME||time>MAX_REFRESH_TIME){
                        et_refresh_time.setText((Integer) SharedPreferencesUtil.getParam(SettingActivity.this,"refreshTime",MIN_REFRESH_TIME)+"");
                    }
                }


            }
        });

        iv_refresh_time_reduce = (ImageView)findViewById(R.id.iv_refresh_time_reduce);
        iv_refresh_time_reduce.setOnClickListener(this);


        iv_refresh_time_plus = (ImageView)findViewById(R.id.iv_refresh_time_plus);
        iv_refresh_time_plus.setOnClickListener(this);


        //是否设置屏幕常亮
        iv_screen_light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SharedPreferencesUtil.setParam(SettingActivity.this, "isScreenLight", true);
                    acquireWakeLock();

                }else{
                    SharedPreferencesUtil.setParam(SettingActivity.this,"isScreenLight",false);
                    releaseWakeLock();
                    //发送广播 让mainactivity同样releaseWakeLock()
                    Intent intent = new Intent();
                    intent.setAction("com.intent.action.releaseWakeLock");
                    SettingActivity.this.sendBroadcast(intent);
                }

                LogUtil.d("SwitchCompat----------------onClick");
            }
        });


        //是否显示刷新时间
        iv_display_refresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SharedPreferencesUtil.setParam(SettingActivity.this,"isShowRefreshTiem",true);
                }else{
                    SharedPreferencesUtil.setParam(SettingActivity.this,"isShowRefreshTiem",false);
                }
            }
        });
    }




    @Override
    protected void onDestroy() {
        //保存 设置的刷新时间间隔
        SharedPreferencesUtil.setParam(this,"refreshTime",Integer.parseInt(et_refresh_time.getText().toString()));

        super.onDestroy();
    }



    @Override
    public void onClick(View v) {
        int time;
        switch (v.getId()){

            case R.id.iv_refresh_time_reduce:
                time = Integer.parseInt(et_refresh_time.getText().toString());
                if(time<=MIN_REFRESH_TIME){
                    ToastUtil.showShort(this,"已到最低限制");
                }else{
                    et_refresh_time.setText(time-1+"");
                }

                break;
            case R.id.iv_refresh_time_plus:
               time = Integer.parseInt(et_refresh_time.getText().toString());
                if(time>=MAX_REFRESH_TIME){
                    ToastUtil.showShort(this,"已到最高限制");
                }else{
                    et_refresh_time.setText(time+1+"");
                }
                break;


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



    private void acquireWakeLock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
            mWakeLock.setReferenceCounted(false);
        }
        mWakeLock.acquire();

    }


    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
}
