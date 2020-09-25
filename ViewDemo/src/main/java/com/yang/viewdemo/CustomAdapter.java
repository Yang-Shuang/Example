package com.yang.viewdemo;

import android.widget.TextView;

import com.yang.viewdemo.base.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * Created by yangshuang
 * on 2018/6/4.
 */

public class CustomAdapter extends BaseRecyclerViewAdapter {

    private List<String> data;

    public CustomAdapter(int mLayoutId,List<String> data) {
        super(mLayoutId);
        this.data = data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }

    @Override
    public int onBindItemCount() {
        return data.size();
    }

    @Override
    public void onBindItemData(BaseViewHolder holder, int position) {
        TextView textView = holder.getView(R.id.item_tv);
        textView.setText(data.get(position));
    }
}
