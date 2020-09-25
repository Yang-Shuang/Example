package com.yang.example.view;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.yang.base.utils.LogUtil;

public class MyViewPager extends ViewPager {


        private boolean isInterceptEvent = false;

        public MyViewPager(Context context) {
                super(context);
        }

        public MyViewPager(Context context, AttributeSet attrs) {
                super(context, attrs);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
                LogUtil.e("MyViewPager----dispatchTouchEvent -- " + ev.getAction());
                boolean b = super.dispatchTouchEvent(ev);
                LogUtil.e("MyViewPager----dispatchTouchEvent -- " + b);
                return b;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
                LogUtil.e("MyViewPager----onInterceptTouchEvent -- " + ev.getAction());
                isInterceptEvent = super.onInterceptTouchEvent(ev);
                LogUtil.e("MyViewPager----onInterceptTouchEvent -- " + isInterceptEvent);
                return isInterceptEvent;
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
                LogUtil.e("MyViewPager----onTouchEvent -- " + ev.getAction());
                boolean b = false;
//                if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP){
//                        isInterceptEvent = true;
//                }
//                if (isInterceptEvent)
//                        b = super.onTouchEvent(ev);
                LogUtil.e("MyViewPager----onTouchEvent -- " + b);
                return b;
        }
}
