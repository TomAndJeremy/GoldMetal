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
    public static void getData(final RequestParams requestParams, final String url,
                               final MyEntity myEntity, final Context context,  final Handler handler ,final int flag) {

        new Thread() {
            @Override
            public void run() {

                HttpUtils httpUtils = new HttpUtils();
                httpUtils.configCurrentHttpCacheExpiry(1000);
                httpUtils.send(HttpRequest.HttpMethod.POST, url, requestParams , new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {
                            myEntity.setObject(JSON.parseObject(responseInfo.result.toString(), myEntity.getObject().getClass()));
                            Message message = new Message();
                            message.what=flag;
                            handler.sendMessage(message);

                        } catch (Exception e) {
                            e.printStackTrace();
                            //   ShowToast.Short(context, "获取数据失败");
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        e.printStackTrace();
                        //     ShowToast.Short(context, "获取数据失败,请检查网络");
                    }
                });

            }
        }.start();
    }
    public static void getKLineData(  final String url, final MyEntity myEntity, final Context context,
                                      final Handler handler ,final ProgressDialog pd ,final int flag) {

        new Thread() {

            @Override
            public void run() {

                HttpUtils httpUtils = new HttpUtils();
                httpUtils.configCurrentHttpCacheExpiry(1000);
                httpUtils.send(HttpRequest.HttpMethod.GET, url,  new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String str;
                        if(responseInfo.result.toString().equals("unkown user")){
                            str = "{\"result\":[]}";
                        }else{
                            str= "{\"result\":"+responseInfo.result.toString()+"}";
                        }
                        try {
                            myEntity.setObject(JSON.parseObject( str , myEntity.getObject().getClass()));
                            Message message = new Message();
                            message.what=flag;
                            handler.sendMessage(message);

                        } catch (Exception e) {
                            //    ShowToast.Short(context, "获取数据失败");
                            pd.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        pd.dismiss();
                        NetWorkUtils.showMsg(context);

                    }
                });

            }
        }.start();
    } public static void getKLineData(  final String url, final MyEntity myEntity, final Context context,
                                      final Handler handler ,final int flag) {

        new Thread() {

            @Override
            public void run() {

                HttpUtils httpUtils = new HttpUtils();
                httpUtils.configCurrentHttpCacheExpiry(1000);
                httpUtils.send(HttpRequest.HttpMethod.GET, url,  new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String str;
                        if(responseInfo.result.toString().equals("unkown user")){
                            str = "{\"result\":[]}";
                        }else{
                            str= "{\"result\":"+responseInfo.result.toString()+"}";
                        }
                        try {
                            myEntity.setObject(JSON.parseObject( str , myEntity.getObject().getClass()));
                            Message message = new Message();
                            message.what=flag;
                            handler.sendMessage(message);

                        } catch (Exception e) {
                            //    ShowToast.Short(context, "获取数据失败");

                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                        NetWorkUtils.showMsg(context);

                    }
                });

            }
        }.start();
    }
}
