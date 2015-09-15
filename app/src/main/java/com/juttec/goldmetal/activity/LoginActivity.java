package com.juttec.goldmetal.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.juttec.goldmetal.R;

/**
 * Created by Jeremy on 2015/9/15.
 */
public class LoginActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_login);
    }
}
