package com.juttec.goldmetal.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.juttec.goldmetal.R;

/**
 * 更多界面
 */
public class MoreFragment extends BaseFragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private RelativeLayout rl_disclaimer;//免责声明
    private RelativeLayout rl_feedback;//意见反馈
    private RelativeLayout rl_about;//关于我们
    private RelativeLayout rl_update;//检查更新
    private RelativeLayout rl_instructions;//使用说明
    private RelativeLayout rl_share;//分享app
    private RelativeLayout rl_setting;//系统设置

    public static MoreFragment newInstance(String param1) {
        MoreFragment fragment = new MoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public MoreFragment() {
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
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        initView(view);
        return view;
    }


    /**
     * 初始化控件
     */
    private void initView(View  view){
        rl_disclaimer = (RelativeLayout)view.findViewById(R.id.rl_disclaimer);
        rl_disclaimer.setOnClickListener(this);

        rl_feedback= (RelativeLayout)view.findViewById(R.id.rl_feedback);
        rl_feedback.setOnClickListener(this);

        rl_about= (RelativeLayout)view.findViewById(R.id.rl_about);
        rl_about.setOnClickListener(this);

        rl_update= (RelativeLayout)view.findViewById(R.id.rl_update);
        rl_update.setOnClickListener(this);

        rl_instructions= (RelativeLayout)view.findViewById(R.id.rl_instructions);
        rl_instructions.setOnClickListener(this);

        rl_share= (RelativeLayout)view.findViewById(R.id.rl_share);
        rl_share.setOnClickListener(this);

        rl_setting= (RelativeLayout)view.findViewById(R.id.rl_setting);
        rl_setting.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.rl_disclaimer:

                break;
            case R.id.rl_feedback:

                break;
            case R.id.rl_about:

                break;
            case R.id.rl_update:

                break;
            case R.id.rl_instructions:

                break;
            case R.id.rl_share:

                break;
            case R.id.rl_setting:

                break;
        }
    }
}
