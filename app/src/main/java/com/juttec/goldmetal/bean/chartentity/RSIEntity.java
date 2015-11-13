package com.juttec.goldmetal.bean.chartentity;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 */
public class RSIEntity {
	

	private List<Double> RSI1;


	private List<Double> diffs;//当日差值


	private int mParameter;//影响RSI指标变化的 变量 默认值为：6 12 24  （范围：2-99）

	public RSIEntity(List<KChartInfo.ResultEntity> OHLCData) {
		RSI1 = new ArrayList<Double>();



		diffs = new ArrayList<Double>();


		List<Double> rsi1 = new ArrayList<Double>();


		mParameter = 6;


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
				open  = Double.parseDouble(OHLCData.get(i).getOpen());

				diffs.add(close-open);

				if(OHLCData.size() - i <= mParameter){
					//所求天数 <= mParameter 时
					for(int j = diffs.size() - 1; j >= 0; j--){


						if(diffs.get(j)>=0){
							//上涨 总数
							riseTotal += diffs.get(j);

						}else{
							//下跌 总数
							fallTotal += diffs.get(j);
						}
					}


				}else{
					//所求天数 > mParameter 时
					riseTotal = 0;
					fallTotal = 0;
					//所求天数 > mDay 时
					for (int a = diffs.size() - 1; a >= diffs.size() - mParameter; a--) {
						if(diffs.get(a)>=0){
							//上涨 总数
							riseTotal += diffs.get(a);

						}else{
							//下跌 总数
							fallTotal += diffs.get(a);
						}
					}

				}

				value = riseTotal/(riseTotal + fallTotal)*100;

				rsi1.add(value);

			}

			for (int i = rsi1.size() - 1; i >= 0; i--) {
				RSI1.add(rsi1.get(i));
			}
		}

	}

	public List<Double> getValue() {
		return RSI1;
	}
  /*	private List<Double> RSI1;
	private List<Double> RSI2;
	private List<Double> RSI3;


	private List<Double> diffs;//当日差值


	private int mParameter;//影响RSI指标变化的 变量 默认值为：6 12 24  （范围：2-99）

	public RSIEntity(List<KChartInfo.ResultEntity> OHLCData) {
		int n = 1;
		List<Double> lc = new ArrayList<>();
		for (int i = 0; i < OHLCData.size(); i++) {
			double close = Double.parseDouble(OHLCData.get(i).getClose());
			if (i < n) {
				lc.add(close);
			} else {
				lc.add(Double.parseDouble(OHLCData.get(i - n).getClose()));
			}
		}

	}
*/


}
