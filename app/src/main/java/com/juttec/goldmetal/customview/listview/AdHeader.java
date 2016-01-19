package com.juttec.goldmetal.customview.listview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.utils.LogUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
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
	private String[] titles; // 图片标题
	private List<View> dots; // 图片标题正文的那些点

	private ArrayList<ImageView> viewlist;

	private TextView tv_title;
	private int currentItem = 0; // 当前图片的索引号


	private List<String> res;

	private Context mContext;

	private DisplayImageOptions options;



	public AdHeader(Context context) {
		mContext = context;
		mHeader = LayoutInflater.from(context).inflate(R.layout.header_advertisement, null);


		dots = new ArrayList<View>();
		dots.add(mHeader.findViewById(R.id.v_dot0));
		dots.add(mHeader.findViewById(R.id.v_dot1));
		dots.add(mHeader.findViewById(R.id.v_dot2));
		dots.add(mHeader.findViewById(R.id.v_dot3));
		dots.add(mHeader.findViewById(R.id.v_dot4));

		tv_title = (TextView) mHeader.findViewById(R.id.tv_title);
		viewPager = (ViewPager) mHeader.findViewById(R.id.vp);

		initData();
	}


	private void initData(){
		options = new DisplayImageOptions.Builder()
				.cacheInMemory(true) // default
				.cacheOnDisk(true) // default
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				.build();

		res = new ArrayList<String>();
		res.add("http://img3.imgtn.bdimg.com/it/u=1496517588,1412442621&fm=11&gp=0.jpg");
		res.add("http://img1.imgtn.bdimg.com/it/u=358534528,3514896003&fm=23&gp=0.jpg");
		res.add("http://img1.imgtn.bdimg.com/it/u=1308380740,689566047&fm=23&gp=0.jpg");
		res.add("http://img2.imgtn.bdimg.com/it/u=2096082599,1768416501&fm=23&gp=0.jpg");
		res.add("http://img5.imgtn.bdimg.com/it/u=991005744,2916820001&fm=23&gp=0.jpg");
		titles = new String[res.size()];
		titles[0] = "男神男神男神男神";
		titles[1] = "萝莉萝莉萝莉萝莉";
		titles[2] = "正太正太正太正太";
		titles[3] = "大叔大叔大叔大叔";
		titles[4] = "御姐御姐御姐御姐";

		viewlist = new ArrayList<ImageView>();
		for(int i=0;i<res.size();i++){
			ImageView imageview = new ImageView(mContext);
			imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
			ImageLoader.getInstance().displayImage(res.get(i), imageview, options);
			viewlist.add(imageview);
		}


		viewPager.setAdapter(new MyAdapter());
		// 设置一个监听器，当ViewPager中的页面改变时调用
		viewPager.setOnPageChangeListener(new MyPageChangeListener());

//		tv_title.setText(titles[0]);


		viewPager.setCurrentItem(Integer.MAX_VALUE/2-(Integer.MAX_VALUE/2)%viewlist.size());//默认在中间，使用户看不到边界
		//开始轮播效果
		handler.sendEmptyMessageDelayed(MSG_BREAK_SILENT, MSG_DELAY);

	}



	public View getView() {
		return mHeader;
	}

	public View getViewPager() {
		return viewPager;
	}




	/**
	 * 请求更新显示的View。
	 */
	protected static final int MSG_UPDATE_IMAGE  = 1;
	/**
	 * 请求暂停轮播。
	 */
	protected static final int MSG_KEEP_SILENT   = 2;
	/**
	 * 请求恢复轮播。
	 */
	protected static final int MSG_BREAK_SILENT  = 3;
	/**
	 * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
	 * 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
	 * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
	 */
	protected static final int MSG_PAGE_CHANGED  = 4;

	//轮播间隔时间
	protected static final long MSG_DELAY = 5000;

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			//检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
			if (handler.hasMessages(MSG_UPDATE_IMAGE)){
				handler.removeMessages(MSG_UPDATE_IMAGE);
			}


			LogUtil.d("msg.what------------------"+msg.what);

			switch (msg.what) {
				case MSG_UPDATE_IMAGE:
					currentItem++;
					viewPager.setCurrentItem(currentItem);
					//准备下次播放
					handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
					break;
				case MSG_KEEP_SILENT:
					//只要不发送消息就暂停了
					break;
				case MSG_BREAK_SILENT:
					handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
					break;
				case MSG_PAGE_CHANGED:
					//记录当前的页号，避免播放的时候页面显示不正确。
					currentItem = msg.arg1;
					break;
				default:
					break;
			}
		}
	};



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

			handler.sendMessage(Message.obtain(handler, MSG_PAGE_CHANGED, position, 0));

			position %= viewlist.size();
			if (position<0){
				position = viewlist.size()+position;
			}

			tv_title.setText(titles[position]);
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;

		}

		//覆写该方法实现轮播效果的暂停和恢复
		@Override
		public void onPageScrollStateChanged(int arg0) {
			switch (arg0) {
				case ViewPager.SCROLL_STATE_DRAGGING:
					handler.sendEmptyMessage(MSG_KEEP_SILENT);
					break;
				case ViewPager.SCROLL_STATE_IDLE:
					handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
					break;
				default:
					break;
			}
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
			//设置成最大，使用户看不到边界
			return Integer.MAX_VALUE;

		}

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
//			ImageView imageview = new ImageView(mContext);
//			ViewPager.LayoutParams params = new ViewPager.LayoutParams();
//			imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
//			ImageLoader.getInstance().displayImage(res.get(arg1), imageview, options);
//			((ViewPager) arg0).addView(imageview);
//			return imageview;

			//对ViewPager页号求模取出View列表中要显示的项
			position %= viewlist.size();
			if (position<0){
				position = viewlist.size()+position;
			}
			ImageView view = viewlist.get(position);
			//如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
			ViewParent vp =view.getParent();
			if (vp!=null){
				ViewGroup parent = (ViewGroup)vp;
				parent.removeView(view);
			}
			container.addView(view);
			//add listeners here if necessary
			return view;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2)
		{
//			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == arg1;
		}
	}






}
