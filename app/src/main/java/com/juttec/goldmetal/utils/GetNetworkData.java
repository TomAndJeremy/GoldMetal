package com.juttec.goldmetal.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.MarketFormInfo;
import com.juttec.goldmetal.bean.MyEntity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;


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
    HttpUtils httpUtils;

    public void getKLineData(final String sUrl, final MyEntity sMyEntity, final Context sContext,
                             final Handler sHandler, final int flag) {
        getKLineData(sUrl, sMyEntity, sContext,
                sHandler, flag, null);
    }

    /**
     * 获取接口数据
     *
     * @param sUrl        链接
     * @param sMyEntity   实体
     * @param sContext    上下文
     * @param sHandler    handle
     * @param flag        消息标志
     * @param refreshTime 数据刷新时间（null 从设置中读取时间（默认60秒），小于0不刷新，大于0直接将值设为睡眠时间）
     */
    public void getKLineData(final String sUrl, final MyEntity sMyEntity, final Context sContext,
                             final Handler sHandler, final int flag, final Integer refreshTime) {


        url = sUrl;
        myEntity = sMyEntity;
        context = sContext;
        handler = sHandler;

        httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(1000);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                shouldConnect = true;

                //给实体类加锁
                synchronized (myEntity) {
                    do {

                        if (MyApplication.canCycle) {
                            httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {

                                    String str;
                                    if (responseInfo.result.toString().equals("unkown user")) {
                                        str = "{\"result\":[]}";
                                    } else {
                                        str = "{\"result\":" + responseInfo.result.toString() + "}";
                                    }

                                    LogUtil.d("股票数据:---------" + str);

                                    try {

                                        if (responseInfo.result.toString().contains("errcode")) {

                                            List<MarketFormInfo.ResultEntity> result = new ArrayList<MarketFormInfo.ResultEntity>();
                                            MarketFormInfo marketFormInfo = new MarketFormInfo();
                                            marketFormInfo.setResult(result);

                                            myEntity.setObject(marketFormInfo);

                                        } else {
                                            myEntity.setObject(JSON.parseObject(str, myEntity.getObject().getClass()));
                                        }


                                        Message message = new Message();
                                        message.what = flag;
                                        LogUtil.d("发消息通知页面更新数据---------");
                                        handler.sendMessage(message);

                                    } catch (Exception e) {
                                        e.printStackTrace();

                                        // ToastUtil.showShort(context, responseInfo.result.toString());

                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    NetWorkUtils.showMsg(context);
                                }
                            });
                        }

                        LogUtil.e("url  " + url);
                        try {
                            int sleepTime = 60 * 1000;//默认1分钟
                            if (refreshTime == null) {
                                sleepTime = (int) (SharedPreferencesUtil.getParam(context, "refreshTime", 10)) * 1000;
                                LogUtil.e("sleeptime  " + sleepTime);
                            } else {
                                sleepTime = refreshTime;
                            }


                            if (sleepTime < 0) {
                                shouldConnect = false;//不循环
                            } else {
                                myEntity.wait(sleepTime);
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } while (shouldConnect);

                }
            }
        });
        thread.start();

    }


    /**
     * 重置url
     *
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
