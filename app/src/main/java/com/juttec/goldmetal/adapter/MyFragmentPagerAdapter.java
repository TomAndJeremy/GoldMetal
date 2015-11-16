package com.juttec.goldmetal.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.fragment.MarketFragment;
import com.juttec.goldmetal.fragment.MomentFragment;
import com.juttec.goldmetal.fragment.MoreFragment;
import com.juttec.goldmetal.fragment.NewsFragment;
import com.juttec.goldmetal.fragment.ServeFragment;

/**
 * Created by Jeremy on 2015/9/10.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 5;
    private String tabTitles[] = new String[]{"行情", "资讯", "交易圈", "服务", "更多"};//tab的文字
    private int[] tabIcon = new int[]{R.drawable.tabicon_market, R.drawable.tabicon_news,
            R.drawable.tabicon_moment, R.drawable.tabicon_serve,
            R.drawable.tabicon_more};//tab的icon
    private Context context;


    //自定义tab界面
    public View getTabView(int position) {

        View v = View.inflate(context, R.layout.cutom_tab, null);
        v.setSelected(false);
        if (position == 0) {
            v.setSelected(true);

        }
        TextView tv = (TextView) v.findViewById(R.id.textView);
        tv.setText(tabTitles[position]);

        ImageView img = (ImageView) v.findViewById(R.id.imageView);
        img.setImageResource(tabIcon[position]);
        return v;
    }


    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment;
        switch (position) {
            case 0:
                fragment =MarketFragment.newInstance("f1");
                break;
            case 1:
                fragment = NewsFragment.newInstance("f2");
                break;
            case 2:
                fragment = MomentFragment.newInstance("f3");
                break;
            case 3:
                fragment = ServeFragment.newInstance("f4");
                break;
            default:
                fragment = MoreFragment.newInstance("f5");
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }


}
