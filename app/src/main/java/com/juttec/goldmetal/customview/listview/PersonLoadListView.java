package com.juttec.goldmetal.customview.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * 
 * 自定义ListView  添加头部头像和关注按钮  和 底部加载更多布局
 *
 */
public class PersonLoadListView extends ListView implements
		AbsListView.OnScrollListener {

	private LoadingFooter mLoadingFooter;//底部

	private PersonHeader mPersonHeader;//个人主页头部


	private OnLoadNextListener mLoadNextListener;

	public PersonLoadListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PersonLoadListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PersonLoadListView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mPersonHeader = new PersonHeader(getContext());
		addHeaderView(mPersonHeader.getView());
		mLoadingFooter = new LoadingFooter(getContext());
		addFooterView(mLoadingFooter.getView());
		setOnScrollListener(this);
	}

	public PersonHeader getHeaderView() {
		return mPersonHeader;
	}

	public void setOnLoadNextListener(OnLoadNextListener listener) {
		mLoadNextListener = listener;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mLoadingFooter.getState() == LoadingFooter.State.Loading
				|| mLoadingFooter.getState() == LoadingFooter.State.TheEnd) {
			return;
		}
		if (firstVisibleItem + visibleItemCount >= totalItemCount
				&& totalItemCount != 0
				&& totalItemCount != (getHeaderViewsCount() + getFooterViewsCount())
				&& mLoadNextListener != null) {
			mLoadingFooter.setState(LoadingFooter.State.Loading);
			mLoadNextListener.onLoadNext();
		}
	}

	public void setState(LoadingFooter.State status) {
		mLoadingFooter.setState(status);
	}

	public void setState(LoadingFooter.State status, long delay) {
		mLoadingFooter.setState(status, delay);
	}


	public interface OnLoadNextListener {
		public void onLoadNext();
	}


	public void stopFooterAnimition(){
		mLoadingFooter.stopFooterAnim();
	}

}
