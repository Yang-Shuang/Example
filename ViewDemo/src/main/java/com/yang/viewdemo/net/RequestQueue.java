package com.yang.viewdemo.net;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by
 * yangshuang on 2018/8/17.
 */

public class RequestQueue {

    private HashMap<Integer, HttpRequest> requestList;

    public RequestQueue() {
        requestList = new HashMap<>();
        HttpLog.log("RequestQueue init");
    }

//    public static synchronized RequestQueue getInstance() {
//        if (requestQueue == null) requestQueue = new RequestQueue();
//        HttpLog.log("RequestQueue getInstance");
//        return requestQueue;
//    }

    public synchronized void add(HttpRequest request) {
        HttpLog.log("RequestQueue start add Reuqest" + request);
        if (request == null)
            return;
        requestList.put(request.hashCode(), request);
        HttpLog.log("RequestQueue end add Reuqest" + request);
    }

    public synchronized void get(int hashCode) {
        HttpLog.log("RequestQueue start get Reuqest" + hashCode);
        requestList.get(hashCode);
        HttpLog.log("RequestQueue end get Reuqest" + hashCode);
    }

    public synchronized void remove(HttpRequest request) {
        HttpLog.log("RequestQueue start remove Reuqest" + request);
        requestList.remove(request);
        HttpLog.log("RequestQueue end remove Reuqest" + request);
    }
}
