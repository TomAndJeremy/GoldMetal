package com.juttec.goldmetal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.DyReplyInfoBean;
import com.juttec.goldmetal.utils.EmojiUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 *
 * 消息详情界面的回复的  adapter
 */
public class MessageReplyAdapter extends BaseAdapter{

    private Context mContext;

    private List<DyReplyInfoBean> mLists;

    private LayoutInflater mInflater;

    private MyApplication app;

    private String currentUserId;//个人主页的用户id   判断是否在个人主页
    private EmojiUtil readEmoji;

    public MessageReplyAdapter(Context context, List<DyReplyInfoBean> list){
        app = (MyApplication) context.getApplicationContext();
        mContext = context;
        mLists = list;
        mInflater = LayoutInflater.from(context);


        readEmoji = new EmojiUtil(context);
        readEmoji.readEmojiIcons();
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
        holder.tv_content.setText(readEmoji.getEditable(dyReplyInfoBean.getReplyContent()));


        return convertView;
    }





    static class ViewHolder{
        TextView tv_reply;//回复的人名
        TextView replay;//回复
        TextView tv_replyed;//被回复的人名
        TextView tv_content;//回复的内容
    }
}



