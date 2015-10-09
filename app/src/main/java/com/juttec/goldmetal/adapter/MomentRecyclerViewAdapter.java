package com.juttec.goldmetal.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.MomentPersonalActivity;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.DynamicEntityList;
import com.juttec.goldmetal.customview.CircleImageView;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.juttec.goldmetal.utils.SnackbarUtil;
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
 * Created by Jeremy on 2015/9/14.
 */
public class MomentRecyclerViewAdapter extends RecyclerView.Adapter<MomentRecyclerViewAdapter.ViewHolder> {

    private OnMyClickListener mOnMyClickListener;

    // 数据集
    ArrayList<DynamicEntityList> entityList;
    Context context;
    MyApplication app;


    //初始化
    public MomentRecyclerViewAdapter(ArrayList<DynamicEntityList> entityList, Context context, MyApplication app) {
        super();
        this.entityList = entityList;
        LogUtil.e(entityList.get(0).toString());
        this.context = context;
        this.app = app;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.dynamic_item, parent, false);
        // 创建一个ViewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        //点击回复按钮的事件回调
        holder.replyIMB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnMyClickListener.onClick(v, position, entityList.get(position).getUserName(), holder.comment);
            }
        });


        holder.name.setText(entityList.get(position).getUserName());//设置用户名
        holder.time.setText(entityList.get(position).getAddTime());//时间
        holder.content.setText(unicode2String(entityList.get(position).getDyContent()));//正文

        //addImagesView(holder.images, position);


        //清楚上次的显示内容
        holder.clean();


        addCommentView(holder.comment, position);//添加评论

        final ArrayList<String> supotNames = getsuportName(position, holder.thumb);//得到点赞的人名


        if (entityList.get(position).getDySupport().size() > 0) {//如果有人点赞
            holder.suport.setVisibility(View.VISIBLE);
            holder.suportMe.setText("我、");
            addSuportView(holder.suportOrther, supotNames);//添加点赞的name
        } else {
            holder.suport.setVisibility(View.GONE);
            holder.suportMe.setText("我");
        }

        if (holder.thumb.isChecked()) {//如果我点赞了设为可见，否则不可见
            holder.suportMe.setVisibility(View.VISIBLE);

        } else {
            holder.suportMe.setVisibility(View.GONE);
        }


        //点赞的点击事件
        holder.thumb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RequestParams params = new RequestParams();
                params.addBodyParameter("dyId", entityList.get(position).getId());
                params.addBodyParameter("userId", app.getUserInfoBean().getUserId());
                params.addBodyParameter("userName", app.getUserInfoBean().getUserNickName());


                LogUtil.e(entityList.get(position).getId()+"  "+app.getUserInfoBean().getUserId()+" "+app.getUserInfoBean().getUserNickName());
                if (isChecked) {
                    params.addBodyParameter("status", "0");//点赞
                    new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getAddOrCancelAttentionUrl(),params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {

                            try {
                                JSONObject object = new JSONObject(responseInfo.result.toString());
                                if ("1".equals(object.getString("status"))) {
                                    holder.suport.setVisibility(View.VISIBLE);
                                    holder.suportMe.setVisibility(View.VISIBLE);
                                } else if ("0".equals(object.getString("status"))) {
                                    ToastUtil.showShort(context, object.getString("promptInfor"));
                                    LogUtil.e("promptInfor  "+object.getString("promptInfor"));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            NetWorkUtils.showMsg(context);
                        }
                    });

                } else {


                    params.addBodyParameter("status", "1");//取消赞
                    new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getAddOrCancelAttentionUrl(),params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {

                            try {
                                JSONObject object = new JSONObject(responseInfo.result.toString());
                                if ("1".equals(object.getString("status"))) {
                                    //我取消咱之后重新设置其他人的name
                                    holder.suportMe.setVisibility(View.GONE);
                                    holder.suportOrther.removeAllViews();
                                    if (entityList.get(position).getDySupport().size() > 0) {//如果人数不为0
                                        addSuportView(holder.suportOrther, supotNames);
                                    } else {
                                        holder.suport.setVisibility(View.GONE);
                                    }

                                } else if ("0".equals(object.getString("status"))) {
                                    ToastUtil.showShort(context, object.getString("promptInfor"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            NetWorkUtils.showMsg(context);
                        }
                    });
                }
            }
        });


        //点击用户头像，用户名
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnMyClickListener.onClick(v, position, null, null);
            }
        });

    }

    @Override
    public int getItemCount() {
        return entityList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView name;//用户名
        TextView time;//时间
        CircleImageView headPortrait;//头像
        TextView content;//内容
        TextView suportMe;//点赞（我）

        CheckBox thumb;//点赞按钮
        ImageButton replyIMB;//回复按钮

        LinearLayout suport;//点赞的父布局
        LinearLayout suportOrther;//放置其他人的点赞
        LinearLayout comment;//回复父布局
        LinearLayout images;//图片父布局

        RelativeLayout relativeLayout;//用户名，头像，点击跳转到该用户主页


        //图片
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;


        public ViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.dynamic_item_user_name);
            time = (TextView) view.findViewById(R.id.dynamic_item_user_time);
            headPortrait = (CircleImageView) view.findViewById(R.id.dynamic_item_head_portrait);
            content = (TextView) view.findViewById(R.id.dynamic_item_content);
            thumb = (CheckBox) view.findViewById(R.id.dynamic_item_thumb);
            replyIMB = (ImageButton) view.findViewById(R.id.dynamic_item_reply);

            imageView1 = (ImageView) view.findViewById(R.id.item_img_1);
            imageView2 = (ImageView) view.findViewById(R.id.item_img_1);
            imageView3 = (ImageView) view.findViewById(R.id.item_img_1);


            images = (LinearLayout) view.findViewById(R.id.item_imgs);

            suport = (LinearLayout) view.findViewById(R.id.item_suport);
            suportMe = (TextView) view.findViewById(R.id.item_suport_me);
            suportOrther = (LinearLayout) view.findViewById(R.id.item_suport_other);

            comment = (LinearLayout) view.findViewById(R.id.item_comment_content);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.meg_detail_info);

        }

        private void clean() {

            if (suportOrther.getChildCount() > 0)
                suportOrther.removeAllViews();
            if (comment.getChildCount() > 0)
                comment.removeAllViews();
            if (images.getChildCount() > 0)
                images.removeAllViews();

        }
    }


    //添加点赞的人名
    private void addSuportView(LinearLayout viewRoot, ArrayList<String> s) {

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < s.size(); i++) {
            TextView tv = new TextView(context);
            tv.setTextColor(Color.rgb(48, 52, 136));
            tv.setLayoutParams(lp);
            if (i == s.size() - 1)
                tv.setText(s.get(i));//最后一个不加“、”号
            else
                tv.setText(s.get(i) + "、");
            viewRoot.addView(tv);
            viewRoot.setVisibility(View.VISIBLE);
        }

    }


    private ArrayList<String> getsuportName(int position, CheckBox checkBox) {
        ArrayList<String> surposeName = new ArrayList<String>();
        for (int i = 0; i < entityList.get(position).getDySupport().size(); i++) {

            //判断返回的点赞名单中是否有自己
            if (entityList.get(position).getDySupport().get(i).getUserName().equals(app.getUserInfoBean().getUserNickName())) {
                checkBox.setChecked(true);
                entityList.get(position).getDySupport().remove(i);

            } else {
                surposeName.add(entityList.get(position).getDySupport().get(i).getUserName());
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


    //添加回复
    public void addReplyView(LinearLayout viewRooot, String replyName, String repliedName, Editable content) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        View commentMsg = LayoutInflater.from(context).inflate(R.layout.item_comment_msg, null);
        commentMsg.setLayoutParams(lp);


        TextView tvReplyName = (TextView) commentMsg.findViewById(R.id.reply_name);//回复人昵称
        TextView hint = (TextView) commentMsg.findViewById(R.id.hint_reply);//“回复”
        TextView tvRepliedName = (TextView) commentMsg.findViewById(R.id.comment_name);//被恢复昵称，可为空
        TextView tvReplyContent = (TextView) commentMsg.findViewById(R.id.comment_content);//回复内容

        tvReplyName.setText(replyName);
        tvRepliedName.setText(repliedName + ":");
        tvReplyContent.setText(content);


        //判断被回复人是否为空
        if (repliedName != null) {
            hint.setVisibility(View.VISIBLE);
            tvRepliedName.setVisibility(View.VISIBLE);
        } else {
            hint.setVisibility(View.GONE);
            tvRepliedName.setVisibility(View.GONE);
        }


        viewRooot.addView(commentMsg);
    }

    //添加评论布局
    private void addCommentView(LinearLayout viewRoot, final int position) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        for (int i = 0; i < entityList.get(position).getDyCommentReply().size(); i++) {


            View commentMsg = LayoutInflater.from(context).inflate(R.layout.item_comment_msg, null);
            commentMsg.setLayoutParams(lp);

            final TextView tvCommentName = (TextView) commentMsg.findViewById(R.id.comment_name);
            TextView tvCommentContent = (TextView) commentMsg.findViewById(R.id.comment_content);

            tvCommentName.setText(entityList.get(position).getDyCommentReply().get(i).getDiscussantName() + ":");
            tvCommentContent.setText(entityList.get(position).getDyCommentReply().get(i).getCommentContent());


            tvCommentName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MomentPersonalActivity.class);
                    context.startActivity(intent);
                }
            });
            viewRoot.addView(commentMsg);


            int size = entityList.get(position).getDyCommentReply().get(i).getDyReply().size();
            final LinearLayout replyRoot = new LinearLayout(context);
            replyRoot.setOrientation(LinearLayout.VERTICAL);

            tvCommentContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // addReplyView(replyRoot, app.getUserInfoBean().getUserNickName(), tvCommentName.getText().toString(), );
                }
            });

            for (int j = 0; j < size; j++) {


                View replyMsg = LayoutInflater.from(context).inflate(R.layout.item_comment_msg, null);
                replyMsg.setLayoutParams(lp);

                TextView tvReplyName = (TextView) replyMsg.findViewById(R.id.reply_name);
                TextView hint = (TextView) replyMsg.findViewById(R.id.hint_reply);
                TextView tvReplyedName = (TextView) replyMsg.findViewById(R.id.comment_name);
                TextView tvReplyContent = (TextView) replyMsg.findViewById(R.id.comment_content);
                tvReplyName.setVisibility(View.VISIBLE);
                hint.setVisibility(View.VISIBLE);

                tvReplyName.setText(entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getUserName());
                tvReplyedName.setText(entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getRepliedName() + ":");
                tvReplyContent.setText(entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getReplyContent());
                replyRoot.addView(replyMsg);
            }
            viewRoot.addView(replyRoot);
        }

    }


    private void addImagesView(LinearLayout viewRoot, int position) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < entityList.get(position).getDyPhoto().size(); i++) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(lp);
            ImageLoader.getInstance().displayImage(app.getImgBaseUrl() + entityList.get(position).getDyPhoto().get(i), imageView);
            viewRoot.addView(imageView);
        }
    }


    //item点击事件回调
    public void setOnMyClickListener(OnMyClickListener mOnMyClickListener) {
        this.mOnMyClickListener = mOnMyClickListener;
    }

    public interface OnMyClickListener {
        /**
         * @param v           点击的view
         * @param posion      点击的position
         * @param repliedName 被恢复人的昵称
         * @param vr          父布局
         */
        void onClick(View v, int posion, String repliedName, LinearLayout vr);
    }


}

