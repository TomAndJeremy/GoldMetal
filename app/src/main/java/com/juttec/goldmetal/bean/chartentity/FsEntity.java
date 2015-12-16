package com.juttec.goldmetal.bean.chartentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FS指标  :分时均线
 */
public class FsEntity {

    List<Double> averagePrices;//均价线
    List<Double> stockPrices;//股价线

    //分时均线由均价线和分时股价线两条线组成，均价线由黄色线表示，分时股价线由白色线表示。

    public FsEntity(List<KChartInfo.ResultEntity> OHLCData) {
        averagePrices = new ArrayList<Double>();
        stockPrices = new ArrayList<Double>();

        List<Double> closes = new ArrayList<Double>();//收盘价集合
        List<Double> vols = new ArrayList<Double>();//总成交量集合
        List<Double> vol1s = new ArrayList<Double>();//
        List<Double> vks = new ArrayList<Double>();//

        double  f1 = 0;

        for (int i=OHLCData.size()-1;i>=0;i--) {
            vols.add(Double.parseDouble(OHLCData.get(i).getVolume()));
            closes.add(Double.parseDouble(OHLCData.get(i).getClose()));
        }

        //
        for(int i = 0; i < vols.size(); i++){
            f1 = f1+vols.get(i);
            vol1s.add(f1);
        }

        for(int i = 0; i < vols.size(); i++){
           vks.add(vols.get(i)*closes.get(i));
        }


        f1 = 0;
        List<Double> vol2s = new ArrayList<Double>();//
        for (int i = 0; i < vks.size(); i++)
        {
            f1 = f1 + vks.get(i);
            vol2s.add(f1);
        }


        List<Double> JJs = new ArrayList<Double>();//
        for (int i = 0; i < vol2s.size(); i++)
        {
            JJs.add(vol2s.get(i)/vol1s.get(i));
        }


        List<Double> Ts = new ArrayList<Double>();//
        for(int i=0;i<JJs.size();i++)
        {
            if (JJs.get(i)> 0)
                Ts.add(1.0);
            else
                Ts.add(0.0);
        }

        //均价线:
        for (int i = 0; i < JJs.size(); i++)
        {
            if ((Ts.get(i) != 0.0))
                averagePrices.add(JJs.get(i));
            else
                averagePrices.add(closes.get(i));
        }

        Collections.reverse(averagePrices);

        stockPrices = closes;
        Collections.reverse(stockPrices);
    }


    public List<Double> getAveragePrices() {
        return averagePrices;
    }

    public List<Double> getStockPrices() {
        return stockPrices;
    }

}
