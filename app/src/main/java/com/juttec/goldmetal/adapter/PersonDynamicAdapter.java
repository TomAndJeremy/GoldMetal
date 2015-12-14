package com.juttec.goldmetal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import com.juttec.goldmetal.activity.LoginActivity;
import com.juttec.goldmetal.activity.MomentPersonalActivity;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.DyCommentReplyBean;
import com.juttec.goldmetal.bean.DyReplyInfoBean;
import com.juttec.goldmetal.bean.DySupportInfoBean;
import com.juttec.goldmetal.bean.DynamicEntityList;
import com.juttec.goldmetal.bean.PhotoBean;
import com.juttec.goldmetal.customview.CircleImageView;
import com.juttec.goldmetal.customview.NoScrollGridView;
import com.juttec.goldmetal.dialog.MyProgressDialog;
import com.juttec.goldmetal.dialog.ReplyPopupWindow;
import com.juttec.goldmetal.utils.EmojiUtil;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 * <p/>
 * 个人主页界面的  adapter
 * 关注界面的adapter
 */
public class PersonDynamicAdapter extends BaseAdapter {

    private Context mContext;

    private List<DynamicEntityList> mLists;

    private LayoutInflater mInflater;


    private String currentUserId;//个人主页 会用到   个人主页用户的id

    private ReplyPopupWindow popupWindow;

    private MyApplication app;

    private MyProgressDialog dialog;//加载时的 进度框

    //    private boolean isSupported = false;//自己是否点赞  默认为false
    private EmojiUtil readEmoji;

    private DisplayImageOptions options;

    //关注界面的构造方法
    public PersonDynamicAdapter(Context context, List<DynamicEntityList> list) {
        options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();

        app = (MyApplication) context.getApplicationContext();
        mContext = context;
        mLists = list;
        mInflater = LayoutInflater.from(context);
        popupWindow = new ReplyPopupWindow(context);
        dialog = new MyProgressDialog(context);
        readEmoji = new EmojiUtil(context);
        readEmoji.readEmojiIcons();

    }

    //个人主页界面的构造方法
    public PersonDynamicAdapter(Context context, List<DynamicEntityList> list, String userid) {
        options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();

        app = (MyApplication) context.getApplicationContext();
        mContext = context;
        mLists = list;
        mInflater = LayoutInflater.from(context);
        currentUserId = userid;
        popupWindow = new ReplyPopupWindow(context);
        dialog = new MyProgressDialog(context);
        readEmoji = new EmojiUtil(context);
        readEmoji.readEmojiIcons();

    }

    private PersonDynamicAdapter create() {

        return this;
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

        LogUtil.d("PersonDynamicAdapter-----------------getview" + position);
        DynamicEntityList dynamicEntityList;

        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dynamic_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.dynamic_item_user_name);
            holder.time = (TextView) convertView.findViewById(R.id.dynamic_item_user_time);
            holder.headPortrait = (CircleImageView) convertView.findViewById(R.id.dynamic_item_head_portrait);
            holder.content = (TextView) convertView.findViewById(R.id.dynamic_item_content);

            holder.thumb = (ImageButton) convertView.findViewById(R.id.dynamic_item_thumb);
            holder.replyIMB = (ImageButton) convertView.findViewById(R.id.dynamic_item_reply);

            holder.gridView = (NoScrollGridView) convertView.findViewById(R.id.gridview);
            holder.comment = (LinearLayout) convertView.findViewById(R.id.item_comment_content);

            holder.suport = (LinearLayout) convertView.findViewById(R.id.item_support);
            holder.supportName = (LinearLayout) convertView.findViewById(R.id.item_support_name);

            holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.meg_detail_info);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        dynamicEntityList = mLists.get(position);
        holder.name.setText(dynamicEntityList.getUserName());//设置用户名
        holder.time.setText(dynamicEntityList.getAddTime());//时间
        if(dynamicEntityList.getDyContent()!=null){
            holder.content.setText(readEmoji.getEditable(dynamicEntityList.getDyContent()));//正文
        }
        //设置头像
        ImageLoader.getInstance().displayImage(MyApplication.ImgBASEURL + dynamicEntityList.getUserPhoto(), holder.headPortrait, options);


        //展示点赞的数据
        if (dynamicEntityList.getDySupport().size() > 0) {
            holder.suport.setVisibility(View.VISIBLE);
            holder.supportName.removeAllViews();
            holder.thumb.setSelected(false);
            addSuportView(holder.thumb, holder.supportName, mLists.get(position).getDySupport());
        } else {
            holder.suport.setVisibility(View.GONE);
            holder.thumb.setSelected(false);
        }


        //动态添加评论和回复的数据
        //现将之前动态添加的view  remove掉
        holder.comment.removeAllViews();
        addCommentView(holder.comment, position);


        //点赞按钮的点击事件
        holder.thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.thumb.setClickable(false);
                //点赞或  取消赞 接口
                if (isLogin()) {
                    supportOrCancle(position, mLists.get(position).getId(), holder.thumb, holder.supportName);
                } else {
                    holder.thumb.setClickable(false);
                }
            }
        });


        //评论按钮的点击事件  对发动态的人进行评论
        holder.replyIMB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.create().show(((Activity) mContext).getCurrentFocus());
                popupWindow.setHint(0, mLists.get(position).getUserName());
                //发送按钮的点击事件
                popupWindow.setOnClickSendListener(new ReplyPopupWindow.OnClickSendListener() {
                    @Override
                    public void onClickSend(String content) {
                        //评论接口
                        if (isLogin()) {
                            comment(position, mLists.get(position).getId(), content);
                        }
                    }
                });
            }
        });


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
                if (currentUserId != null && mLists.get(position).getUserId().equals(currentUserId)) {
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
     */
    protected void imageBrower(int position, ArrayList<String> urls2) {
        Intent intent = new Intent(mContext, ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        mContext.startActivity(intent);
    }


    static class ViewHolder {
        TextView name;//用户名
        TextView time;//时间
        CircleImageView headPortrait;//头像
        TextView content;//内容


        ImageButton thumb;//点赞按钮
        ImageButton replyIMB;//回复按钮

        LinearLayout suport;//点赞的父布局
        LinearLayout supportName;//放置其他人的点赞
        LinearLayout comment;//回复父布局


        RelativeLayout relativeLayout;//用户名，头像，点击跳转到该用户主页

        //展示图片的 GridView
        NoScrollGridView gridView;
    }


    //展示点赞的人名
    private void addSuportView(ImageButton thumb, LinearLayout viewRoot, List<DySupportInfoBean> supportInfoBeans) {
        LogUtil.d("---------------------展示点赞的人名" + supportInfoBeans.toString());

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < (supportInfoBeans.size() > 10 ? 10 : supportInfoBeans.size()); i++) {
            TextView tv = new TextView(mContext);
            tv.setTextColor(Color.rgb(48, 52, 136));
            tv.setLayoutParams(lp);

            String name = supportInfoBeans.get(i).getUserName();
            String id = supportInfoBeans.get(i).getUserId();

            //如果点赞人中包含自己  将点赞按钮设置为已点赞
            if (app.isLogin() && id.equals(app.getUserInfoBean().getUserId())) {
                thumb.setSelected(true);
            }

            if (i == supportInfoBeans.size() - 1) {
                if (supportInfoBeans.size() > 10) {
                    tv.setText(name + "等" + supportInfoBeans.size() + "人");
                } else {
                    tv.setText(name);//最后一个不加“、”号
                }

            } else {
                tv.setText(name + "、");
            }

            viewRoot.addView(tv);
            viewRoot.setVisibility(View.VISIBLE);

            //判断是否在个人主页  并且是否点击的本人  若是本人则不跳转
            if (currentUserId != null && id.equals(currentUserId)) {
            } else {
                clickName(tv, id, name);
            }
        }
    }


    //点击点赞人 名字 跳转至个人主页  ：如果当前在个人主页  点击自己不跳转
    private void clickName(View v, final String userID, final String userName) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否在个人主页  并且是否点击的本人  若是本人则不跳转
                if (currentUserId != null && userID.equals(currentUserId)) {
                    return;
                }
                startActivity(userID, userName);
            }
        });
    }


    private void startActivity(String userID, String userName) {
        Intent intent = new Intent(mContext, MomentPersonalActivity.class);
        intent.putExtra("userId", userID);
        intent.putExtra("userName", userName.replace(":", ""));
        mContext.startActivity(intent);
    }


    /**
     * 动态添加评论 回复 数据
     */
    private void addCommentView(LinearLayout viewRoot, final int position) {
        for (int i = 0; i < mLists.get(position).getDyCommentReply().size(); i++) {
            TextView commentMsg = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_comment_msg, null);

            //获得评论人的姓名与评论内容并设置显示
            String commentName = mLists.get(position).getDyCommentReply().get(i).getDiscussantName();
            String commentContent = mLists.get(position).getDyCommentReply().get(i).getCommentContent();

            SpannableString string = new SpannableString(commentName+":");
            setPartClick(string, mLists.get(position).getDyCommentReply().get(i).getDiscussantId(), commentName, 0, commentName.length());

            commentMsg.setText(string);
            commentMsg.setMovementMethod(LinkMovementMethod.getInstance());

            commentMsg.append(readEmoji.getEditable(commentContent));

            final int finalI = i;
            //评论的点击事件
            commentMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mLists.get(position).getDyCommentReply().get(finalI).getDiscussantId().equals(app.getUserInfoBean().getUserId())) {
                        popupWindow.create().show(v);
                        popupWindow.setHint(1, mLists.get(position).getDyCommentReply().get(finalI).getDiscussantName());
                        popupWindow.setOnClickSendListener(new ReplyPopupWindow.OnClickSendListener() {
                            @Override
                            public void onClickSend(String content) {
                                reply(position, finalI, content);
                            }
                        });
                    }
                }
            });


            //将一条评论添加到父布局中
            viewRoot.addView(commentMsg);


            //添加评论中的回复
            int size = mLists.get(position).getDyCommentReply().get(i).getDyReply().size();
            final LinearLayout replyRoot = new LinearLayout(mContext);
            replyRoot.setOrientation(LinearLayout.VERTICAL);

            for (int j = 0; j < size; j++) {

                TextView replyMsg = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_comment_msg, null);


                String replyName = mLists.get(position).getDyCommentReply().get(i).getDyReply().get(j).getUserName();
                String repliedName = mLists.get(position).getDyCommentReply().get(i).getDyReply().get(j).getRepliedName();
                String replyContent = mLists.get(position).getDyCommentReply().get(i).getDyReply().get(j).getReplyContent();

                String userId = mLists.get(position).getDyCommentReply().get(finalI).getDyReply().get(j).getUserId();
                String repliedId = mLists.get(position).getDyCommentReply().get(finalI).getDyReply().get(j).getRepliedId();


                replyRoot.addView(replyMsg);

                SpannableString userReply = new SpannableString(replyName);
                setPartClick(userReply, userId, replyName, 0, replyName.length());

                SpannableString userReplied = new SpannableString(repliedName + ":");
                setPartClick(userReplied, repliedId, repliedName, 0, repliedName.length());

                replyMsg.setText(userReply);
                replyMsg.append("回复");
                replyMsg.append(userReplied);

                replyMsg.setMovementMethod(LinkMovementMethod.getInstance());
                replyMsg.append(readEmoji.getEditable(replyContent));

                final int finalJ = j;
                replyMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!app.getUserInfoBean().getUserId().equals(mLists.get(position).getDyCommentReply().get(finalI).getDyReply().get(finalJ).getUserId())) {
                            popupWindow.create().show(v);
                            popupWindow.setHint(1, mLists.get(position).getDyCommentReply().get(finalI).getDyReply().get(finalJ).getUserName());
                            popupWindow.setOnClickSendListener(new ReplyPopupWindow.OnClickSendListener() {
                                @Override
                                public void onClickSend(String content) {
                                    reply(position, finalI, finalJ, content);
                                }
                            });
                        }
                    }


                });
            }
            viewRoot.addView(replyRoot);
        }
    }


    /**
     * 评论接口
     *
     * @param position
     * @param dyId     动态编号
     *                 content  评论内容
     */
    private void comment(final int position, final String dyId, final String content) {

        dialog.builder().setMessage("正在提交~").show();
        RequestParams param = new RequestParams();
        param.addBodyParameter("dyId", dyId);

        param.addBodyParameter("discussantId", app.getUserInfoBean().getUserId());
        param.addBodyParameter("discussantName", app.getUserInfoBean().getUserNickName());
        param.addBodyParameter("commentContent", content);

        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getCommentUrl(), param, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(responseInfo.result.toString());

                    ToastUtil.showShort(mContext, object.getString("promptInfor"));
                    if ("1".equals(object.getString("status"))) {
                        popupWindow.dismiss();
                        DyCommentReplyBean dyCommentReplyBean = new DyCommentReplyBean(
                                app.getUserInfoBean().getUserId(),
                                app.getUserInfoBean().getUserNickName(),
                                content,
                                new ArrayList<DyReplyInfoBean>()
                        );

                        Intent intent = new Intent();
                        intent.setAction("com.juttec.goldmetal.comment");
                        intent.putExtra("dyId", dyId);
                        intent.putExtra("comment", dyCommentReplyBean);

                        mContext.sendBroadcast(intent);
                        mLists.get(position).getDyCommentReply().add(dyCommentReplyBean);
                        notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog.dismiss();
                NetWorkUtils.showMsg(mContext);
            }
        });
    }


    /**
     * 点赞或者取消赞的接口
     *
     * @param position
     * @param dyId
     * @param thumb
     * @param viewRoot
     */
    private void supportOrCancle(final int position, String dyId, final ImageButton thumb, final LinearLayout viewRoot) {
        //dialog.builder().setMessage("").show();
        RequestParams params = new RequestParams();
        params.addBodyParameter("dyId", dyId);
        params.addBodyParameter("userId", app.getUserInfoBean().getUserId());
        params.addBodyParameter("userName", app.getUserInfoBean().getUserNickName());
        params.addBodyParameter("status", thumb.isSelected() ? "1" : "0");//如果已点赞 则取消赞   如果没点赞 则点赞

        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getAddOrCancelSupportUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                //dialog.dismiss();
                Intent intent = new Intent();

                intent.putExtra("dyId", mLists.get(position).getId());
                try {
                    JSONObject object = new JSONObject(responseInfo.result.toString());
                    if ("1".equals(object.getString("status"))) {
                        //将点赞状态取反
                        thumb.setSelected(!(thumb.isSelected()));
                        if (thumb.isSelected()) {
                            //点赞了
                            DySupportInfoBean dySupportInfoBean = new DySupportInfoBean(
                                    app.getUserInfoBean().getUserId(),
                                    app.getUserInfoBean().getUserNickName()
                            );
                            mLists.get(position).getDySupport().add(0, dySupportInfoBean);

                            intent.setAction("com.juttec.goldmetal.addsupport");

                            intent.putExtra("support", dySupportInfoBean);
                            mContext.sendBroadcast(intent);

                        } else {
                            //取消赞了
                            mLists.get(position).getDySupport();
                            intent.setAction("com.juttec.goldmetal.cancelsupport");
                            for (int i = 0; i < mLists.get(position).getDySupport().size(); i++) {
                                if (app.getUserInfoBean().getUserId().equals(mLists.get(position).getDySupport().get(i).getUserId())) {

                                    intent.putExtra("support", mLists.get(position).getDySupport().get(i));

                                    mLists.get(position).getDySupport().remove(i);


                                    mContext.sendBroadcast(intent);
                                }
                            }

                        }


                        notifyDataSetChanged();


                    } else if ("0".equals(object.getString("status"))) {
                        ToastUtil.showShort(mContext, object.getString("promptInfor"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    thumb.setClickable(true);
                }


            }

            @Override
            public void onFailure(HttpException error, String msg) {
                thumb.setClickable(true);
                dialog.dismiss();
                NetWorkUtils.showMsg(mContext);
            }
        });
    }

    private boolean isLogin() {
        if (!app.isLogin()) {
            ToastUtil.showShort(mContext, "请先登录再进行操作");
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            return false;
        }

        if (app.getUserInfoBean().getUserNickName() == null) {
            ToastUtil.showShort(mContext, "请先设置昵称再进行操作");
            return false;
        }
        return true;
    }


    //回复评论
    private void reply(final int position, final int i, final String content) {
        reply(position, i, -1, content);
    }

    //回复回复
    private void reply(final int position, final int i, final int j, final String content) {
        RequestParams param = new RequestParams();

        String dyId = mLists.get(position).getId();
        final String commentId = mLists.get(position).getDyCommentReply().get(i).getId();
        final String userId = app.getUserInfoBean().getUserId();
        final String userName = app.getUserInfoBean().getUserNickName();
        final String repliedId;
        final String repliedName;
        if (j == -1) {
            repliedId = mLists.get(position).getDyCommentReply().get(i).getDiscussantId();
            repliedName = mLists.get(position).getDyCommentReply().get(i).getDiscussantName();
        } else {
            repliedId = mLists.get(position).getDyCommentReply().get(i).getDyReply().get(j).getUserId();
            repliedName = mLists.get(position).getDyCommentReply().get(i).getDyReply().get(j).getUserName();
        }


        param.addBodyParameter("dyId", dyId);
        param.addBodyParameter("commentId", commentId);
        param.addBodyParameter("userId", userId);
        param.addBodyParameter("userName", userName);
        param.addBodyParameter("repliedId", repliedId);
        param.addBodyParameter("repliedName", repliedName);
        param.addBodyParameter("replyContent", content);

        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getReplyUrl(), param, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject object = new JSONObject(responseInfo.result.toString());

                    ToastUtil.showShort(mContext, object.getString("promptInfor"));

                    if ("1".equals(object.getString("status"))) {
                        DyReplyInfoBean dyReplyInfoBean = new DyReplyInfoBean(userId, userName, repliedId, repliedName, content);
                        ArrayList<DyReplyInfoBean> dyReplyInfoBeans = (ArrayList<DyReplyInfoBean>) mLists.get(position).getDyCommentReply().get(i).getDyReply();
                        dyReplyInfoBeans.add(dyReplyInfoBean);
                        mLists.get(position).getDyCommentReply().get(i).setDyReply(dyReplyInfoBeans);
                        notifyDataSetChanged();
                        popupWindow.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                NetWorkUtils.showMsg(mContext);
            }
        });
    }

    /**
     * 设置部分文字的点击事件与颜色
     *
     * @param string
     * @param commentUserId
     * @param commentName
     * @param start
     * @param end
     */
    private void setPartClick(SpannableString string, final String commentUserId, final String commentName, int start, int end) {
        //设置点击事件
        string.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                startActivity(commentUserId, commentName);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.rgb(59, 85, 226));    //设置颜色
                ds.setUnderlineText(false);//删除下划线
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


    }

}



