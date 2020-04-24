package com.yang.example.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class HMViewPager extends LinearLayout {

        private static final String TAG = "HMViewPager";

        private static final int UP = 1;
        private static final int DOWN = 2;
        private static final int RIGHT = 3;
        private static final int LEFT = 4;

        public static final int SCROLL_IDLE = 100;
        public static final int TOUCH_SCROLLING = 200;
        public static final int AUTO_SCROLLING = 300;
        private int mOrientation = HORIZONTAL;

        private float mLastX;
        private float mLastY;

        private int mContentWidth;
        private int mContentHeight;

        private int pageCount = 0;
        private int currentPage = 0;
        private int nextPage = 1;
        private int lastPage = 0;
        private int mScrollPointerId = 0;

        private int scrollDirection = 0;
        private int mTouchSlop;
        private int viewStatus = SCROLL_IDLE;

        private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
        private int mMinFlingVelocity, mMaxFlingVelocity;

        ScrollCallBack scrollCallBack = new ScrollCallBack() {
                @Override
                public void onScrollEnd() {
                        viewStatus = SCROLL_IDLE;
                        currentPage = nextPage;
                }
        };
        ScrollRunable mScrollRunable = new ScrollRunable(scrollCallBack);

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
                ViewConfiguration configuration = ViewConfiguration.get(getContext());
                mTouchSlop = configuration.getScaledTouchSlop();
                mMinFlingVelocity = configuration.getScaledMinimumFlingVelocity();
                mMaxFlingVelocity = configuration.getScaledMaximumFlingVelocity();
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

        /**
         * 根据当前滚动位置判断是否要滚动到下一页，判断逻辑为  当前位置 || 当前滚动速率
         *
         * @param isNextPage 此参数由速率计算得出，表示是否要滚动到下一页
         */
        private void mesureNextPage(boolean isNextPage) {
                int pageSize = mOrientation == VERTICAL ? getHeight() : getWidth();
                int scrollDis = mOrientation == VERTICAL ? getScrollY() : getScrollX();
                if (scrollDis == 0) {
                        changePage(0);
                }
                int p = scrollDis / pageSize;
                int n = 1; // n表示加一页或者减一页
                if (scrollDirection == LEFT || scrollDirection == UP) {
                        n = -1;
                }
                int d = p > 0 ? scrollDis - (pageSize * p) : scrollDis;
                p = currentPage + ((d > pageSize / 2 || isNextPage) ? n : 0);
                changePage(p);
        }

        private void changePage(int page) {
                if (page < 0) page = 0;
                if (page > pageCount - 1) page = pageCount - 1;
                lastPage = currentPage;
                nextPage = page;
        }


        private void computeDirection(float x, float y) {
                if (mOrientation == VERTICAL) {
                        if (y <= 0) {
                                scrollDirection = UP;
                        } else {
                                scrollDirection = DOWN;
                        }
                } else {
                        if (x <= 0) {
                                scrollDirection = LEFT;
                        } else {
                                scrollDirection = RIGHT;
                        }
                }
        }

        private void computeScrollBy(int x, int y) {
                int sx = 0;
                int sy = 0;
                switch (scrollDirection) {
                        case LEFT:
                                if (getScrollX() <= 0) {
                                        sx = 0 - getScrollX();
                                } else if (getScrollX() + x < 0) {
                                        sx = 0;
                                } else {
                                        sx = x;
                                }
                                break;
                        case RIGHT:
                                if (getScrollX() >= mContentWidth - getWidth()) {
                                        sx = mContentWidth - getWidth() - getScrollX();
                                } else if (getScrollX() + x >= mContentWidth - getWidth()) {
                                        sx = mContentWidth - getWidth() - getScrollX();
                                } else {
                                        sx = x;
                                }
                                break;
                        case UP:
                                if (getScrollY() <= 0) {
                                        sy = 0 - getScrollY();
                                } else if (getScrollY() + y < 0) {
                                        sy = 0;
                                } else {
                                        sy = y;
                                }
                                break;
                        case DOWN:
                                if (getScrollY() >= mContentHeight - getHeight()) {
                                        sy = mContentHeight - getHeight() - getScrollY();
                                } else if (getScrollY() + y >= mContentHeight - getHeight()) {
                                        sy = mContentHeight - getHeight() - getScrollY();
                                } else {
                                        sy = y;
                                }
                                break;
                }
                scrollBy(sx, sy);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
                Log.e(TAG,"dispatchTouchEvent");
                isExOnTouch = false;
                return super.dispatchTouchEvent(ev);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
                Log.e(TAG,"onInterceptTouchEvent");
                return super.onInterceptTouchEvent(ev);
        }

        private boolean isExOnTouch = false;
        public boolean isExOnTouch(){
                return isExOnTouch;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
                Log.e(TAG,"onTouchEvent");
                isExOnTouch = true;
                final MotionEvent vtev = MotionEvent.obtain(event);
                mVelocityTracker.addMovement(vtev);
                boolean handle = false;
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                                mLastX = event.getX();
                                mLastY = event.getY();
                                viewStatus = SCROLL_IDLE;
                                mScrollRunable.stopAny();
                                mScrollPointerId = event.getPointerId(0);
                                handle = true;
                                break;
                        case MotionEvent.ACTION_UP:
                                touchUp();
                                resetTouch();
                                handle = true;
                                break;
                        case MotionEvent.ACTION_MOVE:
                                handle = touchMove(event);
                                break;
                }
                return handle;
        }

        private boolean touchMove(MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                boolean move = false;
                computeDirection(mLastX - x, mLastY - y);
                if (scrollDirection == RIGHT || scrollDirection == LEFT) {
                        if (viewStatus == SCROLL_IDLE) {
                                if (Math.abs(mLastX - x) > mTouchSlop) viewStatus = TOUCH_SCROLLING;
                        }
                        if (viewStatus == TOUCH_SCROLLING) {
                                computeScrollBy((int) (mLastX - x), 0);
                                mLastX = x;
                                move = true;
                        }
                } else {
                        if (viewStatus == SCROLL_IDLE) {
                                if (Math.abs(mLastY - y) > mTouchSlop) viewStatus = TOUCH_SCROLLING;
                        }
                        if (viewStatus == TOUCH_SCROLLING) {
                                computeScrollBy(0, (int) (mLastY - y));
                                move = true;
                                mLastY = y;
                        }
                }
                return move;
        }

        private void touchUp() {
                if (viewStatus == TOUCH_SCROLLING) {
                        boolean isNextPage = false;
                        mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                        switch (scrollDirection) {
                                case LEFT:
                                case RIGHT:
                                        float xVelocity = mVelocityTracker.getXVelocity(mScrollPointerId);
                                        if (Math.abs(xVelocity) > 500) {
                                                isNextPage = true;
                                        }
                                        break;
                                case UP:
                                case DOWN:
                                        float yVelocity = mVelocityTracker.getYVelocity(mScrollPointerId);
                                        if (Math.abs(yVelocity) > 500) {
                                                isNextPage = true;
                                        }
                                        break;
                        }
                        mesureNextPage(isNextPage);
                        int end = this.nextPage * (mOrientation == VERTICAL ? getHeight() : getWidth());
                        viewStatus = AUTO_SCROLLING;
                        mScrollRunable.scroll(end);
                }
        }

        private void resetTouch() {
                if (mVelocityTracker != null) {
                        mVelocityTracker.clear();
                }
        }


        public interface ScrollCallBack {
                void onScrollEnd();
        }

        public class ScrollRunable implements Runnable {

                private int perSize;
                private int end;
                private ScrollCallBack mCallBack;

                public ScrollRunable(ScrollCallBack callBack) {
                        mCallBack = callBack;
                }

                @Override
                public void run() {
                        if (viewStatus != AUTO_SCROLLING) stopAny();
                        int currentScroll = getCurrentScroll();
                        if (currentScroll != this.end) {
                                if (Math.abs(this.end - currentScroll) <= perSize) {
                                        if (mOrientation == VERTICAL) {
                                                computeScrollBy(0, this.end - currentScroll);
                                        } else {
                                                computeScrollBy(this.end - currentScroll, 0);
                                        }
                                } else {
                                        int b = (this.end - currentScroll) / perSize / 6;
                                        b = Math.abs(b) > 0 ? b : this.end - currentScroll > 0 ? 1 : -1;
                                        if (mOrientation == VERTICAL) {
                                                computeScrollBy(0, b * perSize);
                                        } else {
                                                computeScrollBy(b * perSize, 0);
                                        }
                                }
                                postOnAnimation();
                        } else {
                                if (mCallBack != null) {
                                        mCallBack.onScrollEnd();
                                }
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
