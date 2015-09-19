package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.customview.HeadLayout;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tv_cycle;//周期
    private TextView tv_index;//指标
    private TextView tv_title;//标题

    private Button btn_free_remind;//免费提醒


    private HeadLayout mHeadLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        initView();
    }


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

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.left_text:

                break;

            case R.id.right_text:

                break;

            case R.id.btn_free_remind:
                Intent intent = new Intent(ChartActivity.this,FreeRemindActivity.class);
                startActivity(intent);
                break;
        }
    }
}
