package com.juttec.goldmetal.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.adapter.MomentRecyclerViewAdapter;
import com.juttec.goldmetal.adapter.RecycleViewWithHeadAdapter;
import com.juttec.goldmetal.utils.ToastUtil;

/**
 * Created by Jeremy on 2015/9/21.
 */
public class MomentPersonalActivity extends Activity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_moment);
        initView();
        setRecyclerView();
    }


    private void initView() {
        // 头部
        RelativeLayout head = (RelativeLayout) this.findViewById(R.id.head_layout);
        TextView rightText = (TextView) head.findViewById(R.id.right_text);
        rightText.setOnClickListener(this);


        //下拉刷新
        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
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

            }
        });




    }

    private void setRecyclerView() {
        //recyclerview 头部
        View myHead = View.inflate(this, R.layout.recycleview_head, null);//头布局

        myHead.findViewById(R.id.moment_tabs).setVisibility(View.GONE);

        /*初始化Recyclerview*/
        RecyclerView recyclerView = (RecyclerView)this.findViewById(R.id.moment_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        String[] dataset = new String[20];
        for (int i = 0; i < dataset.length; i++) {
            dataset[i] = "item" + i;
        }


        // 创建Adapter，并指定数据集
      //  MomentRecyclerViewAdapter adapter = new MomentRecyclerViewAdapter(, this);

//        adapter.setOnItemClickListener(new MomentRecyclerViewAdapter.OnItemClickListener() {
//            @Override
//            public void onClick(View v, int posion) {
//                Snackbar.make(v, "this is " + posion, Snackbar.LENGTH_LONG)
//                        .setAction("Action", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                ToastUtil.showLong(getApplicationContext(),"111111111111111");
//                            }
//                        }).show();
//            }
//        });

        // 添加头部
//        RecycleViewWithHeadAdapter myAdapter = new RecycleViewWithHeadAdapter<>(adapter);
//        myAdapter.addHeader(myHead);
//
//
//        // 设置Adapter
//        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public void onClick(View v) {

    }
}
