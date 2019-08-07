package com.yang.example.rx;

import android.os.Bundle;

import rx.Observable;
import rx.Subscriber;

public class BaseOnSubscribe implements Observable.OnSubscribe<Bundle> {

    @Override
    public void call(Subscriber<? super Bundle> subscriber) {
        if (subscriber == null || subscriber.isUnsubscribed()) return;
    }
}
