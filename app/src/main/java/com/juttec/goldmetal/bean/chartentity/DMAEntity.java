package com.juttec.goldmetal.bean.chartentity;

import com.juttec.goldmetal.utils.KChartUtils;
import com.juttec.goldmetal.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jeremy on 2015/11/17.
 */
public class DMAEntity {

    List<Double> DIF;
    List<Double> AMA;

    public List<Double> getDIF() {
        return DIF;
    }

    public List<Double> getAMA() {
        return AMA;
    }

    public DMAEntity(List<KChartInfo.ResultEntity> OHLCData) {

        List<Double> list = new ArrayList<>();
        for (int i = OHLCData.size() - 1; i >= 0; i--) {
            list.add(Double.parseDouble(OHLCData.get(i).getClose()));
        }


        List<Double> MA_10 = KChartUtils.initMA(list, 10);//10天平均数
        List<Double> MA_50 = KChartUtils.initMA(list, 50);//50天平均数


        DIF = new ArrayList<>();
        Double NA = Double.MIN_VALUE;//最小正数
        for (int i = 0; i < OHLCData.size(); i++) {
            double ma10 = MA_10.get(i);
            double ma50 = MA_50.get(i);

            if (ma50 != NA) {
                DIF.add(ma10 - ma50);
            } else {
                DIF.add(NA);
            }


        }

        AMA = KChartUtils.initMA(DIF, 10);//DIF的10天平均数

        Collections.reverse(DIF);
        Collections.reverse(AMA);


        for (Double dd : DIF
                ) {
            LogUtil.e("DIF " + dd);
        }
        for (Double dd : AMA
                ) {
            LogUtil.e("AMA " + dd);
        }

    }
}
