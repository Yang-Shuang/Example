package com.yang.viewdemo.net;

import java.util.HashMap;

/**
 * Created by
 * yangshuang on 2018/8/17.
 */

public class RequestManager {


    private static CustomHttpClient customHttpClient;

    public static void init() {
        if (customHttpClient == null) customHttpClient = CustomHttpClient.getInstance();
    }

    public static void requestData(String url, HashMap<String, Object> params, RequestListener listener) {

    }
}
