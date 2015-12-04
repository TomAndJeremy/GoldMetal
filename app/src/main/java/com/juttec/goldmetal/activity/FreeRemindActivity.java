package com.juttec.goldmetal.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.bean.ReminderFloatBeen;
import com.juttec.goldmetal.bean.ReminderPointBeen;
import com.juttec.goldmetal.dialog.MyAlertDialog;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.ReminderDao;
import com.juttec.goldmetal.utils.SharedPreferencesUtil;
import com.juttec.goldmetal.utils.ToastUtil;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 现货白银免费提醒
 */
public class FreeRemindActivity extends AppCompatActivity implements View.OnClickListener {


    /**
     * 输入框小数的位数
     */
    private static final int DECIMAL_DIGITS = 2;


    private SwitchCompat switchCompat;//浮动提醒的开关
    private TextView valueRemind;//根据浮动值 和 基准值 算出来的 提醒值

    private TextView save;//保存浮动提醒值

    private EditText etBase, etFloat;//基准值 和 浮动值 的 输入框

    private Button btAdd;//点位提醒的添加按钮
    private LinearLayout remindContent;//点位提醒的布局

    private DecimalFormat mDecimalFormat = new DecimalFormat("####.00");//格式化小数

    private MyAlertDialog myAlertDialog;//提示确定删除点位提醒的 dialog
    private MyAlertDialog dialog;//添加点位提醒的dialog


    String symbol;//当前股票号码


    //isFloatingAlert 浮动提醒开关是否打开  boolean
    //ReferencePrice 基准价 float
    //FloatingValue  浮动值 float


    private ReminderDao reminderDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_remind);
        myAlertDialog = new MyAlertDialog(this);
        dialog = new MyAlertDialog(this);
        reminderDao = new ReminderDao(this);



        Intent intent = getIntent();
        symbol = intent.getStringExtra("symbol");

        initView();
        initEnent();
    }

    //初始化控件
    private void initView() {
        remindContent = (LinearLayout) this.findViewById(R.id.point_remind_content);
        btAdd = (Button) this.findViewById(R.id.remind_bt_add);
        btAdd.setSelected(true);
        btAdd.setOnClickListener(this);



        save = (TextView) this.findViewById(R.id.tv_save_ar);
        save.setOnClickListener(this);

        etBase = (EditText) this.findViewById(R.id.et_base_value);
        etFloat = (EditText) this.findViewById(R.id.et_float_value);

     /*   //设置基准价和 浮动值 的值
        etBase.setText("" + (Float) SharedPreferencesUtil.getParam(this, "ReferencePrice", (float) 100.01));
        etFloat.setText("" + (Float) SharedPreferencesUtil.getParam(this, "FloatingValue", (float) 10.01));
*/

        valueRemind = (TextView) this.findViewById(R.id.value_remind);

        switchCompat = (SwitchCompat) this.findViewById(R.id.switch_remind);
        //浮动提醒开关 默认为false
      //  switchCompat.setChecked((Boolean) SharedPreferencesUtil.getParam(this, "isFloatingAlert", false));
        if (switchCompat.isChecked()) {
            valueRemind.setVisibility(View.VISIBLE);
            valueRemind.setText(getTextValue(etBase.getText().toString(), etFloat.getText().toString()));

            save.setSelected(true);
            save.setClickable(true);

        } else {
            save.setSelected(false);
            save.setClickable(false);

        }


        isSetedFloat(reminderDao.getAllFloatDate());
        showPointReminder(reminderDao.getAllPointDate());
    }


    //初始化事件
    private void initEnent() {
        //基准值输入框的监听
        etBase.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //判断浮动提醒开关 是否打开
                if (!switchCompat.isChecked()) {
                    return;
                }
                if ("".equals(etBase.getText().toString().trim())) {
                    etBase.removeTextChangedListener(this);

                    etBase.setText("");
                    etBase.addTextChangedListener(this);//防止循环调用
                }
                String base = etBase.getText().toString().trim();
                String floatvalue = etFloat.getText().toString().trim();
                valueRemind.setText(getTextValue(base, floatvalue));


            }
        });


        //浮动值输入框的监听
        etFloat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //判断浮动提醒开关 是否打开
                if (!switchCompat.isChecked()) {
                    return;
                }

                if ("".equals(etFloat.getText().toString().trim())) {
                    etFloat.removeTextChangedListener(this);
                    etFloat.setText("");
                    etFloat.addTextChangedListener(this);
                }
                String base = etBase.getText().toString().trim();
                String floatvalue = etFloat.getText().toString().trim();
                valueRemind.setText(getTextValue(base, floatvalue));


            }
        });


        //浮动提醒开关的 监听
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LogUtil.d("switchCompat.setOnCheckedChange:" + isChecked);
                if (isChecked) {
                    valueRemind.setVisibility(View.VISIBLE);
                    valueRemind.setText(getTextValue(etBase.getText().toString(), etFloat.getText().toString()));
                    save.setSelected(true);
                    save.setClickable(true);



                } else {
                    save.setSelected(false);
                    save.setClickable(false);
                    valueRemind.setVisibility(View.GONE);

                    //如果关闭，则删除数据库中的该条数据
                    reminderDao.deleteFloat(symbol);

                }
                //将设置的值存起来
                SharedPreferencesUtil.setParam(FreeRemindActivity.this, "isFloatingAlert", isChecked);
            }
        });


        //浮动提醒开关的 touch事件
        switchCompat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String base = etBase.getText().toString().trim();
                String floatvalue = etFloat.getText().toString().trim();
                if ("".equals(base) || "".equals(floatvalue)) {
                    ToastUtil.showShort(FreeRemindActivity.this, "基准价或浮动值不能为空");
                    return true;
                }

                if ((Float.parseFloat(base) - Float.parseFloat(floatvalue)) < 0) {
                    ToastUtil.showShort(FreeRemindActivity.this, "基准价不能小于浮动值");
                    return true;
                }
                return false;
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remind_bt_add:
                //添加点位提醒
                dialog.builder()
                        .setTitle("添加提醒").setEditText("请输入价格");

                dialog.setRadioGroupVisiable();
                dialog.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).
                                hideSoftInputFromWindow(FreeRemindActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


                        String sPoint = "最新价";
                        LogUtil.e(FreeRemindActivity.this, 54, dialog.getResult());
                        if (dialog.getResult() == null || "".equals(dialog.getResult())) {
                            ToastUtil.showShort(FreeRemindActivity.this, "提醒值不能为空");
                            return;
                        }
                        String value = mDecimalFormat.format(Double.parseDouble(dialog.getResult()));
                        sPoint = sPoint + dialog.s + value;

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("StockSymbol", symbol);
                        contentValues.put("Operator", dialog.s);
                        contentValues.put("Value", Double.parseDouble(value));

                        reminderDao.insert(contentValues, 2);

                        addView(sPoint);
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).
                                hideSoftInputFromWindow(FreeRemindActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    }
                });
                dialog.setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(FreeRemindActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                });
                dialog.show();
                dialog.setEditType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);//InputType.TYPE_NUMBER_FLAG_DECIMAL |

                break;
            case R.id.tv_save_ar:

                ContentValues contentValues = new ContentValues();
                contentValues.put("StockSymbol", symbol);
                String base = etBase.getText().toString().trim();
                String floatvalue = etFloat.getText().toString().trim();
                contentValues.put("BasePrice", base);
                contentValues.put("FloatPrice", floatvalue);

                reminderDao.insert(contentValues, 1);
                break;
        }
    }

    private void addView(final String s) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.point_remind_layout, null);
        view.setLayoutParams(lp);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View addview) {

                myAlertDialog.builder().setTitle("删除点位提醒")
                        .setMsg(s)
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String operator = s.substring(3, 4);
                                String value = s.substring(4);

                                if (reminderDao.deletePoint(symbol, operator, value)) {
                                    remindContent.removeView(addview);
                                    ToastUtil.showShort(FreeRemindActivity.this, "删除成功");
                                }


                            }
                        }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myAlertDialog.dismiss();
                    }
                }).show();

                return true;
            }
        });

        TextView tv = (TextView) view.findViewById(R.id.tv_remind_level);
        tv.setText(s);
        remindContent.addView(view);

    }


    //根据基准价和 浮动值 计算提醒值
    private String getTextValue(String base, String index) {

        if ("".equals(base) || "".equals(index)) {
            ToastUtil.showShort(FreeRemindActivity.this, "基准价或浮动值不能为空");
            return "";
        }

        if (switchCompat.isChecked()) {
            float highBoard = 0;
            float lowBoard = 0;
            try {
                highBoard = Float.parseFloat(base) + Float.parseFloat(index);
                lowBoard = Float.parseFloat(base) - Float.parseFloat(index);

            } catch (NumberFormatException e) {
                e.printStackTrace();
                return "";
            }

            if (lowBoard <= 0) {
                ToastUtil.showShort(this, "基准价不能小于浮动值");
                return "";
            }
            //将基准价保存
            SharedPreferencesUtil.setParam(FreeRemindActivity.this, "ReferencePrice", Float.parseFloat(mDecimalFormat.format(Float.parseFloat(base))));
            //将设置的浮动值存起来
            SharedPreferencesUtil.setParam(FreeRemindActivity.this, "FloatingValue", Float.parseFloat(mDecimalFormat.format(Float.parseFloat(index))));

            return "报价>=" + mDecimalFormat.format(lowBoard) + "或报价<=" + mDecimalFormat.format(highBoard);
        }
        return "";
    }

    /**
     * 是否设置了浮动提醒
     * @param floatBeens
     */
    private void isSetedFloat(List<ReminderFloatBeen> floatBeens) {
        if (floatBeens == null) {
            return;
        }
        for (ReminderFloatBeen been :
                floatBeens) {
            if (symbol.equals(been.getStock())) {
                switchCompat.setChecked(true);
                save.setSelected(true);
                save.setClickable(true);

                etBase.setText(been.getBasePrice());
                etFloat.setText(been.getFloatPrice());

            }
        }
    }


    //如果设置了点位提醒，则添加到界面上
    private void showPointReminder(List<ReminderPointBeen> pointBeens) {
        if (pointBeens == null) {
            return;
        }
        for (ReminderPointBeen been : pointBeens
                ) {
            if (symbol.equals(been.getStock())) {

                addView("最新价" + been.getOperator() + been.getValue());
            }
        }
    }

}
