package com.yang.example.activity.androidApi;

import android.os.Bundle;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yang.base.activity.SimpleBarActivity;
import com.yang.example.R;
import com.yang.example.adapter.TBGoodsAdapter;
import com.yang.example.utils.TBLayoutManager;
import com.yang.example.utils.ViewPagerLayoutManager;

public class LayoutManagerTestActivity extends SimpleBarActivity {

    private RecyclerView recycler_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_manager_test);

        recycler_view = findViewById(R.id.recycler_view);

//        recycler_view.setLayoutManager(new TBLayoutManager(recycler_view));
        recycler_view.setLayoutManager(new ViewPagerLayoutManager(this, RecyclerView.VERTICAL));
//        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setAdapter(new TBGoodsAdapter());
    }
}