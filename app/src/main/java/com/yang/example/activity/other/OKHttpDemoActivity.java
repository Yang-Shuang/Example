package com.yang.example.activity.other;

import android.os.Bundle;

import com.yang.base.activity.SimpleBarActivity;
import com.yang.example.R;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OKHttpDemoActivity extends SimpleBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp);
    }

    private void initClient() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("").get().build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void request() {

    }
}
