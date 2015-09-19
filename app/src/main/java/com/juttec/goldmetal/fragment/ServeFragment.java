package com.juttec.goldmetal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.PriceReductionActivity;

/**
 * 服务界面
 */
public class ServeFragment extends BaseFragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";

    private RelativeLayout rl_price_conversion;//价格换算
    private RelativeLayout rl_trading_firm;//实盘交易
    private RelativeLayout rl_trading_simulated;//模拟交易
    private RelativeLayout rl_trading_rules;//交易规则

    private String mParam1;

    public static ServeFragment newInstance(String param1) {
        ServeFragment fragment = new ServeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ServeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_serve, container, false);
        initView(view);
        return view;
    }


    /**
     * 初始化控件
     */
    private void initView(View view) {
        rl_price_conversion = (RelativeLayout) view.findViewById(R.id.rl_price_conversion);
        rl_price_conversion.setOnClickListener(this);

        rl_trading_firm = (RelativeLayout) view.findViewById(R.id.rl_trading_firm);
        rl_trading_firm.setOnClickListener(this);

        rl_trading_simulated = (RelativeLayout) view.findViewById(R.id.rl_trading_simulated);
        rl_trading_simulated.setOnClickListener(this);

        rl_trading_rules = (RelativeLayout) view.findViewById(R.id.rl_trading_rules);
        rl_trading_rules.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rl_price_conversion:
                startActivity(new Intent(getActivity(), PriceReductionActivity.class));
                break;
            case R.id.rl_trading_firm:

                break;
            case R.id.rl_trading_simulated:

                break;
            case R.id.rl_trading_rules:

                break;
        }
    }
}
