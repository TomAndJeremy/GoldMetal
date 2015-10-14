package com.juttec.goldmetal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.customview.HeadLayout;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

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
        Platform.ShareParams sp = new Platform.ShareParams();
        switch (v.getId()){
            case R.id.share_sina_firends:
                showShare();

                break;
            case R.id.share_wechat_friends:
                showShare();
                break;
            case R.id.share_friends:
                showShare();

                break;
            case R.id.share_qq_friends:
                showShare();

                break;
            case R.id.share_qq_zone:
                break;
            case R.id.left_img:
                //返回按钮
                finish();
                break;
        }
    }




    private void showShare() {
        ShareSDK.initSDK(this);
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
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/05/21/oESpJ78_533x800.jpg");

        // 启动分享GUI
        oks.show(this);
    }


}
