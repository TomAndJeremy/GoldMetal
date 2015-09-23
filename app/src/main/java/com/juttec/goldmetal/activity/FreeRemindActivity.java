package com.juttec.goldmetal.activity;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.TransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.dialog.MyAlertDialog;
import com.juttec.goldmetal.utils.LogUtil;

/**
 * 现货白银免费提醒
 */
public class FreeRemindActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout remindContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_remind);

        initView();
    }

    private void initView() {
        remindContent = (LinearLayout) this.findViewById(R.id.point_remind_content);
        Button btAdd = (Button) this.findViewById(R.id.remind_bt_add);
        btAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remind_bt_add:
                final MyAlertDialog dialog = new MyAlertDialog(FreeRemindActivity.this).builder()
                        .setTitle("添加提醒").setEditText("请输入价格");

                dialog.setRadioGroupVisiable();
                dialog.edittxt_result.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
                dialog.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                        String sPoint = "最新价";
                        LogUtil.e(FreeRemindActivity.this, 54, dialog.getResult());
                        if (dialog.getResult() == null || "".equals(dialog.getResult())) {
                            dialog.dismiss();
                            return;
                        }
                        sPoint = sPoint + dialog.s + Double.parseDouble(dialog.getResult());
                        addView(sPoint);
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(FreeRemindActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        dialog.dismiss();
                    }
                });
                dialog.show();


        }
    }

    private void addView(String s) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.point_remind_layout, null);
        view.setLayoutParams(lp);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                remindContent.removeView(v);
                return true;
            }
        });
        TextView tv = (TextView) view.findViewById(R.id.tv_remind_level);
        tv.setText(s);
        remindContent.addView(view);

    }
}
