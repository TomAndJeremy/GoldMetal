package com.juttec.goldmetal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.MessageBean;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 消息详情界面
 */

public class MessageDetailActivity extends AppCompatActivity {

    private ImageView iv_photo;

    private TextView tv_name;

    private TextView tv_tiem;

    private TextView tv_content;

    private MyProgressDialog dialog;//加载时的 进度框

    private MyApplication app ;

    private MessageBean messageBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        app = (MyApplication) getApplication();
        dialog = new MyProgressDialog(this);
        //初始化控件
        initView();


        //调接口 获取消息详情数据
        getMessageDetailData(getIntent().getStringExtra("msgType"),getIntent().getStringExtra("msgDetailsId"));

    }

    private void  initView(){
        iv_photo = (ImageView) findViewById(R.id.msg_detail_head_portrait);
        tv_name = (TextView) findViewById(R.id.msg_detail_user_name);
        tv_tiem = (TextView) findViewById(R.id.tv_time);
        tv_content = (TextView) findViewById(R.id.msg_detail_content);

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
        params.addBodyParameter("msgDetailsId",msgDetailsId);

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
                            messageBean =  new MessageBean();
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


                    }else{
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

}
