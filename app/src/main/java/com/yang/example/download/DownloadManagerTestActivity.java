package com.yang.example.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.yang.example.R;
import com.yang.example.activity.SimpleBarActivity;
import com.yang.example.utils.NetWorkUtils;

import java.io.File;

public class DownloadManagerTestActivity extends SimpleBarActivity {


    private DownloadManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager_test);

    }

    public void onIntertnetClick(View view){
        showToast("" + NetWorkUtils.isConnectInternet(this));
    }
    public void onClick(View view){

        if (manager == null) {
            manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://download.ugoshop.com/17ugo_android_test.apk"));
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(true);
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle("DownloadTest");
        request.setDescription("新版***下载中...");
        request.setVisibleInDownloadsUi(true);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        //设置下载的路径
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "17ugo_android_test.apk");
        request.setDestinationUri(Uri.fromFile(file));
        if (manager != null) {
            long downloadId = manager.enqueue(request);
        }


        registerReceiver(new BroadcastReceiver() {
                             @Override
                             public void onReceive(Context context, Intent intent) {
                                 Log.e("registerReceiver","--ACTION_DOWNLOAD_COMPLETE---");
                             }
                         },
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }
}
