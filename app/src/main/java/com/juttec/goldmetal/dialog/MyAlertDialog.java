package com.juttec.goldmetal.dialog;


import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.juttec.goldmetal.R;

public class MyAlertDialog {

    public String s;//记录大于还是小于

    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView txt_title;
    private TextView txt_msg;
    public EditText edittxt_result;
    private LinearLayout dialog_Group;
    private View dialog_marBottom;
    private LinearLayout ll_btn_yes_no;
    private Button btn_neg;
    private Button btn_pos;
    private Button btn_single;
    private Display display;
    private RadioGroup radioGroup;
    private RadioButton beyond, low;


    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showEditText = false;
    private boolean showLayout = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;
    private boolean showSingleBtn = false;
    private boolean showRadiaGroup = false;


    public MyAlertDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public MyAlertDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_alertdialog, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setVisibility(View.GONE);
        txt_msg = (TextView) view.findViewById(R.id.txt_msg);
        txt_msg.setVisibility(View.GONE);
        edittxt_result = (EditText) view.findViewById(R.id.edittxt_result);
        edittxt_result.setVisibility(View.GONE);
        dialog_Group = (LinearLayout) view.findViewById(R.id.dialog_Group);
        dialog_Group.setVisibility(View.GONE);
        dialog_marBottom = view.findViewById(R.id.dialog_marBottom);

        radioGroup = (RadioGroup) view.findViewById(R.id.dialog_radiogroup);
        radioGroup.setVisibility(View.GONE);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (group.getCheckedRadioButtonId()) {
                    case R.id.radiobutton_beyond:
                        s = ">";
                        break;
                    case R.id.radiobutton_low:
                        s = "<";
                        break;
                }


            }
        });





        ll_btn_yes_no = (LinearLayout) view.findViewById(R.id.ll_btn);
        ll_btn_yes_no.setVisibility(View.GONE);

        btn_single = (Button) view.findViewById(R.id.btn_single);
        btn_single.setVisibility(View.GONE);

        btn_neg = (Button) view.findViewById(R.id.btn_neg);
//		btn_neg.setVisibility(View.GONE);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
//		btn_pos.setVisibility(View.GONE);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.85), LayoutParams.WRAP_CONTENT));

        return this;
    }


    public MyAlertDialog setTitle(String title) {
        showTitle = true;
        if ("".equals(title)) {
            txt_title.setText("标题");
        } else {
            txt_title.setText(title);
        }
        return this;
    }

    public MyAlertDialog setEditText(String msg) {
        showEditText = true;
        if ("".equals(msg)) {
            edittxt_result.setHint("内容");
        } else {
            edittxt_result.setHint(msg);
        }
        return this;
    }


    //取得edittext里的内容
    public String getResult() {
        return edittxt_result.getText().toString();
    }

    //设置edittext的 输入类型
    public void setEditType(){
        edittxt_result.setInputType(EditorInfo.TYPE_CLASS_PHONE);
    }


    public MyAlertDialog setMsg(String msg) {
        showMsg = true;
        if ("".equals(msg)) {
            txt_msg.setText("内容");
        } else {
            txt_msg.setText(msg);
        }
        return this;
    }

    public MyAlertDialog setView(View view) {
        showLayout = true;
        if (view == null) {
            showLayout = false;
        } else
            dialog_Group.addView(view,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        return this;
    }


    public MyAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public MyAlertDialog setPositiveButton(String text,
                                           final OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("确定");
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public MyAlertDialog setNegativeButton(String text,
                                           final OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText("取消");
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }


    public MyAlertDialog setSingleButton(String text,
                                         final OnClickListener listener) {
        showSingleBtn = true;
//        if ("".equals(text)) {
//            btn_neg.setText("确定");
//        } else {
        btn_single.setText(text);
//        }
        btn_single.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }


    private void setLayout() {
        /*
         * if (!showTitle && !showMsg) { txt_title.setText("提示");
		 * txt_title.setVisibility(View.VISIBLE); }
		 */

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showEditText) {
            edittxt_result.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }

        if (showLayout) {
            dialog_Group.setVisibility(View.VISIBLE);
//            dialog_marBottom.setVisibility(View.GONE);
        }

		/*
		 * if (!showPosBtn && !showNegBtn) { btn_pos.setText("确定");
		 * btn_pos.setVisibility(View.VISIBLE); btn_pos.setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { dialog.dismiss(); } }); }
		 */

        if (showPosBtn && showNegBtn) {
//			btn_pos.setVisibility(View.VISIBLE);
//			btn_neg.setVisibility(View.VISIBLE);
            ll_btn_yes_no.setVisibility(View.VISIBLE);
        }

//		if (showPosBtn && !showNegBtn) {
//			btn_pos.setVisibility(View.VISIBLE);
//		}
//
//		if (!showPosBtn && showNegBtn) {
//			btn_neg.setVisibility(View.VISIBLE);
//		}

        if (showSingleBtn) {
            btn_single.setVisibility(View.VISIBLE);
        }
    }


    public MyAlertDialog setRadioGroupVisiable() {

        radioGroup.setVisibility(View.VISIBLE);
        RadioButton  beyond= (RadioButton) radioGroup.findViewById(R.id.radiobutton_beyond);
        beyond.setChecked(true);
        return this;

    }


    public void show() {
        setLayout();
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
