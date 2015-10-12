package com.juttec.goldmetal.adapter;

import android.app.Activity;
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
import com.juttec.goldmetal.bean.DyReplyInfoBean;
import com.juttec.goldmetal.customview.listview.NoScrollListView;
import com.juttec.goldmetal.dialog.MyProgressDialog;
import com.juttec.goldmetal.dialog.ReplyPopupWindow;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

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

    private MyProgressDialog dialog;//加载时的 进度框

    private ReplyPopupWindow popupWindow;

    private String dyId;//所在的 动态的动态id  调用回复接口的时候用到

    private ReplyAdapter replyAdapter;


    //关注界面  的构造方法
    public CommentAdapter(Context context, List<DyCommentReplyBean> list,String dyId){
        app = (MyApplication) context.getApplicationContext();
        mContext = context;
        mLists = list;
        this.dyId = dyId;
        mInflater = LayoutInflater.from(context);
        popupWindow = new ReplyPopupWindow(context);
        dialog = new MyProgressDialog(context);
    }

    //个人主页 的构造方法
    public CommentAdapter(Context context, List<DyCommentReplyBean> list,String dyId,String userid){
        app = (MyApplication) context.getApplicationContext();
        mContext = context;
        mLists = list;
        this.dyId = dyId;
        mInflater = LayoutInflater.from(context);
        currentUserId = userid;
        popupWindow = new ReplyPopupWindow(context);
        dialog = new MyProgressDialog(context);
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
            replyAdapter = new ReplyAdapter(mContext, dyCommentReplyBean.getDyReply(),currentUserId);
            holder.replyListview.setAdapter(replyAdapter);
        }else{
            replyAdapter = new ReplyAdapter(mContext, dyCommentReplyBean.getDyReply());
            holder.replyListview.setAdapter(replyAdapter);
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
                //判断评论的是否是自己  ： 不能回复自己
                if(checkSelf(mLists.get(position).getDiscussantId())){
                       return;
                }

                popupWindow.create().show(((Activity) mContext).getCurrentFocus());
                popupWindow.setHint(1, mLists.get(position).getDiscussantName());
                //发送按钮的点击事件
                popupWindow.setOnClickSendListener(new ReplyPopupWindow.OnClickSendListener() {
                    @Override
                    public void onClickSend(String content) {
                        //回复接口
                        reply(position, dyId, mLists.get(position).getId(), mLists.get(position).getDiscussantId(), mLists.get(position).getDiscussantName(), content);
                    }
                });

            }
        });



        //回复的点击事件
        holder.replyListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
                //判断评论的是否是自己  ： 不能回复自己
                if(checkSelf(mLists.get(position).getDyReply().get(pos).getUserId())){
                    return;
                }

                popupWindow.create().show(((Activity)mContext).getCurrentFocus());
                popupWindow.setHint(1,mLists.get(position).getDyReply().get(pos).getUserName());
                //发送按钮的点击事件
                popupWindow.setOnClickSendListener(new ReplyPopupWindow.OnClickSendListener() {
                    @Override
                    public void onClickSend(String content) {
                        //回复接口
                        reply(position, dyId, mLists.get(position).getDyReply().get(pos).getId(),
                                mLists.get(position).getDyReply().get(pos).getUserId(),
                                mLists.get(position).getDyReply().get(pos).getUserName(),
                                content);
                    }
                });
            }
        });



        return convertView;
    }


    /**
     * 检查回复的  是否是自己  ：不能回复自己
     * true:是自己   false:不是自己
     */
   private boolean checkSelf(String id){
        if(app.getUserInfoBean().getUserId().equals(id)){
            return true;
        }
       return false;
    }



    static class ViewHolder{
        TextView tv_comment;//评论的人名
        TextView tv_content;//评论的内容
        NoScrollListView replyListview;//用于填充回复的数据
        LinearLayout ll_layout;//评论内容所占的一行
    }


    /**
     *
     * @param position
     * @param dyId 动态编号
     * @param commentId 评论编号ID
     * @param repliedId  被回复人id
     * @param repliedName  被回复人昵称
     * @param content  回复内容
     */
    private void reply(final int position, String dyId, String commentId, final String repliedId, final String repliedName, final String content) {
        dialog.builder().setMessage("正在提交~").show();
        RequestParams param = new RequestParams();
        param.addBodyParameter("dyId", dyId);
        param.addBodyParameter("commentId", commentId);
        param.addBodyParameter("userId", app.getUserInfoBean().getUserId());
        param.addBodyParameter("userName", app.getUserInfoBean().getUserNickName());
        param.addBodyParameter("repliedId", repliedId);
        param.addBodyParameter("repliedName", repliedName);
        param.addBodyParameter("replyContent", content);

        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getReplyUrl(), param, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(responseInfo.result.toString());

                    ToastUtil.showShort(mContext, object.getString("promptInfor"));
                    if ("1".equals(object.getString("status"))) {
                        popupWindow.dismiss();

                        DyReplyInfoBean dyReplyInfoBean = new DyReplyInfoBean(
                                app.getUserInfoBean().getUserId(),
                                app.getUserInfoBean().getUserNickName(),
                                repliedId,repliedName,content
                        );

                        mLists.get(position).getDyReply().add(dyReplyInfoBean);
                        replyAdapter.notifyDataSetChanged();
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











//    //评论或者 回复的 接口回调
//    public interface OnCommentAndReplyClickListener{
//        void onCommentAndReplyClick();
//    }
//
//    private OnCommentAndReplyClickListener mCommentAndReplyClickListener;
//
//    public void setOnLoadNextListener(OnCommentAndReplyClickListener listener) {
//        mCommentAndReplyClickListener = listener;
//    }
}



