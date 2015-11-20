package com.juttec.goldmetal.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.juttec.goldmetal.bean.MyEntity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;


/**
 * Created by jeremy on 2015/10/20.
 */


public class GetNetworkData {

    private String url;
    private MyEntity myEntity;
    private  Context context;
    private  Handler handler;
    private  Thread thread;
    private  boolean shouldConnect;



    /**
     * 获取接口数据
     *
     * @param sUrl
     * @param sMyEntity
     * @param sContext
     * @param sHandler
     * @param flag
     */
    public  void getKLineData(final String sUrl, final MyEntity sMyEntity, final Context sContext,
                                    final Handler sHandler, final int flag) {

        url = sUrl;
        myEntity = sMyEntity;
        context = sContext;
        handler = sHandler;


        thread= new Thread(new Runnable() {
            @Override
            public void run() {
                shouldConnect = true;

                while (shouldConnect) {
                    HttpUtils httpUtils = new HttpUtils();
                    httpUtils.configCurrentHttpCacheExpiry(1000);
                    httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {

                            String str;
                            if (responseInfo.result.toString().equals("unkown user")) {
                                str = "{\"result\":[]}";
                            } else {
                                str = "{\"result\":" + responseInfo.result.toString() + "}";
                            }

                            LogUtil.d("自选数据:---------" + str);

                            try {

                                myEntity.setObject(JSON.parseObject(str, myEntity.getObject().getClass()));
                                Message message = new Message();
                                message.what = flag;
                                LogUtil.d("发消息通知---------");
                                handler.sendMessage(message);

                            } catch (Exception e) {
                                e.printStackTrace();

                                ToastUtil.showShort(context, responseInfo.result.toString());

                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            NetWorkUtils.showMsg(context);
                        }
                    });

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

    }


    public  void setUrl(final String sUrl) {
        url = sUrl;

    }

    public  void reset(final String sUrl, final MyEntity sMyEntity, final Context sContext,
                             final Handler sHandler) {
        url = sUrl;
        myEntity = sMyEntity;
        context = sContext;
        handler = sHandler;
    }

    public  void stop() {

        if (thread != null&&thread.isAlive()) {
          shouldConnect = false;
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
