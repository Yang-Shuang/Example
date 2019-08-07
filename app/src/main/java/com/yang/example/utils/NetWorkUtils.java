package com.yang.example.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

public class NetWorkUtils {

    public static boolean isConnectInternet(Context c) {
        boolean flag = false;
        Context context = c.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = connectivity.getAllNetworks();
                if (networks != null) {
                    for (int i = 0; i < networks.length; i++) {
                        if (connectivity.getNetworkInfo(networks[i]).getState() == NetworkInfo.State.CONNECTED) {
                            flag = true;
                        }
                    }
                }
            } else {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();

                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            flag = true;
                        }
                    }
                }
            }
        }
        return flag;
    }
}
