package com.yang.viewdemo.net;

import android.os.Handler;
import android.os.Message;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuang
 * on 2018/8/17.
 */

public class CustomHttpClient {

    private static RequestQueue mQueue;

    private static CustomHttpClient client;
    private static Handler mHandler;
    private static CusHttpConnection connection;

    private CustomHttpClient() {
        mQueue = new RequestQueue();
        mHandler = new HttpHandler();
        connection = new CusHttpConnectionImpl();
    }

    public static CustomHttpClient getInstance() {
        if (client == null) client = new CustomHttpClient();
        return client;
    }

    public void execute(HttpRequest request) {
        mQueue.add(request);
        RequestRunable requestRunable = new RequestRunable(mHandler, request, connection);
        new Thread(requestRunable).start();
    }

    static class HttpHandler extends Handler {

    }

}
