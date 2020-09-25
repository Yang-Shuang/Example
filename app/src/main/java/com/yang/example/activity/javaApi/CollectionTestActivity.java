package com.yang.example.activity.javaApi;

import android.os.Bundle;

import com.yang.example.R;
import com.yang.base.activity.SimpleBarActivity;
import com.yang.base.rx.SimpleTask;
import com.yang.base.utils.LogUtil;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.yang.base.rx.SimpleTask.TASK_ID;

public class CollectionTestActivity extends SimpleBarActivity {

    private int times = 100000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_test);

        Bundle bundle = new Bundle();
        bundle.putInt(TASK_ID, 101);
        bundle.putInt("times", times);
        bundle.putSerializable("Class", ArrayList.class);
        asyncExecute(new SimpleTask(bundle) {
            @Override
            public Bundle run(Bundle params) {
                Class c = (Class) params.getSerializable("Class");
                long time = testAdd(c, times);
                params.putLong("time", time);
                return params;
            }
        }).execute(this);

        LinkedList l = new LinkedList();
        l.remove(1);
    }

    
    @Override
    public void onLoadCompleted(int taskID, Bundle reault) {
        super.onLoadCompleted(taskID, reault);
        switch (taskID) {
            case 101:
                long time = reault.getLong("time");
                int t = reault.getInt("times");
                Class c = (Class) reault.getSerializable("Class");
                LogUtil.e(c.getName() + "---普通增加元素测试：次数:" + t + " 时间：" + time);
                break;
        }
    }

    private long testAdd(Class<? extends AbstractList> c, long count) {
        try {
            LogUtil.e(c.getName() + "---普通增加元素测试开始--");
            AbstractList<Integer> list = c.newInstance();
            long time = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                list.add(i);
            }
            time = System.currentTimeMillis() - time;
            return time;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private long testAddRandom(Class<? extends AbstractList> c, long count) {
        return 0;
    }
}
