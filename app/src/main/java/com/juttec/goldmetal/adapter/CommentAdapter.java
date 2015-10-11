package com.juttec.goldmetal.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.MomentPersonalActivity;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.DyCommentReplyBean;
import com.juttec.goldmetal.customview.listview.NoScrollListView;
import com.juttec.goldmetal.utils.ToastUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 *
 * 评论界面的  adapter
 */
public class CommentAdapter extends BaseAdapter{

    private Context mContext;

    private List<DyCommentReplyBean> mLists;

    private LayoutInflater mInflater;

    private MyApplication app;

    private String currentUserId;//个人主页的用户id   判断是否在个人主页


    public CommentAdapter(Context context, List<DyCommentReplyBean> list){
        app = (MyApplication) context.getApplicationContext();
        mContext = context;
        mLists = list;
        mInflater = LayoutInflater.from(context);
    }

    public CommentAdapter(Context context, List<DyCommentReplyBean> list,String userid){
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
        DyCommentReplyBean dyCommentReplyBean;

        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.comment_item,null);
            holder = new ViewHolder();
            holder.tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_comment_content);
            holder.replyListview = (NoScrollListView) convertView.findViewById(R.id.reply_listview);
            holder.ll_layout = (LinearLayout) convertView.findViewById(R.id.ll_comment);


            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        dyCommentReplyBean = mLists.get(position);
        holder.tv_comment.setText(dyCommentReplyBean.getDiscussantName());
        holder.tv_content.setText(dyCommentReplyBean.getCommentContent());

        //填充回复数据
        if(currentUserId!=null){
            //判断是否在个人主页  若是个人主页
            holder.replyListview.setAdapter(new ReplyAdapter(mContext, dyCommentReplyBean.getDyReply(),currentUserId));
        }else{
            holder.replyListview.setAdapter(new ReplyAdapter(mContext, dyCommentReplyBean.getDyReply()));
        }



        //评论人名字的点击事件   点击跳转到个人主页
        holder.tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否在个人主页  并且是否点击的本人  若是本人则不跳转
                if(currentUserId!=null&&mLists.get(position).getDiscussantId().equals(currentUserId)){
                    return;
                }
                Intent intent = new Intent(mContext, MomentPersonalActivity.class);
                intent.putExtra("userId", mLists.get(position).getDiscussantId());
                intent.putExtra("userName", mLists.get(position).getDiscussantName());
                mContext.startActivity(intent);
            }
        });



        //评论内容的点击事件    即：回复
        holder.ll_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ToastUtil.showShort(mContext,"回复"+mLists.get(position).getDiscussantName());
            }
        });



        //回复的点击事件
        holder.replyListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                ToastUtil.showShort(mContext,"回复"+mLists.get(position).getDyReply().get(pos).getUserName());
            }
        });



        return convertView;
    }





    static class ViewHolder{
        TextView tv_comment;//评论的人名
        TextView tv_content;//评论的内容
        NoScrollListView replyListview;//用于填充回复的数据
        LinearLayout ll_layout;//评论内容所占的一行
    }


    //评论或者 回复的 接口回调
    public interface OnCommentAndReplyClickListener{
        void onCommentAndReplyClick();
    }

    private OnCommentAndReplyClickListener mCommentAndReplyClickListener;

    public void setOnLoadNextListener(OnCommentAndReplyClickListener listener) {
        mCommentAndReplyClickListener = listener;
    }
}



