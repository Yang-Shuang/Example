package com.yang.example.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.yang.example.R;
import com.yang.example.utils.LogUtil;

public class WebViewTestActivity extends SimpleBarActivity {

    private WebView mWebView;
    private View back, next, refresh, go;
    private EditText urlEdt;
    private ProgressBar bar;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_test);

        mWebView = (WebView) findViewById(R.id.webview);
        back = findViewById(R.id.web_back_btn);
        next = findViewById(R.id.web_next_btn);
        refresh = findViewById(R.id.web_refresh_btn);
        go = findViewById(R.id.web_go_btn);
        urlEdt = (EditText) findViewById(R.id.web_url_edt);
        bar = (ProgressBar) findViewById(R.id.web_progressbar);

        back.setOnClickListener(this);
        next.setOnClickListener(this);
        refresh.setOnClickListener(this);
        go.setOnClickListener(this);

        initWebView();
        urlEdt.setText("https://m.ugoshop.com/channels/mobile/?oauth_nonce=87443&oauth_signature_method=SHA&oauth_version=1.0&oauth_timestamp=1562655237700&deviceType=android7.1.2&appVersion=5.1.17&channelName=UGO&uid=&device_id=MWVkYTdhZGYyNWJjZGFjZGU1YzcyMzQwNWU5MTA1NGY%3D&accessToken=&screenResolution=1080*1794&getui_clientid=1eda7adf25bcdacde5c723405e91054f&bdUserId=775301176490045044&bdChannelId=4197927873572975352&id=5907&oauth_signature=76cb3f54559d939ab906f43723d33b68b1392481366afc4efd8730b289589577");
    }

    @SuppressLint("NewApi")
    private void initWebView(){
        WebView.setWebContentsDebuggingEnabled(true);

        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.removeJavascriptInterface("accessibility");
        mWebView.removeJavascriptInterface("accessibilityTraversal");
        //2019-01-28 lxs 新增 user-agent 使得web端正确判断
        String ua = mWebView.getSettings().getUserAgentString();
        mWebView.getSettings().setUserAgentString(ua+";huimaiApp");

        if (Build.VERSION.SDK_INT <= 18) {
            mWebView.getSettings().setSavePassword(false);
        }
        mWebView.getSettings().setJavaScriptEnabled(true);

//        mWebView.getSettings().setUseWideViewPort(true);
//        mWebView.getSettings().setLoadWithOverviewMode(true);

        //v3.6.15 防止越权访问，跨域等安全问题
//        mWebView.getSettings().setAllowFileAccess(false);
//        mWebView.getSettings().setAllowFileAccessFromFileURLs(false);//在JELLY_BEAN以前的版本默认是true,在JELLY_BEAN及以后的版本中默认已被禁止。
//        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(false);//在JELLY_BEAN以前的版本默认是true,在JELLY_BEAN及以后的版本中默认已被禁止。

        //5.0是默认不支持mixed content的，即不支持同时加载https和http混合模式；手动设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setBuiltInZoomControls(false);// 是否要启用内置的缩放功能false不启用
        if (Build.VERSION.SDK_INT >= 11) {
            mWebView.getSettings().setDisplayZoomControls(false);
        }
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                bar.setProgress(newProgress);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                bar.setVisibility(View.VISIBLE);
                LogUtil.e("onPageStarted---" + (System.currentTimeMillis() - time));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                bar.setVisibility(View.GONE);
                long t = (System.currentTimeMillis() - time);
                LogUtil.e("onPageFinished---" + t);
                showToast("onPageFinished:" + t);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        time = System.currentTimeMillis();
        mWebView.loadUrl("https://m.ugoshop.com/channels/mobile/?oauth_nonce=87443&oauth_signature_method=SHA&oauth_version=1.0&oauth_timestamp=1562655237700&deviceType=android7.1.2&appVersion=5.1.17&channelName=UGO&uid=&device_id=MWVkYTdhZGYyNWJjZGFjZGU1YzcyMzQwNWU5MTA1NGY%3D&accessToken=&screenResolution=1080*1794&getui_clientid=1eda7adf25bcdacde5c723405e91054f&bdUserId=775301176490045044&bdChannelId=4197927873572975352&id=5907&oauth_signature=76cb3f54559d939ab906f43723d33b68b1392481366afc4efd8730b289589577");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.web_back_btn:
                mWebView.goBack();
                time = System.currentTimeMillis();
                break;
            case R.id.web_next_btn:
                time = System.currentTimeMillis();
                mWebView.goForward();
                break;
            case R.id.web_refresh_btn:
                time = System.currentTimeMillis();
                mWebView.reload();
                break;
            case R.id.web_go_btn:
                String url = urlEdt.getText().toString();
                if (TextUtils.isEmpty(url)) return;
                time = System.currentTimeMillis();
                LogUtil.e("loadUrl---" + time);
                mWebView.loadUrl(url);
                break;
        }
    }
}
