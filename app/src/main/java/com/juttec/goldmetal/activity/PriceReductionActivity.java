package com.juttec.goldmetal.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.utils.LogUtil;

import java.text.DecimalFormat;

public class PriceReductionActivity extends AppCompatActivity {

    private final double KG_OUNCE = 35.2739619;
    private double dollarRmb = 6.1127;
    EditText etDollarOunce, etRmbOunce, etRmbKg;
    TextWatcher twDollarOunce, twRmbOunce, twRmbKg;
    DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_reduction);
        decimalFormat = new DecimalFormat("###.00");
        etDollarOunce = (EditText) this.findViewById(R.id.et_dollar_ounce);
        etRmbOunce = (EditText) this.findViewById(R.id.et_rmb_ounce);
        etRmbKg = (EditText) this.findViewById(R.id.et_rmb_kg);

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


            LogUtil.e("priceDollarOunce  " + priceDollarOunce + "  ,priceRmbOunce  " + priceRmbOunce + ",priceRmbKg  " + priceRmbKg);

            etRmbOunce.setText(decimalFormat.format(priceRmbOunce));
            etRmbKg.setText(decimalFormat.format(priceRmbKg));
        }

        @Override
        public void afterTextChanged(Editable s) {
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
            LogUtil.e("priceDollarOunce  " + priceDollarOunce + "  ,priceRmbOunce  " + priceRmbOunce + ",priceRmbKg  " + priceRmbKg);
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


            priceRmbOunce  = Double.parseDouble(s.toString());
            priceRmbKg = priceRmbOunce * KG_OUNCE;
            priceDollarOunce = priceRmbOunce / dollarRmb;
            LogUtil.e("priceDollarOunce  " + priceDollarOunce + "  ,priceRmbOunce  " + priceRmbOunce + ",priceRmbKg  " + priceRmbKg);
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
