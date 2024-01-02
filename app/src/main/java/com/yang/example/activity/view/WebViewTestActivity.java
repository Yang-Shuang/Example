package com.yang.example.activity.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.yang.example.R;
import com.yang.base.activity.SimpleBarActivity;
import com.yang.base.utils.LogUtil;
import com.yang.example.utils.WebViewUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class WebViewTestActivity extends SimpleBarActivity {

    private static final String TAG = "WebView";

    private static final String huimaiUrl = "https://m.ugoshop.com";
//    private static final String huimaiUrl = "http://open.thunderurl.com/test/edit.html";

    private WebView mWebView;
    private View back, next, refresh, go, web_change_btn;
    private EditText urlEdt;
    private ProgressBar bar;
    private long time;
    private List<String> urls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_test);

        time = System.currentTimeMillis();
        LogUtil.e(TAG, "findViewById : " + time);
        mWebView = (WebView) findViewById(R.id.webview);
        long et = System.currentTimeMillis();
        LogUtil.e(TAG, "findViewById-End : " + (et - time) + "  :  " + et);
        time = et;
        web_change_btn = findViewById(R.id.web_change_btn);
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
        web_change_btn.setOnClickListener(this);

        initWebView();
        urlEdt.setText(huimaiUrl);

        urls.add(huimaiUrl);
        urls.add("file://" + getFilesDir() + "/html/ugo.html");
    }

    @SuppressLint("NewApi")
    private void initWebView() {
        WebViewUtils.initWebView(mWebView);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                LogUtil.e("onProgressChanged---" + newProgress);
                if (newProgress > 0 && newProgress != 100) {
                    bar.setVisibility(View.VISIBLE);
                }
                bar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                LogUtil.e("onReceivedTitle---" + title);
                setTitleStr("" + title);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                time = System.currentTimeMillis();
                LogUtil.e("shouldOverrideUrlLoading---" + url);
                if (url.startsWith("xunlei")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
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
//        time = System.currentTimeMillis();
//        LogUtil.e(TAG, "loadUrl : " + time);
//        mWebView.loadUrl(huimaiUrl);
//        long t = System.currentTimeMillis();
//        LogUtil.e(TAG, "loadUrl-END : " + (t - time) + "    :   " + t);
//        time = t;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.web_change_btn:
//                String eUrl = urlEdt.getText().toString();
//                if (TextUtils.isEmpty(eUrl)) return;
//                int index = urls.indexOf(eUrl);
//                if (index != -1) {
//                    if (urls.size() != 1) {
//                        index = index == urls.size() - 1 ? 0 : index + 1;
//                        urlEdt.setText(urls.get(index));
//                    }
//                } else {
//                    urlEdt.setText(urls.get(0));
//                }
                try {
                    Field field = mWebView.getClass().getDeclaredField("mProvider");
                    field.setAccessible(true);
                    Object o = field.get(mWebView);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
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
                if (!urls.contains(url)) {
                    urls.add(url);
                }
                time = System.currentTimeMillis();
                LogUtil.e("loadUrl---" + time);
                mWebView.loadUrl(url);
                break;
        }
    }
}
