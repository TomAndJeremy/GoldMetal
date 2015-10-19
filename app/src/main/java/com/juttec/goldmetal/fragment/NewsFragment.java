package com.juttec.goldmetal.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.news.AnalysisActivity;
import com.juttec.goldmetal.activity.news.ExchangeInforActivity;
import com.juttec.goldmetal.activity.news.InvestmentOrgActivity;
import com.juttec.goldmetal.activity.news.ReviewActivity;

/**
 * 资讯界面
 */

public class NewsFragment extends BaseFragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private TextView tv_headlines;//财经头条
    private TextView tv_calendar;//财经日历
    private TextView tv_broadcast;//快讯直播
    private TextView tv_review;//机构评论
    private TextView tv_analysis;//深度解析
    private TextView tv_announcement;//交易公告
    private TextView tv_institution;//投资机构


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
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        initView(view);
        return view;
    }


    /**
     * 初始化控件
     */
    private void initView(View view) {
        tv_headlines = (TextView) view.findViewById(R.id.tv_headlines);
        tv_headlines.setOnClickListener(this);

        tv_calendar = (TextView) view.findViewById(R.id.tv_calendar);
        tv_calendar.setOnClickListener(this);

        tv_broadcast = (TextView) view.findViewById(R.id.tv_broadcast);
        tv_broadcast.setOnClickListener(this);

        tv_review = (TextView) view.findViewById(R.id.tv_review);
        tv_review.setOnClickListener(this);

        tv_analysis = (TextView) view.findViewById(R.id.tv_analysis);
        tv_analysis.setOnClickListener(this);

        tv_announcement = (TextView) view.findViewById(R.id.tv_announcement);
        tv_announcement.setOnClickListener(this);

        tv_institution = (TextView) view.findViewById(R.id.tv_institution);
        tv_institution.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.tv_headlines:

                break;
            case R.id.tv_calendar:

                break;
            case R.id.tv_broadcast:

                break;
            case R.id.tv_review:
                intent = new Intent(getActivity(), ReviewActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_analysis:
                //深度解析
                intent = new Intent(getActivity(), AnalysisActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_announcement:
                intent = new Intent(getActivity(), ExchangeInforActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_institution:
                intent = new Intent(getActivity(), InvestmentOrgActivity.class);
                startActivity(intent);
                break;
        }
    }


}
