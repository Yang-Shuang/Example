package com.yang.example.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.example.R;
import com.yang.example.rx.BaseSubscriber;
import com.yang.example.rx.SimpleOperator;
import com.yang.example.rx.SimpleTask;
import com.yang.example.utils.StringUtil;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BaseActivity extends FragmentActivity {


    private Toast mToast;
    private View loadingView;
    private BaseSubscriber subscriber;
    private HashMap<String, Method> methods;
    public static final int RESULT_SUCCEED = 0;
    public static final int RESULT_FAILD = -1;

    /**
     * 返回显示当前面页面元素的根视图， 用来添加loading视图 和  请求错误视图， 以及其他一些通用的视图
     */
    public ViewGroup getRootView() {
        return (ViewGroup) getWindow().getDecorView();
    }

    protected void showLoading() {
        if (loadingView == null) {
            loadingView = LayoutInflater.from(this).inflate(R.layout.view_loading, null);
            loadingView.setTag(R.id.loading_count, 1);
        } else {
            int tag = (Integer) loadingView.getTag(R.id.loading_count);
            loadingView.setTag(R.id.loading_count, tag + 1);
        }

        if (loadingView.getParent() != null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = loadingView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        ViewGroup vg = getRootView();
        vg.addView(loadingView, layoutParams);
    }

    protected void dimisssLoading() {
        if (loadingView == null || loadingView.getParent() == null) {
            return;
        }
        int tag = (Integer) loadingView.getTag(R.id.loading_count);
        tag -= 1;
        if (tag <= 0) {
            tag = 0;
            ViewGroup vg = getRootView();
            vg.removeView(loadingView);
        }
        loadingView.setTag(R.id.loading_count, tag);
    }

    public void showToast(String message) {
        if (StringUtil.isEmpty(message)) return;
        if (mToast == null) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.view_toast, null);
            mToast = new Toast(this);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setView(view);
        }
        TextView v = (TextView) mToast.getView().findViewById(R.id.toast_tv);
        v.setText(message);
        mToast.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (subscriber != null && !subscriber.isUnsubscribed()) {
            subscriber.unsubscribe();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscriber != null) {
            subscriber.unsubscribe();
            subscriber.realease();
        }
        if (methods != null) {
            methods.clear();
        }
    }

    protected Method getMethods(String name) {
        if (methods == null) methods = new HashMap<>();
        if (methods.get(name) != null) {
            return methods.get(name);
        } else {
            Method method = null;
            try {
                method = this.getClass().getMethod(name, Integer.class, Object.class);
                methods.put(name, method);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return method;
        }
    }

    protected TaskQueue asyncExecute(SimpleTask task) {
        return new TaskQueue(task);
    }

    protected void asyncExecute(SimpleTask... task) {
        if (task == null || task.length == 0) {
            return;
        }
        if (subscriber == null) {
            subscriber = new BaseSubscriber(this);
        }
//        showLoading();
        Observable observable = Observable.create(task[0]);
        for (int i = 1; i < task.length; i++) {
            SimpleOperator operator = new SimpleOperator(task[i]);
            observable = observable.lift(operator);
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    private Observer<Bundle> getLiftObserver(final SimpleTask task) {
        return new Subscriber<Bundle>() {
            @Override
            public final void onCompleted() {
            }

            @Override
            public final void onError(Throwable e) {
                task.call(this);
            }

            @Override
            public final void onNext(Bundle args) {
                task.call(this);
            }
        };
    }

    public void onLoadCompleted(int taskID, Bundle reault) {
//        dimisssLoading();
    }

    protected class TaskQueue {
        private ArrayList<SimpleTask> tasks;

        public TaskQueue() {
            tasks = new ArrayList<SimpleTask>();
        }

        public TaskQueue(SimpleTask task) {
            if (tasks == null) tasks = new ArrayList<SimpleTask>();
            tasks.add(task);
        }

        public TaskQueue next(SimpleTask task) {
            tasks.add(task);
            return this;
        }

        public void execute(BaseActivity activity) {
            SimpleTask[] ts = new SimpleTask[tasks.size()];
            activity.asyncExecute(tasks.toArray(ts));
        }

    }

}
