package com.juttec.goldmetal.utils;

import com.juttec.goldmetal.bean.chartentity.KChartInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by jeremy on 2015/11/17.
 */
public class KChartUtils {
    /**
     * 初始化MA值，从数组的最后一个数据开始初始化
     *
     * @param closes
     * @param n
     * @return
     */

    public static List<Double> initMA(List<Double> entityList, int n) {

        List<Double> closes = entityList;
        List<Double> MBs = new ArrayList<>();
        double f1 = 0.0;
        double NA = Double.MIN_VALUE;

        for (int i = 0; i < closes.size(); i++) {
            if (closes.get(i) == NA) {
                MBs.add(NA);
                continue;
            } else {
                f1 = f1 + closes.get(i);
            }
            if ((n != 0) && i >= (n - 1)) {
                if (closes.get(i - n + 1) != NA) {
                    if (closes.get(i) == NA) {
                        MBs.add(NA);
                    } else {
                        MBs.add(f1 / n);
                    }
                    f1 = f1 - closes.get((i - n) + 1);
                } else {
                    MBs.add(NA);
                }
            } else if (n == 0) {
                MBs.add(f1 / (i + 1));
            } else {
                MBs.add(NA);
            }

        }
        return MBs;
    }
 /*public static List<Double> initMA(List<Double> entityList, int days) {
        if (days < 2 || entityList == null || entityList.size() <= 0) {
            return null;
        }
        List<Double> MAValues = new ArrayList<Double>();

        Double sum = 0.0;
        Double avg = 0.0;
        for (int i = 0; i<entityList.size(); i++) {
            Double close = entityList.get(i);
            if (i <=days) {
                sum = sum + close;
                avg = sum / (entityList.size() - i);
            } else {
                sum = close + avg * (days - 1);
                avg = sum / days;
            }
            MAValues.add(avg);
        }

        List<Double> result = new ArrayList<Double>();
        for (int j = MAValues.size() - 1; j >= 0; j--) {
            result.add(MAValues.get(j));
        }
        return result;
    }

*/
}
