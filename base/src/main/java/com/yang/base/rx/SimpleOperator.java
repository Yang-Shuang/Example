package com.yang.base.rx;

import android.os.Bundle;

import rx.Observable;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorThrowable;

public class SimpleOperator implements Observable.Operator<Bundle, Bundle> {

    private SimpleTask task;

    public SimpleOperator(SimpleTask task) {
        this.task = task;
    }

    @Override
    public Subscriber<? super Bundle> call(final Subscriber<? super Bundle> subscriber) {
        if (subscriber == null || subscriber.isUnsubscribed()) return null;
        return new Subscriber<android.os.Bundle>(subscriber) {

            @Override
            public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onNext(android.os.Bundle t) {
                try {
                    task.call(subscriber, t);
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    onError(OnErrorThrowable.addValueAsLastCause(e, t));
                }
            }

        };
    }
}
