package com.yang.example.activity.javaApi;

import android.os.Bundle;

import com.yang.example.R;
import com.yang.example.activity.SimpleBarActivity;

public class SynchronizedTestActivity extends SimpleBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronized_test);
    }

    static class DataManager {
        private static DataManager mDataManager;

        public DataManager() {
        }

        public static DataManager getInstance() {
            return mDataManager == null ? mDataManager = new DataManager() : mDataManager;
        }

        public static void increase(){

        }
        public synchronized static void  syncIncrease(){

        }

        public void add(){

        }
        public synchronized void syncAdd(){

        }
    }
}
