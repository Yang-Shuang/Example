package com.yang.example.activity;

import android.os.Bundle;

import com.yang.example.R;

public class SynchronizedTestActivity extends SimpleBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronized_test);
    }

    static class DataManager {
        private static DataManager mDataManager;

        private DataManager() {
        }

        public static DataManager getInstance() {
            return mDataManager == null ? mDataManager = new DataManager() : mDataManager;
        }

        public static boolean isInit() {
            return mDataManager != null;
        }
    }
}
