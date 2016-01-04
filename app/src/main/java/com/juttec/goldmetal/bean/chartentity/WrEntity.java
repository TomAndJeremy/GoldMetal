package com.juttec.goldmetal.bean.chartentity;

import com.juttec.goldmetal.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * WR指标  :威廉指标
 */
public class WrEntity {

    List<Double> wr1s;//白线
    List<Double> wr2s;//黄线

    List<Double> closes;
    List<Double> highs;
    List<Double> lows;

    List<String> dates;

    //WR1一般是6天买卖强弱指标；WR2一般是10天买卖强弱指标；

    public WrEntity(List<KChartInfo.ResultEntity> OHLCData) {
        wr1s = new ArrayList<Double>();
        wr2s = new ArrayList<Double>();

        closes = new ArrayList<Double>();//收盘价集合
        highs = new ArrayList<Double>();//最高值集合
        lows = new ArrayList<Double>();//最低值集合

        dates = new ArrayList<String>();//最低值集合

        for (int i=OHLCData.size()-1;i>=0;i--) {
            highs.add(Double.parseDouble(OHLCData.get(i).getHigh()));
            closes.add(Double.parseDouble(OHLCData.get(i).getClose()));
            lows.add(Double.parseDouble(OHLCData.get(i).getLow()));

            dates.add((OHLCData.get(i).getDate()));

//            LogUtil.d("-------"+i+"---"+OHLCData.get(i).getHigh()+"--------"+OHLCData.get(i).getLow()+"--------"+OHLCData.get(i).getDate()+"\n");
        }


        wr1s = getWR(6);
        wr2s = getWR(10);

        Collections.reverse(wr1s);
        Collections.reverse(wr2s);
    }


    public List<Double> getWr1s() {

        for (Double dd:wr1s
             ) {
            LogUtil.e("wr1s  " + dd);
        }
        return wr1s;
    }

    public List<Double> getWr2s() {
        for (Double dd:wr2s
                ) {
            LogUtil.e("wr2s  " + dd);
        }
        return wr2s;
    }


    /**
     *
     * @param n  WR1 n=6;  WR2 n=10;
     * @return WR1 WR2
     */
    private  List<Double> getWR(int n){
        List<Double> T5s = new ArrayList<Double>();
        double  f1 = 0;
        for (int i = 0; i < highs.size(); i++)
        {
            f1 = -1000000.0;
            int k = 0;
            if ((i > n))
                k = i - n;
            for (int j = k; j <= i; j++)
            {
                if ((highs.get(j) > f1))
                    f1 = highs.get(j);
            }
//            LogUtil.d("-------"+i+"---"+f1+"--------"+dates.get(i)+"\n");
            T5s.add(f1);
        }




        List<Double> T6s = new ArrayList<Double>();
        for(int i=0;i<T5s.size();i++)
        {
            T6s.add(T5s.get(i)-closes.get(i));
        }



        List<Double> T7s = new ArrayList<Double>();

        for (int i = 0; i < lows.size(); i++)
        {
            f1 = 1000000.0;
            int k = 0;
            if ((i > n))
                k = i - n;
            for (int j = k; j <= i; j++)
            {
                if ((lows.get(j) < f1))
                    f1 = lows.get(j);
            }
            T7s.add(f1);
        }


        List<Double> T8s = new ArrayList<Double>();
        for(int i=0;i<T5s.size();i++)
        {
            T8s.add(T5s.get(i)-T7s.get(i));
        }


        List<Double> T10s = new ArrayList<Double>();
        for(int i=0;i<T6s.size();i++)
        {
            T10s.add(100*T6s.get(i));
    }

        List<Double> T11s = new ArrayList<Double>();
        for(int i=0;i<T8s.size();i++)
        {
            T11s.add(T10s.get(i)/T8s.get(i));
        }
        return T11s;
    }

}
