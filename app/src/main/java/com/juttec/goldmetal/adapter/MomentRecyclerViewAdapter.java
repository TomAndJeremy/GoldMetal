package com.juttec.goldmetal.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.ImagePagerActivity;
import com.juttec.goldmetal.activity.MomentPersonalActivity;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.DyCommentReplyBean;
import com.juttec.goldmetal.bean.DyReplyInfoBean;
import com.juttec.goldmetal.bean.DySupportInfoBean;
import com.juttec.goldmetal.bean.DynamicEntityList;
import com.juttec.goldmetal.bean.PhotoBean;
import com.juttec.goldmetal.customview.CircleImageView;
import com.juttec.goldmetal.customview.NoScrollGridView;
import com.juttec.goldmetal.utils.NetWorkUtils;
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
import java.util.HashMap;
import java.util.Map;

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
        clickToComment(holder.replyIMB, position, entityList.get(position).getId(), null, app.getUserInfoBean().getUserId(), app.getUserInfoBean().getUserNickName(), null, null, holder.comment);

        holder.name.setText(entityList.get(position).getUserName());//设置用户名
        holder.time.setText(entityList.get(position).getAddTime());//时间
        holder.content.setText(unicode2String(entityList.get(position).getDyContent()));//正文


        ImageLoader.getInstance().displayImage(MyApplication.ImgBASEURL + entityList.get(position).getUserPhoto(), holder.headPortrait);
//        addImagesView(holder.images, position);

        //图片的集合
        final ArrayList<PhotoBean> photoBeanList = entityList.get(position).getDyPhoto();
//        if (photoBeanList == null || photoBeanList.size() == 0) { // 没有图片资源就隐藏GridView
//            holder.gridView.setVisibility(View.GONE);
//        } else {
//            holder.gridView.setAdapter(new NoScrollGridAdapter(context, photoBeanList));
//        }
        holder.gridView.setAdapter(new NoScrollGridAdapter(context, photoBeanList));
        // 点击回帖九宫格，查看大图
        holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                ArrayList<String> imageUrls = new ArrayList<String>();
                for (int i = 0; i < photoBeanList.size(); i++) {
                    imageUrls.add(photoBeanList.get(i).getDyPhoto());
                }
                imageBrower(position, imageUrls);
            }
        });

        //清除上次的动态添加的内容内容
        holder.clean();


        addCommentView(holder.comment, position);//添加评论


        final ArrayList<Map<String, String>> supotNames = getsuportGuy(position, holder.thumb);//得到点赞的人名


        if (supotNames.size() > 0) {//如果有人点赞
            holder.suport.setVisibility(View.VISIBLE);
            holder.suportMe.setText("我、");
            addSuportView(holder.suportOrther, supotNames);//添加点赞的name
        } else {
            holder.suport.setVisibility(View.GONE);
            holder.suportMe.setText("我");
        }

        if (holder.thumb.isSelected()) {//如果我点赞了设为可见，否则不可见
            holder.suportMe.setVisibility(View.VISIBLE);

        } else {
            holder.suportMe.setVisibility(View.GONE);
        }


        //点赞的点击事件
        holder.thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                params.addBodyParameter("dyId", entityList.get(position).getId());
                params.addBodyParameter("userId", app.getUserInfoBean().getUserId());
                params.addBodyParameter("userName", app.getUserInfoBean().getUserNickName());


                if (holder.thumb.isSelected()) {
                    holder.thumb.setClickable(false);
                    params.addBodyParameter("status", "1");//取消赞
                    new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getAddOrCancelSupportUrl(), params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {

                            try {
                                JSONObject object = new JSONObject(responseInfo.result.toString());
                                if ("1".equals(object.getString("status"))) {
                                    //我取消赞之后重新设置其他人的name
                                    holder.suportMe.setVisibility(View.GONE);
                                    holder.suportOrther.removeAllViews();
                                    for (int i = 0; i < entityList.get(position).getDySupport().size(); i++) {

                                        if (entityList.get(position).getDySupport().get(i).getUserId().equals(app.getUserInfoBean().getUserId())) {
                                            entityList.get(position).getDySupport().remove(i);
                                            break;
                                        }

                                    }

                                    if (entityList.get(position).getDySupport().size() > 0) {//如果人数不为0
                                        addSuportView(holder.suportOrther, supotNames);
                                    } else {
                                        holder.suport.setVisibility(View.GONE);
                                    }
                                    holder.thumb.setSelected(false);


                                } else if ("0".equals(object.getString("status"))) {
                                    ToastUtil.showShort(context, object.getString("promptInfor"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            holder.thumb.setClickable(true);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            NetWorkUtils.showMsg(context);
                            holder.thumb.setClickable(true);
                        }
                    });
                } else {
                    params.addBodyParameter("status", "0");//点赞
                    new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getAddOrCancelSupportUrl(), params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {

                            try {
                                JSONObject object = new JSONObject(responseInfo.result.toString());
                                if ("1".equals(object.getString("status"))) {


                                    DySupportInfoBean dySupportInfoBean = new DySupportInfoBean();
                                    dySupportInfoBean.setId(object.getString("message1"));
                                    dySupportInfoBean.setUserId(app.getUserInfoBean().getUserId());
                                    dySupportInfoBean.setUserName(app.getUserInfoBean().getUserNickName());
                                    entityList.get(position).getDySupport().add(dySupportInfoBean);


                                    holder.suport.setVisibility(View.VISIBLE);
                                    holder.suportMe.setVisibility(View.VISIBLE);
                                    holder.thumb.setSelected(true);
                                } else if ("0".equals(object.getString("status"))) {
                                    ToastUtil.showShort(context, object.getString("promptInfor"));


                                }

                                holder.thumb.setClickable(true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            NetWorkUtils.showMsg(context);
                            holder.thumb.setClickable(true);
                        }
                    });

                }
            }


        });


        //点击用户头像，用户名
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mOnMyClickListener.onClick(v, position, null, null);
                Intent intent = new Intent(context, MomentPersonalActivity.class);
                intent.putExtra("userId", entityList.get(position).getUserId());
                intent.putExtra("userName", entityList.get(position).getUserName());
                context.startActivity(intent);

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

        ImageButton thumb;//点赞按钮
        ImageButton replyIMB;//回复按钮

        LinearLayout suport;//点赞的父布局
        LinearLayout suportOrther;//放置其他人的点赞
        LinearLayout comment;//回复父布局

        RelativeLayout relativeLayout;//用户名，头像，点击跳转到该用户主页


        //展示图片的 GridView
        NoScrollGridView gridView;

        public ViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.dynamic_item_user_name);
            time = (TextView) view.findViewById(R.id.dynamic_item_user_time);
            headPortrait = (CircleImageView) view.findViewById(R.id.dynamic_item_head_portrait);
            content = (TextView) view.findViewById(R.id.dynamic_item_content);
            thumb = (ImageButton) view.findViewById(R.id.dynamic_item_thumb);
            replyIMB = (ImageButton) view.findViewById(R.id.dynamic_item_reply);

            gridView = (NoScrollGridView) view.findViewById(R.id.gridview);

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



        }
    }


    //添加点赞的人名
    private void addSuportView(LinearLayout viewRoot, ArrayList<Map<String, String>> s) {

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < s.size(); i++) {
            TextView tv = new TextView(context);
            tv.setTextColor(Color.rgb(48, 52, 136));
            tv.setLayoutParams(lp);

            String name = s.get(i).get("name");
            String id = s.get(i).get("id");
            if (i == s.size() - 1)
                tv.setText(name);//最后一个不加“、”号
            else
                tv.setText(s.get(i).get("name") + "、");
            viewRoot.addView(tv);
            viewRoot.setVisibility(View.VISIBLE);
            clickName(tv, id, name);
        }

    }


    //得到点赞的人
    private ArrayList<Map<String, String>> getsuportGuy(int position, ImageButton thumb) {
        ArrayList<Map<String, String>> surposes = new ArrayList<Map<String, String>>();

        for (int i = 0; i < entityList.get(position).getDySupport().size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            String name = entityList.get(position).getDySupport().get(i).getUserName();
            //判断返回的点赞名单中是否有自己
            if (name.equals(app.getUserInfoBean().getUserNickName())) {
                thumb.setSelected(true);
                continue;
            }
            map.put("name", name);
            map.put("id", entityList.get(position).getDySupport().get(i).getUserId());
            surposes.add(map);
        }

        return surposes;
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
    public void addReplyView(int position, LinearLayout viewRooot, String replyName, String repliedName, Editable content, String commentId, String repliedId) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        DyCommentReplyBean dyCommentReplyBean = new DyCommentReplyBean();
        dyCommentReplyBean.setId(commentId);
        dyCommentReplyBean.setDiscussantId(app.getUserInfoBean().getUserId());
        dyCommentReplyBean.setDiscussantName(app.getUserInfoBean().getUserName());
        dyCommentReplyBean.setCommentContent(content.toString());




        View commentMsg = LayoutInflater.from(context).inflate(R.layout.item_comment_msg, null);
        commentMsg.setLayoutParams(lp);


        TextView tvReplyName = (TextView) commentMsg.findViewById(R.id.reply_name);//回复人昵称
        TextView hint = (TextView) commentMsg.findViewById(R.id.hint_reply);//“回复”
        TextView tvRepliedName = (TextView) commentMsg.findViewById(R.id.comment_name);//被恢复昵称，可为空
        TextView tvReplyContent = (TextView) commentMsg.findViewById(R.id.comment_content);//回复内容


        tvReplyContent.setText(content);


        //判断被回复人是否为空
        if (repliedName != null) {



            tvReplyName.setText(replyName);
            hint.setVisibility(View.VISIBLE);
            tvRepliedName.setVisibility(View.VISIBLE);

            tvRepliedName.setText(repliedName + ":");
        } else {

            DyReplyInfoBean dyReplyInfoBean = new DyReplyInfoBean();
            dyReplyInfoBean.setUserId(app.getUserInfoBean().getUserId());
            dyReplyInfoBean.setUserName(app.getUserInfoBean().getUserNickName());
            dyReplyInfoBean.setRepliedId(repliedId);
//            dyReplyInfoBean.setRepliedName();
//            dyCommentReplyBean.setDyReply();


            tvReplyName.setText(replyName + ":");
            hint.setVisibility(View.GONE);
            tvRepliedName.setVisibility(View.GONE);


        }
        viewRooot.addView(commentMsg);
        entityList.get(position).getDyCommentReply().add(dyCommentReplyBean);

    }

    //添加评论布局
    private void addCommentView(LinearLayout viewRoot, final int position) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        for (int i = 0; i < entityList.get(position).getDyCommentReply().size(); i++) {


            View commentMsg = LayoutInflater.from(context).inflate(R.layout.item_comment_msg, null);
            commentMsg.setLayoutParams(lp);

            final TextView tvCommentName = (TextView) commentMsg.findViewById(R.id.comment_name);
            TextView tvCommentContent = (TextView) commentMsg.findViewById(R.id.comment_content);

            String commentName = entityList.get(position).getDyCommentReply().get(i).getDiscussantName() + ":";//
            String commentContent = entityList.get(position).getDyCommentReply().get(i).getCommentContent();

            tvCommentName.setText(commentName);
            tvCommentContent.setText(commentContent);


            //点击昵称跳转到用户个人界面
            clickName(tvCommentName, entityList.get(position).getDyCommentReply().get(i).getDiscussantId(), commentName);


            viewRoot.addView(commentMsg);


            int size = entityList.get(position).getDyCommentReply().get(i).getDyReply().size();
            final LinearLayout replyRoot = new LinearLayout(context);
            replyRoot.setOrientation(LinearLayout.VERTICAL);

            //点击评论，回复该评论
            clickToComment(commentMsg, position, entityList.get(position).getId(), entityList.get(position).getDyCommentReply().get(i).getId(), app.getUserInfoBean().getUserId(), app.getUserInfoBean().getUserNickName(), entityList.get(position).getDyCommentReply().get(i).getDiscussantId(), entityList.get(position).getDyCommentReply().get(i).getDiscussantName(), replyRoot);


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

                clickToComment(replyMsg, position, entityList.get(position).getId(), entityList.get(position).getDyCommentReply().get(i).getId(), app.getUserInfoBean().getUserId(), app.getUserInfoBean().getUserNickName(), entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getUserId(), entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getUserName(), replyRoot);

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


    /**
     * 打开图片查看器
     */
    protected void imageBrower(int position, ArrayList<String> urls2) {
        Intent intent = new Intent(context, ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        context.startActivity(intent);
    }


    public void setOnMyClickListener(OnMyClickListener mOnMyClickListener) {
        this.mOnMyClickListener = mOnMyClickListener;
    }

    public interface OnMyClickListener {
        /**
         * @param v           点击的view
         * @param position
         * @param dyId
         * @param commentId
         * @param userId
         * @param userName
         * @param repliedId   点击的position
         * @param repliedName 被恢复人的昵称
         * @param vr          父布局
         */
        void onClick(View v, int position, String dyId, String commentId, String userId, String userName, String repliedId, String repliedName, LinearLayout vr);
    }


    //点击姓名跳转
    private void clickName(View v, final String userID, final String userName) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MomentPersonalActivity.class);
                intent.putExtra("userId", userID);
                intent.putExtra("userName", userName);
                context.startActivity(intent);
            }
        });
    }

    /**
     * @param v           点击的view
     * @param position    点击的位置
     * @param dyId        动态编号
     * @param commentId   评论编号
     * @param userId      回复人ID
     * @param userName    回复人昵称
     * @param repliedId   被回复人ID
     * @param repliedName 被回复人昵称
     * @param viewRoot
     */
    private void clickToComment(View v, final int position, final String dyId, final String commentId, final String userId, final String userName, final String repliedId, final String repliedName, final LinearLayout viewRoot) {

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnMyClickListener.onClick(v, position, dyId, commentId, userId, userName, repliedId, repliedName, viewRoot);
            }
        });
    }


}

