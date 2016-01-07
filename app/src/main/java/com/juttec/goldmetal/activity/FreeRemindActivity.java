package com.juttec.goldmetal.activity;

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
import com.juttec.goldmetal.bean.PointWarnBean;
import com.juttec.goldmetal.customview.HeadLayout;
import com.juttec.goldmetal.dialog.MyAlertDialog;
import com.juttec.goldmetal.dialog.MyProgressDialog;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 免费提醒界面
 */
public class FreeRemindActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 输入框小数的位数
     */
    private static final int DECIMAL_DIGITS = 2;


    private HeadLayout mHeadLayout;//标题布局
    private SwitchCompat switchCompat;//浮动提醒的开关
    private TextView valueRemind;//根据浮动值 和 基准值 算出来的 提醒值

    private TextView save;//保存浮动提醒值

    private EditText etBase, etFloat;//基准值 和 浮动值 的 输入框

    private Button btAdd;//点位提醒的添加按钮
    private LinearLayout remindContent;//点位提醒的布局

    private DecimalFormat mDecimalFormat = new DecimalFormat("####.00");//格式化小数

    private MyAlertDialog myAlertDialog;//提示确定删除点位提醒的 dialog
    private MyAlertDialog dialog;//添加点位提醒的dialog


    //正在加载 进度框
    private MyProgressDialog dialog_progress;

    private String symbol;//当前股票号码 由前个界面传递过来的  作为sharepreference的key值  value为boolean型代表浮动开关是否打开
    private String stockName;//此股票的股票名称  由前个界面传递过来的
    private String currentValueBase;//此股票的当前价  由前个界面传递过来的


    private List<PointWarnBean> mPointWarnBeanList = new ArrayList<PointWarnBean>();

    private MyApplication app;

    private String floatWarnId;//浮动提醒的id


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_remind);
        app = (MyApplication) getApplication();

        myAlertDialog = new MyAlertDialog(this);
        dialog = new MyAlertDialog(this);
        dialog_progress = new MyProgressDialog(this);

        Intent intent = getIntent();
        symbol = intent.getStringExtra("symbol");
        stockName = intent.getStringExtra("stockName");
        currentValueBase = intent.getStringExtra("currentValue");

        initView();
        initEnent();

        //调接口查询点位提醒和浮动提醒
        getWarnData();
       }

    //初始化控件
    private void initView() {
        mHeadLayout = (HeadLayout) findViewById(R.id.head_layout);
        TextView tv_title = (TextView) mHeadLayout.findViewById(R.id.head_title);
        //设置标题
        tv_title.setText(stockName + "免费提醒");

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

        //设置基准价(股票的当前价)和 浮动值 （默认的值）
        etBase.setText(mDecimalFormat.format(Float.parseFloat(currentValueBase)));
        etFloat.setText(mDecimalFormat.format((Float.parseFloat(currentValueBase) / 10)));

    }


    /**
     * 根据接口获取的提醒数据  在界面上显示
     */
    private void initData() {
        //设置浮动开关
        //设置基准价和浮动值
        //显示点位提醒
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
                LogUtil.d("switchCompat.setOnCheckedChange---------------:" + isChecked);
                if (isChecked) {
                    //调用添加浮动提醒  的 接口
                } else {
                    //删除浮动提醒接口
                }
            }
        });


        //浮动提醒开关的 touch事件
        switchCompat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isNotReasonable();
                        break;
                }
                return true;
            }
        });

    }


    /**
     * 判断输入的 基准价和浮动值是否不合理
     *
     * @return
     */
    private boolean isNotReasonable() {
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

        //根据开关的状态 调添加浮动提醒和删除浮动提醒的接口
        if (switchCompat.isChecked()) {
            //删除浮动提醒的接口
            delFloatWarn();
        } else {
            //调添加浮动提醒的接口
            addFloatWarn(base,floatvalue);
        }
        return true;
    }


    /**
     * 点击事件的监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remind_bt_add:
                //弹出   添加点位提醒数据 的dialog  进行点位数据的添加
                showAddPointDialog();

                break;
            case R.id.tv_save_ar:
                //保存按钮  调用添加 浮动提醒 的接口
                String base = etBase.getText().toString().trim();
                String floatvalue = etFloat.getText().toString().trim();
                if ("".equals(base) || "".equals(floatvalue)) {
                    ToastUtil.showShort(FreeRemindActivity.this, "基准价或浮动值不能为空");
                    return;
                }
                if ((Float.parseFloat(base) - Float.parseFloat(floatvalue)) < 0) {
                    ToastUtil.showShort(FreeRemindActivity.this, "基准价不能小于浮动值");
                    return;
                }
                addFloatWarn(base,floatvalue);
                break;
        }
    }


    /**
     * 显示添加点位提醒的dialog
     */
    private void showAddPointDialog() {
        //添加点位提醒
        dialog.builder().setTitle("添加提醒").setEditText("请输入价格");
        dialog.setRadioGroupVisiable();
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LogUtil.d("添加提醒---符号：", dialog.getResult());
                if (dialog.getResult() == null || "".equals(dialog.getResult())) {
                    ToastUtil.showShort(FreeRemindActivity.this, "提醒值不能为空");
                    return;
                }
                String value = mDecimalFormat.format(Double.parseDouble(dialog.getResult()));

                //判断添加的数据是否已存在
                for (int i = 0; i < mPointWarnBeanList.size(); i++) {
                    if (value.equals(mPointWarnBeanList.get(i).getNewestPrice())) {
                        if (dialog.s.equals(mPointWarnBeanList.get(i).getLogicOperator())) {
                            ToastUtil.showShort(FreeRemindActivity.this, "该点位提醒已存在");
                            return;
                        }
                    }

                }

                //调接口 添加点位提醒
                addPtWarn(dialog.s, value);


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

    }


    /**
     * 如果设置了点位提醒，则添加到界面上
     *
     * @param pointBeens
     */



    /**
     * 添加点位提醒布局
     * 并监听点位数据的删除事件
     *
     * @param strWarnData
     */
    private void addView(final String pointWarnId,final String strWarnData) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.point_remind_layout, null);
        view.setLayoutParams(lp);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除点位提醒的dialog
                showDelPtDialog(pointWarnId,strWarnData, view);
            }
        });

        TextView tv = (TextView) view.findViewById(R.id.tv_remind_level);
        tv.setText(strWarnData);
        remindContent.addView(view);
    }


    /**
     * 删除点位提醒的dialog
     *
     * @param strWarnData
     * @param addView
     */
    private void showDelPtDialog(final String pointWarnId,final String strWarnData, final View addView) {
        myAlertDialog.builder().setTitle("删除点位提醒")
                .setMsg(strWarnData)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String operator = strWarnData.substring(3, 4);
                        String value = strWarnData.substring(4);

                        //删除点位提醒的接口
                        delPtWarn(pointWarnId,addView);
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAlertDialog.dismiss();
            }
        }).show();
    }


    //根据基准价和 浮动值 计算提醒值
    private String getTextValue(String base, String index) {

        if ("".equals(base) || "".equals(index)) {
            ToastUtil.showShort(FreeRemindActivity.this, "基准价或浮动值不能为空");
            //将保存按钮设置为 不可以点击的状态
            save.setSelected(false);
            save.setClickable(false);
            return "";
        }


        if (switchCompat.isChecked()) {

            try {
                if (Float.parseFloat(base) - Float.parseFloat(index) < 0) {
                    //将保存按钮设置为 不可以点击的状态
                    save.setSelected(false);
                    save.setClickable(false);
                    ToastUtil.showShort(this, "基准价不能小于浮动值");
                    return "";
                } else {
                    //将保存按钮设置为 可以点击的状态
                    save.setSelected(true);
                    save.setClickable(true);
                }


//               mDecimalFormat.format(Float.parseFloat(base) - Float.parseFloat(index));
//               mDecimalFormat.format(Float.parseFloat(base) + Float.parseFloat(index));

            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }


            return "报价>=" + mDecimalFormat.format(Float.parseFloat(base) + Float.parseFloat(index)) + "或报价<=" + mDecimalFormat.format(Float.parseFloat(base) - Float.parseFloat(index));
        }
        return "";
    }


    /**
     * 添加点位提醒
     * operator：大于 或是  小于
     * value：用户设置的点位值
     */
    private void addPtWarn(final String operator, final String value) {
        dialog_progress.builder().setMessage("请稍等~").show();

        RequestParams params = new RequestParams();
        params.addBodyParameter("mobile", app.getUserInfoBean().getMobile());
        params.addBodyParameter("stockCode", symbol);
        params.addBodyParameter("stockName", stockName);
        params.addBodyParameter("logicOperator", operator.equals(">") ? "大于" : "小于");
        params.addBodyParameter("rulingPrice", value);
        LogUtil.d("添加点位提醒接口参数：" + app.getUserInfoBean().getMobile() + symbol + stockName + operator + value);

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.addPtWarn(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog_progress.dismiss();

                LogUtil.d("添加点位提醒：" + responseInfo.result.toString());
                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    String status = object.getString("status");
                    String promptInfor = object.getString("promptInfor");
                    JSONObject object1 =object.getJSONObject("message1");
                    String pointWarnId = object1.getString("id");
                    if ("1".equals(status)) {
                        //添加此点位的布局
                        PointWarnBean pointWarnBean = new PointWarnBean();
                        pointWarnBean.setLogicOperator(operator);
                        pointWarnBean.setNewestPrice(value);
                        pointWarnBean.setPointWarnId(pointWarnId);
                        mPointWarnBeanList.add(pointWarnBean);
                        //添加此点位的布局
                        String sPoint = "最新价" + operator + value;
                        addView(pointWarnId,sPoint);


                    } else {
                    }
                    ToastUtil.showShort(FreeRemindActivity.this, promptInfor);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog_progress.dismiss();
                NetWorkUtils.showMsg(FreeRemindActivity.this);
            }
        });
    }


    /**
     * 添加浮动提醒
     * lessThan：基准价-浮动值
     * greaterThan：基准价+浮动值
     */
    private void addFloatWarn(String baseValue,String floatValue) {
        dialog_progress.builder().setMessage("请稍等~").show();
        RequestParams params = new RequestParams();
        params.addBodyParameter("mobile", app.getUserInfoBean().getMobile());
        params.addBodyParameter("stockCode", symbol);
        params.addBodyParameter("stockName", stockName);
        params.addBodyParameter("lessThan", mDecimalFormat.format(Float.parseFloat(baseValue)));//基准价
        params.addBodyParameter("greaterThan", mDecimalFormat.format(Float.parseFloat(floatValue)));//浮动值

        LogUtil.d("添加浮动提醒接口参数：" + app.getUserInfoBean().getMobile() + symbol + stockName + mDecimalFormat.format(Float.parseFloat(baseValue)) + mDecimalFormat.format(Float.parseFloat(floatValue)));

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.addFloatWarn(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog_progress.dismiss();

                LogUtil.d("添加浮动提醒：" + responseInfo.result.toString());
                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    String status = object.getString("status");
                    String promptInfor = object.getString("promptInfor");
                    JSONObject object1 =object.getJSONObject("message1");
                    floatWarnId = object1.getString("id");
                    if ("1".equals(status)) {
                        //打开浮动开关
                        switchCompat.setChecked(true);
                        //显示 提醒值布局
                        valueRemind.setVisibility(View.VISIBLE);
                        valueRemind.setText(getTextValue(etBase.getText().toString(), etFloat.getText().toString()));
                        //保存按钮设置为可点击
                        save.setSelected(true);
                        save.setClickable(true);
                    } else {

                    }
                    ToastUtil.showShort(FreeRemindActivity.this, promptInfor);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog_progress.dismiss();
                NetWorkUtils.showMsg(FreeRemindActivity.this);
            }
        });

    }


    /**
     * 获取用户添加的浮动提醒和点位提醒 数据
     */
    private void getWarnData() {
        dialog_progress.builder().setMessage("请稍等~").show();

        RequestParams params = new RequestParams();
        params.addBodyParameter("mobile", app.getUserInfoBean().getMobile());
//        params.addBodyParameter("stockCode",symbol);
        params.addBodyParameter("stockName", stockName);

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.getWarnData(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog_progress.dismiss();

                LogUtil.d("获取提醒数据：" + responseInfo.result.toString());
                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    String status = object.getString("status");
                    String promptInfor = object.getString("promptInfor");
                    if ("1".equals(status)) {

                        JSONObject obj = object.getJSONObject("entityList");
                        String baseValue = obj.getString("lessThan");//基准价
                        String floatValue = obj.getString("greaterThan");//浮动值
                        floatWarnId = obj.getString("id");

                        JSONArray jsonArray = obj.getJSONArray("pointList");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            PointWarnBean pointWarnBean = new PointWarnBean();
                            JSONObject objArray = (JSONObject) jsonArray.get(i);
                            pointWarnBean.setLogicOperator(objArray.getString("logicOperator"));
                            pointWarnBean.setNewestPrice(objArray.getDouble("rulingPrice") + "");
                            pointWarnBean.setPointWarnId(objArray.getString("id"));
                            mPointWarnBeanList.add(pointWarnBean);
                        }

                        //添加点位提醒
                        for (int i = 0; i < mPointWarnBeanList.size(); i++) {
//                            LogUtil.d("点位提醒Unicode1："+mPointWarnBeanList.get(i).getLogicOperator());
//                            LogUtil.d("点位提醒符号1："+MyApplication.unicode2String(mPointWarnBeanList.get(i).getLogicOperator()));
                            addView(mPointWarnBeanList.get(i).getPointWarnId(),"最新价" + mPointWarnBeanList.get(i).getLogicOperator() + mPointWarnBeanList.get(i).getNewestPrice());
                        }


                        if (Float.parseFloat(baseValue) >= 0 && Float.parseFloat(floatValue) >= 0) {
                            //如果用户设置了浮动提醒
                            switchCompat.setChecked(true);

                            etBase.setText(baseValue);
                            etFloat.setText(floatValue);
                            valueRemind.setVisibility(View.VISIBLE);
                            valueRemind.setText(getTextValue(etBase.getText().toString(), etFloat.getText().toString()));
                        } else {
                            //此股票没有设置浮动提醒
                            switchCompat.setChecked(false);
                            save.setSelected(false);
                            save.setClickable(false);
                            //设置基准价(股票的当前价)和 浮动值 （默认的值）
                            etBase.setText(mDecimalFormat.format(Float.parseFloat(currentValueBase)));
                            etFloat.setText(mDecimalFormat.format((Float.parseFloat(currentValueBase) / 10)));
//                           mDecimalFormat.format(Float.parseFloat(currentValueBase) - (Float.parseFloat(currentValueBase) / 10));
//                           mDecimalFormat.format(Float.parseFloat(currentValueBase) + (Float.parseFloat(currentValueBase) / 10));
                        }
                    } else {

                    }
                    ToastUtil.showShort(FreeRemindActivity.this, promptInfor);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog_progress.dismiss();
                LogUtil.d("onFailure--------------:"+error.toString());
                NetWorkUtils.showMsg(FreeRemindActivity.this);
            }
        });
    }


    /**
     * 删除点位提醒
     */
    private void delPtWarn(String pointWarnId,final View addView) {
        dialog_progress.builder().setMessage("请稍等~").show();

        RequestParams params = new RequestParams();
        params.addBodyParameter("id", pointWarnId);
//        params.addBodyParameter("stockCode",symbol);
//        params.addBodyParameter("stockName", stockName);
//        params.addBodyParameter("logicOperator", logicOperator);//操作符
//        params.addBodyParameter("rulingPrice", rulingPrice);//设置的提醒值

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.delPtWarn(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog_progress.dismiss();

                LogUtil.d("删除点位提醒：" + responseInfo.result.toString());
                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    String status = object.getString("status");
                    String promptInfor = object.getString("promptInfor");
                    if ("1".equals(status)) {
                        //将布局删除
                        remindContent.removeView(addView);
                        ToastUtil.showShort(FreeRemindActivity.this, "删除成功");
                    } else {
                        ToastUtil.showShort(FreeRemindActivity.this, promptInfor);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(HttpException error, String msg) {
                dialog_progress.dismiss();
                NetWorkUtils.showMsg(FreeRemindActivity.this);
            }
        });
    }


    /**
     * 删除浮动提醒
     */
    private void delFloatWarn() {
        dialog_progress.builder().setMessage("请稍等~").show();

        RequestParams params = new RequestParams();
        params.addBodyParameter("id", floatWarnId);
//        params.addBodyParameter("mobile", app.getUserInfoBean().getMobile());
//        params.addBodyParameter("stockCode",symbol);
//        params.addBodyParameter("stockName", stockName);

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.delFloatWarn(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog_progress.dismiss();

                LogUtil.d("删除浮动提醒：" + responseInfo.result.toString());
                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    String status = object.getString("status");
                    String promptInfor = object.getString("promptInfor");
                    if ("1".equals(status)) {
                        //关闭浮动开关
                        switchCompat.setChecked(false);
                        //保存按钮设置为不可点
                        save.setSelected(false);
                        save.setClickable(false);
                        //将提醒值布局隐藏
                        valueRemind.setVisibility(View.GONE);
                    } else {

                    }
                    ToastUtil.showShort(FreeRemindActivity.this, promptInfor);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog_progress.dismiss();
                NetWorkUtils.showMsg(FreeRemindActivity.this);
            }
        });
    }



}
