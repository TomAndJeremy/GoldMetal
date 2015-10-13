package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.adapter.MessageReplyAdapter;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.DyReplyInfoBean;
import com.juttec.goldmetal.bean.MessageBean;
import com.juttec.goldmetal.customview.listview.NoScrollListView;
import com.juttec.goldmetal.dialog.MyProgressDialog;
import com.juttec.goldmetal.dialog.ReplyPopupWindow;
import com.juttec.goldmetal.utils.LogUtil;
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
import java.util.List;

/**
 * 消息详情界面
 */

public class MessageDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_photo;//头像

    private TextView tv_name;//用户昵称

    private TextView tv_tiem;//时间

    private TextView tv_content;//内容

    private ImageButton btn_reply;//回复按钮

    private MyApplication app ;

    private MessageBean messageBean;//消息实体

    private NoScrollListView mListView;

    private MyProgressDialog dialog;//加载时的 进度框

    private ReplyPopupWindow popupWindow;

    private List<DyReplyInfoBean> mLists;

    private MessageReplyAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        app = (MyApplication) getApplication();
        dialog = new MyProgressDialog(this);
        popupWindow = new ReplyPopupWindow(this);

        mLists = new ArrayList<DyReplyInfoBean>();
        //初始化控件
        initView();


        //调接口 获取消息详情数据
        getMessageDetailData(getIntent().getStringExtra("msgType"),getIntent().getStringExtra("msgDetailsId"));

    }

    private void  initView(){
        RelativeLayout head = (RelativeLayout) this.findViewById(R.id.head_layout);
        TextView rightText = (TextView) head.findViewById(R.id.right_text);
        rightText.setOnClickListener(this);

        iv_photo = (ImageView) findViewById(R.id.msg_detail_head_portrait);
        tv_name = (TextView) findViewById(R.id.msg_detail_user_name);
        tv_tiem = (TextView) findViewById(R.id.tv_time);
        tv_content = (TextView) findViewById(R.id.msg_detail_content);

        btn_reply = (ImageButton) findViewById(R.id.ib_reply);
        btn_reply.setOnClickListener(this);
        mListView = (NoScrollListView) findViewById(R.id.reply_listview);
    }


    private void initData(){
        ImageLoader.getInstance().displayImage(app.getImgBaseUrl() + messageBean.getMsgUserPhoto(), iv_photo);
        tv_name.setText(messageBean.getMsgReplyerName());
        tv_tiem.setText(messageBean.getMsgAddTime());
        tv_content.setText(messageBean.getMsgBriefContent());
    }



    /**
     * 调接口  获取消息详情数据
     */
    private void getMessageDetailData(String msgType,String msgDetailsId){
        dialog.builder().setMessage("正在努力加载~").show();
        RequestParams params = new RequestParams();
        params.addBodyParameter("msgType", msgType);
        params.addBodyParameter("msgDetailsId", msgDetailsId);

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.getGetMsgDetailsUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog.dismiss();
                LogUtil.d("getMessageDetailData--------" + responseInfo.result.toString());

                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    String status = object.getString("status");
                    String promptInfor = object.getString("promptInfor");
                    if ("1".equals(status)) {
                        JSONObject obj = object.getJSONObject("entityList");
                        messageBean = new MessageBean();
                        messageBean.setMsgUserPhoto(obj.getString("messagerPhoto"));
                        messageBean.setMsgDyId(obj.getString("dyId"));
                        messageBean.setMsgAddTime(obj.getString("addTime"));
                        //评论
                        messageBean.setMsgCommentId(obj.getString("commentId"));
                        messageBean.setMsgReplyerId(obj.getString("messagerId"));
                        messageBean.setMsgReplyerName(obj.getString("messagerName"));
                        messageBean.setMsgBriefContent(obj.getString("msgContent"));


                        //填充数据
                        initData();


                    } else {
                        ToastUtil.showShort(MessageDetailActivity.this, promptInfor);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog.dismiss();
                NetWorkUtils.showMsg(MessageDetailActivity.this);
            }
        });
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.right_text:
                startActivity(new Intent(MessageDetailActivity.this, PublishTopicActivity.class));
                break;

            case R.id.ib_reply:
                //回复按钮的点击事件
                popupWindow.create().show(v);
                popupWindow.setHint(1, messageBean.getMsgReplyerName());
                //发送按钮的点击事件
                popupWindow.setOnClickSendListener(new ReplyPopupWindow.OnClickSendListener() {
                    @Override
                    public void onClickSend(String content) {
                        //回复接口
                        reply( content);
                    }
                });


                break;
        }

    }



    /**
     *
     * @param content  回复内容
     */
    private void reply( final String content) {
        dialog.builder().setMessage("正在提交~").show();
        RequestParams param = new RequestParams();
        param.addBodyParameter("dyId", messageBean.getMsgDyId());
        param.addBodyParameter("commentId", messageBean.getMsgCommentId());
        param.addBodyParameter("userId", app.getUserInfoBean().getUserId());
        param.addBodyParameter("userName", app.getUserInfoBean().getUserNickName());
        param.addBodyParameter("repliedId", messageBean.getMsgReplyerId());
        param.addBodyParameter("repliedName", messageBean.getMsgReplyerName());
        param.addBodyParameter("replyContent", content);

        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getReplyUrl(), param, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(responseInfo.result.toString());

                    ToastUtil.showShort(MessageDetailActivity.this, object.getString("promptInfor"));
                    if ("1".equals(object.getString("status"))) {
                        popupWindow.dismiss();

                        DyReplyInfoBean dyReplyInfoBean = new DyReplyInfoBean(
                                app.getUserInfoBean().getUserId(),
                                app.getUserInfoBean().getUserNickName(),
                                messageBean.getMsgReplyerId(),
                                messageBean.getMsgReplyerName(),
                                content
                        );

                        mLists.add(dyReplyInfoBean);
                        if(mAdapter==null){
                            mAdapter = new MessageReplyAdapter(MessageDetailActivity.this,mLists);
                            mListView.setAdapter(mAdapter);
                        }else{
                            mAdapter.notifyDataSetChanged();
                        }



                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog.dismiss();
                NetWorkUtils.showMsg(MessageDetailActivity.this);
            }
        });
    }



}
