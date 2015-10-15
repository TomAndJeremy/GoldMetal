package com.juttec.goldmetal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.AccountActivity;
import com.juttec.goldmetal.activity.Announcementactivity;
import com.juttec.goldmetal.activity.ChartActivity;
import com.juttec.goldmetal.activity.CreateAccount.AccountNoticeActivity;
import com.juttec.goldmetal.dialog.MyAlertDialog;


public class MarketFragment extends BaseFragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private ImageView iv_search;//搜索
    private LinearLayout ll_chart;

    private Button btCreateAccount;//开户按钮

    public static MarketFragment newInstance(String param1) {
        MarketFragment fragment = new MarketFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public MarketFragment() {
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
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_market, container, false);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.market_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("自选"));
        tabLayout.addTab(tabLayout.newTab().setText("现货"));
        tabLayout.addTab(tabLayout.newTab().setText("股票"));

        initView(view);
        return view;
    }




    private void initView(View view){
        iv_search = (ImageView) view.findViewById(R.id.market_search);

        ImageView rightImg = (ImageView) view.findViewById(R.id.right_img);
        ImageView leftImg = (ImageView) view.findViewById(R.id.left_img);
        rightImg.setOnClickListener(this);
        leftImg.setOnClickListener(this);
        
        iv_search.setOnClickListener(this);
        ll_chart= (LinearLayout) view.findViewById(R.id.ll_chart);
        ll_chart.setOnClickListener(this);

        btCreateAccount = (Button) view.findViewById(R.id.fragment_market_bt_createaccount);
        btCreateAccount.setOnClickListener(this) ;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.market_search:
                final MyAlertDialog dialog = new MyAlertDialog(getActivity());
                dialog.builder()
                        .setTitle("搜索个股").setEditText("请输入个股代码")
                        .setSingleButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }).show();

                break;

            case R.id.right_img:
                startActivity(new Intent(getActivity(), AccountActivity.class));
                break;

            case R.id.left_img:
                startActivity(new Intent(getActivity(), Announcementactivity.class));
                break;

            case R.id.ll_chart:
                Intent intent = new Intent(getActivity(), ChartActivity.class);
                startActivity(intent);
                break;

            case R.id.fragment_market_bt_createaccount:
                //开户界面
                startActivity(new Intent(getActivity(), AccountNoticeActivity.class));
                break;
        }

    }



}
