package com.yang.example.activity;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yang.example.R;
import com.yang.example.adapter.ExampleListAdapter;
import com.yang.example.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends SimpleBarActivity {

    static {
        System.loadLibrary("native-lib");
    }

    public native String stringFromJNI();

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showLeftImage(false);
        mRecyclerView = (RecyclerView) findViewById(R.id.main_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        LogUtil.e("***********************" + stringFromJNI());
    }

    private void loadData() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), PackageManager.GET_ACTIVITIES);
            List<HashMap<String, String>> list = new ArrayList<>();
            for (ActivityInfo info : packageInfo.activities) {
                if (info.nonLocalizedLabel == null) continue;
                HashMap<String, String> map = new HashMap<>();
                map.put("name", info.nonLocalizedLabel.toString());
                map.put("class", info.name);
                list.add(map);
            }
            mRecyclerView.setAdapter(new ExampleListAdapter(list));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
