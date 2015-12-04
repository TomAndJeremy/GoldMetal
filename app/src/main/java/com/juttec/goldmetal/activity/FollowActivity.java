package com.juttec.goldmetal.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.juttec.goldmetal.R;
import com.juttec.goldmetal.adapter.PersonDynamicAdapter;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.DynamicEntityList;
import com.juttec.goldmetal.bean.PersonDynamicMsgBean;
import com.juttec.goldmetal.customview.listview.LoadMoreListView;
import com.juttec.goldmetal.customview.listview.LoadingFooter;
import com.juttec.goldmetal.dialog.MyProgressDialog;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;

/**
 * 交易圈  关注界面
 */

public class FollowActivity extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, LoadMoreListView.OnLoadNextListener {


    // 加载更多
    public static final int MSG_LOAD_MORE = 0;

    // 刷新  和 第一次进来
    public static final int MSG_REFRESH = 1;


    private SwipeRefreshLayout swipeLayout;//刷新控件

    private LoadMoreListView mListView;//自定义的ListView


    private RelativeLayout topLayout;//标题栏


    private MyApplication app;

    private String userId;//activity传递过来的用户ID
    private String name;//用户昵称


    private int pageIndex = 1;//请求第几页的数据 默认为第一页
    private int totalPage;//消息的总页数


    private Gson gson;//解析数据

    private ArrayList<DynamicEntityList> entityList;//动态数据的集合

    private PersonDynamicAdapter mAdapter;


    private MyProgressDialog dialog;//加载时的 进度框


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        app = (MyApplication) getApplication();
        dialog = new MyProgressDialog(this);

        gson = new Gson();


        entityList = new ArrayList<DynamicEntityList>();


        initView();


        //获取个人动态信息接口
        getData(MSG_REFRESH);
    }


    private void initView() {
        // 头部
        topLayout = (RelativeLayout) this.findViewById(R.id.head_layout);
        TextView rightText = (TextView) topLayout.findViewById(R.id.right_text);
        rightText.setOnClickListener(this);

        //下拉刷新控件的 设置
        swipeLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
        swipeLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        });

        mListView = (LoadMoreListView) findViewById(R.id.listview);

        // 加载更多的监听
        mListView.setOnLoadNextListener(this);



    }


    //数据加载完毕后 填充数据
    private void initData() {
        if (mAdapter == null) {
            mAdapter = new PersonDynamicAdapter(this, entityList);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

        mListView.setState(LoadingFooter.State.Idle);
    }


    /**
     * 刷新的监听事件
     */
    @Override
    public void onRefresh() {
        pageIndex = 1;
        getData(MSG_REFRESH);
    }


    /**
     * 加载更多的监听事件
     */
    @Override
    public void onLoadNext() {
        if (pageIndex >= totalPage) {
            //没有更多数据了
            mListView.setState(LoadingFooter.State.TheEnd);
            return;
        }
        //页数+1
        pageIndex++;
        getData(MSG_LOAD_MORE);

    }


    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.right_text:
                //发布消息的点击事件
                startActivity(new Intent(FollowActivity.this, PublishTopicActivity.class));
                break;

        }

    }


    /**
     * 获取动态的接口
     * <p/>
     * type 类型 all：所有 attention：关注 personal：个人
     * state:分为刷新和加载
     */
    private void getData(final int state) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", app.getUserInfoBean().getUserId());
        params.addBodyParameter("pageIndex", pageIndex + "");
        params.addBodyParameter("dyType", MyApplication.DYNAMIC_TYPE_ATTENTION);
        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getGetDynamicUrl(), params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        //第一次进来 或者 刷新时
                        if (state == MSG_REFRESH) {
                            swipeLayout.setRefreshing(false);
                            if (entityList != null) {
                                //将entityList里的数据清空
                                entityList.clear();
                            }
                        }
                        LogUtil.d("关注全部动态：---------------" + responseInfo.result.toString());

                        PersonDynamicMsgBean dynamicMsgBean = gson.fromJson(responseInfo.result.toString(), PersonDynamicMsgBean.class);


//                      entityList = dynamicMsgBean.getEntityList();
                        entityList.addAll(dynamicMsgBean.getEntityList());


                        totalPage = Integer.parseInt(dynamicMsgBean.getMessage1());
                        if (totalPage == 0) {
                            ToastUtil.showShort(FollowActivity.this, "没有任何动态，您可以去关注更多的人");
                            return;
                        }


                        //填充数据
                        initData();

                    }


                    @Override
                    public void onFailure(HttpException error, String msg) {
                        //第一次进来 或者 刷新时
                        if (state == MSG_REFRESH) {
                            swipeLayout.setRefreshing(false);
                        }
                        NetWorkUtils.showMsg(FollowActivity.this);
                    }

                }
        );
    }




}
