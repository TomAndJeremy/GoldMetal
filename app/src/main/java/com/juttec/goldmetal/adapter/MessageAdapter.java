package com.juttec.goldmetal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.MessageBean;
import com.juttec.goldmetal.utils.EmojiUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 *
 * 消息界面的  adapter
 */
public class MessageAdapter extends BaseAdapter{

    private Context mContext;

    private List<MessageBean> mLists;

    private LayoutInflater mInflater;

    private MyApplication app;



    private EmojiUtil readEmoji;


    public MessageAdapter(Context context,List<MessageBean> list){
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
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageBean messageBean;

        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.adapter_msg_item,null);
            holder = new ViewHolder();
            holder.iv_photo = (ImageView) convertView.findViewById(R.id.iv_photo);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);


            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        messageBean = mLists.get(position);
        holder.tv_name.setText(messageBean.getMsgReplyerName());
        holder.tv_title.setText(readEmoji.getEditable(messageBean.getMsgBirefTitle()));
        holder.tv_content.setText(readEmoji.getEditable(messageBean.getMsgBriefContent()));
        holder.tv_time.setText(messageBean.getMsgAddTime());
        ImageLoader.getInstance().displayImage(app.getImgBaseUrl() + messageBean.getMsgUserPhoto(), holder.iv_photo);

        return convertView;
    }





    static class ViewHolder{
        ImageView iv_photo;
        TextView tv_name;
        TextView tv_title;
        TextView tv_content;
        TextView tv_time;
    }
}



