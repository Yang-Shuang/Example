package com.yang.viewdemo.net;

/**
 * Created by
 * yangshuang on 2018/8/17.
 */

public class UrlUtil {

    private String url;
    private String host;
    private String scheme;
    private String port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public UrlUtil(String url) {
        this.url = url;

    }

    private void analysis() {
        if (url == null || url.equals("") || url.length() < 5) return;
        if (url.contains("://") && url.indexOf("://") == url.lastIndexOf("://") && !url.startsWith("://")) {
            scheme =  url.substring(0, url.indexOf("://"));
            String[] urls = url.split("://");
            if (urls.length >= 1 && urls[1] != null && !urls[1].equals("")) {
                if (urls[1].contains("?")) {
                    String str = urls[1].split("\\?")[0];
                    String h = str.contains("/") ? str.split("/")[0] : str;
                    if (h.contains(":")){
                        String hostPort[] = h.split(":");
                        host = hostPort[0];
                        port = hostPort[1];
                    } else {
                        host = h;
                    }
                } else {
                    String str = urls[1];
                    String h = str.contains("/") ? str.split("/")[0] : str;
                    if (h.contains(":")){
                        String hostPort[] = h.split(":");
                        host = hostPort[0];
                        port = hostPort[1];
                    } else {
                        host = h;
                    }
                }
            }
        }
    }
}
