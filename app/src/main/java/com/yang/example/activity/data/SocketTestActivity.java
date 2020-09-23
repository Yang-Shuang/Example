package com.yang.example.activity.data;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.github.messenger.Messenger;
import com.github.messenger.bean.MessageBean;
import com.yang.example.R;
import com.yang.example.activity.SimpleBarActivity;
import com.yang.example.utils.LogUtil;
import com.yang.example.utils.StreamUtils;
import com.yang.example.view.NoteView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Random;

public class SocketTestActivity extends SimpleBarActivity {

    private NoteView mNoteView;
    private EditText urlEdit;
    private Messenger messenger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_test);

        mNoteView = findViewById(R.id.socket_noteview);
        urlEdit = findViewById(R.id.socket_url_edt);
        urlEdit.setText("10.9.112.33:10101");

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.socket_connect:
                final String url = urlEdit.getText().toString();
                if (!url.contains(":")) {
                    showToast("url 非法");
                }
                if (messenger != null) {
                    messenger.release();
                }
                String[] urls = url.split(":");
                String host = urls[0];
                String port = urls[1];
                messenger = Messenger.create(host,Integer.valueOf(port),Messenger.TYPE_MOBILE);
                break;
            case R.id.socket_send:
                Random random = new Random();
                MessageBean bean = new MessageBean("" + System.currentTimeMillis() + random.nextInt(999999));
                bean.setCode("" + MessageBean.REQUEST_DATA);
                bean.setInfo("什么鬼");
                messenger.sendMessage(bean, new MessageBean.ResponseLisenter() {

                    @Override
                    public void onResponse(MessageBean bean) {

                    }

                    @Override
                    public void onError(MessageBean bean) {

                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        messenger.release();
        super.onDestroy();
    }

    @Override
    public void onLoadCompleted(int taskID, Bundle reault) {
        super.onLoadCompleted(taskID, reault);
        LogUtil.e("taskID --" + taskID);
    }

    class SocketThread extends Thread {

        private String host;
        private int port;
        private boolean running = false;
        private Socket socket;
        private LinkedList<String> requestList = new LinkedList<>();
        private OutputStream outputStream;
        private InputStream inputStream;

        public SocketThread(String host, int port) {
            super();
            this.port = port;
            this.host = host;
        }

        public synchronized void request(String req) {
            requestList.offer(req);
        }

        @Override
        public synchronized void start() {
            this.running = true;
            super.start();
        }

        public void stopSelf() {
            this.running = false;
        }

        @Override
        public void run() {
            super.run();
            while (running) {
                try {
                    if (socket == null) {
                        socket = new Socket(host, port);
                        outputStream = socket.getOutputStream();
                        inputStream = socket.getInputStream();
                        LogUtil.e("socket connect");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    running = false;
                }
                try {
                    if (requestList.size() > 0) {
                        String req = requestList.poll();
                        outputStream.write(req.getBytes());
                        outputStream.write(StreamUtils.END.getBytes());
                        outputStream.flush();
                        LogUtil.e("outputStream flush : " + req);
                    }
                    String response = StreamUtils.input2String(inputStream);
                    if (response == null) continue;
                    LogUtil.e("response : " + response);
                    if (response.contains("heart_check")) {
                        requestList.add(response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof SocketException) {
                        running = false;
                    }
                }
            }
            try {
                if (socket != null && socket.isConnected()) {
                    socket.close();
                }
                socket = null;
                if (outputStream != null) {
                    outputStream.close();
                }
                outputStream = null;
                if (inputStream != null) {
                    inputStream.close();
                }
                inputStream = null;
                requestList.clear();
                requestList = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
