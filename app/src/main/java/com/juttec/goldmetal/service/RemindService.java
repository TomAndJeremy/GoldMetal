package com.juttec.goldmetal.service;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.support.v7.app.NotificationCompat;


import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.MainActivity;
import com.juttec.goldmetal.bean.ReminderFloatBeen;
import com.juttec.goldmetal.bean.ReminderPointBeen;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.ReminderDao;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class RemindService extends Service {


    Context context;
    ReminderDao reminderDao;
    String url;
    List<ReminderFloatBeen> floatBeens;
    List<ReminderPointBeen> pointBeens;

    //初始化
    public RemindService() {
        reminderDao = new ReminderDao(this);
        context = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtil.e("RemindService  onCreate");
        new Thread(new Runnable() {
            @Override
            public void run() {

                do {
                    floatReminder();//循环检测浮动提醒的值
                    pointReminder();//循环检测点位提醒的值

                    try {
                        Thread.sleep(20000);//每20秒检测一次
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (true);


            }


        }).start();

    }


    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    /**
     * 浮动提醒
     */
    private void floatReminder() {
        floatBeens = reminderDao.getAllFloatDate();

        if (floatBeens == null) {
            return;
        }
        //每次循还要重置url
        url = "http://db2015.wstock.cn/wsDB_API/stock.php?r_type=2&query=Name,NewPrice&symbol=";

        /*去除重复的股票号，得到所有股票号*/
        List<String> tmpNames = new ArrayList<>();
        for (ReminderFloatBeen been :
                floatBeens) {
            if (tmpNames.contains(been.getStock())) {
                continue;
            } else {
                tmpNames.add(been.getStock());
            }
        }

        /*拼接url*/
        for (int i = 0; i < tmpNames.size(); i++) {
            if (i == 0) {
                url += tmpNames.get(i);
            } else {
                url = url + "," + tmpNames.get(i);//拼接url
            }
        }

        //联网获取数据验证
        new HttpUtils().send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {

                            JSONArray array = new JSONArray(responseInfo.result.toString());
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                double newPrice = Double.parseDouble(object.getString("NewPrice"));//最新价
                                double basePrice = Double.parseDouble(floatBeens.get(i).getBasePrice());//基准价
                                double floatPrice = Double.parseDouble(floatBeens.get(i).getFloatPrice());//浮动值
                                String stockSymbol = floatBeens.get(i).getStock();
                                if (newPrice < basePrice - floatPrice) {//低于浮动范围
                                    /*修改提醒的值，否则会一直提醒*/
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("StockSymbol", stockSymbol);
                                    contentValues.put("BasePrice", newPrice);
                                    reminderDao.updata(contentValues);

                                    showNotigication(1, object.getString("Name"), stockSymbol, "最新价" + newPrice + "<" + (basePrice - floatPrice));

                                } else if (newPrice > basePrice + floatPrice) {//高于浮动范围
                                    /*修改提醒的值*/
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("StockSymbol", stockSymbol);
                                    contentValues.put("BasePrice", newPrice);
                                    reminderDao.updata(contentValues);


                                    showNotigication(1, object.getString("Name"), stockSymbol, "最新价" + newPrice + ">" + (basePrice + floatPrice));

                                }

                            }


                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {

                    }
                }

        );


    }


    private void pointReminder() {
        pointBeens = reminderDao.getAllPointDate();
        if (pointBeens == null) {
            return;
        }
        //每次循还要重置url
        url = "http://db2015.wstock.cn/wsDB_API/stock.php?r_type=2&query=Symbol,Name,NewPrice&symbol=";

        List<String> tmpNames = new ArrayList<>();

        for (ReminderPointBeen been :
                pointBeens) {
            if (tmpNames.contains(been.getStock())) {
                continue;
            } else {
                tmpNames.add(been.getStock());
            }

        }

        for (int i = 0; i < tmpNames.size(); i++) {
            if (i == 0) {
                url += tmpNames.get(i);
            } else {
                url = url + "," + tmpNames.get(i);//拼接url
            }
        }
        new HttpUtils().send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {

                            JSONArray array = new JSONArray(responseInfo.result.toString());

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                String stockSymbol = object.getString("Symbol");
                                String stockName = object.getString("Name");
                                double newPrice = Double.parseDouble(object.getString("NewPrice"));

                                for (int j = 0; j < pointBeens.size(); j++) {
                                    ReminderPointBeen been = pointBeens.get(j);
                                    if (stockSymbol.equals(been.getStock())) {
                                        if (been.getOperator().equals(">")) {
                                            if (newPrice > Double.parseDouble(been.getValue())) {

                                                reminderDao.deletePoint(stockSymbol, ">", been.getValue());//触发提醒后删除
                                                showNotigication(-1, stockName, stockSymbol, "最新价" + newPrice + ">" + been.getValue());
                                            }
                                        } else if ((been.getOperator().equals("<"))) {
                                            if (newPrice < Double.parseDouble(been.getValue())) {
                                                reminderDao.deletePoint(stockSymbol, "<", been.getValue());//触发提醒后删除
                                                showNotigication(-1, stockName, stockSymbol, "最新价" + newPrice + "<" + been.getValue());
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {

                    }
                }
        );
    }

    /**
     * 展示notification
     *
     * @param type
     * @param stockName
     * @param symbol
     * @param content
     */

    private void showNotigication(int type, String stockName, String symbol, String content) {

        //type>0 浮动提醒，else 点位提醒
        String title = type > 0 ? "浮动提醒：" : "点位提醒：" +
                "";
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("国金贵金属:" + title)
                .setContentTitle("国金贵金属")
                .setContentText(title + stockName + content)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);
        Intent intent = new Intent(context, MainActivity.class);//点击启动应用

        intent.putExtra("symbol", symbol);
        intent.putExtra("name", stockName);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);


        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        int requCode;
        if (type > 0) {
            requCode = Integer.parseInt(symbol.substring(2));//股票号作为浮动提醒notification的id
        } else {
            requCode = Integer.parseInt(symbol.substring(2)) + (int) new Date().getTime();//股票号加时间作为点位提醒notification的id
            new Date();
        }

        //显示通知
        notificationManager.notify(requCode, mBuilder.build());
    }
}
