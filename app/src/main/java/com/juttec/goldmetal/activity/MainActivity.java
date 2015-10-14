package com.juttec.goldmetal.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.adapter.MyFragmentPagerAdapter;
import com.juttec.goldmetal.fragment.BaseFragment;
import com.juttec.goldmetal.fragment.MomentFragment;

public class MainActivity extends AppCompatActivity implements BaseFragment.OnFragmentInteractionListener {
    ViewPager viewPager;
    TabLayout tabLayout;
    //初始化adapter
    MyFragmentPagerAdapter myFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        viewPager.setCurrentItem(2);


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (viewPager == null) {
           MomentFragment momentFragment= (MomentFragment) myFragmentPagerAdapter.getItem(2);
            if (momentFragment != null) {
                if (momentFragment.myAdapter != null) {
                    momentFragment.myAdapter.notifyDataSetChanged();
                }
            }

        }
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
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
