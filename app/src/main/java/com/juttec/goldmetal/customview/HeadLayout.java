package com.juttec.goldmetal.customview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;

/**
 * Created by Jeremy on 2015/9/11.
 * 自定义的头部
 */
public class HeadLayout extends RelativeLayout {

    TextView title;

    public HeadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);


        LayoutInflater.from(context).inflate(R.layout.custom_head, this);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyHeadView);


        //标题
        String titleText = array.getString(R.styleable.MyHeadView_headtitle);
        title = (TextView) this.findViewById(R.id.head_title);
        title.setText(titleText);


        //左边文字
        String leftText = array.getString(R.styleable.MyHeadView_lefttext);
        if (leftText != null) {
            TextView leftTextView = (TextView) this.findViewById(R.id.left_text);
            leftTextView.setText(leftText);
        }

        //左边图片
        int leftimg = array.getResourceId(R.styleable.MyHeadView_leftimg, 0);
        if (leftimg != 0) {
            ImageView leftImageView = (ImageView) this.findViewById(R.id.left_img);
            leftImageView.setImageResource(leftimg);
            boolean back = array.getBoolean(R.styleable.MyHeadView_back, true);//左上的图片点击时是否执行下面结束activity的事件

            if (back) {
                leftImageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Activity) getContext()).finish();
                    }
                });

            }

        }

        //右边文字
        String rightText = array.getString(R.styleable.MyHeadView_righttext);
        if (rightText != null) {
            TextView rightTextView = (TextView) this.findViewById(R.id.right_text);
            rightTextView.setText(rightText);
        }

        //右边图片
        int rightimg = array.getResourceId(R.styleable.MyHeadView_rightimg, 0);
        if (rightimg != 0) {
            ImageView rightImageView = (ImageView) this.findViewById(R.id.right_img);
            rightImageView.setImageResource(rightimg);
        }


        array.recycle();


    }

    public void setHeadTitle(String sTitle) {
        title.setText(sTitle);
    }


}
