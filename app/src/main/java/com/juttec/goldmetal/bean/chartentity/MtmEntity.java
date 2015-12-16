package com.juttec.goldmetal.bean.chartentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *MTM指标  :动量指标
 */
public class MtmEntity {

    List<Double> MTMs;//
    List<Double> MTMMAs;//

    //是一种专门研究股价波动的中短期技术分析工具。
    //MTM白色的线    MTMMA黄色的线

    public MtmEntity(List<KChartInfo.ResultEntity> OHLCData) {
        MTMs = new ArrayList<Double>();
        MTMMAs = new ArrayList<Double>();

        List<Double> closes = new ArrayList<Double>();//收盘价集合

        for (int i=OHLCData.size()-1;i>=0;i-- ) {
            closes.add(Double.parseDouble(OHLCData.get(i).getClose()));
        }


        int n=12;
        List<Double> Ts = new ArrayList<Double>();//
        for (int i = 0; i < closes.size(); i++)
        {
            if ((i < n))
                Ts.add(closes.get(0));
            else
                Ts.add(closes.get(i - n));
        }


        //MTM:
        for(int i = 0; i < closes.size(); i++)
        {
            MTMs.add(closes.get(i)- Ts.get(i));
        }


        //MTMMA:
        double f1 = 0;
        n=6;
        double NA = Double.MIN_VALUE;
        for (int i = 0; i < MTMs.size(); i++)
        {
            if (MTMs.get(i) == NA)
                MTMMAs.add(NA);
            else
                f1 = f1 + MTMs.get(i);


            if ((n != 0) && (i >= (n - 1)))
            {
                if (MTMs.get(i - n + 1) != NA)
                {
                    if (MTMs.get(i) == NA)
                        MTMMAs.add(NA);
                    else
                        MTMMAs.add( f1 / n);
                    f1 = f1 - MTMs.get(i - n + 1);
                }
                else
                    MTMMAs.add(NA);
            }
            else {
                MTMMAs.add(NA);
            }
        }






        Collections.reverse(MTMs);

        Collections.reverse(MTMMAs);
    }


    public List<Double> getMTMs() {
        return MTMs;
    }

    public List<Double> getMTMMAs() {
        return MTMMAs;
    }

}
