package com.yang.example.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.webkit.WebView;

import com.yang.base.activity.BaseActivity;
import com.yang.example.R;

public class LauncherActivity extends BaseActivity {

    private boolean delay = false;
    private boolean webInit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        WebView webView = findViewById(R.id.web_view);
//        WebViewUtils.initWebView(webView);
//        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                webInit = true;
//                toHome();
//            }
//        });
//        webView.loadUrl("https://m.ugoshop.com/");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                delay = true;
                toHome();
            }
        }, 2000);
    }

    private synchronized void toHome(){
        if (delay && webInit && !isFinishing()){
            startActivity(new Intent(LauncherActivity.this, MainActivity.class));
            finish();
        }
    }
}
