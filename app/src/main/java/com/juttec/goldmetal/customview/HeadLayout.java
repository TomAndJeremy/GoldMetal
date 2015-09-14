package com.juttec.goldmetal.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;

/**
 * Created by Jeremy on 2015/9/11.
 */
public class HeadLayout extends RelativeLayout {



    public HeadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.custom_head, this);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyHeadView);


        String titleText = array.getString(R.styleable.MyHeadView_headtitle);
        TextView title = (TextView) this.findViewById(R.id.head_title);
        title.setText(titleText);

        String leftText = array.getString(R.styleable.MyHeadView_lefttext);
        if (leftText != null) {
            TextView leftTextView = (TextView) this.findViewById(R.id.left_text);
            leftTextView.setText(leftText);
        }
        int leftimg = array.getResourceId(R.styleable.MyHeadView_leftimg, 0);
        if (leftimg != 0) {
            ImageView leftImageView = (ImageView) this.findViewById(R.id.left_img);
            leftImageView.setImageResource(leftimg);
        }

        String rightText = array.getString(R.styleable.MyHeadView_righttext);
        if (rightText != null) {
            TextView rightTextView = (TextView) this.findViewById(R.id.right_text);
            rightTextView.setText(rightText);
        }

        int rightimg = array.getResourceId(R.styleable.MyHeadView_rightimg, 0);
        if (rightimg != 0) {
            ImageView rightImageView = (ImageView) this.findViewById(R.id.right_img);
            rightImageView.setImageResource(rightimg);
        }
        array.recycle();

    }


}
