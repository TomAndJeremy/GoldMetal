package com.juttec.goldmetal.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.bean.MyEntity;
import com.juttec.goldmetal.bean.chartentity.KChartInfo;
import com.juttec.goldmetal.customview.chartview.KChartsView;
import com.juttec.goldmetal.customview.chartview.MyKChartsView;
import com.juttec.goldmetal.utils.GetNetworkData;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.util.LogUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class MarketKChartsFragment extends Fragment {
    private MyKChartsView mMyChartsView;
    private List<KChartInfo.ResultEntity> datas;
    private Handler handler;
    private MyEntity myEntity;
    private static final int NEWEST = 0;  //加载最新数据的标识
    private KChartInfo kChartInfo;
    public static String url;
    private static MarketKChartsFragment kChartsFragment;
    private String index;

    /**
     * 静态工厂方法需要一个int型的值来初始化fragment的参数，
     * 然后返回新的fragment到调用者
     */
    public static MarketKChartsFragment newInstance(String myurl) {

        kChartsFragment = new MarketKChartsFragment();
        url = myurl;
        return kChartsFragment;
    }

    public void setIndex(String index) {
        if (mMyChartsView != null) {
            mMyChartsView.setTabTitle(index);
            mMyChartsView.postInvalidate();
        }
    }

    public void setUpIndex(String index) {
        if (mMyChartsView != null) {
            mMyChartsView.setUpTitle(index);
            mMyChartsView.postInvalidate();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kcharts, null);
        mMyChartsView = (MyKChartsView) view.findViewById(R.id.my_charts_view);
        mMyChartsView.setTabTitle("MACD");
        mMyChartsView.setUpTitle("SMA");
        handler = new MyHandler();
        datas = new ArrayList<KChartInfo.ResultEntity>();
        kChartInfo = new KChartInfo();
        myEntity = new MyEntity(kChartInfo);
      new   GetNetworkData().getKLineData(url, myEntity, getActivity(), handler, NEWEST);
        return view;
    }


    /**
     * 自定义Handler
     */
    class MyHandler extends Handler {

        public MyHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            kChartInfo = (KChartInfo) myEntity.getObject();
            switch (msg.what) {
                case NEWEST:
                    datas = kChartInfo.getResult();
                    //datas = getFromAssets();
                    if (datas.size() == 0) {
                        ToastUtil.showShort(getActivity(), "没有数据...");
                        break;
                    } else {
                        mMyChartsView.setOHLCData(datas);

                        mMyChartsView.postInvalidate();
                        break;
                    }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("onDestroy");
        kChartsFragment = null;
    }

    //从assets 文件夹中获取文件并读取数据
    public   List<KChartInfo.ResultEntity> getFromAssets() {
        List<KChartInfo.ResultEntity> datas = new ArrayList<>();


        try {
            InputStream close = getResources().getAssets().open("close" + ".txt");
            InputStream high = getResources().getAssets().open("high" + ".txt");
            InputStream low = getResources().getAssets().open("low" + ".txt");
            InputStream open = getResources().getAssets().open("open" + ".txt");
            InputStream time = getResources().getAssets().open("time" + ".txt");
            InputStream date = getResources().getAssets().open("date" + ".txt");


            BufferedReader BufferedReaderClose = new BufferedReader(new InputStreamReader(close));
            BufferedReader BufferedReaderhigh = new BufferedReader(new InputStreamReader(high));
            BufferedReader BufferedReaderlow = new BufferedReader(new InputStreamReader(low));
            BufferedReader BufferedReaderopen = new BufferedReader(new InputStreamReader(open));
            BufferedReader BufferedReadertime = new BufferedReader(new InputStreamReader(time));
            BufferedReader BufferedReaderdate = new BufferedReader(new InputStreamReader(date));
            String s1 = null;
            while ((s1 = BufferedReaderClose.readLine()) != null) {
                KChartInfo.ResultEntity entity = new KChartInfo().new ResultEntity();

                String s2 = BufferedReaderhigh.readLine();
                String s3 = BufferedReaderlow.readLine();
                String s4 = BufferedReaderopen.readLine();
                String s5 = BufferedReadertime.readLine();
                String s6 = BufferedReaderdate.readLine();
                entity.setClose(s1);
                entity.setHigh(s2);
                entity.setLow(s3);
                entity.setOpen(s4);
                entity.setDate(s6);

                datas.add(entity);
            }
            BufferedReaderClose.close();
            BufferedReaderhigh.close();
            BufferedReaderlow.close();
            BufferedReaderopen.close();
            BufferedReadertime.close();
            BufferedReaderdate.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.reverse(datas);
        return datas;
    }
}

