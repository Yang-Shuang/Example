package com.yang.viewdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.LogInterceptor;
import com.orhanobut.hawk.NoEncryption;
import com.yang.base.activity.SimpleBarActivity;
import com.yang.viewdemo.base.BaseRecyclerViewAdapter;
import com.yang.viewdemo.service.GetDataService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

public class DataActivity extends SimpleBarActivity {

    private RecyclerView recyclerView;
    private Messenger sender, receiver;
    private ServiceConnection connection;
    private Handler myHandler;
    private CustomAdapter adapter;
    private ArrayList<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        GetDataService.init(this);

        recyclerView = findViewById(R.id.data_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myHandler = new MyHandler(this);
        receiver = new Messenger(myHandler);

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                sender = new Messenger(service);
                Message message = Message.obtain(null, GetDataService.OPEN_HEARTBEAT_MSG);
                message.replyTo = receiver;
                try {
                    sender.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        mData = new ArrayList<>();
        adapter = new CustomAdapter(R.layout.list_item, mData);

        adapter.setRefreshListener(new BaseRecyclerViewAdapter.RefreshListener() {
            @Override
            public void onRefresh() {
                Message message = Message.obtain(null, GetDataService.MSG_RESET_DATA);
                message.replyTo = receiver;
                try {
                    sender.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
//        adapter.setLoadMoreListener(new BaseRecyclerViewAdapter.LoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                Message message = Message.obtain(null, GetDataService.MSG_GET_DATA);
//                message.replyTo = receiver;
//                try {
//                    sender.send(message);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        recyclerView.setAdapter(adapter);
        Intent intent = new Intent(this, GetDataService.class);
        intent.putExtra(GetDataService.BINDER_TYPE, GetDataService.BINDER_MESSENGER);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Hawk.init(this).setLogInterceptor(new LogInterceptor() {
            @Override
            public void onLog(String message) {
                Log.e("ViewDemo", message);
            }
        }).setEncryption(new NoEncryption()).build();
        Log.e("ViewDemo", "testValue----------" + Hawk.get("testValue", "null"));
    }

    public void onReceiveMsg(Message message) {
        adapter.completeRefresh();
        int what = message.what;
        if (what == GetDataService.MSG_RESET_DATA) mData.clear();
        Bundle bundle = message.getData();
        if (bundle == null || bundle.getStringArrayList("time") == null) return;
        ArrayList<String> data = bundle.getStringArrayList("time");
        data.removeAll(mData);
        Collections.reverse(data);
        mData.addAll(0, data);
        adapter.notifyDataSetChanged();
    }

    static class MyHandler extends Handler {

        WeakReference<DataActivity> reference;

        MyHandler(DataActivity activity) {
            this.reference = new WeakReference<DataActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (reference != null && reference.get() != null) {
                reference.get().onReceiveMsg(msg);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
