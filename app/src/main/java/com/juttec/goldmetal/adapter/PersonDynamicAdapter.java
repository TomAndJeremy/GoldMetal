package com.juttec.goldmetal.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.ImagePagerActivity;
import com.juttec.goldmetal.activity.MomentPersonalActivity;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.DynamicEntityList;
import com.juttec.goldmetal.bean.PhotoBean;
import com.juttec.goldmetal.customview.CircleImageView;
import com.juttec.goldmetal.customview.NoScrollGridView;
import com.juttec.goldmetal.customview.listview.NoScrollListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 *
 * 个人主页界面的  adapter
 * 关注界面的adapter
 */
public class PersonDynamicAdapter extends BaseAdapter{

    private Context mContext;

    private List<DynamicEntityList> mLists;

    private LayoutInflater mInflater;

    private MyApplication app;

    private String currentUserId;//个人主页 会用到   个人主页用户的id


    public PersonDynamicAdapter(Context context, List<DynamicEntityList> list){
        app = (MyApplication) context.getApplicationContext();
        mContext = context;
        mLists = list;
        mInflater = LayoutInflater.from(context);
    }

    public PersonDynamicAdapter(Context context, List<DynamicEntityList> list,String userid){
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
        DynamicEntityList dynamicEntityList;

        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.dynamic_item,null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.dynamic_item_user_name);
            holder.time = (TextView) convertView.findViewById(R.id.dynamic_item_user_time);
            holder.headPortrait = (CircleImageView) convertView.findViewById(R.id.dynamic_item_head_portrait);
            holder.content = (TextView) convertView.findViewById(R.id.dynamic_item_content);

            holder.replyIMB = (ImageButton) convertView.findViewById(R.id.dynamic_item_reply);

            holder.gridView = (NoScrollGridView) convertView.findViewById(R.id.gridview);
            holder.commentListView = (NoScrollListView) convertView.findViewById(R.id.comment_listview);

            holder.suport = (LinearLayout) convertView.findViewById(R.id.item_suport);
            holder.suportMe = (TextView) convertView.findViewById(R.id.item_suport_me);
            holder.suportOrther = (LinearLayout) convertView.findViewById(R.id.item_suport_other);

            holder.comment = (LinearLayout) convertView.findViewById(R.id.item_comment_content);
            holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.meg_detail_info);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        dynamicEntityList = mLists.get(position);
        holder.name.setText(dynamicEntityList.getUserName());//设置用户名
        holder.time.setText(dynamicEntityList.getAddTime());//时间
        holder.content.setText(unicode2String(dynamicEntityList.getDyContent()));//正文
        //设置头像
        ImageLoader.getInstance().displayImage(MyApplication.ImgBASEURL+dynamicEntityList.getUserPhoto(),  holder.headPortrait);

        //评论按钮的点击事件  对发动态的人进行评论
        holder.replyIMB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        //填充评论回复列表
        if(currentUserId!=null){
            //判断是否在个人主页   若在个人主页
            holder.commentListView.setAdapter(new CommentAdapter(mContext,dynamicEntityList.getDyCommentReply(),currentUserId));
        }else{

            holder.commentListView.setAdapter(new CommentAdapter(mContext,dynamicEntityList.getDyCommentReply()));
        }


        //图片的集合
        final ArrayList<PhotoBean> photoBeanList = dynamicEntityList.getDyPhoto();
        holder.gridView.setAdapter(new NoScrollGridAdapter(mContext, photoBeanList));

        // 点击回帖九宫格，查看大图
        holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> imageUrls = new ArrayList<String>();
                for (int i = 0; i < photoBeanList.size(); i++) {
                    imageUrls.add(photoBeanList.get(i).getDyPhoto());
                }
                imageBrower(position, imageUrls);
            }
        });


        //点击用户头像，用户名  进入个人主页
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否在个人主页  并且是否点击的本人  若是本人则不跳转
                if(currentUserId!=null&&mLists.get(position).getUserId().equals(currentUserId)){
                    return;
                }
                Intent intent = new Intent(mContext, MomentPersonalActivity.class);
                intent.putExtra("userId", mLists.get(position).getUserId());
                intent.putExtra("userName", mLists.get(position).getUserName());
                mContext.startActivity(intent);

            }
        });



        return convertView;
    }

    /**
     * 打开图片查看器
     *
     */
    protected void imageBrower(int position, ArrayList<String> urls2) {
        Intent intent = new Intent(mContext, ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        mContext.startActivity(intent);
    }





    static class ViewHolder{
        TextView name;//用户名
        TextView time;//时间
        CircleImageView headPortrait;//头像
        TextView content;//内容

        TextView suportMe;//点赞（我）


        ImageButton replyIMB;//回复按钮

        LinearLayout suport;//点赞的父布局
        LinearLayout suportOrther;//放置其他人的点赞
        LinearLayout comment;//回复父布局


        RelativeLayout relativeLayout;//用户名，头像，点击跳转到该用户主页

        //展示图片的 GridView
        NoScrollGridView gridView;

        //显示 评论 回复 的Listview
        NoScrollListView commentListView;
    }



    /**
     * unicode 转字符串
     */
    private String unicode2String(String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }
        return string.toString();
    }

}



