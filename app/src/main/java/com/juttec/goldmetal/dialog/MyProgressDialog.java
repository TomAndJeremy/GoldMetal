package com.juttec.goldmetal.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;

/**
 * Created by Administrator on 2015/9/24.
 *
 * 自定义progressDialog
 */
public class MyProgressDialog {


    private LinearLayout ll_bg;//父布局
    private TextView tv_msg;//提示信息


    private Context mContext;
    private Dialog dialog;


    public MyProgressDialog(Context context){
        mContext = context;
    }


    public MyProgressDialog builder(){
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_myprogressdialog, null);
        ll_bg = (LinearLayout) view.findViewById(R.id.ll_progress_bg);
        tv_msg = (TextView) view.findViewById(R.id.tv_msg);

        // 定义Dialog布局和参数
        dialog = new Dialog(mContext, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        // 调整dialog背景大小
        ll_bg.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        return this;
    }



    public MyProgressDialog setMessage(String msg){

        tv_msg.setText(msg);
        return this;
    }


    public MyProgressDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public MyProgressDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }



    public void show() {
        dialog.show();
    }


    public void dismiss() {

        dialog.dismiss();
    }

}
