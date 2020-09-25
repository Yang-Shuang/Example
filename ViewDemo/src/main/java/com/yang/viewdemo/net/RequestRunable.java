package com.yang.viewdemo.net;

import android.os.Handler;
import android.os.Message;

import java.net.Socket;

/**
 * Created by
 * yangshuang on 2018/8/17.
 */

public class RequestRunable implements Runnable {

    private Handler mHandler;
    private HttpRequest request;
    private CusHttpConnection connection;

    RequestRunable(Handler handler, HttpRequest request, CusHttpConnection cusHttpConnection) {
        this.mHandler = handler;
        this.request = request;
        this.connection = cusHttpConnection;
    }

    @Override
    public void run() {
        connection.connect(request.getHost(), request.getPort());

        connection.sendHttpData(request);

        byte[] data = connection.responseData(request);

        Message msg = mHandler.obtainMessage(request.hashCode(), data);
        mHandler.sendMessage(msg);
        mHandler = null;
        request = null;
        connection = null;
    }
}
