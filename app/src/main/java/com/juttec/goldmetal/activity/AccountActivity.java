package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;

public class AccountActivity extends AppCompatActivity {

    RelativeLayout head;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        head= (RelativeLayout) this.findViewById(R.id.head_layout);
        TextView lefttext = (TextView) head.findViewById(R.id.left_text);
        lefttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this,ContactUsActivity.class));
            }
        });
    }
}
