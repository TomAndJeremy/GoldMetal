package com.juttec.goldmetal.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.juttec.goldmetal.application.MyApplication;
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

    private String url;//路径
    private MyEntity myEntity;//实体
    private Context context;//上下文
    private Handler handler;
    private Thread thread;
    private boolean shouldConnect;//是否执行循环


    /**
     * 获取接口数据
     *
     * @param sUrl
     * @param sMyEntity
     * @param sContext
     * @param sHandler
     * @param flag
     */
    public void getKLineData(final String sUrl, final MyEntity sMyEntity, final Context sContext,
                             final Handler sHandler, final int flag) {

        url = sUrl;
        myEntity = sMyEntity;
        context = sContext;
        handler = sHandler;


        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                shouldConnect = true;

                //给实体类加锁
                synchronized (myEntity) {
                    do {
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
                            myEntity.wait(10000);//每10秒钟执行一次
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (shouldConnect && MyApplication.canCycle);

                }
            }
        });
        thread.start();

    }



    /**
     * 重置url
     * @param sUrl
     */
    public void setUrl(String sUrl) {
        setUrl(sUrl, true);

    }

    public void setUrl(String sUrl, boolean shouldConnect) {
        url = sUrl;
        if (myEntity != null) {
            synchronized (myEntity) {
                myEntity.notifyAll();//唤醒线程
            }
        }

        this.shouldConnect = shouldConnect;

    }



    //停止
    public void stop() {


        if (thread != null && thread.isAlive()) {
            shouldConnect = false;
            if (myEntity != null) {
                synchronized (myEntity) {
                    myEntity.notifyAll();
                }
            }
            thread.interrupt();

        }
    }

    //判断线程isAlive
    public boolean isAlive() {
        if (thread == null) {
            return false;
        } else {
            return thread.isAlive();
        }

    }
}
