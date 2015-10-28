package com.juttec.goldmetal.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.customview.HeadLayout;
import com.juttec.goldmetal.fragment.MarketKChartsFragment;
import com.juttec.goldmetal.fragment.MarketTimesFragment;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_cycle;//周期
    private TextView tv_index;//指标
    private TextView tv_title;//标题

    private Button btn_times,btn_Kline, btn_free_remind;//分时图，K线图，免费提醒


    private HeadLayout mHeadLayout;
    MarketTimesFragment timesFragment;//分时图
    MarketKChartsFragment kChartsFragment;//K线图
    //从MarketFragment传递过来的
    private String name;//名称
    private String symbol;//代码
    private String name_cycle;
    private FragmentManager fragmentManager;

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
            "MACD指标",
            "BOLL指标",
            "KDJ指标",
            "RSI指标",
            "SMA指标",
            "EMA指标",
            "ENV指标"
    };

    private boolean isKLine = false;//是否显示的是K线图  默认为False

    //K线图URL   +&return_t=3&qt_type=15
    private String KLINES_URL;

    //return_t=0  // 总体类型 0日线  1月线   2周线  3为分钟线 4为季线 5为年线  (当值为3时qt_type的值才有效,默认值0)
    private int return_t = 3;

    //qt_type     // 分钟数据，例如qt_type=1 则返回1分钟数据、qt_type=3 则返回3分钟数据，以此类推  (默认值为1)
    private int qt_type = 15;


    //分时图URL
    private String TIME_URL ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        symbol = getIntent().getStringExtra("symbol");
        name = getIntent().getStringExtra("name");

        KLINES_URL = "http://db2015.wstock.cn/wsDB_API/kline.php?symbol=" + symbol +
                "&r_type=2&u=qq3585&p=qq3771&num=100";

        TIME_URL = "http://db2015.wstock.cn/wsDB_API/TheTimeTrend.php?r_type=2&symbol="+symbol+
                "&u=qq3585&p=qq3771";



     
        initView();

        initData();
        //设置相应的周期和指标
        setCycle();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        LogUtil.d("分时图TIME_URL-------------" + TIME_URL);
        timesFragment =  MarketTimesFragment.newInstance(TIME_URL);
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
        tv_title.setText(name);
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
    }


    private void initData() {

    }

    //根据当前是K线图或分时图  设置 当前的周期属性
    private void setCycle(){
        if(isKLine){
            tv_index.setVisibility(View.VISIBLE);
            cycles_current = cycles_klines;
            btn_times.setSelected(false);
            btn_Kline.setSelected(true);
        }else{
            tv_index.setVisibility(View.GONE);
            cycles_current = cycles_time;
            btn_times.setSelected(true);
            btn_Kline.setSelected(false);
        }
    }


    //获取当前时间
    private String getDate(int day){
//        Date date = new Date();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String mDate = simpleDateFormat.format(date).replaceAll(" ", "%20");

        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(new Date()); //设置时间为当前时间
        ca.add(Calendar.DAY_OF_MONTH, -day); //日减1
        Date lastDay = ca.getTime(); //结果
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String mDate = sf.format(lastDay);
        return  mDate;
    }
    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder;

        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        switch (v.getId()) {
            case R.id.left_text:
                //周期

                builder = new AlertDialog.Builder(ChartActivity.this);
                // builder.create().requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.setItems(cycles_current, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name_cycle = cycles_current[which];
                        if(isKLine){
                            //K线图1 5 15 30 60分钟 4小时  日线 周线  月线
                            switch (which){
                                case  0:
                                    return_t = 3;
                                    qt_type = 1;
                                    LogUtil.d("KLINES_URL-------------"+KLINES_URL+"--------which--------"+which);
                                    break;
                                case  1:
                                    return_t = 3;
                                    qt_type = 5;
                                    break;
                                case  2:
                                    return_t = 3;
                                    qt_type = 15;
                                    break;
                                case  3:
                                    return_t = 3;
                                    qt_type = 30;
                                    break;
                                case  4:
                                    return_t = 3;
                                    qt_type = 60;
                                    break;
                                case  5:
                                    return_t = 3;
                                    qt_type = 240;
                                    break;
                                case  6:
                                    return_t = 0;
                                    qt_type = 1;
                                    break;
                                case  7:
                                    return_t = 2;
                                    qt_type = 1;
                                    break;
                                case  8:
                                    return_t = 1;
                                    qt_type = 1;
                                    break;
                            }
                            tv_title.setText(name+"-"+name_cycle);
                            kChartsFragment = MarketKChartsFragment.newInstance(KLINES_URL+"&return_t="+return_t+"&qt_type="+qt_type);
                            LogUtil.d("KLINES_URL-------------"+KLINES_URL+"&return_t="+return_t+"&qt_type="+qt_type);
                            transaction.replace(R.id.fragment_container, kChartsFragment).commit();
                        }else{
                            //分时图 24 、48、72、96小时
                            String url = null;
                            switch(which){
                                case 0:
                                    url = TIME_URL ;
                                    break;
                                case 1:
                                    url = TIME_URL+"&date="+getDate(1);
                                    break;
                                case 2:
                                    url = TIME_URL+"&date="+getDate(2);
                                    break;
                                case 3:
                                    url = TIME_URL+"&date="+getDate(3);
                                    break;
                            }
                            tv_title.setText(name+"-"+name_cycle);
                            timesFragment = MarketTimesFragment.newInstance(url);
                            LogUtil.d("Time_URL-------------" + url);
                            transaction.replace(R.id.fragment_container, timesFragment).commit();
                        }
                    }




                }).show();

                break;

            case R.id.right_text:
                //指标
                builder = new AlertDialog.Builder(ChartActivity.this);
                builder.setItems(indexs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String index = indexs[which].replace("指标","");


                        LogUtil.e(ChartActivity.this, 145, "index   " + index);
                        if (kChartsFragment != null) {
                            if (index.equals("MACD") || index.equals("KDJ") || index.equals("RSI")) {
                                kChartsFragment.setIndex(index);
                            }

                        }
                        ToastUtil.showShort(ChartActivity.this, index + "被选中");

                    }

                }).show();


                break;
            case R.id.btn_time:
        //分时图
        isKLine = false;
        setCycle();
        LogUtil.d("分时图TIME_URL-------------"+TIME_URL);
        timesFragment = MarketTimesFragment.newInstance(TIME_URL);

        transaction.replace(R.id.fragment_container, timesFragment).commit();
                break;
            case R.id.btn_k_line:
                //K线图
        isKLine = true;
        setCycle();
        kChartsFragment = MarketKChartsFragment.newInstance(KLINES_URL+"&return_t="+return_t+"&qt_type="+qt_type);
        LogUtil.d("KLINES_URL-------------"+KLINES_URL+"&return_t="+return_t+"&qt_type="+qt_type);

        transaction.replace(R.id.fragment_container, kChartsFragment).commit();

                break;

            case R.id.btn_free_remind:
                //免费提醒
                Intent intent = new Intent(ChartActivity.this, FreeRemindActivity.class);
                startActivity(intent);
                break;
        }

    }
}
