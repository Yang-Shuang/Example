package com.yang.viewdemo;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.LogInterceptor;
import com.orhanobut.hawk.NoEncryption;
import com.umf.pay.sdk.UmfPay;
import com.yang.base.activity.SimpleBarActivity;
import com.yang.viewdemo.base.BaseRecyclerViewAdapter;
import java.util.Arrays;

public class MainActivity extends SimpleBarActivity {

    private String[] activityNames = {"自定义View事件", "进程间通信数据查看", "AIDL跨进程通信", "OkHttp Demo", "ViewGroupTest"};
    private Class[] activityClasses = {CustomViewActivity.class, DataActivity.class, AIDLDataActivity.class, HttpDemoActivity.class, ViewGroupTestActivity.class};

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UmfPay.init(getApplication());
        //微信在开放平台注册的appid，不设置该参数不能使用微信支付
        UmfPay.setDebug(true);

        recyclerView = findViewById(R.id.main_rv);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        final CustomAdapter adapter = new CustomAdapter(R.layout.list_item, Arrays.asList(activityNames));
        adapter.setItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View item, int position) {
                startActivity(new Intent(MainActivity.this, activityClasses[position]));
            }
        });
        recyclerView.setAdapter(adapter);
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
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UmfPay.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) { //
            } else if (resultCode == Activity.RESULT_CANCELED) { //
            } else { //
            }
        }
        //商户以商户后台查单结果为准
        if (data != null) {
            String payCode = data.getStringExtra(UmfPay.RESULT_CODE);
            String payMsg = data.getStringExtra(UmfPay.RESULT_MSG);
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.main_hawk_tv) {
            new MaterialDialog.Builder(this)
                    .content("存储一个hawk值")
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .input("存储一个hawk值", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            Hawk.put("testValue", input.toString());
                        }
                    }).show();
        }
    }
}
