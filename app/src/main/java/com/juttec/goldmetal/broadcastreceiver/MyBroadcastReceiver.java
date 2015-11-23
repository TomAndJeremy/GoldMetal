package com.juttec.goldmetal.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.juttec.goldmetal.adapter.RecycleViewWithHeadAdapter;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.DyCommentReplyBean;
import com.juttec.goldmetal.bean.DySupportInfoBean;
import com.juttec.goldmetal.bean.DynamicEntityList;
import com.juttec.goldmetal.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Jeremy on 2015/10/14.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    //接收到广播会被自动调用
    ArrayList<DynamicEntityList> entityList;
    RecycleViewWithHeadAdapter myAdapter;
    MyApplication app;

    // private int position = -1;


    public MyBroadcastReceiver() {
    }

    public MyBroadcastReceiver(ArrayList<DynamicEntityList> entityList, MyApplication app) {
        this.entityList = entityList;

        this.app = app;
    }

    public void setMyAdapter(RecycleViewWithHeadAdapter myAdapter) {
        this.myAdapter = myAdapter;
    }//传递adapter

    @Override
    public void onReceive(Context context, Intent intent) {
        //从Intent中获取action
        for (int i = 0; i < entityList.size(); i++) {
            if (intent.getStringExtra("dyId").equals(entityList.get(i).getId())) {

                if ("com.juttec.goldmetal.addsupport".equals(intent.getAction())) {
                    //将点赞实体添加到集合众
                    entityList.get(i).getDySupport().add(0,(DySupportInfoBean) intent.getParcelableExtra("support"));//点赞
                    myAdapter.notifyDataSetChanged();
                } else if ("com.juttec.goldmetal.cancelsupport".equals(intent.getAction())) {//取消赞
                    //取消咱
                    for (int j = 0; j < entityList.get(i).getDySupport().size(); j++) {
                        if (app.getUserInfoBean().getUserId().equals(entityList.get(i).getDySupport().get(j).getUserId())) {
                            entityList.get(i).getDySupport().remove(j);
                            myAdapter.notifyDataSetChanged();
                            return;
                        }
                    }
                } else if ("com.juttec.goldmetal.comment".equals(intent.getAction())) {//添加评论
                    //添加评论
                    entityList.get(i).getDyCommentReply().add((DyCommentReplyBean) intent.getParcelableExtra("comment"));
                    myAdapter.notifyDataSetChanged();
                }
                return;
            }

        }
    }
}