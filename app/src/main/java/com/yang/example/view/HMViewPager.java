package com.yang.example.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class HMViewPager extends LinearLayout {

        private static final String TAG = "HMViewPager";
        private int mOrientation = HORIZONTAL;

        private float mLastX;
        private float mLastY;

        private int mContentWidth;
        private int mContentHeight;

        private int pageCount = 0;
        private int currentPage = 0;
        private int nextPage = 1;
        private int lastPage = 0;

        ScrollRunable mScrollRunable = new ScrollRunable();

        public HMViewPager(Context context) {
                super(context);
                initViews(null, 0);
        }

        public HMViewPager(Context context, AttributeSet attrs) {
                super(context, attrs);
                initViews(attrs, 0);
        }

        public HMViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
                initViews(attrs, defStyleAttr);
        }

        private void initViews(AttributeSet attrs, int defStyleAttr) {
                mOrientation = getOrientation();
        }

        @Override
        public void addView(View child, int index, ViewGroup.LayoutParams params) {
                Log.e(TAG, "addView");
                super.addView(child, index, params);
        }

        @Override
        protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
                Log.e(TAG, "addViewInLayout");
                return super.addViewInLayout(child, index, params);
        }

        @Override
        public void onViewAdded(View child) {
                super.onViewAdded(child);
                Log.e(TAG, "onViewAdded");
        }


        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
                if (mOrientation == VERTICAL) {
                        layoutVertical(l, t, r, b);
                } else {
                        layoutHorizontal(l, t, r, b);
                }
        }

        private void layoutHorizontal(int left, int top, int right, int bottom) {
                final int count = getChildCount();
                int childTop = 0;
                int childLeft = 0;
                int start = 0;
                int dir = 1;
                pageCount = 0;
                mContentHeight = getHeight();
                for (int i = 0; i < count; i++) {
                        final int childIndex = start + dir * i;
                        final View child = getChildAt(childIndex);
                        if (child == null) {
                                childLeft += 0;
                        } else if (child.getVisibility() != GONE) {
                                final LinearLayout.LayoutParams lp =
                                        (LinearLayout.LayoutParams) child.getLayoutParams();
                                lp.width = getWidth();
                                lp.height = getHeight();
                                int childWidth = lp.width;
                                int childHeight = lp.height;
                                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
                                childLeft += childWidth;
                                mContentWidth += childWidth;
                                pageCount++;
                        }
                }
        }

        private void layoutVertical(int l, int t, int r, int b) {
                final int count = getChildCount();
                int childTop = 0;
                int childLeft = 0;
                int start = 0;
                int dir = 1;
                pageCount = 0;
                mContentWidth = getWidth();
                for (int i = 0; i < count; i++) {
                        final int childIndex = start + dir * i;
                        final View child = getChildAt(childIndex);
                        if (child == null) {
                                childTop += 0;
                        } else if (child.getVisibility() != GONE) {
                                final LinearLayout.LayoutParams lp =
                                        (LinearLayout.LayoutParams) child.getLayoutParams();
                                lp.width = getWidth();
                                lp.height = getHeight();
                                int childWidth = lp.width;
                                int childHeight = lp.height;
                                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
                                childTop += childHeight;
                                mContentHeight += childHeight;
                                pageCount++;
                        }
                }
        }

        private void mesureNextPage() {
                int pageSize = mOrientation == VERTICAL ? getHeight() : getWidth();
                int scrollDis = mOrientation == VERTICAL ? getScrollY() : getScrollX();
                if (scrollDis == 0) {
                        lastPage = currentPage;
                        nextPage = 0;
                } else {
                        int p = scrollDis / pageSize;
                        if (p > 0) {
                                int d = scrollDis - (pageSize * p);
                                p = p + ((d > pageSize / 2) ? 1 : 0);
                        } else {
                                p = scrollDis > pageSize / 2 ? 1 : 0;
                        }
                        lastPage = currentPage;
                        nextPage = p;
                        currentPage = p;

                }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
                return super.dispatchTouchEvent(ev);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
                return super.onInterceptTouchEvent(ev);
        }


        @Override
        public boolean onTouchEvent(MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                                mLastX = event.getX();
                                mLastY = event.getY();
                                break;
                        case MotionEvent.ACTION_UP:
                                mesureNextPage();
                                int end = this.nextPage * getWidth();
                                mScrollRunable.scroll(end);
                                break;
                        case MotionEvent.ACTION_MOVE:
                                float x = event.getX();
                                scrollBy((int) (mLastX - x), 0);
                                mLastX = x;
                                break;
                }
                return true;
        }


        public class ScrollRunable implements Runnable {

                private int perSize;
                private int end;

                public ScrollRunable() {

                }

                @Override
                public void run() {
                        int currentScroll = getCurrentScroll();
                        if (currentScroll != this.end) {
                                if (Math.abs(this.end - currentScroll) <= perSize) {
                                        if (mOrientation == VERTICAL) {
                                                scrollBy(0, this.end - currentScroll);
                                        } else {
                                                scrollBy(this.end - currentScroll, 0);
                                        }
                                } else {
                                        int b = (this.end - currentScroll) / perSize / 6;
                                        b = Math.abs(b) > 0 ? b : 1;
                                        if (mOrientation == VERTICAL) {
                                                scrollBy(0, b * perSize);
                                        } else {
                                                scrollBy(b * perSize, 0);
                                        }
                                }
                                postOnAnimation();
                        } else {
                                removeCallbacks(this);
                        }
                }

                private int getCurrentScroll() {
                        return mOrientation == VERTICAL ? getScrollY() : getScrollX();
                }

                public void scroll(int end) {
                        perSize = mOrientation == VERTICAL ? getHeight() / 60 : getWidth() / 60;
                        this.end = end;
                        removeCallbacks(this);
                        ViewCompat.postOnAnimation(HMViewPager.this, this);
                }


                private void postOnAnimation() {
                        removeCallbacks(this);
                        ViewCompat.postOnAnimation(HMViewPager.this, this);
                }

                public void stopAny() {
                        removeCallbacks(this);
                }
        }
}
