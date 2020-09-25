package com.yang.viewdemo.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;


/**
 * Created by
 * yangshuang on 2018/8/20.
 */

public class CustomScrollView extends LinearLayout {

    private int minHeight;
    private int downY;
    private boolean moving;
    private int scrollY;

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        try {
            DisplayMetrics metric = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metric);
            int screen_height = metric.heightPixels;
            minHeight = (int) (screen_height / 100f * 1.5f);
        } catch (Exception var3) {
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e("ViewDemo", "dispatchTouchEvent--" + moving + "---" + ev.getAction());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) ev.getY();
                scrollY = getScrollY();
                return false;
            case MotionEvent.ACTION_UP:
                moving = false;
                return false;
            case MotionEvent.ACTION_MOVE:
                return true;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("ViewDemo", "onTouchEvent--" + moving);
        if (Math.abs(event.getY() - downY) >= minHeight) {
            moving = true;
        }
        if (moving) {
            scrollTo(0, (int) (scrollY + event.getY() - downY));
            return true;
        }
        return true;
    }
}
