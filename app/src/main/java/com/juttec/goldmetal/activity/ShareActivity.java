package com.juttec.goldmetal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.customview.HeadLayout;

/**
 * 分享APP界面
 */
public class ShareActivity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout rl_sina_friends;//分享给新浪好友
    private RelativeLayout rl_wechat_friends;//分享给微信好友
    private RelativeLayout rl_friends;//分享到朋友圈
    private RelativeLayout rl_qq_friends;//分享给qq好友
    private RelativeLayout rl_qq_zone;//分享到qq空间

    //头部布局
    private HeadLayout mTopbar;
    //头部布局中的  返回按钮
    private ImageView iv_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        initView();
    }


    /**
     * 初始化控件
     */
    private void initView(){

        mTopbar = (HeadLayout) findViewById(R.id.head_layout);
        iv_back = (ImageView) mTopbar.findViewById(R.id.left_img);
        iv_back.setOnClickListener(this);

        rl_sina_friends = (RelativeLayout)findViewById(R.id.share_sina_firends);
        rl_sina_friends.setOnClickListener(this);

        rl_wechat_friends = (RelativeLayout)findViewById(R.id.share_wechat_friends);
        rl_wechat_friends.setOnClickListener(this);

        rl_friends = (RelativeLayout)findViewById(R.id.share_friends);
        rl_friends.setOnClickListener(this);

        rl_qq_friends = (RelativeLayout)findViewById(R.id.share_qq_friends);
        rl_qq_friends.setOnClickListener(this);

        rl_qq_zone = (RelativeLayout)findViewById(R.id.share_qq_zone);
        rl_qq_zone.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share_sina_firends:

                break;
            case R.id.share_wechat_friends:

                break;
            case R.id.share_friends:

                break;
            case R.id.share_qq_friends:

                break;
            case R.id.share_qq_zone:

                break;
            case R.id.left_img:
                //返回按钮
                finish();
                break;
        }
    }
}
