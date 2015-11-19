package com.juttec.goldmetal.utils;

import com.juttec.goldmetal.bean.chartentity.KChartInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by jeremy on 2015/11/17.
 *
 * 画图时用到的工具类
 */
public class KChartUtils {
    /**
     * 初始化MA（n日内的平均）值
     *
     * @param closes  所有数值
     * @param n 平均数参数
     * @return
     */

    public static List<Double> initMA(List<Double> closes, int n) {

        List<Double> MBs = new ArrayList<>();//计算后的平均数
        double f1 = 0.0;//存储数据和
        double NA = Double.MIN_VALUE;//double所能表示的最小数

        for (int i = 0; i < closes.size(); i++) {
            if (closes.get(i) == NA) {
                MBs.add(NA);
                continue;
            } else {
                f1 = f1 + closes.get(i);//求和
            }
            if ((n != 0) && i >= (n - 1)) {
                if (closes.get(i - n + 1) != NA) {
                    if (closes.get(i) == NA) {
                        MBs.add(NA);
                    } else {
                        MBs.add(f1 / n);//平局数
                    }
                    f1 = f1 - closes.get((i - n) + 1);//求今天往前n天的和
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

}
