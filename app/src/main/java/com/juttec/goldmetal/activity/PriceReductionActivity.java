package com.juttec.goldmetal.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * 价格换算
 */
public class PriceReductionActivity extends AppCompatActivity {

    private final double KG_OUNCE = 35.2739619;//千克与盎司的比率
    private double dollarRmb;//美元与人民币的汇率
    EditText etDollarOunce, etRmbOunce, etRmbKg;
    TextWatcher twDollarOunce, twRmbOunce, twRmbKg;//美元/盎司，人民币/盎司，人民币/千克
    DecimalFormat decimalFormat;//将数字格式化成保留小数点后两位

    private MyApplication app;
    private TextView tvRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_reduction);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();
        app = (MyApplication) getApplication();
        decimalFormat = new DecimalFormat("###.00");
        etDollarOunce = (EditText) this.findViewById(R.id.et_dollar_ounce);
        etRmbOunce = (EditText) this.findViewById(R.id.et_rmb_ounce);
        etRmbKg = (EditText) this.findViewById(R.id.et_rmb_kg);
        tvRate = (TextView) this.findViewById(R.id.tv_rate);


       //获取美元人民币汇率
        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getGetExchangeRateUrl(), new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(responseInfo.result.toString());

                    String status = object.getString("status");
                    if ("1".equals(status)) {
                        dollarRmb = Double.parseDouble(object.getString("message1"));
                        tvRate.setText(dollarRmb+"");


                    } else {
                        ToastUtil.showShort(getApplication(),"获取汇率失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog.dismiss();
                NetWorkUtils.showMsg(PriceReductionActivity.this);
            }
        });


        //设置监听事件
        twDollarOunce = new DOTextWatcher();
        twRmbOunce = new ROTextWatcher();
        twRmbKg = new RKTextWatcher();

        etDollarOunce.addTextChangedListener(twDollarOunce);
        etRmbOunce.addTextChangedListener(twRmbOunce);
        etRmbKg.addTextChangedListener(twRmbKg);
    }


    class DOTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            //取消其他两个文本框的监听事件，以防止循环调用导致栈溢出
            etRmbOunce.removeTextChangedListener(twRmbOunce);
            etRmbKg.removeTextChangedListener(twRmbKg);

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if ("".equals(s.toString())) {
                etRmbOunce.setText("");
                etRmbKg.setText("");
                return;
            }

            double priceDollarOunce;
            double priceRmbOunce;
            double priceRmbKg;


            priceDollarOunce = Double.parseDouble(s.toString());

            priceRmbOunce = priceDollarOunce * dollarRmb;
            priceRmbKg = priceRmbOunce * KG_OUNCE;


            etRmbOunce.setText(decimalFormat.format(priceRmbOunce));
            etRmbKg.setText(decimalFormat.format(priceRmbKg));
        }

        @Override
        public void afterTextChanged(Editable s) {
            //重新添加监听事件
            etRmbOunce.addTextChangedListener(twRmbOunce);
            etRmbKg.addTextChangedListener(twRmbKg);
        }

    }


    class RKTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            etRmbOunce.removeTextChangedListener(twRmbOunce);
            etDollarOunce.removeTextChangedListener(twDollarOunce);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if ("".equals(s.toString())) {
                etRmbOunce.setText("");
                etDollarOunce.setText("");
                return;
            }

            double priceDollarOunce;
            double priceRmbOunce;
            double priceRmbKg;


            priceRmbKg = Double.parseDouble(s.toString());
            priceRmbOunce = priceRmbKg / KG_OUNCE;
            priceDollarOunce = priceRmbOunce / dollarRmb;
            etRmbOunce.setText(decimalFormat.format(priceRmbOunce));
            etDollarOunce.setText(decimalFormat.format(priceDollarOunce));

        }

        @Override
        public void afterTextChanged(Editable s) {
            etRmbOunce.addTextChangedListener(twRmbOunce);
            etDollarOunce.addTextChangedListener(twDollarOunce);
        }
    }

    class ROTextWatcher implements TextWatcher {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            etRmbKg.removeTextChangedListener(twRmbKg);
            etDollarOunce.removeTextChangedListener(twDollarOunce);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if ("".equals(s.toString())) {
                etRmbKg.setText("");
                etDollarOunce.setText("");
                return;
            }

            double priceDollarOunce;
            double priceRmbOunce;
            double priceRmbKg;


            priceRmbOunce = Double.parseDouble(s.toString());
            priceRmbKg = priceRmbOunce * KG_OUNCE;
            priceDollarOunce = priceRmbOunce / dollarRmb;
            etRmbKg.setText(decimalFormat.format(priceRmbKg));
            etDollarOunce.setText(decimalFormat.format(priceDollarOunce));

        }

        @Override
        public void afterTextChanged(Editable s) {
            etRmbKg.addTextChangedListener(twRmbKg);
            etDollarOunce.addTextChangedListener(twDollarOunce);
        }
    }
}
