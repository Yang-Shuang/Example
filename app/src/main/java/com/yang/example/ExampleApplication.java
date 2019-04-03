package com.yang.example;

import android.app.Application;

import com.yang.example.utils.DatabaseHelper;
import com.yang.example.utils.ScreenUtil;

public class ExampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ScreenUtil.init(getApplicationContext());
        DatabaseHelper.init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
