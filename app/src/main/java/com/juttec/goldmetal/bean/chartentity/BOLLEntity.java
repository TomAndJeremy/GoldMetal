package com.juttec.goldmetal.bean.chartentity;

import com.juttec.goldmetal.utils.KChartUtils;
import com.juttec.goldmetal.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 布林线  实体类
 */

public class BOLLEntity {


    //上轨线UP是UP数值的连线，用黄色线表示；
    // 中轨线MB是MB数值的连线，用白色线表示；
    // 下轨线DN是DN数值的连线，用紫色线表示

    private List<Double> UPs;
    private List<Double> MBs;
    private List<Double> DNs;


	/*（1）计算MA
            MA=N日内的收盘价之和÷N
	 （2）计算标准差MD
			MD=平方根N日的（C－MA）的两次方之和除以N
	 （3）计算MB、UP、DN线
	         MB=（N－1）日的MA
			UP=MB+k×MD
			DN=MB－k×MD
	（K为参数，可根据股票的特性来做相应的调整，一般默认为2）*/

    private int k = 2; //影响BOLL指标变化的 变量 默认值为：2（范围：2-99）
    private int N = 20;//N日内的收盘价之和中的N  默认值为20

    private int M = N - 1;//中线=MA(N_1)

    public BOLLEntity(List<KChartInfo.ResultEntity> OHLCData) {

        List<Double> closes = new ArrayList<>();
        for (int i = OHLCData.size() - 1; i >= 0; i--) {
            closes.add(Double.parseDouble(OHLCData.get(i).getClose()));//获得收盘价
        }


        MBs = KChartUtils.initMA(closes, N);//中线，n日内的收盘价
        UPs = new ArrayList<>();
        DNs = new ArrayList<>();

        double f1 = 0.0;//和
        double ma = 0.0, avedev, devsq;
        List<Double> list = new ArrayList<>();//方差中间值
        for (int i = 0; i < closes.size(); i++) {
            f1 = f1 + closes.get(i);
            if (i >= M) {
                f1 = f1 - closes.get(i - M);
                ma = f1 / M;
            } else {
                continue;
            }
            avedev = 0.0;
            devsq = 0.0;
            for (int p = i; p > i - M + 1; p--) {
                avedev = avedev + Math.abs(closes.get(p) - ma);
                devsq = devsq + (closes.get(p) - ma) * (closes.get(p) - ma);//平方和


            }

            list.add(Math.sqrt(devsq / (M - 1)));//方差

            if (i == M) {
                for (int q = M - 1; q > -1; q--) {
                    list.add(0, list.get(list.size() - 1));//补全之前的数据
                }
            }

        }

        for (Double d : list
                ) {
            LogUtil.e("方差 "+d);
        }
        for (int i = 0; i < MBs.size(); i++) {


            UPs.add(MBs.get(i) + list.get(i) * k);//上线
            DNs.add(MBs.get(i) - list.get(i) * k);//下线

        }


        //将数据反转
        Collections.reverse(MBs);
        Collections.reverse(UPs);
        Collections.reverse(DNs);


       /* for (Double b : MBs
                ) {
            LogUtil.e("MBs " + b);
        }
        LogUtil.e("---------------------------------------------------------");
        for (Double b : UPs
                ) {
            LogUtil.e("UPs " + b);
        }
        LogUtil.e("---------------------------------------------------------");
        for (Double b : DNs
                ) {
            LogUtil.e("DN " + b);
        }*/

    }

    public List<Double> getUPs() {
        return UPs;

    }

    public List<Double> getMBs() {
        return MBs;
    }

    public List<Double> getDNs() {
        return DNs;
    }


}





