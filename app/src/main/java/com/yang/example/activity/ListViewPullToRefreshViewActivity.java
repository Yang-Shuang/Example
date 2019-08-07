package com.yang.example.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yang.example.R;
import com.yang.example.utils.LogUtil;
import com.yang.example.view.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

public class ListViewPullToRefreshViewActivity extends SimpleBarActivity {

    private ListView mListView;
    private RecyclerView mRecyclerView;
    private PullToRefreshView listRefresh, recyclerRefresh;
    private Button changeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_pull_to_refresh_view);

        mListView = findViewById(R.id.list_view);
        mRecyclerView = findViewById(R.id.recycler_view);
        listRefresh = findViewById(R.id.list_view_content);
        recyclerRefresh = findViewById(R.id.recycler_view_content);
        changeButton = findViewById(R.id.change_view_button);


        ArrayList<String> items = new ArrayList<String>();
        for (int i = 0; i < 60; i++) {
            items.add("" + i);
        }
        mListView.setAdapter(new MySimpleAdapter(items, this));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MyRecyclerViewAdapter(items));

        if (listRefresh.getVisibility() == View.VISIBLE) {
            changeButton.setText("当前：ListView");
        } else {
            changeButton.setText("当前：RecyclerView");
        }
        PullToRefreshView.OnHeaderRefreshListener listener = new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(final PullToRefreshView view) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.onHeaderRefreshComplete();
                    }
                }, 3000);
            }
        };
        PullToRefreshView.OnFooterRefreshListener footerRefreshListener = new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(final PullToRefreshView view) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.onFooterRefreshComplete();
                    }
                }, 3000);
            }
        };
        listRefresh.setOnHeaderRefreshListener(listener);
        recyclerRefresh.setOnHeaderRefreshListener(listener);
        listRefresh.setOnFooterRefreshListener(footerRefreshListener);
        recyclerRefresh.setOnFooterRefreshListener(footerRefreshListener);
    }

    public void onClick(View view) {
        if (listRefresh.getVisibility() == View.VISIBLE) {
            listRefresh.setVisibility(View.GONE);
            recyclerRefresh.setVisibility(View.VISIBLE);
            changeButton.setText("当前：RecyclerView");
        } else {
            listRefresh.setVisibility(View.VISIBLE);
            recyclerRefresh.setVisibility(View.GONE);
            changeButton.setText("当前：ListView");
        }
    }

    private class MySimpleAdapter extends BaseAdapter {

        private List<String> mData;
        private Context mContext;

        public MySimpleAdapter(List<String> mData, Context mContext) {
            this.mData = mData;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LogUtil.e("------------   getView   ------------");
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, null);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.text1.setText(mData.get(position));
            return convertView;
        }

        class Holder {
            public TextView text1;

            public Holder(View content) {
                this.text1 = content.findViewById(android.R.id.text1);
            }
        }
    }

    private class MyRecyclerViewAdapter extends RecyclerView.Adapter {

        private List<String> mData;

        public MyRecyclerViewAdapter(List<String> mData) {
            this.mData = mData;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, null);
            return new VHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            LogUtil.e("------------   onBindViewHolder   ------------");
            VHolder vHolder = (VHolder) holder;
            vHolder.text1.setText(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        private class VHolder extends RecyclerView.ViewHolder {
            public TextView text1;

            public VHolder(View itemView) {
                super(itemView);
                this.text1 = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
