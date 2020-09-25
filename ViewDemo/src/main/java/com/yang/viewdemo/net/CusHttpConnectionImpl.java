package com.yang.viewdemo.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by
 * yangshuang on 2018/8/18.
 */

public class CusHttpConnectionImpl implements CusHttpConnection {


    private Socket socket;

    @Override
    public boolean connect(String host, String port) {
        int p = port == null || "".equals(port) ? 80 : Integer.valueOf(port);
        try {
            if (socket == null) {
                socket = new Socket();
                InetSocketAddress address = new InetSocketAddress(host, p);
                socket.connect(address, 3000);
            } else {
                if (socket.getPort() != p || !socket.getInetAddress().getHostName().equals(host)) {
                    socket.close();
                    socket.connect(new InetSocketAddress(host, p));
                } else if (!socket.isConnected()) {
                    socket.connect(new InetSocketAddress(host, p));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void sendHttpData(HttpRequest request) {
        try {
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(), "utf-8");
//            osw.write("GET " + path + " HTTP/1.1\r\n");
//            osw.write("Host: " + host + " \r\n");
            //http协议必须在报文头后面再加一个换行，通知服务器发送完成，不然服务器会一直等待
            osw.write("\r\n");
            osw.flush();
            osw.close();
            socket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public byte[] responseData(HttpRequest request) {
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "utf-8"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            bufferedReader.close();
//            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
