package com.juttec.goldmetal.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.FollowActivity;
import com.juttec.goldmetal.activity.MessageActivity;
import com.juttec.goldmetal.activity.MomentPersonalActivity;
import com.juttec.goldmetal.activity.PublishTopicActivity;
import com.juttec.goldmetal.adapter.MomentRecyclerViewAdapter;
import com.juttec.goldmetal.adapter.RecycleViewWithHeadAdapter;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.DynamicEntityList;
import com.juttec.goldmetal.bean.DynamicMsgBean;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;


public class MomentFragment extends BaseFragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    MyApplication app;
    //tabs
    TextView dynamic, message, follow;

    ArrayList<DynamicEntityList> entityList;

    RecyclerView recyclerView;

    MomentRecyclerViewAdapter adapter;
    RecycleViewWithHeadAdapter myAdapter;

    View myHead;

    Gson gson;

    public static MomentFragment newInstance(String param1) {
        MomentFragment fragment = new MomentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public MomentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        app = (MyApplication) getActivity().getApplication();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_moment, container, false);

        initView(view);

        setRecyclerView(view);


        gson = new Gson();
        getInfo(1, MyApplication.DYNAMIC_TYPE_ALL);

        return view;
    }

    private void initView(View view) {
        // 头部
        RelativeLayout head = (RelativeLayout) view.findViewById(R.id.head_layout);
        TextView rightText = (TextView) head.findViewById(R.id.right_text);
        rightText.setOnClickListener(this);


        //下拉刷新
        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        refreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO: 2015/9/14
                refreshLayout.setRefreshing(true);
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
                Toast.makeText(getActivity(), "121312", Toast.LENGTH_LONG).show();

            }
        });


    }


    private void setRecyclerView(View view) {
        //recyclerview 头部
        myHead = View.inflate(getActivity(), R.layout.recycleview_head, null);//头布局
        myHead.findViewById(R.id.moment_btn_cancel).setVisibility(View.GONE);
        myHead.findViewById(R.id.moment_btn_follow).setVisibility(View.GONE);


        //init tabs
        initTabs(myHead);


        /*初始化Recyclerview*/
        recyclerView = (RecyclerView) view.findViewById(R.id.moment_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void initTabs(View view) {
        dynamic = (TextView) view.findViewById(R.id.moment_tv_dynamic);
        message = (TextView) view.findViewById(R.id.moment_tv_message);
        follow = (TextView) view.findViewById(R.id.moment_tv_follow);
        dynamic.setSelected(true);
        dynamic.setOnClickListener(this);
        message.setOnClickListener(this);
        follow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.moment_tv_dynamic:
                dynamic.setSelected(true);
                break;
            case R.id.moment_tv_message:

                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
            case R.id.moment_tv_follow:

                startActivity(new Intent(getActivity(), FollowActivity.class));
                break;
            case R.id.right_text:
                startActivity(new Intent(getActivity(), PublishTopicActivity.class));
                break;
        }
    }

    /**
     * @param page 页数
     * @param type 类型 all：所有 attention：关注 personal：个人
     */
    private void getInfo(int page, String type) {
        RequestParams params = new RequestParams();

        params.addBodyParameter("userId", app.getUserInfoBean().getUserId());
        params.addBodyParameter("pageIndex", page + "");
        params.addBodyParameter("dyType", type);
        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getGetDynamicUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                DynamicMsgBean dynamicMsgBean = gson.fromJson(responseInfo.result.toString(), DynamicMsgBean.class);


                entityList = dynamicMsgBean.getEntityList();


                adapter = new MomentRecyclerViewAdapter(entityList, getActivity(),app);
                adapter.setOnItemClickListener(new MomentRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View v, int posion) {
                        startActivity(new Intent(getActivity(), MomentPersonalActivity.class));
                    }
                });

                // 添加头部
                myAdapter = new RecycleViewWithHeadAdapter<>(adapter);
                myAdapter.addHeader(myHead);
                // 设置Adapter
                recyclerView.setAdapter(myAdapter);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                NetWorkUtils.showMsg(getActivity());

            }
        });

    }



}
