package com.juttec.goldmetal.bean.chartentity;

import android.util.Log;

import com.juttec.goldmetal.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;


public class KDJEntity {
    private ArrayList<Double> Ks;
    private ArrayList<Double> Ds;
    private ArrayList<Double> Js;

    private ArrayList<Double> maxs;
    private ArrayList<Double> mins;

    private int mDay;//影响KDJ指标变化的 变量 默认值为：9  （范围：2-90）

    public KDJEntity(List<KChartInfo.ResultEntity> OHLCData) {
        Ks = new ArrayList<Double>();
        Ds = new ArrayList<Double>();
        Js = new ArrayList<Double>();

        maxs = new ArrayList<Double>();
        mins = new ArrayList<Double>();

        ArrayList<Double> ks = new ArrayList<Double>();
        ArrayList<Double> ds = new ArrayList<Double>();
        ArrayList<Double> js = new ArrayList<Double>();

        mDay = 9;

        double k = 0.0;
        double d = 0.0;
        double j = 0.0;
        double rSV = 0.0;

        if (OHLCData != null && OHLCData.size() > 0) {

            KChartInfo.ResultEntity oHLCEntity = OHLCData.get(OHLCData.size() - 1);
            double high = Double.parseDouble(oHLCEntity.getHigh());
            double low = Double.parseDouble(oHLCEntity.getLow());

            for (int i = OHLCData.size() - 1; i >= 0; i--) {
                oHLCEntity = OHLCData.get(i);

                Double mLow = Double.parseDouble(oHLCEntity.getLow());
                Double mHigh = Double.parseDouble(oHLCEntity.getHigh());

                mins.add(mLow);
                maxs.add(mHigh);

                if (i < OHLCData.size() - 1) {
                    if (OHLCData.size() - i <= mDay) {
                        //所求天数 <= mDay 时
                        high = high > mHigh ? high : mHigh;
                        low = low < mLow ? low : mLow;
                    } else {
                        high = maxs.get(OHLCData.size() - i - 1);
                        low = mins.get(OHLCData.size() - i - 1);
                        //所求天数 > mDay 时
                        for (int a = mins.size() - 1; a >= mins.size() - mDay; a--) {
                            high = high > maxs.get(a) ? high : maxs.get(a);
                            low = low < mins.get(a) ? low : mins.get(a);
                        }

                    }
                }


                if (high != low) {
                    rSV = (Double.parseDouble(oHLCEntity.getClose()) - low) / (high - low) * 100;


                }


                if (i == OHLCData.size() - 1) {
//					k = rSV;
//					d = k;
                    /*k = 50 * 2 / 3 + rSV / 3;
					d = 50 * 2 / 3 + k / 3;*/
                    d = k = rSV;

                } else {
                    k = k * 2 / 3 + rSV / 3;
                    d = d * 2 / 3 + k / 3;
                }
                j = 3 * k - 2 * d;

                ks.add(k);
                ds.add(d);
                js.add(j);
            }
            for (int i = ks.size() - 1; i >= 0; i--) {
                Ks.add(ks.get(i));
                Ds.add(ds.get(i));
                Js.add(js.get(i));
            }

            for (Double dd : Ks
                    ) {
                LogUtil.e("K   " + dd);
            }
            for (Double dd : Ds
                    ) {
                LogUtil.e("d   " + dd);
            }
            for (Double ss : Js
                    ) {
                LogUtil.e("j   " + ss);
            }


        }

    }


    public ArrayList<Double> getK() {
        return Ks;
    }

    public ArrayList<Double> getD() {
        return Ds;
    }

    public ArrayList<Double> getJ() {
        return Js;
    }
}
