package com.yang.viewdemo;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.yang.base.activity.SimpleBarActivity;
import com.yang.viewdemo.weight.CustomRecyclerView;

import java.util.ArrayList;

public class CustomViewActivity extends SimpleBarActivity {

    CustomRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view);

        recyclerView = findViewById(R.id.main_rv);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            data.add("" + i);
        }
        recyclerView.setAdapter(new CustomAdapter(R.layout.item, data));

    }
}
