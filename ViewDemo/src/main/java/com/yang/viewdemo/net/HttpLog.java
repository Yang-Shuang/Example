package com.yang.viewdemo.net;

import android.util.Log;

/**
 * Created by
 * yangshuang on 2018/8/17.
 */

public class HttpLog {

    public static void log(String meseage) {
        log("HttpLog", meseage);
    }

    public static void log(String tag, String meseage) {
        if (tag == null || tag.equals("")) {
            tag = "HttpLog";
        }
        if (meseage == null || meseage.equals("")) return;
        Log.i(tag, meseage);
    }
}
