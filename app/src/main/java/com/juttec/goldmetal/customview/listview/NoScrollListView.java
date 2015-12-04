package com.juttec.goldmetal.customview.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * 自定义的ListView  实现ListView中嵌套ListView效果
 *
 *
 */
public class NoScrollListView extends ListView {

	public NoScrollListView(Context context) {
		super(context);
		init();
	}

	public NoScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NoScrollListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}


	private void init(){
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
		this.setLayoutParams(layoutParams);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}


}
