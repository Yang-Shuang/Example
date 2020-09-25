package com.yang.viewdemo;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.yang.viewdemo.service.GetDataService;

/**
 * Created by
 * yangshuang on 2018/6/19.
 */

public class VApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("VApplication","onCreate");
        startService(new Intent(this, GetDataService.class));
    }
}
