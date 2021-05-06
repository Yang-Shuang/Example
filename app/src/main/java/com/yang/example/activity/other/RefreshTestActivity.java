package com.yang.example.activity.other;

import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yang.base.activity.SimpleBarActivity;
import com.yang.example.R;
import com.yang.example.adapter.StringItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class RefreshTestActivity extends SimpleBarActivity {


    private RecyclerView refresh_test_rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_test);

        refresh_test_rv = findViewById(R.id.refresh_test_rv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        refresh_test_rv.addItemDecoration(itemDecoration);
        refresh_test_rv.setLayoutManager(layoutManager);


        List<String> data1 = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            String s = String.format("我是第%d个", i);
            data1.add(s);
        }

        StringItemAdapter mAdapter = new StringItemAdapter(data1);
        refresh_test_rv.setAdapter(mAdapter);
    }
}
