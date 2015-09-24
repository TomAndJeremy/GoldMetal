package com.juttec.goldmetal.adapter;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.DynamicEntityList;
import com.juttec.goldmetal.customview.CircleImageView;
import com.juttec.goldmetal.utils.LogUtil;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Jeremy on 2015/9/14.
 */
public class MomentRecyclerViewAdapter extends RecyclerView.Adapter<MomentRecyclerViewAdapter.ViewHolder> {

    private OnItemClickListener mOnItemClickListener;

    // 数据集
    ArrayList<DynamicEntityList> entityList;
    Context context;
    MyApplication app;


    public MomentRecyclerViewAdapter(ArrayList<DynamicEntityList> entityList, Context context, MyApplication app) {
        super();
        this.entityList = entityList;
        LogUtil.e(entityList.get(0).toString());

        this.context = context;
        this.app = app;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView

        //View view = View.inflate(context, R.layout.dynamic_item, null);

        View view = LayoutInflater.from(context).inflate(R.layout.dynamic_item, parent, false);

        // 创建一个ViewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        holder.name.setText(entityList.get(position).getUserName());
        holder.time.setText(entityList.get(position).getAddTime());
        // holder.headPortrait


        holder.content.setText(unicode2String(entityList.get(position).getDyContent()));

        if (entityList.get(position).getDySupport().size() > 0) {
            holder.suport.setVisibility(View.VISIBLE);
            holder.suportMe.setText("我、");
            addView(holder.suportOrther, getsuportName(position, holder.thumb));
        } else {
            holder.suport.setVisibility(View.GONE);
            holder.suportMe.setText("我");
        }

        if (holder.thumb.isChecked()) {
            holder.suportMe.setVisibility(View.VISIBLE);

        } else {
            holder.suportMe.setVisibility(View.GONE);
        }

        holder.thumb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.suport.setVisibility(View.VISIBLE);
                    holder.suportMe.setVisibility(View.VISIBLE);


                } else {
                    holder.suportMe.setVisibility(View.GONE);
                    holder.suportOrther.removeAllViews();
                    if (entityList.get(position).getDySupport().size() > 0) {
                        addView(holder.suportOrther, getsuportName(position, holder.thumb));
                    } else {
                        holder.suport.setVisibility(View.GONE);
                    }
                }
            }
        });


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View view;

        TextView name;
        TextView time;
        CircleImageView headPortrait;
        TextView content;
        TextView suportMe;

        CheckBox thumb;
        ImageButton replyIMB;

        LinearLayout suport;
        LinearLayout suportOrther;


        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;


        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = (TextView) view.findViewById(R.id.dynamic_item_user_name);
            time = (TextView) view.findViewById(R.id.dynamic_item_user_time);
            headPortrait = (CircleImageView) view.findViewById(R.id.dynamic_item_head_portrait);
            content = (TextView) view.findViewById(R.id.dynamic_item_content);
            thumb = (CheckBox) view.findViewById(R.id.dynamic_item_thumb);
            replyIMB = (ImageButton) view.findViewById(R.id.dynamic_item_reply);

            imageView1 = (ImageView) view.findViewById(R.id.item_img_1);
            imageView2 = (ImageView) view.findViewById(R.id.item_img_1);
            imageView3 = (ImageView) view.findViewById(R.id.item_img_1);


            suportMe = (TextView) view.findViewById(R.id.item_suport_me);
            suport = (LinearLayout) view.findViewById(R.id.item_suport);
            suportOrther = (LinearLayout) view.findViewById(R.id.item_suport_other);
        }
    }


    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(View v, int posion);
    }


    private void addView(LinearLayout viewRoot, String[] s) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < s.length; i++) {
            TextView tv = new TextView(context);
            tv.setTextColor(Color.rgb(48, 52, 136));
            tv.setLayoutParams(lp);
            if (i == s.length - 1)
                tv.setText(s[i]);
            else
                tv.setText(s[i] + "、");
            viewRoot.addView(tv);
        }

    }


    private String[] getsuportName(int position, CheckBox checkBox) {
        String[] surposeName = new String[entityList.get(position).getDySupport().size()];
        for (int i = 0; i < surposeName.length; i++) {
            surposeName[i] = entityList.get(position).getDySupport().get(i).getUserName();

            if (surposeName[i].equals(app.getUserInfoBean().getUserName())) {
                checkBox.setChecked(true);
            }
        }
        return surposeName;
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

