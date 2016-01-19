package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.adapter.MessageAdapter;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.MessageBean;
import com.juttec.goldmetal.customview.listview.LoadMoreListView;
import com.juttec.goldmetal.customview.listview.LoadingFooter;
import com.juttec.goldmetal.dialog.MyProgressDialog;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 交易圈   消息界面
 */

public class MessageActivity extends AppCompatActivity implements View.OnClickListener ,SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, LoadMoreListView.OnLoadNextListener {

    // 加载更多
    public static final int MSG_LOAD_MORE = 0;
    // 刷新
    public static final int MSG_REFRESH = 1;
    // 第一次加载
    public static final int MSG_LOAD_FIRST = 2;


    private SwipeRefreshLayout swipeLayout;
    private LoadMoreListView mListView;

    private MessageAdapter mAdapter;

    private List<MessageBean> messageBeanList;

    private MyProgressDialog dialog;//加载时的 进度框

    private MyApplication app ;

    private int pageIndex = 1;//请求数据的页数   默认为第一页数据
    private int totalPage;//消息的总页数

    private PopupWindow pw;//删除

    private TextView tv_delete;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        app = (MyApplication) getApplication();
        dialog = new MyProgressDialog(this);

        messageBeanList = new ArrayList<MessageBean>();
        //初始化控件
        initView();


        //调接口 获取 消息数据
        getMessageData(MSG_LOAD_FIRST);
    }


    //初始化控件
    private void initView(){
        RelativeLayout head = (RelativeLayout) this.findViewById(R.id.head_layout);
        TextView rightText = (TextView) head.findViewById(R.id.right_text);
        rightText.setOnClickListener(this);


        tv_delete = new TextView(this);
        tv_delete.setText("删除");
        tv_delete.setTextColor(Color.WHITE);
        tv_delete.setTextSize(18);
        tv_delete.setGravity(Gravity.CENTER);


        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mListView = (LoadMoreListView) findViewById(R.id.listview);
        //短按事件的监听
        mListView.setOnItemClickListener(this);
        //长按事件的监听
        mListView.setOnItemLongClickListener(this);
        //加载更多的监听
        mListView.setOnLoadNextListener(this);



        // 顶部刷新的样式
        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);


        swipeLayout.setOnRefreshListener(this);

    }



    //填充数据
    private void initData(int flag){
        if(mAdapter == null){
            mAdapter = new MessageAdapter(this,messageBeanList);
            mListView.setAdapter(mAdapter);
        }else{

            mAdapter.notifyDataSetChanged();
        }

        if(flag == MSG_REFRESH){
            //刷新
            swipeLayout.setRefreshing(false);
            mListView.smoothScrollToPosition(0);
            mAdapter.notifyDataSetChanged();
        }
        mListView.setState(LoadingFooter.State.Idle);

    }


    //刷新的方法
    @Override
    public void onRefresh() {
        pageIndex = 1;
        getMessageData(MSG_REFRESH);

    }


    //加载更多
    @Override
    public void onLoadNext() {

        if(pageIndex>=totalPage){
            //没有更多数据了
            mListView.setState(LoadingFooter.State.TheEnd);
            return;
        }
        //页数+1
        pageIndex++;
        getMessageData(MSG_LOAD_MORE);
    }



    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_text:
                startActivity(new Intent(MessageActivity.this, PublishTopicActivity.class));
                break;
        }
    }




    @Override
    protected void onStop() {
        super.onStop();
        //停止底部的  加载更多动画
        mListView.stopFooterAnimition();
    }



    /**
     * 调接口  获取消息数据
     * flag:刷新    加载更多
     */
    private void getMessageData(final int flag){
        if(flag==MSG_LOAD_FIRST){
            dialog.builder().setMessage("正在努力加载~").show();
        }

        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", app.getUserInfoBean().getUserId());
        params.addBodyParameter("pageIndex", pageIndex + "");

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.getGetMyMessageUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                swipeLayout.setRefreshing(false);
                dialog.dismiss();
                LogUtil.d("getMessageData--------" + responseInfo.result.toString());

                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    String status = object.getString("status");
                    String promptInfor = object.getString("promptInfor");

                    if ("1".equals(status)) {
                        totalPage =  Integer.parseInt(object.getString("message1"));
                        if(totalPage ==0){
                            LogUtil.d("-----------------------当前没有数据");
                          ToastUtil.showShort(MessageActivity.this,"您还没有任何消息");
                            return;
                        }
                        JSONArray msgArray = object.getJSONArray("entityList");
                        if(flag==MSG_REFRESH){
                            //刷新
                            messageBeanList.clear();
                        }
                        for (int i = 0; i < msgArray.length(); i++) {
                            MessageBean messageBean = new MessageBean();
                            JSONObject obj = (JSONObject) msgArray.get(i);
                            messageBean.setMsgId(obj.getString("id"));
                            messageBean.setMsgUserId(obj.getString("userId"));
                            messageBean.setMsgUserPhoto(obj.getString("userPhoto"));
                            messageBean.setMsgDyId(obj.getString("dyId"));
                            messageBean.setMsgAddTime(obj.getString("addTime"));
                            messageBean.setMsgType(obj.getString("msgType"));
                            if (obj.getString("msgType").equals("1")) {
                                //评论
                                messageBean.setMsgCommentId(obj.getString("commentId"));
                            } else if (obj.getString("msgType").equals("2")) {
                                //回复
                                messageBean.setMsgReplyId(obj.getString("replyId"));
                            }

                            messageBean.setMsgReplyerId(obj.getString("replyerId"));
                            messageBean.setMsgReplyerName(obj.getString("replyerName"));
                            messageBean.setMsgBirefTitle(obj.getString("birefTitle"));
                            messageBean.setMsgBriefContent(obj.getString("briefContent"));

                            messageBeanList.add(messageBean);
                        }
                        //填充数据
                        initData(flag);
                    } else {
                        mListView.setState(LoadingFooter.State.Idle);
                        ToastUtil.showShort(MessageActivity.this, promptInfor);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog.dismiss();
                swipeLayout.setRefreshing(false);
                mListView.setState(LoadingFooter.State.Idle);
                NetWorkUtils.showMsg(MessageActivity.this);

            }
        });
    }


    //listview 的短按事件  进入详情
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if("0".equals(messageBeanList.get(position).getMsgType())){
            //赞
            return;
        }

        String  msgType = null;
        String  msgDetailsId = null;
        if("1".equals(messageBeanList.get(position).getMsgType())){
            //评论
            msgType = "1";
            msgDetailsId = messageBeanList.get(position).getMsgCommentId();

        }else if("2".equals(messageBeanList.get(position).getMsgType())){
            //回复
            msgType = "2";
            msgDetailsId = messageBeanList.get(position).getMsgReplyId();
        }

        Intent intent = new Intent(this,MessageDetailActivity.class);
        intent.putExtra("msgType", msgType);
        intent.putExtra("msgDetailsId", msgDetailsId);
        startActivity(intent);
    }




    //listview的 长按事件   删除
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if(pw==null){
            pw = new PopupWindow(this);
            pw.setWidth(200);
            pw.setHeight(90);
            pw.setContentView(tv_delete);
            pw.setFocusable(true);
            pw.setOutsideTouchable(false);

        }
        pw.showAsDropDown(view, view.getWidth() / 2, -30);


        //删除消息
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
                deleteMessage(messageBeanList.get(position).getMsgId(), position);
            }
        });

        return true;
    }



    /**
     * 删除消息 的  接口
     * @param msgId
     */
    private  void deleteMessage(String msgId, final int position){
        dialog.builder().setMessage("正在删除~").show();
        RequestParams params = new RequestParams();
        params.addBodyParameter("msgId",msgId);

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.getDelMessageUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog.dismiss();
                LogUtil.d("deleteMessage--------"+responseInfo.result.toString());

                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    String status = object.getString("status");
                    String promptInfor = object.getString("promptInfor");
                    if ("1".equals(status)) {

                        messageBeanList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        if(messageBeanList.size()==0){
                            mListView.setState(LoadingFooter.State.Idle);
                        }

                    }else{
                        ToastUtil.showShort(MessageActivity.this,promptInfor);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(HttpException error, String msg) {
                dialog.dismiss();
                NetWorkUtils.showMsg(MessageActivity.this);
            }
        });
    }


}
