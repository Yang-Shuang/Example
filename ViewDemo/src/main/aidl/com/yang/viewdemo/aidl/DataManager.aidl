// DataManager.aidl
package com.yang.viewdemo.aidl;

// Declare any non-default types here with import statements
import com.yang.viewdemo.aidl.DataBean;
interface DataManager {

    List<DataBean> getData();
    void addData(in DataBean data);
}
