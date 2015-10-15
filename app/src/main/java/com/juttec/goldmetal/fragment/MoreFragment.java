package com.juttec.goldmetal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.AboutActivity;
import com.juttec.goldmetal.activity.FeedbackActivity;
import com.juttec.goldmetal.activity.SettingActivity;
import com.juttec.goldmetal.dialog.MyAlertDialog;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 更多界面
 */
public class MoreFragment extends BaseFragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private TextView rl_disclaimer;//免责声明
    private TextView rl_feedback;//意见反馈
    private TextView rl_about;//关于我们
    private TextView rl_update;//检查更新
    private TextView rl_instructions;//使用说明
    private TextView rl_share;//分享app
    private TextView rl_setting;//系统设置

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
        rl_disclaimer = (TextView)view.findViewById(R.id.fragment_more_tv_disclaimer);
        rl_disclaimer.setOnClickListener(this);

        rl_feedback= (TextView)view.findViewById(R.id.fragment_more_tv_feedback);
        rl_feedback.setOnClickListener(this);

        rl_about= (TextView)view.findViewById(R.id.fragment_more_tv_about);
        rl_about.setOnClickListener(this);

        rl_update= (TextView)view.findViewById(R.id.fragment_more_tv_update);
        rl_update.setOnClickListener(this);

        rl_instructions= (TextView)view.findViewById(R.id.fragment_more_tv_instructions);
        rl_instructions.setOnClickListener(this);

        rl_share= (TextView)view.findViewById(R.id.fragment_more_tv_share);
        rl_share.setOnClickListener(this);

        rl_setting= (TextView)view.findViewById(R.id.fragment_more_tv_setting);
        rl_setting.setOnClickListener(this);
    }



    private void showShare() {
        ShareSDK.initSDK(getActivity());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("国金贵金属");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("这个软件很不错  快来下载吧");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");
        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/05/21/oESpJ78_533x800.jpg");
//
//        // 启动分享GUI
        oks.show(getActivity());
    }



    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()){

            case R.id.fragment_more_tv_disclaimer:
                intent = new Intent(getActivity(), AboutActivity.class);
                intent.putExtra("fromActivity","免责声明");
                getActivity().startActivity(intent);
                break;
            case R.id.fragment_more_tv_feedback:
                intent = new Intent(getActivity(), FeedbackActivity.class);
                getActivity().startActivity(intent);

                break;
            case R.id.fragment_more_tv_about:
                intent = new Intent(getActivity(), AboutActivity.class);
                intent.putExtra("fromActivity","关于我们");
                getActivity().startActivity(intent);

                break;
            case R.id.fragment_more_tv_update:
                //检查版本更新
                final MyAlertDialog dialog = new MyAlertDialog(getActivity());
                dialog.builder().setTitle("提示")
                        .setMsg("您的软件为最新版本")
                        .setSingleButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }).show();

                break;
            case R.id.fragment_more_tv_instructions:
                intent = new Intent(getActivity(), AboutActivity.class);
                intent.putExtra("fromActivity","使用说明");
                getActivity().startActivity(intent);

                break;

            case R.id.fragment_more_tv_share:
                showShare();
//                intent = new Intent(getActivity(), ShareActivity.class);
//                getActivity().startActivity(intent);

                break;
            case R.id.fragment_more_tv_setting:
                intent = new Intent(getActivity(),  SettingActivity.class);
                getActivity().startActivity(intent);

                break;
        }
    }
}
