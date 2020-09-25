package com.yang.viewdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.yang.base.activity.SimpleBarActivity;
import com.yang.viewdemo.aidl.GetDataImpl;
import com.yang.viewdemo.base.BaseRecyclerViewAdapter;
import com.yang.viewdemo.service.GetDataService;

import java.util.ArrayList;
import java.util.Collections;

public class AIDLDataActivity extends SimpleBarActivity {

    private RecyclerView recyclerView;
    private ServiceConnection connection;
    private CustomAdapter adapter;
    private ArrayList<String> mData;
    private GetDataImpl impl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidldata);
        GetDataService.init(this);
        setRightImage(R.drawable.icon_delete);

        recyclerView = findViewById(R.id.data_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mData = new ArrayList<>();
        adapter = new CustomAdapter(R.layout.list_item, mData);

        adapter.setRefreshListener(new BaseRecyclerViewAdapter.RefreshListener() {
            @Override
            public void onRefresh() {
                adapter.completeRefresh();
                mData.clear();
                try {
                    ArrayList<String> data = (ArrayList<String>) impl.getData();
                    data.removeAll(mData);
                    Collections.reverse(data);
                    mData.addAll(0, data);
                    adapter.notifyDataSetChanged();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        recyclerView.setAdapter(adapter);


        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                impl = GetDataImpl.Stub.asInterface(service);
                try {
                    ArrayList<String> data = (ArrayList<String>) impl.getData();
                    data.removeAll(mData);
                    Collections.reverse(data);
                    mData.addAll(0, data);
                    adapter.notifyDataSetChanged();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intent = new Intent(this, GetDataService.class);
        intent.putExtra(GetDataService.BINDER_TYPE, GetDataService.BINDER_ALDL);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onRightButtonClick() {
        super.onRightButtonClick();
    }

    public void onClick(View v) {
        try {
            impl.clearData();
            mData.clear();
            ArrayList<String> data = (ArrayList<String>) impl.getData();
            data.removeAll(mData);
            Collections.reverse(data);
            mData.addAll(0, data);
            adapter.notifyDataSetChanged();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
