package com.yang.example.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenUtil {
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static float SCREEN_DENSITY;
    public static float SCREEN_DENSITYDpi;

    public ScreenUtil() {
    }

    public static void init(Context context) {
        try {
            DisplayMetrics metric = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metric);
            SCREEN_WIDTH = metric.widthPixels;
            SCREEN_HEIGHT = metric.heightPixels;
            SCREEN_DENSITY = metric.density;
            SCREEN_DENSITYDpi = (float) metric.densityDpi;
        } catch (Exception var3) {
            LogUtil.e(var3.getMessage());
        }

    }

    public static int dp2px(float dpValue) {
        return (int) (dpValue * SCREEN_DENSITY + 0.5F);
    }

    public static int px2dp(float pxValue) {
        return (int) (pxValue / SCREEN_DENSITY + 0.5F);
    }
}
