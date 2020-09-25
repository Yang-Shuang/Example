package com.yang.viewdemo.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by
 * yangshuang on 2018/6/13.
 */

public class DataBean implements Parcelable {

    private String data;
    private String name;
    private int count;
    private boolean isShow;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    protected DataBean(Parcel in) {
        name = in.readString();
        count = in.readInt();
        isShow = in.readInt() == 1;
        data = in.readString();
    }

    public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
        @Override
        public DataBean createFromParcel(Parcel in) {
            return new DataBean(in);
        }

        @Override
        public DataBean[] newArray(int size) {
            return new DataBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(count);
        dest.writeInt(isShow ? 1 : 0);
        dest.writeString(data);
    }
}
