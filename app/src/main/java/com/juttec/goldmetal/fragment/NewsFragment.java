package com.juttec.goldmetal.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.juttec.goldmetal.R;

/**
 * 资讯界面
 */

public class NewsFragment extends BaseFragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private RelativeLayout rl_headlines;//财经头条
    private RelativeLayout rl_calendar;//财经日历
    private RelativeLayout rl_broadcast;//快讯直播
    private RelativeLayout rl_review;//机构评论
    private RelativeLayout rl_analysis;//深度解析
    private RelativeLayout rl_announcement;//交易公告
    private RelativeLayout rl_institution;//投资机构

    public static NewsFragment newInstance(String param1) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news,container,false);
        initView(view);
        return view;
    }


    /**
     * 初始化控件
     */
    private void initView(View  view){
        rl_headlines = (RelativeLayout)view.findViewById(R.id.rl_headlines);
        rl_headlines.setOnClickListener(this);

        rl_calendar= (RelativeLayout)view.findViewById(R.id.rl_calendar);
        rl_calendar.setOnClickListener(this);

        rl_broadcast= (RelativeLayout)view.findViewById(R.id.rl_broadcast);
        rl_broadcast.setOnClickListener(this);

        rl_review= (RelativeLayout)view.findViewById(R.id.rl_review);
        rl_review.setOnClickListener(this);

        rl_analysis= (RelativeLayout)view.findViewById(R.id.rl_analysis);
        rl_analysis.setOnClickListener(this);

        rl_announcement= (RelativeLayout)view.findViewById(R.id.rl_announcement);
        rl_announcement.setOnClickListener(this);

        rl_institution= (RelativeLayout)view.findViewById(R.id.rl_institution);
        rl_institution.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.rl_headlines:

                break;
            case R.id.rl_calendar:

                break;
            case R.id.rl_broadcast:

                break;
            case R.id.rl_review:

                break;
            case R.id.rl_analysis:

                break;
            case R.id.rl_announcement:

                break;
            case R.id.rl_institution:

                break;
        }
    }


}
