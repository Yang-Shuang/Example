package com.yang.base.utils;

import android.util.Log;

import com.yang.base.BuildConfig;


public class LogUtil {
    private static final String Tag = "LogUtil";

    public static void w(String message) {
        w(Tag, message);
    }

    public static void w(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message);
        }
    }

    public static void w(Object obj, String message) {
        if (BuildConfig.DEBUG) {
            Log.w(obj.getClass().getName(), message);
        }
    }

    public static void e(String message) {
        e(Tag, message);
    }

    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message == null ? "null" : message);
        }
    }

    public static void e(Object obj, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(obj.getClass().getName(), message);
        }
    }

    public static void i(String message) {
        e(Tag, message);
    }

    public static void i(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void i(Object obj, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(obj.getClass().getName(), message);
        }
    }
}
