package com.juttec.goldmetal.bean.chartentity;

import com.juttec.goldmetal.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *
 */
public class RSIEntity {


    private List<Double> RSI1;
    private List<Double> RSI2;
    private List<Double> RSI3;
    double NA = Double.MIN_VALUE;

    private List<Double> diffs;//当日差值


    private int mParameter;//影响RSI指标变化的 变量 默认值为：6 12 24  （范围：2-99）

    public RSIEntity(List<KChartInfo.ResultEntity> OHLCData) {
        RSI1 = calRSI(OHLCData, 6);
        RSI2 = calRSI(OHLCData, 12);
        RSI3 = calRSI(OHLCData, 24);
    }

    /* private List<Double> calRSI(List<KChartInfo.ResultEntity> OHLCData, int day) {


           diffs = new ArrayList<Double>();


           List<Double> rsi = new ArrayList<Double>();


           mParameter = day;


           double close = 0;
           double open = 0;


           double riseTotal = 0.0;
           double fallTotal = 0.0;

           double value = 0.0;


   //		A为N日内收盘价的正数之和，B为N日内收盘价的负数之和乘以（—1）
   //		这样，A和B均为正，将A、B代入RSI计算公式，
   //		则 RSI（N）=A÷（A＋B）×100

           if (OHLCData != null && OHLCData.size() > 0) {

               for (int i = OHLCData.size() - 1; i >= 0; i--) {
                   close = Double.parseDouble(OHLCData.get(i).getClose());
                   open = Double.parseDouble(OHLCData.get(i).getOpen());

                   diffs.add(close - open);
                   riseTotal = 0;
                   fallTotal = 0;
                   if (OHLCData.size() - i <= mParameter) {
                       //所求天数 <= mParameter 时
                       for (int j = diffs.size() - 1; j >= 0; j--) {


                           if (diffs.get(j) >= 0) {
                               //上涨 总数
                               riseTotal += diffs.get(j);

                           } else {
                               //下跌 总数
                               fallTotal += diffs.get(j);
                           }
                       }


                   } else {
                       //所求天数 > mParameter 时


                       for (int a = diffs.size() - 1; a >= diffs.size() - mParameter; a--) {
                           if (diffs.get(a) >= 0) {
                               //上涨 总数
                               riseTotal += diffs.get(a);

                           } else {
                               //下跌 总数
                               fallTotal += diffs.get(a);
                           }
                       }

                   }

                   value = riseTotal / (riseTotal - fallTotal) * 100;

                   rsi.add(value);

               }

               Collections.reverse(rsi);
           }
           return rsi;

       }
   */
    private List<Double> calRSI(List<KChartInfo.ResultEntity> OHLCData, int day) {

        List<Double> rsi = new ArrayList<>();
        if (OHLCData != null && OHLCData.size() > 0) {
            int p = 1;
            List<Double> closes = new ArrayList<>();
            for (int i = OHLCData.size() - 1; i >= 0; i--) {
                closes.add(Double.parseDouble(OHLCData.get(i).getClose()));
            }
            List<Double> lc = new ArrayList<>();
            for (int i = 0; i < closes.size(); i++) {
                if (i < p) {
                    lc.add(closes.get(0));
                } else {
                    lc.add(closes.get(i - p));
                }
            }

            List<Double> minus = new ArrayList<>();
            for (int i = 0; i < closes.size(); i++) {
                double var = closes.get(i) - lc.get(i);
                minus.add(var);
            }

            List<Double> up = new ArrayList<>();
            for (double v : minus
                    ) {
                if (v > 0.0) {
                    up.add(v);
                } else {
                    up.add(0.0);
                }
            }

            int n = day, m = 1;

            List<Double> upEMA = getEMA(up, m, n);


            List<Double> allSum = new ArrayList<>();
            for (double v :
                 minus   ) {
                allSum.add(Math.abs(v));
            }

            List<Double> allEMA = getEMA(allSum, m, n);



            for (int i = 0; i < upEMA.size(); i++) {
                if (allEMA.get(i) == 0.0) {
                    rsi.add(0.0);
                } else {
                    rsi.add((upEMA.get(i) / allEMA.get(i) * 100));

                }

            }
            Collections.reverse(rsi);

        }

        return rsi;
    }

    public List<Double> getRSI1() {
        int i = 0;
        for (Double aDouble : RSI1
                ) {
            LogUtil.e("RSI1  " + i + " " + aDouble);
            i++;
        }
        return RSI1;
    }

    public List<Double> getRSI2() {
        return RSI2;
    }

    public List<Double> getRSI3() {
        return RSI3;
    }

    private List<Double> getEMA(List<Double> up,int m,int n) {
        List<Double> upEMA = new ArrayList<>();

        for (int i = 0; i < up.size(); i++) {
            if (up.get(i) == NA) {
                upEMA.add(up.get(i));
                continue;

            }

            if (i == 0) {
                upEMA.add(up.get(i));
            } else {
                if (upEMA.get(i - 1) == NA) {
                    upEMA.add(up.get(i));
                } else {
                    upEMA.add(m * up.get(i) + ((n - m) * upEMA.get(i - 1)) / n);
                }

            }
        }
        return upEMA;
    }

}
