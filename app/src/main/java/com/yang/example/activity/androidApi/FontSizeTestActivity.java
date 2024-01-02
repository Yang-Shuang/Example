package com.yang.example.activity.androidApi;


import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;

import com.yang.base.activity.SimpleBarActivity;
import com.yang.example.R;
import com.yang.example.view.NoteView;

import java.lang.reflect.Method;

public class FontSizeTestActivity extends SimpleBarActivity {

    private NoteView noteview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_size_test);

        noteview = findViewById(R.id.noteview);


    }


    @Override
    protected void onResume() {
        super.onResume();
        DisplayMetrics outMetrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(outMetrics);
        int screenWidth = outMetrics.widthPixels;
        int screenHeight = outMetrics.heightPixels;

        Configuration configuration = getResources().getConfiguration();
        noteview.addText("config.fontScale : " + configuration.fontScale);
        noteview.addText("config.densityDpi : " + configuration.densityDpi);
        noteview.addText("config.screenWidthDp : " + configuration.screenWidthDp);

        noteview.addText("mMetrics.xdpi : " + getResources().getDisplayMetrics().xdpi);
        noteview.addText("mMetrics.density : " + getResources().getDisplayMetrics().density);
        noteview.addText("mMetrics.densityDpi : " + getResources().getDisplayMetrics().densityDpi);
        noteview.addText("mMetrics.scaledDensity : " + getResources().getDisplayMetrics().scaledDensity);
        noteview.addText("default density : " + getDefaultDisplayDensity());
        noteview.addText("screenWidth : " + screenWidth);
        Point p1 = new Point();
        display.getSize(p1);
        Point p2 = new Point();
        display.getRealSize(p2);
        DisplayMetrics outMetrics1 = new DisplayMetrics();
        display.getRealMetrics(outMetrics1);
        noteview.addText("display.getSize : " + p1.x);
        noteview.addText("display.getRealSize : " + p2.x);
        noteview.addText("display.getRealMetrics : " + outMetrics1.widthPixels);
    }

    /**
     * 获取手机出厂时默认的densityDpi
     */
    private int getDefaultDisplayDensity() {
        try {
            Class aClass = Class.forName("android.view.WindowManagerGlobal");
            Method method = aClass.getMethod("getWindowManagerService");
            method.setAccessible(true);
            Object iwm = method.invoke(aClass);
            Method getInitialDisplayDensity = iwm.getClass().getMethod("getInitialDisplayDensity", int.class);
            getInitialDisplayDensity.setAccessible(true);
            Object densityDpi = getInitialDisplayDensity.invoke(iwm, Display.DEFAULT_DISPLAY);
            return (int) densityDpi;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    protected void onRightButtonClick() {
        super.onRightButtonClick();
        DisplayMetrics outMetrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(outMetrics);

    }
}