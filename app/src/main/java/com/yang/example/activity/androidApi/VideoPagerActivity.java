package com.yang.example.activity.androidApi;


import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.yang.base.activity.BaseActivity;
import com.yang.example.R;
import com.yang.example.adapter.TBGoodsAdapter;
import com.yang.example.utils.PagerLayoutManager;

public class VideoPagerActivity extends BaseActivity {

    private RecyclerView recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_pager);

        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new PagerLayoutManager(RecyclerView.VERTICAL, recycler_view));
        recycler_view.setAdapter(new TBGoodsAdapter());
    }
}