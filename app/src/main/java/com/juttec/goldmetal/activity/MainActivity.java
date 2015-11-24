package com.juttec.goldmetal.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.adapter.MyFragmentPagerAdapter;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.fragment.BaseFragment;
import com.juttec.goldmetal.utils.GetNetworkData;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.SharedPreferencesUtil;
import com.juttec.goldmetal.utils.ToastUtil;

public class MainActivity extends AppCompatActivity implements BaseFragment.OnFragmentInteractionListener {
    ViewPager viewPager;
    TabLayout tabLayout;
    //初始化adapter
    MyFragmentPagerAdapter myFragmentPagerAdapter;

    private PowerManager.WakeLock mWakeLock;//设置屏幕常亮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //MyTag可以随便写,可以写应用名称等
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");

        //在释放之前，屏幕一直亮着（有可能会变暗,但是还可以看到屏幕内容,换成PowerManager.SCREEN_BRIGHT_WAKE_LOCK不会变暗）


        //退出当前账号
        if (getIntent().getFlags() == Intent.FLAG_ACTIVITY_CLEAR_TOP && getIntent().getStringExtra("from").equals("AccountActivity")) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        init();


    }


    @Override
    protected void onResume() {
        if ((Boolean) SharedPreferencesUtil.getParam(MainActivity.this, "isScreenLight", true)) {
            acquireWakeLock();
            LogUtil.d("----------mWakeLock.acquire()");
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        LogUtil.d("----------mWakeLock.release()");
        releaseWakeLock();
        super.onDestroy();
    }

    /*
                初始化
                 */
    private void init() {

        //找到控件
        viewPager = (ViewPager) this.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);
        tabLayout = (TabLayout) this.findViewById(R.id.tabLayout);


        //初始化adapter
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(myFragmentPagerAdapter);

        //tablayout与viewpager关联
        tabLayout.setupWithViewPager(viewPager);
        // 自定义tablayout布局
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);

            if (tab != null) {
                tab.setCustomView(myFragmentPagerAdapter.getTabView(i));
            }
        }


        tabLayout.getTabAt(0).select();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private void acquireWakeLock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
        }
        mWakeLock.setReferenceCounted(false);
        mWakeLock.acquire();

    }


    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }


    //如果两次按键时间间隔大于2秒，则不退出
    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    ToastUtil.showShort(this, "再按一次退出程序");
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {   //两次按键小于2秒时，退出应用
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

}
