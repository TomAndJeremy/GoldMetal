package com.juttec.goldmetal.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.customview.HeadLayout;
import com.juttec.goldmetal.dialog.MyAlertDialog;
import com.juttec.goldmetal.fragment.MarketKChartsFragment;
import com.juttec.goldmetal.fragment.MarketTimesFragment;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.juttec.goldmetal.utils.SharedPreferencesUtil;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_cycle;//周期
    private TextView tv_index;//指标
    private TextView tv_title;//标题

    private TextView tvHigh, tvLow, tvOpen, tvLastClose,tvCurrent,tvAmount;
    private Button btn_times, btn_Kline, btn_free_remind,btn_msg_remid;//分时图，K线图，免费提醒


    private HeadLayout mHeadLayout;
    MarketTimesFragment timesFragment;//分时图
    MarketKChartsFragment kChartsFragment;//K线图
    //从MarketFragment传递过来的
    private String name;//名称
    private String symbol;//代码

    private String name_cycle_kline = "15分钟";//k线图的周期
    private String name_cycle_time="24小时";//分时图的周期

    private String upIndex_kline = "SMA";//k线图的上面显示的指标
    private String tabIndex_kline = "MACD";//k线图下面显示的指标

    private FragmentManager fragmentManager;

    private boolean cycle = true;
    private String cycles_current[] = null;
    //分时图周期
    private String cycles_time[] = new String[]{
            "24小时",
            "48小时",
            "72小时",
            "96小时"
    };

    //K线图 周期
    //周期
    private String cycles_klines[] = new String[]{
            "1分钟",
            "5分钟",
            "15分钟",
            "30分钟",
            "60分钟",
            "4小时",
            "日线",
            "周线",
            "月线"
    };


    //K线图指标
    private String indexs[] = new String[]{
            "SMA指标",
            "BOLL指标",
            "MACD指标",
            "KDJ指标",
            "RSI指标",
            "DMA指标",
            "FS指标",
            "WR指标",
            "MTM指标"
    };

    private boolean isKLine = false;//是否显示的是K线图  默认为False

    //股票详情url
    private String MARKET_URL;

    //分时图URL
    private String TIME_URL;
    //K线图URL
    private String KLINES_URL;

    //return_t=0  // 总体类型 0日线  1月线   2周线  3为分钟线 4为季线 5为年线  (当值为3时qt_type的值才有效,默认值0)
    private int return_t = 3;

    //qt_type     // 分钟数据，例如qt_type=1 则返回1分钟数据、qt_type=3 则返回3分钟数据，以此类推  (默认值为1)
    private int qt_type = 15;

    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        app = (MyApplication) getApplication();

        symbol = getIntent().getStringExtra("symbol");
        name = getIntent().getStringExtra("name");

        MARKET_URL = "http://db2015.wstock.cn/wsDB_API/stock.php?symbol=" + symbol + "&query=High,Low,Open,LastClose,NewPrice,Amount&r_type=2";

        //&u=qq3585&p=qq3771
        KLINES_URL = "http://db2015.wstock.cn/wsDB_API/kline.php?symbol=" + symbol +
                "&r_type=2&num=100";

        TIME_URL = "http://db2015.wstock.cn/wsDB_API/TheTimeTrend.php?r_type=2&symbol=" + symbol;

        initView();

        initData();
        //设置相应的周期和指标
        setCycle();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        timesFragment = MarketTimesFragment.newInstance(TIME_URL);
        transaction.replace(R.id.fragment_container, timesFragment).commit();
        btn_times.setSelected(true);
    }


    /**
     * 初始化控件
     */
    private void initView() {
        mHeadLayout = (HeadLayout) findViewById(R.id.head_layout);
        tv_title = (TextView) mHeadLayout.findViewById(R.id.head_title);
        //设置标题
        tv_title.setText(name+" -"+name_cycle_time);
        tv_cycle = (TextView) mHeadLayout.findViewById(R.id.left_text);
        tv_cycle.setOnClickListener(this);

        tv_index = (TextView) mHeadLayout.findViewById(R.id.right_text);
        tv_index.setOnClickListener(this);


        btn_times = (Button) findViewById(R.id.btn_time);
        btn_times.setOnClickListener(this);

        btn_Kline = (Button) findViewById(R.id.btn_k_line);
        btn_Kline.setOnClickListener(this);

        btn_free_remind = (Button) findViewById(R.id.btn_free_remind);
        btn_free_remind.setOnClickListener(this);

        btn_msg_remid = (Button) findViewById(R.id.btn_msg_remind);
        btn_msg_remid.setOnClickListener(this);


        tvHigh = (TextView) this.findViewById(R.id.ca_tv_high);
        tvLow = (TextView) this.findViewById(R.id.ca_tv_low);
        tvOpen = (TextView) this.findViewById(R.id.ca_tv_open);
        tvLastClose = (TextView) this.findViewById(R.id.ca_tv_lastclose);
        tvCurrent = (TextView) this.findViewById(R.id.tv_current);
        tvAmount = (TextView) this.findViewById(R.id.tv_total);
    }


    //根据当前是K线图或分时图  设置 当前的周期属性
    private void setCycle() {
        if (isKLine) {
            tv_index.setVisibility(View.VISIBLE);
            cycles_current = cycles_klines;
            btn_times.setSelected(false);
            btn_Kline.setSelected(true);
        } else {
            tv_index.setVisibility(View.GONE);
            cycles_current = cycles_time;
            btn_times.setSelected(true);
            btn_Kline.setSelected(false);
        }
    }


    //获取当前时间
    private String getDate(int day) {
//        Date date = new Date();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String mDate = simpleDateFormat.format(date).replaceAll(" ", "%20");

        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(new Date()); //设置时间为当前时间
        ca.add(Calendar.DAY_OF_MONTH, -day); //日减1
        Date lastDay = ca.getTime(); //结果
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String mDate = sf.format(lastDay);
        return mDate;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChartActivity.this);
        AlertDialog dialog;
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        switch (v.getId()) {
            case R.id.left_text:
                //周期
                builder.setItems(cycles_current, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name_cycle = cycles_current[which];
                        if (isKLine) {
                            name_cycle_kline = name_cycle;
                            //K线图1 5 15 30 60分钟 4小时  日线 周线  月线
                            switch (which) {
                                case 0:
                                    return_t = 3;
                                    qt_type = 1;
                                    LogUtil.d("KLINES_URL-------------" + KLINES_URL + "--------which--------" + which);
                                    break;
                                case 1:
                                    return_t = 3;
                                    qt_type = 5;
                                    break;
                                case 2:
                                    return_t = 3;
                                    qt_type = 15;
                                    break;
                                case 3:
                                    return_t = 3;
                                    qt_type = 30;
                                    break;
                                case 4:
                                    return_t = 3;
                                    qt_type = 60;
                                    break;
                                case 5:
                                    return_t = 3;
                                    qt_type = 240;
                                    break;
                                case 6:
                                    return_t = 0;
                                    qt_type = 1;
                                    break;
                                case 7:
                                    return_t = 2;
                                    qt_type = 1;
                                    break;
                                case 8:
                                    return_t = 1;
                                    qt_type = 1;
                                    break;
                            }
                            tv_title.setText(name + " -" + name_cycle_kline);
                            kChartsFragment = MarketKChartsFragment.newInstance(KLINES_URL + "&return_t=" + return_t + "&qt_type=" + qt_type,upIndex_kline,tabIndex_kline);
                            LogUtil.d("KLINES_URL-------------" + KLINES_URL + "&return_t=" + return_t + "&qt_type=" + qt_type);
                            transaction.replace(R.id.fragment_container, kChartsFragment).commit();
                        } else {
                            name_cycle_time = name_cycle;
                            //分时图 24 、48、72、96小时
                            String url = null;
                            switch (which) {
                                case 0:
                                    url = TIME_URL;
                                    break;
                                case 1:
                                    url = TIME_URL + "&date=" + getDate(1);
                                    break;
                                case 2:
                                    url = TIME_URL + "&date=" + getDate(2);
                                    break;
                                case 3:
                                    url = TIME_URL + "&date=" + getDate(3);
                                    break;
                            }
                            tv_title.setText(name + " -" + name_cycle_time);
                            timesFragment = MarketTimesFragment.newInstance(url);
                            LogUtil.d("Time_URL-------------" + url);
                            transaction.replace(R.id.fragment_container, timesFragment).commit();
                        }
                    }


                });

                dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();


                break;

            case R.id.right_text:
                //指标
                builder.setItems(indexs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String index = indexs[which].replace("指标","");


                        LogUtil.e(ChartActivity.this, 145, "index   " + index);
                        if (kChartsFragment != null) {
                            //index.equals("FS")
                            if (index.equals("MACD") || index.equals("KDJ") || index.equals("RSI") || index.equals("DMA")
                                    || index.equals("MTM")|| index.equals("WR")) {
                                tabIndex_kline = index;
                                kChartsFragment.setIndex(index);
                            } else {
                                upIndex_kline = index;
                                kChartsFragment.setUpIndex(index);
                            }
                        }
                        ToastUtil.showShort(ChartActivity.this, index + "被选中");
                    }

                });
                dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();

                break;
            case R.id.btn_time:
                //分时图
                isKLine = false;
                setCycle();
                LogUtil.d("分时图TIME_URL-------------" + TIME_URL);
                timesFragment = MarketTimesFragment.newInstance(TIME_URL);
                transaction.replace(R.id.fragment_container, timesFragment).commit();
                tv_title.setText(name + " -"+name_cycle_time);
                break;
            case R.id.btn_k_line:
                //K线图
                isKLine = true;
                setCycle();
                kChartsFragment = MarketKChartsFragment.newInstance(KLINES_URL + "&return_t=" + return_t + "&qt_type=" + qt_type,upIndex_kline,tabIndex_kline);
                LogUtil.d("KLINES_URL-------------" + KLINES_URL + "&return_t=" + return_t + "&qt_type=" + qt_type);
                transaction.replace(R.id.fragment_container, kChartsFragment).commit();
                tv_title.setText(name +  " -"+name_cycle_kline);
                break;

            case R.id.btn_free_remind:



                //免费提醒
                //判断是否登录
                if (app.getUserInfoBean()!=null) {
                    Intent intent = new Intent(ChartActivity.this, FreeRemindActivity.class);
                    intent.putExtra("symbol", symbol);
                    intent.putExtra("stockName",name);
                    intent.putExtra("currentValue", tvCurrent.getText()+"");
                    startActivity(intent);
                } else {
                    //如果没有登录
                    final MyAlertDialog mdialog = new MyAlertDialog(ChartActivity.this);
                    mdialog.builder().setTitle("提示")
                            .setMsg("您还没有登录，请先登录后再进行操作！")
                            .setSingleButton("前去登录", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ChartActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    mdialog.dismiss();
                                }
                            }).show();
                }

                break;
            case R.id.btn_msg_remind:

                if (app.getUserInfoBean()==null) {
                    //如果没有登录
                    final MyAlertDialog mdialog = new MyAlertDialog(ChartActivity.this);
                    mdialog.builder().setTitle("提示")
                            .setMsg("您还没有登录，请先登录后再进行操作！")
                            .setSingleButton("前去登录", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ChartActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    mdialog.dismiss();
                                }
                            }).show();
                    return;

                }

                    String msg = "";
                if ("0".equals(app.getUserInfoBean().getNoteWarn())) {
                    msg = "是否开启短信提醒";
                } else if ("1".equals(app.getUserInfoBean().getNoteWarn())) {
                    msg = "是否关闭短信提醒";
                }

                final MyAlertDialog mdialog = new MyAlertDialog(ChartActivity.this);
                mdialog.builder().setTitle("提示")
                        .setMsg(msg)
                        .setNegativeButton("点错了", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                RequestParams params = new RequestParams();
                                params.addBodyParameter("mobile", app.getUserInfoBean().getMobile());
                                new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getNoteWarn(), params, new RequestCallBack<String>() {

                                    @Override
                                    public void onSuccess(ResponseInfo<String> responseInfo) {
                                        try {
                                            JSONObject object = new JSONObject(responseInfo.result.toString());
                                            ToastUtil.showShort(ChartActivity.this,object.getString("promptInfor"));
                                            if ("1".equals(object.getString("status"))) {
                                                if ("1".equals(app.getUserInfoBean().getNoteWarn())) {
                                                    app.getUserInfoBean().setNoteWarn("0");
                                                    SharedPreferencesUtil.setParam(ChartActivity.this,"noteWarn","0");
                                                } else {
                                                    app.getUserInfoBean().setNoteWarn("1");
                                                    SharedPreferencesUtil.setParam(ChartActivity.this,"noteWarn","1");
                                                }
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(HttpException error, String msg) {
                                        NetWorkUtils.showMsg(ChartActivity.this);

                                    }
                                });
                                mdialog.dismiss();
                            }
                        }).show();
                break;

        }

    }


    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (cycle) {
                    new HttpUtils().send(HttpRequest.HttpMethod.GET, MARKET_URL, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            String result = responseInfo.result.toString().substring(1, responseInfo.result.toString().length() - 1);
                            MyData d = new Gson().fromJson(result, MyData.class);
                            tvLastClose.setText(d.getLastClose());
                            tvOpen.setText(d.getOpen());
                            tvLow.setText(d.getLow());
                            tvHigh.setText(d.getHigh());
//                            LogUtil.d("height------newprice:"+d.getHigh()+"-------"+d.getNewPrice());
                            tvCurrent.setText(d.getNewPrice());
                            tvAmount.setText(d.getAmount());
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            NetWorkUtils.showMsg(ChartActivity.this);
                        }
                    });

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        cycle = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        cycle = false;
    }

    class MyData {
        //"High": "5479.66",
        // "Low": "5446.6",
        // "Open": "5463.3",
        // "LastClose": "5469.07"
        private String High;//最高价
        private String Low;//最低价
        private String Open;//开盘价
        private String LastClose;//昨日收盘价
        private String NewPrice;//当前价，收盘后为收盘价 4字节
        private String Amount;//当日总成交额 4字节

        public String getAmount() {
            return Amount;
        }

        public void setAmount(String amount) {
            Amount = amount;
        }

        public String getNewPrice() {
            return NewPrice;
        }

        public void setNewPrice(String newPrice) {
            NewPrice = newPrice;
        }

        public void setHigh(String high) {
            High = high;
        }

        public void setLow(String low) {
            Low = low;
        }

        public void setOpen(String open) {
            Open = open;
        }

        public void setLastClose(String lastClose) {
            LastClose = lastClose;
        }

        public String getHigh() {

            return High;
        }

        public String getLow() {
            return Low;
        }

        public String getOpen() {
            return Open;
        }

        public String getLastClose() {
            return LastClose;
        }
    }
}
