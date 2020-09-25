package com.yang.viewdemo;

import android.os.Bundle;
import android.view.View;

import com.yang.base.activity.SimpleBarActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class OkHttpDemoActivity extends SimpleBarActivity {


    private static boolean runThread = true;

    private Thread netThread, netThread1;
    private MyRunable runnable;

    static class MyRunable implements Runnable {


        OutputStream stream;
        String[] ps;

        public String[] getPs() {
            return ps;
        }

        public void setPs(String[] ps) {
            this.ps = ps;
        }

        public MyRunable(OutputStream stream) {
            this.stream = stream;
        }

        public OutputStream getStream() {
            return this.stream;
        }

        @Override
        public void run() {
            try {
                OutputStreamWriter osw = new OutputStreamWriter(stream, "utf-8");
                while (runThread) {
                    if (ps != null && ps.length > 0) {
                        for (String p : ps) {
                            osw.write(getParams(p));
                            osw.flush();
                            System.out.println("send params---" + p);
                        }
                        ps = null;
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class ReadRunable implements Runnable {
        InputStream stream;

        public ReadRunable(InputStream stream) {
            this.stream = stream;
        }

        @Override
        public void run() {
            try {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(stream, "utf-8"));
                System.out.println("receiver params");
                while (true) {
                    StringBuilder line = new StringBuilder();
                    String str = null;
                    while ((str = bufferedReader.readLine()) != null) {
                        line.append(str + "\r\n");
                        System.out.println(str);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http_demo);
        runThread = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }).start();
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.title_back_tv) {
            finish();
        } else if (id == R.id.textView) {
            runnable.setPs(new String[]{"name=yang1&age=12"});
        } else if (id == R.id.textView1) {
            runnable.setPs(new String[]{"name=yang2&age=13"});
        } else if (id == R.id.textView2) {
            runnable.setPs(new String[]{"name=yang1&age=12", "name=yang2&age=13", "name=yang2&age=14"});
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        runThread = false;
        runnable = null;
        netThread = null;
        netThread1 = null;
    }

    private void loadData() {
        try {
            Socket socket = new Socket();
            InetSocketAddress address = new InetSocketAddress("192.168.31.163", 8080);
            socket.connect(address, 3000000);
            socket.setKeepAlive(true);
            concurrentOutPut(socket.getOutputStream());
            concurrentInput(socket.getInputStream());

            runnable = new MyRunable(socket.getOutputStream());
            netThread = new Thread(runnable);
            netThread.start();

            netThread1 = new Thread(new ReadRunable(socket.getInputStream()));
            netThread1.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void concurrentInput(final InputStream stream) {
        try {
            BufferedReader bufferedReader = null;
            bufferedReader = new BufferedReader(
                    new InputStreamReader(stream, "utf-8"));
            final BufferedReader finalBufferedReader = bufferedReader;

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    StringBuilder line = new StringBuilder();
                    String str = null;
                    System.out.println("receiver params");
                    try {
                        while ((str = finalBufferedReader.readLine()) != null) {
                            line.append(str + "\r\n");
                            System.out.println(str);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 1000);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void concurrentOutPut(final OutputStream stream) {
        final String paramss[] = {"name=yang1&age=12", "name=yang2&age=13", "name=yang2&age=14"};

        try {
            OutputStreamWriter osw = null;
            osw = new OutputStreamWriter(stream, "utf-8");
            final OutputStreamWriter finalosw = osw;
            new Timer().schedule(new TimerTask() {
                int index = 0;

                @Override
                public void run() {
                    if (index <= 2) {
                        try {
                            finalosw.write(getParams(paramss[index]));
                            finalosw.flush();
                            System.out.println("send params---" + paramss[index]);
                            index++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, 0, 100);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static String getParams(String params) {
        StringBuffer sBuf = new StringBuffer();
        sBuf.append("\r\n");
        sBuf.append("POST " + "/test" + " " + "HTTP/1.1\r\n");
        sBuf.append("Accept: " + "text/html, application/json, */*\r\n");
        sBuf.append("Accept-Language: zh_CN\r\n");
//            sBuf.append("Content-Type: application/x-www-form-urlencoded\r\n");
        sBuf.append("Content-Type: application/x-www-form-urlencoded;charset=utf-8 \r\n");
        sBuf.append("Host: " + "10.134.54.131:8080" + " \r\n");
        sBuf.append("Content-Length: " + params.length() + "\r\n");
        sBuf.append("Connection: Keep-Alive\r\n");
        sBuf.append("Cache-Control: no-cache\r\n");
//            http协议必须在报文头后面再加一个换行，通知服务器发送完成，不然服务器会一直等待
        sBuf.append("\r\n");
        sBuf.append(params);
        sBuf.append("\r\n");
        sBuf.append("\r\n");
        return sBuf.toString();
    }
}
