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
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.ReminderFloatBeen;
import com.juttec.goldmetal.bean.ReminderPointBeen;
import com.juttec.goldmetal.dialog.MyAlertDialog;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.ReminderDao;
import com.juttec.goldmetal.utils.ToastUtil;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 免费提醒界面
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


    private String symbol;//当前股票号码 由前个界面传递过来的  作为sharepreference的key值  value为boolean型代表浮动开关是否打开
    private String currentValueBase;//此股票的当前价  由前个界面传递过来的


    private ReminderDao reminderDao;

    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_remind);
        app = (MyApplication) getApplication();

        myAlertDialog = new MyAlertDialog(this);
        dialog = new MyAlertDialog(this);
        reminderDao = new ReminderDao(this);



        Intent intent = getIntent();
        symbol = intent.getStringExtra("symbol");
        currentValueBase = intent.getStringExtra("currentValue");

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


        valueRemind = (TextView) this.findViewById(R.id.value_remind);

        switchCompat = (SwitchCompat) this.findViewById(R.id.switch_remind);
        //浮动提醒开关 默认为false
        switchCompat.setChecked(isSetedFloat(reminderDao.getAllFloatDate(app.getUserInfoBean().getUserId())));
        if (switchCompat.isChecked()) {
            //此股票设置了浮动提醒
            //从数据库中取出 此股票的基准价和浮动值
            //提醒值显示
            valueRemind.setVisibility(View.VISIBLE);
            valueRemind.setText(getTextValue(etBase.getText().toString(), etFloat.getText().toString()));
            //保存按钮设置为可点击状态
            save.setSelected(true);
            save.setClickable(true);

        } else {
            //此股票没有设置浮动提醒
            save.setSelected(false);
            save.setClickable(false);
            //设置基准价(股票的当前价)和 浮动值 （默认的值）
            etBase.setText("" + currentValueBase);
            etFloat.setText("" +(Float.parseFloat(currentValueBase)-1>0?Float.parseFloat(currentValueBase)-1:Float.parseFloat(currentValueBase)));
        }

        showPointReminder(reminderDao.getAllPointDate(app.getUserInfoBean().getUserId()));
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
                    //打开浮动开关
                    valueRemind.setVisibility(View.VISIBLE);
                    valueRemind.setText(getTextValue(etBase.getText().toString(), etFloat.getText().toString()));
                    save.setSelected(true);
                    save.setClickable(true);
                    //设置浮动提醒
                    setFloatValue();
                } else {
                    //关闭浮动开关
                    save.setSelected(false);
                    save.setClickable(false);
                    valueRemind.setVisibility(View.GONE);

                    //如果关闭，则删除数据库中的该条数据
                    reminderDao.deleteFloat(app.getUserInfoBean().getUserId(),symbol);

                }
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
                        contentValues.put("UserId", app.getUserInfoBean().getUserId());

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
                //保存按钮  设置浮动提醒
                setFloatValue();
                break;
        }
    }




    /**
     * 设置浮动提醒
     * 将数据存入数据库 若已存在该股票 则执行更新操作
     */
    private void setFloatValue(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("StockSymbol", symbol);
        String base = etBase.getText().toString().trim();
        String floatvalue = etFloat.getText().toString().trim();
        contentValues.put("BasePrice", Float.parseFloat(mDecimalFormat.format(Float.parseFloat(base))));
        contentValues.put("FloatPrice", Float.parseFloat(mDecimalFormat.format(Float.parseFloat(floatvalue))));
        contentValues.put("UserId", app.getUserInfoBean().getUserId());

        reminderDao.insert(contentValues, 1);
        ToastUtil.showShort(FreeRemindActivity.this,"浮动提醒值设置成功");
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

                                if (reminderDao.deletePoint(app.getUserInfoBean().getUserId(),symbol, operator, value)) {
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
            //关闭浮动开关
            save.setSelected(false);
            save.setClickable(false);
            return "";
        }else{
            //打开浮动开关
            save.setSelected(true);
            save.setClickable(true);
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

            if (lowBoard < 0) {
                ToastUtil.showShort(this, "基准价不能小于浮动值");
                return "";
            }

            return "报价>=" + mDecimalFormat.format(highBoard) + "或报价<=" + mDecimalFormat.format(lowBoard);
        }
        return "";
    }

    /**
     * 是否设置了浮动提醒
     * @param floatBeens
     */
    private boolean isSetedFloat(List<ReminderFloatBeen> floatBeens) {
        if (floatBeens == null) {
            return false;
        }else{
            for (ReminderFloatBeen been :floatBeens) {
                if (symbol.equals(been.getStock())) {
                    etBase.setText(been.getBasePrice());
                    etFloat.setText(been.getFloatPrice());
                }
            }
            return true;
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
