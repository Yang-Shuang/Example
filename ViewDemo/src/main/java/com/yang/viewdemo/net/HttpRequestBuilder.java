package com.yang.viewdemo.net;

import java.util.HashMap;

/**
 * Created by
 * yangshuang on 2018/8/17.
 */

public class HttpRequestBuilder {

    private String url;
    private HashMap<String, Object> params;
    private String method;

    HttpRequestBuilder() {

    }

    public HttpRequestBuilder withUrl(String url) {
        this.url = url;
        return this;
    }

    public HttpRequestBuilder withMethod(String method) {
        this.method = method;
        return this;
    }

    public HttpRequestBuilder withParams(HashMap<String, Object> params) {
        this.params = params;
        return this;
    }

    public HttpRequest build() {
        HttpRequest request = new HttpRequest();

        if (url == null || url.equals("")) throw new NullPointerException("url is null");
        UrlUtil urlUtil = new UrlUtil(url);
        String scheme = urlUtil.getScheme();
        String host = urlUtil.getHost();
        String port = urlUtil.getPort();
        if (scheme == null || !url.startsWith("http") || !url.startsWith("https")) {
            throw new IllegalArgumentException("url is illegal:" + url);
        }
        if (host == null || host.equals("")) throw new IllegalArgumentException("url is illegal:" + url);
        request.setUrl(url);
        request.setHost(host);
        request.setScheme(scheme);
        request.setPort(port);
        if (method == null) request.setMethod("GET");
        return request;
    }
}
