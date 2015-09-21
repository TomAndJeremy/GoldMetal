package com.juttec.goldmetal.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.customview.HeadLayout;
import com.juttec.goldmetal.utils.ToastUtil;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tv_cycle;//周期
    private TextView tv_index;//指标
    private TextView tv_title;//标题

    private Button btn_free_remind;//免费提醒


    private HeadLayout mHeadLayout;


    //周期
    private String cycles[] = new String[]{
            "1分钟",
            "5分钟",
            "15分钟",
            "30分钟",
            "60分钟",
            "4小时",
            "日线",
            "周线",
            "月线"
    };

    //指标
    private String indexs[] = new String[]{
        "MACD指标",
        "BOLL指标",
        "KDJ指标",
        "RSI指标",
        "SMA指标",
        "EMA指标",
        "ENV指标"
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        initView();

        initData();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        mHeadLayout = (HeadLayout) findViewById(R.id.head_layout);
        tv_title = (TextView) mHeadLayout.findViewById(R.id.head_title);

        tv_cycle = (TextView) mHeadLayout.findViewById(R.id.left_text);
        tv_cycle.setOnClickListener(this);

        tv_index = (TextView) mHeadLayout.findViewById(R.id.right_text);
        tv_index.setOnClickListener(this);

        btn_free_remind = (Button) findViewById(R.id.btn_free_remind);
        btn_free_remind.setOnClickListener(this);

    }


    private void initData(){

    }



    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder;

        switch (v.getId()){
            case R.id.left_text:

                 builder = new AlertDialog.Builder(ChartActivity.this);
               // builder.create().requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.setItems(cycles, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ToastUtil.showShort(ChartActivity.this,cycles[which]+"被选中");

                    }

                }).show();

                break;

            case R.id.right_text:
                 builder = new AlertDialog.Builder(ChartActivity.this);
                builder.setItems(indexs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ToastUtil.showShort(ChartActivity.this,indexs[which]+"被选中");

                    }

                }).show();


                break;

            case R.id.btn_free_remind:
                Intent intent = new Intent(ChartActivity.this,FreeRemindActivity.class);
                startActivity(intent);
                break;
        }
    }
}
