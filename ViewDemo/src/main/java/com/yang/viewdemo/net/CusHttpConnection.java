package com.yang.viewdemo.net;

/**
 * Created by yangshuang
 * on 2018/8/18.
 */

public interface CusHttpConnection {

    boolean connect(String host, String port);

    void sendHttpData(HttpRequest request);

    byte[] responseData(HttpRequest request);
}
