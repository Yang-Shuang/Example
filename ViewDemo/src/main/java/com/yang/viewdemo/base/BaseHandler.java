package com.yang.viewdemo.base;

import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * Created by yangshuang
 * on 2018/6/22.
 */

public class BaseHandler extends Handler {
    public BaseHandler(Object object, Callback callback) {
        super(callback);
        this.objectWeakReference = new WeakReference<Object>(object);
    }

    WeakReference<Object> objectWeakReference;


    public <T extends Object> T getObject() {
        T t = (T) objectWeakReference.get();
        return t;
    }
}
