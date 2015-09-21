package com.juttec.goldmetal.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.juttec.goldmetal.R;

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
                addView();
        }
    }

    private void addView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.point_remind_layout, null);
        view.setLayoutParams(lp);
        remindContent.addView(view);
    }
}
