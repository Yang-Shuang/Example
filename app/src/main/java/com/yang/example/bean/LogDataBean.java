package com.yang.example.bean;

public class LogDataBean extends BaseBean {

    private String type;
    private String time;
    private String content;
    private String tag;

    public LogDataBean() {
    }

    public LogDataBean(String type, String time, String content, String tag) {
        this.type = type;
        this.time = time;
        this.content = content;
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
