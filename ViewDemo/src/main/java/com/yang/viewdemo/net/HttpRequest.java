package com.yang.viewdemo.net;

import java.util.List;
import java.util.Map;

/**
 * Created by
 * yangshuang on 2018/8/17.
 */

class HttpRequest {

    HttpRequest() {

    }

    private String url;
    private String method;
    private List<String> head;
    private List<Map<String, Object>> query;
    private Enum status = Status.WAITING;//
    private String scheme;
    private String host;
    private String port;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getHead() {
        return head;
    }

    public void setHead(List<String> head) {
        this.head = head;
    }

    public List<Map<String, Object>> getQuery() {
        return query;
    }

    public void setQuery(List<Map<String, Object>> query) {
        this.query = query;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void cancel() {
        this.status = Status.HAS_CANCEL;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Enum getStatus() {
        return status;
    }

    public void setStatus(Enum status) {
        this.status = status;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    enum Status {
        WAITING,
        REQUESTING,
        COMPLETE,
        HAS_CANCEL
    }
}
