package com.yang.example.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yang.example.R;

import java.util.List;

public class StringItemAdapter  extends RecyclerView.Adapter {

    private List<String> mDatas;

    public StringItemAdapter(List<String> mDatas) {
        this.mDatas = mDatas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StringViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_string, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StringViewHolder viewHolder = (StringViewHolder) holder;
        viewHolder.textView.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class StringViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public StringViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv);
        }
    }
}
