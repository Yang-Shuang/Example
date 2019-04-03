package com.yang.example.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.yang.example.R;

public class LauncherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);
    }
}
