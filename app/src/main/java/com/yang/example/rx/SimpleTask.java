package com.yang.example.rx;

import android.os.Bundle;

import rx.Subscriber;

public abstract class SimpleTask extends BaseOnSubscribe {

    public static final String TASK_ID = "TASK_ID";

    private Bundle params, results;

    public SimpleTask() {
        this.params = new Bundle();
    }

    public SimpleTask(int taskID) {
        this.params = new Bundle();
        params.putInt(TASK_ID, taskID);
    }

    public SimpleTask(Bundle params) {
        this.params = params;
    }

    public void call(Subscriber<? super Bundle> subscriber, Bundle p) {
        if (this.params != null) {
            params.putAll(p);
        } else {
            this.params = p;
        }
        this.call(subscriber);
    }

    @Override
    public void call(Subscriber<? super Bundle> subscriber) {
        try {
            results = run(this.params);
        } catch (Exception e){
            results.putString("error",e.getMessage());
        }
        subscriber.onNext(results);
    }

    public abstract Bundle run(Bundle params);
}
