package com.yang.viewdemo.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import androidx.annotation.Nullable;
import android.util.Log;

import com.yang.viewdemo.aidl.GetDataImpl;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by
 * yangshuang on 2018/6/13.
 */

public class GetDataService extends Service {

    public static final int MSG_RESET_DATA = 1001;
    public static final int MSG_GET_DATA = 1002;
    public static final int OPEN_HEARTBEAT_MSG = 1003;
    public static final int CLOSE_HEARTBEAT_MSG = 1004;
    public static final String BINDER_TYPE = "BINDER_TYPE";
    public static final String BINDER_MESSENGER = "BINDER_MESSENGER";
    public static final String BINDER_ALDL = "BINDER_ALDL";

    private static class Messagehanlder extends Handler {

        WeakReference<GetDataService> reference;

        Messagehanlder(GetDataService service) {
            this.reference = new WeakReference<GetDataService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_GET_DATA:
                    break;
                case MSG_RESET_DATA:
                    reference.get().reSetData();
                    break;
                case OPEN_HEARTBEAT_MSG:
                    reference.get().setHeartBeatSendMessenger(msg.replyTo);
                    reference.get().setHeartBeatSend(true);
                    break;
                case CLOSE_HEARTBEAT_MSG:
                    reference.get().setHeartBeatSendMessenger(null);
                    reference.get().setHeartBeatSend(false);
                    return;
            }
            if (msg.replyTo != null) {
                Messenger client = msg.replyTo;
                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("time", reference.get().getRunTimes());
                message.setData(bundle);
                message.what = msg.what;
                try {
                    client.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Messenger messenger = new Messenger(new Messagehanlder(this));

    private Binder mBinder = new GetDataImpl.Stub() {
        @Override
        public List<String> getData() throws RemoteException {
            return runTimes;
        }

        @Override
        public void clearData() throws RemoteException {
            runTimes.clear();
        }
    };

    private ArrayList<String> runTimes;
    private Timer mTimer;
    private boolean showLog = false;
    private SimpleDateFormat format;
    private boolean sendForHeartBeat = false;
    private Messenger replyMessenger;
    private static GetDataService mService;

    public static void init(Context context){
        if (mService == null) {
            context.startService(new Intent(context, GetDataService.class));
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mService = this;
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Log.e("GetDataService", "onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("GetDataService", "onBind");
        String type = intent.getStringExtra(BINDER_TYPE);
        if (type.equals(BINDER_MESSENGER)) {
            return messenger.getBinder();
        } else if (type.equals(BINDER_ALDL)) {
            return mBinder;
        }
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("GetDataService", "onStartCommand");
        initData(false);
        return super.onStartCommand(intent, flags, startId);
    }

    public ArrayList<String> getRunTimes() {
        return runTimes;
    }

    public void reSetData() {
        initData(true);
    }

    public void setHeartBeatSend(boolean allow) {
        this.sendForHeartBeat = allow;
    }

    public void setHeartBeatSendMessenger(Messenger reply) {
        this.replyMessenger = reply;
    }

    private void initData(boolean reset) {
        if (reset || runTimes == null || mTimer == null) {
            runTimes = new ArrayList<>();
            if (mTimer == null) {
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        String time = format.format(System.currentTimeMillis());
                        runTimes.add(time);
                        if (showLog) {
                            Log.e("GetDataService", "add : " + time);
                        }
                        if (sendForHeartBeat) {
                            Message message = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("time", runTimes);
                            message.setData(bundle);
                            message.what = OPEN_HEARTBEAT_MSG;
                            try {
                                replyMessenger.send(message);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 0, 1000);
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.e("GetDataService", "onDestroy");
        super.onDestroy();
    }
}
