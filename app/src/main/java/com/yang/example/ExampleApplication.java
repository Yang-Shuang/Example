package com.yang.example;

import androidx.multidex.MultiDexApplication;

//import com.growingio.android.sdk.collection.Configuration;
//import com.growingio.android.sdk.collection.GrowingIO;
import com.yang.example.utils.DatabaseHelper;
import com.yang.example.utils.ScreenUtil;

public class ExampleApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ScreenUtil.init(getApplicationContext());
        DatabaseHelper.init(getApplicationContext());
//        GrowingIO.startWithConfiguration(this, new Configuration()
//                .trackAllFragments()
//                .setChannel("XXX应用商店").setDebugMode(true)
//        );
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
