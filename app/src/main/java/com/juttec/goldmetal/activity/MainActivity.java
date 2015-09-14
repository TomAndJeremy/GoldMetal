package com.juttec.goldmetal.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.adapter.MyFragmentPagerAdapter;
import com.juttec.goldmetal.customview.HeadLayout;
import com.juttec.goldmetal.fragment.BaseFragment;

public class MainActivity extends AppCompatActivity implements BaseFragment.OnFragmentInteractionListener {
    ViewPager viewPager;
    TabLayout tabLayout;
 HeadLayout headLayout;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        //让tablayout响应，使第一个tab变色
        viewPager.setCurrentItem(1);
        viewPager.setCurrentItem(0);
    }

    /*
    初始化
     */
    private void init() {

        //找到控件
        viewPager = (ViewPager) this.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);
        tabLayout = (TabLayout) this.findViewById(R.id.tabLayout);

        headLayout = (HeadLayout) this.findViewById(R.id.head_layout);

        imageView = (ImageView) headLayout.findViewById(R.id.left_img);//得到自定义布局中的控件

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "11213", Toast.LENGTH_LONG).show();
            }
        });


        //初始化adapter
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), this);
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
