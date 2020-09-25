package com.yang.base.rx;

import android.os.Bundle;

import com.yang.base.activity.BaseActivity;

import rx.Subscriber;

public class BaseSubscriber extends Subscriber<Bundle> {

    private BaseActivity activity;

    public BaseSubscriber(BaseActivity activity) {
        this.activity = activity;
    }

    public void realease() {
        this.activity = null;
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        Bundle bundle = new Bundle();
        bundle.putString("error", e.getMessage());
        activity.onLoadCompleted(-1, bundle);
    }

    @Override
    public void onNext(Bundle bundle) {
        if (bundle != null) {
            activity.onLoadCompleted(bundle.getInt(SimpleTask.TASK_ID), bundle);
        }
    }
}
