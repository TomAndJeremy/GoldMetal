package com.juttec.goldmetal.customview.listview;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

/**
 * 展示广告的ViewPager头部
 * AutoLoadListView的头部
 */

public class AdHeader {


	private View mHeader;
	private ViewPager viewPager; // android-support-v4中的滑动组件
	private LinearLayout ll_dot;//点的布局
	private String[] titles; // 图片标题
	private List<View> dots; // 图片标题正文的那些点

	private ArrayList<ImageView> viewlist;

	private TextView tv_title;
	private int currentItem = 0; // 当前图片的索引号


	//private List<String> res;
	private int[] res = {R.mipmap.content_news_analysis_pic_bg};

	private Context mContext;

	private DisplayImageOptions options;



	public AdHeader(Context context) {
		mContext = context;
		mHeader = LayoutInflater.from(context).inflate(R.layout.header_advertisement, null);

		tv_title = (TextView) mHeader.findViewById(R.id.tv_title);
		viewPager = (ViewPager) mHeader.findViewById(R.id.vp);
		ll_dot = (LinearLayout) mHeader.findViewById(R.id.ll_dot);


		initData();
	}


	private void initData(){
		options = new DisplayImageOptions.Builder()
				.cacheInMemory(true) // default
				.cacheOnDisk(true) // default
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				.build();

//		res = new ArrayList<String>();
//		res.add("http://img3.imgtn.bdimg.com/it/u=1496517588,1412442621&fm=11&gp=0.jpg");
//		res.add("http://img1.imgtn.bdimg.com/it/u=358534528,3514896003&fm=23&gp=0.jpg");
//		res.add("http://img1.imgtn.bdimg.com/it/u=1308380740,689566047&fm=23&gp=0.jpg");
//		res.add("http://img2.imgtn.bdimg.com/it/u=2096082599,1768416501&fm=23&gp=0.jpg");
//		res.add("http://img5.imgtn.bdimg.com/it/u=991005744,2916820001&fm=23&gp=0.jpg");



		titles = new String[res.length];
		for(int i=0;i<res.length;i++){
			titles[i] = "联储纪要续命金银 后市震荡等待良机";
		}


		dots = new ArrayList<View>();
		for(int i=0;i<res.length;i++){
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8,8);
			params.leftMargin = 5;
			View view = new View(mContext);
			view.setLayoutParams(params);
			dots.add(view);
		}
//		dots.add(mHeader.findViewById(R.id.v_dot0));
//		dots.add(mHeader.findViewById(R.id.v_dot1));
//		dots.add(mHeader.findViewById(R.id.v_dot2));
//		dots.add(mHeader.findViewById(R.id.v_dot3));
//		dots.add(mHeader.findViewById(R.id.v_dot4));


		viewlist = new ArrayList<ImageView>();
		for(int i=0;i<res.length;i++){
			ImageView imageview = new ImageView(mContext);
			imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageview.setImageResource(res[i]);
//			ImageLoader.getInstance().displayImage(res.get(i), imageview, options);
			viewlist.add(imageview);
		}


		viewPager.setAdapter(new MyAdapter());
		// 设置一个监听器，当ViewPager中的页面改变时调用
		viewPager.setOnPageChangeListener(new MyPageChangeListener());

//		tv_title.setText(titles[0]);


//		viewPager.setCurrentItem(Integer.MAX_VALUE/2-(Integer.MAX_VALUE/2)%viewlist.size());//默认在中间，使用户看不到边界
		//开始轮播效果
		//handler.sendEmptyMessageDelayed(MSG_BREAK_SILENT, MSG_DELAY);

	}



	public View getView() {
		return mHeader;
	}

	public View getViewPager() {
		return viewPager;
	}


	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 *
	 * @author Administrator
	 *
	 */
	private class MyPageChangeListener implements ViewPager.OnPageChangeListener
	{
		private int oldPosition = 0;
		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position)
		{
//			currentItem = position;
//			oldPosition = position;

//			handler.sendMessage(Message.obtain(handler, MSG_PAGE_CHANGED, position, 0));

//			position %= viewlist.size();
//			if (position<0){
//				position = viewlist.size()+position;
//			}

			tv_title.setText(titles[position]);
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;

		}

		//覆写该方法实现轮播效果的暂停和恢复
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2)
		{
		}
	}



	/**
	 * 填充ViewPager页面的适配器
	 *
	 * @author Administrator
	 *
	 */
	private class MyAdapter extends PagerAdapter
	{

		@Override
		public int getCount()
		{
			return res.length;

		}

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			ImageView imageview = new ImageView(mContext);
			ViewPager.LayoutParams params = new ViewPager.LayoutParams();
			imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageview.setImageResource(res[position]);
//			ImageLoader.getInstance().displayImage(res.get(arg1), imageview, options);
			((ViewPager) container).addView(imageview);
			return imageview;


		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2)
		{
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == arg1;
		}
	}






}
