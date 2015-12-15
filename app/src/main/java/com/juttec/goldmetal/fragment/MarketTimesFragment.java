package com.juttec.goldmetal.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.bean.MyEntity;
import com.juttec.goldmetal.bean.chartentity.TimesEntity;
import com.juttec.goldmetal.customview.chartview.TimesView;
import com.juttec.goldmetal.utils.GetNetworkData;
import com.juttec.goldmetal.utils.ToastUtil;

import java.util.List;


public class MarketTimesFragment extends Fragment {
	private TimesView mTimesView;
	private Context mContext;

	private List<TimesEntity.ResultEntity> datas;
	private MyEntity myEntity;
	private Handler handler;
	private TimesEntity timesEntity;
	private static final int NEWEST = 0;
	private List<TimesEntity> timesList;


	private static MarketTimesFragment marketTimesFragment;
	private static String TIME_URL;

	private GetNetworkData getNetworkData;



	/**
	 * 静态工厂方法需要一个值来初始化fragment的参数，
	 * 然后返回新的fragment到调用者
	 */
	public static MarketTimesFragment newInstance(String myurl) {
		if(marketTimesFragment == null){
			marketTimesFragment = new MarketTimesFragment();
		}
		TIME_URL = myurl;
		return marketTimesFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		handler = new MyHandler();
		timesEntity = new TimesEntity();
		myEntity = new MyEntity(timesEntity);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_market_times, null);
		mTimesView = (TimesView) view.findViewById(R.id.my_fenshi_view);
		return view;
	}


	@Override
	public void onResume() {
		super.onResume();
		getNetworkData = new GetNetworkData();
		getNetworkData.getKLineData(TIME_URL, myEntity, mContext, handler, NEWEST,60*1000);//60秒刷新一次
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
			timesEntity = (TimesEntity) myEntity.getObject();
			switch (msg.what) {
				case NEWEST:
					datas = timesEntity.getResult();
					if (datas.size() == 0) {
						ToastUtil.showShort(mContext, "没有数据...");
						break;
					} else {

						mTimesView.setTimesList(datas);
						break;
					}
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		getNetworkData.stop();
	}
}
