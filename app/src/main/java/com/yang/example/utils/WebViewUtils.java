package com.yang.example.utils;

import android.os.Build;
import androidx.annotation.RequiresApi;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewUtils {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void initWebView(WebView mWebView){

        WebView.setWebContentsDebuggingEnabled(true);

        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.removeJavascriptInterface("accessibility");
        mWebView.removeJavascriptInterface("accessibilityTraversal");
        //2019-01-28 lxs 新增 user-agent 使得web端正确判断
        String ua = mWebView.getSettings().getUserAgentString();
        mWebView.getSettings().setUserAgentString(ua + ";huimaiApp");

        if (Build.VERSION.SDK_INT <= 18) {
            mWebView.getSettings().setSavePassword(false);
        }
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        //v3.6.15 防止越权访问，跨域等安全问题
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);//在JELLY_BEAN以前的版本默认是true,在JELLY_BEAN及以后的版本中默认已被禁止。
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);//在JELLY_BEAN以前的版本默认是true,在JELLY_BEAN及以后的版本中默认已被禁止。

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
    }
}
