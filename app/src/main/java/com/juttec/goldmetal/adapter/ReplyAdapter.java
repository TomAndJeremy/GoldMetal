package com.juttec.goldmetal.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.MomentPersonalActivity;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.DyReplyInfoBean;

import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 *
 * 评论回复界面的  adapter
 */
public class ReplyAdapter extends BaseAdapter{

    private Context mContext;

    private List<DyReplyInfoBean> mLists;

    private LayoutInflater mInflater;

    private MyApplication app;

    private String currentUserId;//个人主页的用户id   判断是否在个人主页


    public ReplyAdapter(Context context, List<DyReplyInfoBean> list){
        app = (MyApplication) context.getApplicationContext();
        mContext = context;
        mLists = list;
        mInflater = LayoutInflater.from(context);
    }


    public ReplyAdapter(Context context, List<DyReplyInfoBean> list,String userid){
        app = (MyApplication) context.getApplicationContext();
        mContext = context;
        mLists = list;
        mInflater = LayoutInflater.from(context);
        currentUserId = userid;
    }

    @Override
    public int getCount() {

        return mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        DyReplyInfoBean dyReplyInfoBean;

        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.reply_item,null);
            holder = new ViewHolder();
            holder.tv_reply = (TextView) convertView.findViewById(R.id.tv_reply);
            holder.replay = (TextView) convertView.findViewById(R.id.reply);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_reply_content);
            holder.tv_replyed = (TextView) convertView.findViewById(R.id.tv_replyed);


            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        dyReplyInfoBean = mLists.get(position);
        holder.tv_reply.setText(dyReplyInfoBean.getUserName());
        holder.tv_replyed.setText(dyReplyInfoBean.getRepliedName());
        holder.tv_content.setText(dyReplyInfoBean.getReplyContent());



        //回复人名字的点击事件   点击跳转到个人主页
        holder.tv_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否在个人主页  并且是否点击的本人  若是本人则不跳转
                if (currentUserId != null && mLists.get(position).getUserId().equals(currentUserId)) {
                    return;
                }
                Intent intent = new Intent(mContext, MomentPersonalActivity.class);
                intent.putExtra("userId", mLists.get(position).getUserId());
                intent.putExtra("userName", mLists.get(position).getUserName());
                mContext.startActivity(intent);
            }
        });


        //被回复人名字的点击事件   点击跳转到个人主页
        holder.tv_replyed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否在个人主页  并且是否点击的本人  若是本人则不跳转
                if (currentUserId != null && mLists.get(position).getUserId().equals(currentUserId)) {
                    return;
                }
                Intent intent = new Intent(mContext, MomentPersonalActivity.class);
                intent.putExtra("userId", mLists.get(position).getRepliedId());
                intent.putExtra("userName", mLists.get(position).getRepliedName());
                mContext.startActivity(intent);
            }
        });




        return convertView;
    }





    static class ViewHolder{
        TextView tv_reply;//回复的人名
        TextView replay;//回复
        TextView tv_replyed;//被回复的人名
        TextView tv_content;//回复的内容
    }
}



