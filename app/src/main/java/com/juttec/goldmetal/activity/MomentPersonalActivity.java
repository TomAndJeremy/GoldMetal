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
import com.juttec.goldmetal.customview.listview.LoadingFooter;
import com.juttec.goldmetal.customview.listview.PersonLoadListView;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jeremy on 2015/9/21.
 * 交易圈 个人主页界面
 */
public class MomentPersonalActivity extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, PersonLoadListView.OnLoadNextListener {


    // 加载更多
    public static final int MSG_LOAD_MORE = 0;

    // 刷新  和 第一次进来
    public static final int MSG_REFRESH = 1;



    private SwipeRefreshLayout swipeLayout;//刷新控件

    private PersonLoadListView mListView;//自定义的ListView


    private RelativeLayout topLayout;//标题栏
    private TextView tv_title;//个人名称


    private MyApplication app;

    private String userId;//activity传递过来的用户ID
    private String headPhoto;//头像
    private String name;//用户昵称


    private int pageIndex = 1;//请求第几页的数据 默认为第一页
    private int totalPage;//消息的总页数


    private Gson gson;//解析数据

    private ArrayList<DynamicEntityList> entityList;//动态数据的集合

    private PersonDynamicAdapter mAdapter;

    private boolean isFocus = false;//是否关注了 true:关注了  false:没有关注

    private MyProgressDialog dialog;//加载时的 进度框


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_moment);
        app = (MyApplication) getApplication();
        dialog = new MyProgressDialog(this);

        gson = new Gson();
        userId = getIntent().getStringExtra("userId");
        name = getIntent().getStringExtra("userName");

        entityList = new ArrayList<DynamicEntityList>();

        initView();


        //获取用户头像接口
        getUserPhoto();

        //获取个人动态信息接口
        getData(MSG_REFRESH);
    }


    private void initView() {
        // 头部
        topLayout = (RelativeLayout) this.findViewById(R.id.head_layout);
        TextView rightText = (TextView) topLayout.findViewById(R.id.right_text);
        tv_title = (TextView) topLayout.findViewById(R.id.head_title);
        tv_title.setText(name);
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


        mListView = (PersonLoadListView) findViewById(R.id.listview);

        //当用户进入自己的主页时   隐藏关注按钮
        if (app.isLogin()) {
            if (userId.equals(app.getUserInfoBean().getUserId())) {
                mListView.getHeaderView().hideFocusButton();
            }
        }


        // 加载更多的监听
        mListView.setOnLoadNextListener(this);


        //关注与取消关注的 监听事件
        mListView.getHeaderView().getFocusView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置关注或取消关注接口

                if (isLogin()) {
                    setFocusOr();
                }

            }
        });
    }



    //数据加载完毕后 填充数据
    private void initData(){
        if(mAdapter==null){
            mAdapter  = new PersonDynamicAdapter(this,entityList,userId);
            mListView.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
        }

        mListView.setState(LoadingFooter.State.Idle);
    }



    /**
     * 刷新的监听事件
     */
    @Override
    public void onRefresh() {
        pageIndex =1;
        getData(MSG_REFRESH);
    }


    /**
     * 加载更多的监听事件
     */
    @Override
    public void onLoadNext() {
        if(pageIndex>=totalPage){
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
                startActivity(new Intent(MomentPersonalActivity.this, PublishTopicActivity.class));
                break;

        }

    }




    /**
     * 获取动态的接口
     *
     * type 类型 all：所有 attention：关注 personal：个人
     *  state:分为刷新和加载
     */
    private void getData(final int state) {
        //第一次进来 或者 刷新时
//        if(state==MSG_REFRESH){
//            swipeLayout.setRefreshing(true);
//        }

        RequestParams params = new RequestParams();

        params.addBodyParameter("userIdOne", userId);

        if (app.isLogin()) {

            params.addBodyParameter("userId", app.getUserInfoBean().getUserId());
        } else {
            params.addBodyParameter("userId", "123");
        }
        params.addBodyParameter("pageIndex", pageIndex +"");
        params.addBodyParameter("dyType", MyApplication.DYNAMIC_TYPE_PERSONAL);
        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getGetDynamicUrl(), params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        //第一次进来 或者 刷新时
                        if (state == MSG_REFRESH) {
                            swipeLayout.setRefreshing(false);
                            if(entityList!=null){
                                //将entityList里的数据清空
                                entityList.clear();
                            }
                        }
                        LogUtil.d("个人中心全部动态：---------------" + responseInfo.result.toString());

                        PersonDynamicMsgBean dynamicMsgBean = gson.fromJson(responseInfo.result.toString(), PersonDynamicMsgBean.class);


//                      entityList = dynamicMsgBean.getEntityList();
                        entityList.addAll(dynamicMsgBean.getEntityList());
                        //设置是否关注
                        isFocus = Integer.parseInt(dynamicMsgBean.getMessage2()) == 0 ? true : false;
                        mListView.getHeaderView().setFocusOr(isFocus);

                        totalPage = Integer.parseInt(dynamicMsgBean.getMessage1());
                        if(totalPage ==0){
                            ToastUtil.showShort(MomentPersonalActivity.this,name+"没有任何动态");
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
                        NetWorkUtils.showMsg(MomentPersonalActivity.this);
                    }

                }
        );
    }


    /**
     * 调接口 设置关注与取消关注
     */
    private void setFocusOr(){
        dialog.builder().show();
        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", app.getUserInfoBean().getUserId());//关注人编号ID
        params.addBodyParameter("userName", app.getUserInfoBean().getUserNickName());//关注人昵称
        params.addBodyParameter("attentionedId", userId);//被关注人编号ID
        params.addBodyParameter("attentionedName", name);//被关注人昵称
        params.addBodyParameter("status",""+(isFocus?1:0));//关注标记  0：关注 1：取消关注

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.getAddOrCancelAttentionUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog.dismiss();
                LogUtil.d("setFocusOr--------" + responseInfo.result.toString());

                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    String status = object.getString("status");
                    String promptInfor = object.getString("promptInfor");
                    if ("1".equals(status)) {
                        //更改关注按钮 状态
                        isFocus = !isFocus;
                        mListView.getHeaderView().setFocusOr(isFocus);
                        ToastUtil.showShort(MomentPersonalActivity.this, promptInfor);
                    } else {
                        ToastUtil.showShort(MomentPersonalActivity.this, promptInfor);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog.dismiss();
                NetWorkUtils.showMsg(MomentPersonalActivity.this);

            }
        });
    }



    //获取用户头像的  接口
    private void getUserPhoto() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", userId);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.getGetUserPhotoUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtil.d("获取用户头像接口返回结果:" + responseInfo.result.toString());
                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    if ("1".equals(object.getString("status"))) {

                        headPhoto = object.getString("message1");
                        ImageLoader.getInstance().displayImage(app.getImgBaseUrl() + headPhoto, mListView.getHeaderView().getPhotoView());

                    } else {
                        ToastUtil.showShort(MomentPersonalActivity.this, "获取头像失败");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                ToastUtil.showShort(MomentPersonalActivity.this, "请检查网络是否正常连接");
            }
        });

    }

    private boolean isLogin() {
        if (!app.isLogin()) {
            ToastUtil.showShort(this,"请先登录再进行操作");
            startActivity(new Intent(this, LoginActivity.class));
            return false;
        }

        if (app.getUserInfoBean().getUserNickName() == null) {
            ToastUtil.showShort(this,"请先设置昵称再进行操作");
            return false;
        }
        return true;
    }

}
