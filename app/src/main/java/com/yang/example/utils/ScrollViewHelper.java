package com.yang.example.utils;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.VelocityTrackerCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

public class ScrollViewHelper {

    private View mTarget;
    private int mTouchSlop;
    private float touchY;
    private final int SCROLL_STATE_IDLE = 0;
    private final int SCROLL_STATE_DRAGGING = 1;
    private final int SCROLL_STATE_SETTLING = 2;
    private int mMinFlingVelocity, mMaxFlingVelocity;
    private int scrollStatus = 0;

    private static final int INVALID_POINTER = -1;
    private int mScrollPointerId = INVALID_POINTER;
    private OnScrollEventListener onScrollEventListener;

    public void setOnScrollEventListener(OnScrollEventListener onScrollEventListener) {
        this.onScrollEventListener = onScrollEventListener;
    }

    public ScrollViewHelper(View mTarget) {
        this.mTarget = mTarget;

        ViewConfiguration vc = ViewConfiguration.get(mTarget.getContext());
        mTouchSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
    }

    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();

    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);
        final MotionEvent vtev = MotionEvent.obtain(event);
        mVelocityTracker.addMovement(vtev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setScrollState(SCROLL_STATE_IDLE);
                mScrollPointerId = event.getPointerId(0);
                touchY = event.getY();
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                mScrollPointerId = event.getPointerId(event.getActionIndex());
                touchY = (int) (event.getY(event.getActionIndex()) + 0.5f);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                final int index = event.findPointerIndex(mScrollPointerId);
                if (index < 0) {
                    return false;
                }
                float y = event.getY(index);
                float dy = touchY - y;
                if (scrollStatus != SCROLL_STATE_DRAGGING) {
                    boolean startScroll = false;
                    if (Math.abs(dy) > mTouchSlop) {
                        if (dy > 0) {
                            dy -= mTouchSlop;
                        } else {
                            dy += mTouchSlop;
                        }
                        startScroll = true;
                    }
                    if (startScroll) {
                        scrollStatus = SCROLL_STATE_DRAGGING;
                    }
                }
                if (scrollStatus == SCROLL_STATE_DRAGGING) {
                    touchY = y;
                    if (onScrollEventListener != null) {
                        onScrollEventListener.onScrollBy(0, (int) dy);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                float yVelocity = -VelocityTrackerCompat.getYVelocity(mVelocityTracker, mScrollPointerId);
                if (Math.abs(yVelocity) < mMinFlingVelocity) {
                    yVelocity = 0F;
                } else {
                    yVelocity = Math.max(-mMaxFlingVelocity, Math.min(yVelocity, mMaxFlingVelocity));
                }
                if (yVelocity != 0 && scrollStatus != SCROLL_STATE_SETTLING && onScrollEventListener != null) {
                    onScrollEventListener.onFling(0, (int) yVelocity);
                    setScrollState(SCROLL_STATE_SETTLING);
                } else {
                    setScrollState(SCROLL_STATE_IDLE);
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.clear();
                }
                break;
        }
        vtev.recycle();
        return true;
    }

    private void setScrollState(int state) {
        if (state == scrollStatus) {
            return;
        }
        scrollStatus = state;
        if (state != SCROLL_STATE_SETTLING && onScrollEventListener != null) {
            onScrollEventListener.onStopFling();
        }
    }

    public interface OnScrollEventListener {
        void onStopScroll();

        void onScrollBy(int x, int y);

        void onFling(int vx, int vy);

        void onStopFling();
    }

}
