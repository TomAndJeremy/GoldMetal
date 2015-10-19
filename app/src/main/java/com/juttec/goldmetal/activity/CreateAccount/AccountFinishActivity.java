package com.juttec.goldmetal.activity.CreateAccount;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.juttec.goldmetal.R;

/**
 * 开户完成界面
 */

public class AccountFinishActivity extends AppCompatActivity {

    private Button btn_complete;//完成按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_finish);


        btn_complete = (Button) findViewById(R.id.btn_complete);
        btn_complete.setSelected(true);

        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountFinishActivity.this,AccountNoticeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
