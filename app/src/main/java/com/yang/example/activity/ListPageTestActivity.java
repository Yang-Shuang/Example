package com.yang.example.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yang.example.R;
import com.yang.example.view.HMListPagerView;
import com.yang.example.view.HMListPagerView.PageHolder;

import java.util.ArrayList;
import java.util.List;

public class ListPageTestActivity extends SimpleBarActivity {

    private HMListPagerView hm_list_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_page_test);

        hm_list_pager = findViewById(R.id.hm_list_pager);
    }

    private MyAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter == null) {
            adapter = new MyAdapter();
            hm_list_pager.setAdapter(adapter);
        }

//        hm_list_pager.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (adapter.getItemCount() <= 3) {
//                    adapter.add();
//                    adapter.notifyDataChange();
//                }
//            }
//        }, 2000);

    }

    private class MyHolder extends PageHolder {

        TextView textView;

        public MyHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview);
        }

        public void bindData(int position) {
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.RED);
            textView.setTextSize(150);
            textView.setText("" + position);
        }
    }

    private class MyAdapter extends HMListPagerView.PageAdapter {

        private List<String> list = new ArrayList<>();
        private int count = 10;

        @Override
        public PageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_page, null));
        }

        @Override
        public void onBindViewHolder(PageHolder holder, int position) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.bindData(position);
        }

        @Override
        public int getItemCount() {
            return count;
        }

        public void add() {
            count = 10;
        }
    }

}
