package com.juttec.goldmetal.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.bean.MyEntity;
import com.juttec.goldmetal.bean.chartentity.KChartInfo;
import com.juttec.goldmetal.customview.chartview.KChartsView;
import com.juttec.goldmetal.utils.GetNetworkData;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.util.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class MarketKChartsFragment extends Fragment {
	private KChartsView mMyChartsView;
	private List<KChartInfo.ResultEntity> datas;
	private Handler handler;
	private MyEntity myEntity;
	private static final int NEWEST = 0;  //加载最新数据的标识
	private KChartInfo kChartInfo;
	public static String url;
	private static MarketKChartsFragment kChartsFragment;

	/**
	 * 静态工厂方法需要一个int型的值来初始化fragment的参数，
	 * 然后返回新的fragment到调用者
	 */
	public static MarketKChartsFragment newInstance(String myurl) {

		kChartsFragment = new MarketKChartsFragment();
		url = myurl;
		return kChartsFragment;
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_kcharts, null);
		mMyChartsView = (KChartsView) view.findViewById(R.id.my_charts_view);
		handler = new MyHandler();
		datas = new ArrayList<KChartInfo.ResultEntity>();
		kChartInfo = new KChartInfo();
		myEntity = new MyEntity(kChartInfo);
		GetNetworkData.getKLineData(url, myEntity, getActivity(), handler,NEWEST);
		return view;
	}



	/**
	 * 自定义Handler
	 */
	class MyHandler extends Handler {

		public MyHandler() {
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			kChartInfo = (KChartInfo) myEntity.getObject();
			switch (msg.what) {
				case NEWEST:
					datas = kChartInfo.getResult();
					if (datas.size() == 0) {
						ToastUtil.showShort(getActivity(), "没有数据...");
						break;
					} else {
						mMyChartsView.setOHLCData(datas);
						mMyChartsView.setLowerChartTabTitles(new String[]{"MACD", "KDJ", "RSI"});
						mMyChartsView.postInvalidate();
						break;
					}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtils.e("onDestroy");
		kChartsFragment = null;
	}
}
