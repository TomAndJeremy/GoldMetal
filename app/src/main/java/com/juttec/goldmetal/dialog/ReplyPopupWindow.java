package com.juttec.goldmetal.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.utils.ToastUtil;

/**
 * Created by Administrator on 2015/10/11.
 * 用于评论 和 回复 显示的 popupwindow
 */
public class ReplyPopupWindow {

    private Context mContext;
    //评论或回复的输入框
    private EditText mEditText;
    //表情按钮
    private ImageButton mImageButton;
    //发送按钮
    private Button mSend;

    private PopupWindow popupWindow;



    public ReplyPopupWindow(Context context){

        mContext = context;

    }


    public ReplyPopupWindow create(){
       View contentview = LayoutInflater.from(mContext).inflate(R.layout.commonality_comments, null);
        contentview.setFocusable(true); // 这个很重要
        contentview.setFocusableInTouchMode(true);
        popupWindow = new PopupWindow(contentview, LinearLayout.LayoutParams.MATCH_PARENT, 250);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        contentview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                    popupWindow.dismiss();

                    return true;
                }
                return false;
            }
        });



        mEditText = (EditText) contentview.findViewById(R.id.comment_et_reply);
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();

        InputMethodManager inputMethodManager =
                (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(mEditText.getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);


        mImageButton = (ImageButton) contentview.findViewById(R.id.comment_ib_emoji);

        mSend = (Button) contentview.findViewById(R.id.btn_send);

        //发送按钮的点击事件
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mEditText.getText().toString();
                if(TextUtils.isEmpty(content)||"".equals(content)||content.trim().length()<=0){
                    ToastUtil.showShort(mContext, "内容不能为空");
                    return;
                }
                mOnClickSendListener.onClickSend(content);
            }
        });

        return this;
    }


    //popupwindowde 的显示
    public void show(View view){
        popupWindow.showAtLocation(((Activity)mContext).getCurrentFocus(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //popupwindowde 的消失
    public  void  dismiss(){
        popupWindow.dismiss();
    }


    //设置edittext中 提示 type:0 表示评论   1：回复
    public void setHint(int type,String name){

        if(type==0){
            mEditText.setHint("评论" + name);
        }else{
            mEditText.setHint("回复" + name);
        }
    }






    //接口  作用：发送按钮的点击事件
    public interface  OnClickSendListener{
        void onClickSend(String content);
    }


    private OnClickSendListener mOnClickSendListener;

    public void setOnClickSendListener(OnClickSendListener listener){
        mOnClickSendListener = listener;
    }

}
