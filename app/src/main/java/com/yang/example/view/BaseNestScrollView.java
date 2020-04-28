package com.yang.example.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewParentCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.yang.example.R;
import com.yang.example.utils.ScrollViewHelper;

public class BaseNestScrollView extends FrameLayout implements NestedScrollingParent2, NestedScrollingChild2 {

    private static final String TAG = "BaseNestScrollView";
    private boolean log = true;
    private View ceilingView;
    private ViewFlinger mViewFlinger;

    private NestedScrollingParentHelper mParentHelper;
    private NestedScrollingChildHelper mChildHelper;
    private int scrollVerticalrange = 0;
    private RecyclerView.OnScrollListener onScrollListener;
    private ScrollViewHelper mScrollViewHelper;

    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    private void log(String msg) {
        if (log && msg != null)
            Log.i(TAG, msg);
    }

    public BaseNestScrollView(Context context) {
        super(context);
    }

    public BaseNestScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mViewFlinger = new ViewFlinger();

        mParentHelper = new NestedScrollingParentHelper(this);
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
        mScrollViewHelper = new ScrollViewHelper(this);

        mScrollViewHelper.setOnScrollEventListener(new ScrollViewHelper.OnScrollEventListener() {
            @Override
            public void onScrollBy(int x, int y) {
                if (isScrollFlingParent(y)) {
                    computeScrollY(y);
                }
            }

            @Override
            public void onFling(int vx, int vy) {
                if (ceilingView != null) {
                    View view = getChildAt(indexOfChild(ceilingView) + (vy > 0 ? 1 : -1));
                    RecyclerView r = findRecyclerView(view);
                    mViewFlinger.fling(r, vy);
                }
            }

            @Override
            public void onStopFling() {
                mViewFlinger.stop();
            }
        });
    }

    public BaseNestScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutVertical(left, top, right, bottom);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (child.getLayoutParams() instanceof NestLayoutParams && ((NestLayoutParams) child.getLayoutParams()).isHead) {
            ceilingView = child;
        }

        if (child instanceof RecyclerView) {
            ((RecyclerView) child).addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (onScrollListener != null)
                        onScrollListener.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (onScrollListener != null)
                        onScrollListener.onScrolled(recyclerView, dx, dy);
                }
            });
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new NestLayoutParams(getContext(), attrs);
    }

    /**
     * 垂直线性方式布局
     */
    private void layoutVertical(int l, int t, int r, int b) {
        final int count = getChildCount();
        int y = getPaddingTop();
        int x = getPaddingLeft();
        int ceilHeight = 0;

        log("Parent Width : " + getWidth());
        log("Parent Height : " + getHeight());
        scrollVerticalrange = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            MarginLayoutParams childParams = (MarginLayoutParams) child.getLayoutParams();
            // 子view最大宽度  默认充满父控件
            int childWidth = getWidth() - getPaddingLeft() - getPaddingRight() - childParams.leftMargin - childParams.rightMargin;
            // 子view最大高度  需减去吸顶卡位child高度
            int childHeight = getHeight() - getPaddingTop() - getPaddingBottom() - ceilHeight;

            int measuredHeight = child.getMeasuredHeight();
            log(i + "Child measureHeight : " + measuredHeight);
            if (measuredHeight < childHeight) {
                // 子view MeasuredHeight小于childHeight  说明此子view 不会充满全凭，如只有一个item的recycleview，或者固定高度tab等
                childHeight = child.getMeasuredHeight();
            }
            x = x + childParams.leftMargin;
            y = y + childParams.topMargin;
            child.layout(x, y, x + childWidth, y + childHeight);
            // 如果是吸顶卡位view  累加ceilHeight
            if (childParams instanceof NestLayoutParams && ((NestLayoutParams) childParams).isHead) {
                ceilHeight += childHeight;
            }
            scrollVerticalrange += childHeight + childParams.topMargin + childParams.bottomMargin;
            y = y + childHeight;
        }
    }

    /**
     * 判断child是否在ceilingView 上面，用于区别ceilingView 上下方列表
     */
    private boolean isAboveCeilingChild(View child) {
        if (ceilingView == null) return true;
        return indexOfChildOrParent(child) < indexOfChild(ceilingView);
    }

    private boolean isCeilingVisiable() {
        if (ceilingView == null) return false;
        MarginLayoutParams p = (MarginLayoutParams) ceilingView.getLayoutParams();
        return getHeight() + getScrollY() > ceilingView.getTop() - p.topMargin;
    }

    private boolean isCeilingTop() {
        if (ceilingView == null) return false;
        return getScrollY() >= ceilingView.getTop();
    }

    int indexOfChildOrParent(View child) {
        int i = indexOfChild(child);
        return i != -1 ? i : indexOfChildOrParent((View) child.getParent());
    }

    /**
     * 非Recycleview view传递事件时 使用此方法判断是否滚动当前view
     */
    boolean isScrollFlingParent(int dy) {
        if (dy > 0) {
            return getScrollY() < getHeight();
        } else {
            return getScrollY() > 0;
        }
    }

    boolean isScrollFlingParent(RecyclerView recyclerView, int dy) {
        if (recyclerView == null) return isScrollFlingParent(dy);
        boolean isAbove = isAboveCeilingChild(recyclerView);
        if (isAbove) {
            // ceilingView 上方RecyclerView
            if (dy > 0) {
                return recyclerViewIsBotom(recyclerView);
            } else {
                return isCeilingVisiable(); // recyclerViewIsTop(recyclerView);
            }
        } else {
            if (dy > 0) {
                return isCeilingVisiable() && !isCeilingTop(); //  || recyclerViewIsBotom(recyclerView)
            } else {
                return recyclerViewIsTop(recyclerView);
            }
        }
    }

    private String getIdName(View view) {
        if (view.getId() == -1) return "NO_ID";
        return view.getContext().getResources().getResourceEntryName(view.getId());
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mScrollViewHelper.onTouchEvent(event);
    }

    //***********************************    NestedScrollingParent2   *************************************
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        mViewFlinger.stop();
        return true;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mParentHelper.onNestedScrollAccepted(child, target, axes);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mParentHelper.onStopNestedScroll(target);
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null, type);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @Nullable int[] consumed, int type) {
        if (consumed == null || dy == 0 || Math.abs(dx) > Math.abs(dy)) return;
        if (target instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) target;
            if (isScrollFlingParent(recyclerView, dy)) {
                consumed[1] = computeScrollY(dy);
            } else {
                ViewParentCompat.onNestedPreScroll(getParent(), this, dx, dy, consumed, type);
            }
        } else {
            if (isScrollFlingParent(dy)) {
                consumed[1] = computeScrollY(dy);
            } else {
                ViewParentCompat.onNestedPreScroll(getParent(), this, dx, dy, consumed, type);
            }
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (target instanceof RecyclerView) {
            log("onNestedPreFling : " + getIdName(target) + " : " + velocityY);
            mViewFlinger.fling((RecyclerView) target, velocityY);
            return true;
        } else if (ceilingView != null) {
            View view = getChildAt(indexOfChild(ceilingView) + (velocityY > 0 ? 1 : -1));
            RecyclerView r = findRecyclerView(view);
            mViewFlinger.fling(r, velocityY);
            return true;
        }
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, false);
    }
    //***********************************    NestedScrollingParent2   *************************************

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }

    //***********************************    NestedScrollingChild2   *************************************
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        return mChildHelper.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public void stopNestedScroll(int type) {
        mChildHelper.stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return mChildHelper.hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow,
                                           int type) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
    //***********************************    NestedScrollingChild2   *************************************


    public int getRealScrollY() {
        int y = getScrollY();
        if (getChildCount() > 0 && getChildAt(0) instanceof RecyclerView) {
            y = y + ((RecyclerView) getChildAt(0)).computeVerticalScrollOffset();
        }
        return y;
    }

    @Override
    protected int computeVerticalScrollRange() {
        return scrollVerticalrange;
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }

    @Override
    protected int computeHorizontalScrollExtent() {
        return super.computeHorizontalScrollExtent();
    }

    /**
     * 计算并滚动Y
     *
     * @return 可返回当前控件消耗的滚动距离
     */
    private int computeScrollY(int y) {
        int sy = 0;
        if (y >= 0) {
            int range = scrollVerticalrange - ceilingView.getTop() > getHeight() ? ceilingView.getTop() : scrollVerticalrange - getHeight();
            // 确保上拉不会滑出
            if (range <= 0) return 0;
            sy = getScrollY() + y > range ? range - getScrollY() : y;
        } else {
            // 确保下拉不会滑出
            sy = getScrollY() < Math.abs(y) ? -getScrollY() : y;
        }
        scrollBy(0, sy);
        if (sy != 0) {
            if (onScrollListener != null) {
                onScrollListener.onScrollStateChanged(null, 1);
                onScrollListener.onScrolled(null, 0, sy);
                onScrollListener.onScrollStateChanged(null, 0);
            }
        }
        return sy;
    }

    private boolean recyclerViewIsTop(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(-1);
    }

    private boolean recyclerViewIsBotom(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }

    private int getScrollY(RecyclerView recyclerView) {
        return recyclerView.computeVerticalScrollOffset();
    }

    private int getScrollRange(RecyclerView recyclerView) {
        return recyclerView.computeVerticalScrollRange() - computeVerticalScrollExtent();
    }

    RecyclerView findRecyclerView(View view) {
        if (view == null) {
            return null;
        } else {
            if (view instanceof RecyclerView) {
                return (RecyclerView) view;
            } else if (view instanceof ViewPager) {
                ViewPager pager = (ViewPager) view;
                if (pager.getChildCount() > 0) {
                    if (pager.getChildAt(pager.getCurrentItem()) instanceof RecyclerView) {
                        return (RecyclerView) pager.getChildAt(pager.getCurrentItem());
                    } else if (pager.getChildAt(pager.getCurrentItem()) instanceof ViewGroup) {
                        return findRecyclerView(((ViewGroup) pager.getChildAt(pager.getCurrentItem())).getChildAt(0));
                    }
                    return null;
                } else {
                    return null;
                }
            } else if (view instanceof ViewGroup) {
                return findRecyclerView(((ViewGroup) view).getChildAt(0));
            } else {
                return null;
            }
        }
    }

    public class NestLayoutParams extends FrameLayout.LayoutParams {

        public boolean isHead = false;

        public NestLayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.BaseNestScrollView);
            isHead = a.getBoolean(R.styleable.BaseNestScrollView_layout_head, false);
            a.recycle();
        }
    }

    class ViewFlinger implements Runnable {
        private OverScroller mScroller;
        private RecyclerView mTarget;
        private boolean mEatRunOnAnimationRequest = false;
        private boolean mReSchedulePostAnimationCallback = false;
        private int lastCurrY = 0;

        public ViewFlinger() {
            mScroller = new OverScroller(getContext());
        }

        @Override
        public void run() {
            disableRunOnAnimationRequests();
            final OverScroller scroller = mScroller;
            if (scroller.computeScrollOffset()) {
                // scroller 计算此次fling需要fling的总距离
                int scrollToY = scroller.getCurrY();
                int scrollByY = scrollToY - lastCurrY;
                lastCurrY = scrollToY;
                log("需要fling的总距离 :  " + scrollByY);

                boolean fling = false; // 是否继续post 此fling runable
                if (scrollByY != 0) {
                    if (isScrollFlingParent(mTarget, scrollByY)) {
                        // fling 从parent开始 > recycleView
                        scrollByY = flingParent(scrollByY);
                        if (scrollByY != 0) {
                            flingOther(scrollByY);
                        } else {
                            fling = true;
                        }
                    } else {
                        // fling 从子RecycleView开始  > parent > recycleView
                        scrollByY = flingTarget(scrollByY);
                        if (scrollByY != 0) {
                            scrollByY = flingParent(scrollByY);
                            if (scrollByY != 0) {
                                flingOther(scrollByY);
                            } else {
                                fling = true;
                            }
                        } else {
                            fling = true;
                        }
                    }
                } else {
                    fling = true;
                }
                if (fling)
                    postOnAnimation();
            }
            enableRunOnAnimationRequests();
        }

        int flingParent(int scrollByY) {
            //计算parent能滚动的距离
            int sy = computeCanScrollY(scrollByY);
            if (sy != 0) {
                scrollBy(0, sy);
                log("Parent  fling " + sy);
                if (onScrollListener != null) {
                    onScrollListener.onScrollStateChanged(null, 2);
                    onScrollListener.onScrolled(null, 0, sy);
                    onScrollListener.onScrollStateChanged(null, 0);
                }
            }
            return scrollByY - sy;
        }

        int flingTarget(int scrollByY) {
            // 计算RecycleView 能滚动的距离 y或者 <y;
            int targetScrollY = computeTargetFlingDistance(mTarget, scrollByY);
            if (targetScrollY != 0) {
                mTarget.scrollBy(0, targetScrollY);
                log("RecyclerView  fling " + targetScrollY);
            }
            return scrollByY - targetScrollY;
        }

        void flingOther(int scrollByY) {
            // 将剩余速率传递给 上\下方 RecyclerView，继续fling
            View view = getChildAt(indexOfChild(ceilingView) + (scrollByY > 0 ? 1 : -1));
            if (view != null) {
                float vel = mScroller.getCurrVelocity();
                RecyclerView recyclerView = findRecyclerView(view);
                vel = scrollByY < 0 ? -vel : vel;
                if (recyclerView != null) {
                    log("Other RecyclerView  fling vel : " + vel);
                    recyclerView.fling(0, (int) vel);
                }
            }
        }

        public void fling(RecyclerView target, float velocityY) {
            log("开始fling");
            mTarget = target;
            lastCurrY = 0;
            mScroller.fling(0, 0, 0, (int) velocityY, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            postOnAnimation();
        }

        public void stop() {
            removeCallbacks(this);
            mScroller.abortAnimation();
        }

        private void disableRunOnAnimationRequests() {
            mReSchedulePostAnimationCallback = false;
            mEatRunOnAnimationRequest = true;
        }

        private void enableRunOnAnimationRequests() {
            mEatRunOnAnimationRequest = false;
            if (mReSchedulePostAnimationCallback) {
                postOnAnimation();
            }
        }

        void postOnAnimation() {
            if (mEatRunOnAnimationRequest) {
                mReSchedulePostAnimationCallback = true;
            } else {
                removeCallbacks(this);
                ViewCompat.postOnAnimation(BaseNestScrollView.this, this);
            }
        }

        int computeTargetFlingDistance(RecyclerView view, int fy) {
            if (view == null) return 0;
            int sy = getScrollY(view);
            int range = getScrollRange(view);

            if (range == 0) return 0;
            if (fy < 0) {
                return sy + fy < 0 ? -sy : fy;
            } else {
                return sy + fy > range ? range - sy : fy;
            }
        }

        int computeCanScrollY(int y) {
            int sy = 0;
            if (y >= 0) {
                int range = scrollVerticalrange - ceilingView.getTop() > getHeight() ? ceilingView.getTop() : scrollVerticalrange - getHeight();
                if (range <= 0) return 0;
                sy = getScrollY() + y > range ? range - getScrollY() : y;
            } else {
                sy = getScrollY() + y < 0 ? -getScrollY() : y;
            }
            return sy;
        }
    }
}
