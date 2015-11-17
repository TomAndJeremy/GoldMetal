package com.juttec.goldmetal.bean.chartentity;

import java.util.ArrayList;
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


	public BOLLEntity(List<KChartInfo.ResultEntity> OHLCData) {
		UPs = new ArrayList<Double>();
		MBs = new ArrayList<Double>();
		DNs = new ArrayList<Double>();

		List<Double> ups = new ArrayList<Double>();
		List<Double> mbs = new ArrayList<Double>();
		List<Double> dns = new ArrayList<Double>();

		List<Double> mas = new ArrayList<Double>();//ma数据的集合


		List<Double> closes= new ArrayList<Double>();//收盘价的集合

		double close = 0.0;//收盘价
		double closeTotal = 0.0;//总收盘价

		double ma = 0.0;//N日内的收盘价之和÷N
		double md = 0.0;//  标准差   MD=平方根N日的（C－MA）的两次方之和除以N

		double MB = 0.0;
		double UP = 0.0;
		double DN = 0.0;


		if (OHLCData != null && OHLCData.size() > 0) {

			for (int i = OHLCData.size() - 1; i >= 0; i--) {
				close = Double.parseDouble(OHLCData.get(i).getClose());
				closes.add(close);

				closeTotal = closeTotal + close;


				//计算MA  与  MD
				if(OHLCData.size() - i < N){
					//所求天数 < N时
					ma = closeTotal/(OHLCData.size() - i);
					double sum = 0.0;
					sum = sum+ (close - ma)*(close - ma);
					md = Math.sqrt(sum/(OHLCData.size() - i));

				}else{
					double mCloseTotal = closeTotal;
					//所求天数 >= N时
					for(int j = OHLCData.size() - i-N;j>0;j--){
						mCloseTotal = mCloseTotal - closes.get(j);
					}

					ma = mCloseTotal/N;
					double sum = 0.0;
					sum = sum+ (close - ma)*(close - ma);
					md = Math.sqrt(sum/N);
				}

				mas.add(ma);

				//第一天的MB值  设置为 第一天的MA值
				if(i ==OHLCData.size() - 1 ){
					MB = mas.get(0);

				}else{
					MB = mas.get(OHLCData.size()-i-2);
				}

				UP = MB + k*md;
				DN = MB - k*md;

				mbs.add(MB);
				ups.add(UP);
				dns.add(DN);
			}


			for (int i = mbs.size() - 1; i >= 0; i--) {
				MBs.add(mbs.get(i));
				UPs.add(ups.get(i));
				DNs.add(DNs.get(i));
			}
		}

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
