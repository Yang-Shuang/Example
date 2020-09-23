package com.yang.example.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.core.view.ViewParentCompat;
import androidx.recyclerview.widget.RecyclerView;
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
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;

    private boolean log = false;
    private ViewFlinger mViewFlinger;
    private boolean isFlinging = false;
    private boolean isKeepTop = false;

    private NestedScrollingParentHelper mParentHelper;
    private NestedScrollingChildHelper mChildHelper;
    private int scrollVerticalrange = 0;
    private OnScrollListener onScrollListener;

    private View ceilingView;
    private ScrollViewHelper mScrollViewHelper;
    private int scrollState = SCROLL_STATE_IDLE;
    private boolean isNestedScroll = false;
    private boolean isTouchScroll = false;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
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
        this(context, null);
    }

    public BaseNestScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseNestScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mViewFlinger = new ViewFlinger();

        mParentHelper = new NestedScrollingParentHelper(this);
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
        mScrollViewHelper = new ScrollViewHelper(this);

        mScrollViewHelper.setOnScrollEventListener(new ScrollViewHelper.OnScrollEventListener() {
            @Override
            public void onStopScroll() {
                isTouchScroll = false;
                setScrollState(SCROLL_STATE_IDLE);
            }

            @Override
            public void onScrollBy(int x, int y) {
                isTouchScroll = true;
                if (isScrollFlingParent(y)) {
                    computeScrollY(y, 1);
                    setScrollState(SCROLL_STATE_DRAGGING);
                }
            }

            @Override
            public void onFling(int vx, int vy) {
                isTouchScroll = true;
                if (ceilingView != null && ceilingView.getVisibility() != GONE) {
                    View view = getChildAt(indexOfChild(ceilingView) + (vy > 0 ? 1 : -1));
                    if (view.getVisibility() == GONE) return;
                    RecyclerView r = findRecyclerView(view);
                    setScrollState(SCROLL_STATE_SETTLING);
                    mViewFlinger.fling(r, vy);
                }
            }

            @Override
            public void onStopFling() {
                isTouchScroll = false;
                mViewFlinger.stop();
                setScrollState(SCROLL_STATE_IDLE);
            }
        });
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

    private void setScrollState(int state) {
        if (scrollState != state) {
            scrollState = state;
            if (onScrollListener != null) {
                onScrollListener.onScrollStateChanged(state);
            }
        }
    }

    private void notifyScroll(View view) {
        log("notifyScroll ");
        if (onScrollListener != null) {
            onScrollListener.onScrolled(view);
            if (ceilingView != null && ceilingView.getVisibility() != GONE) {
                if (isKeepTop != (getScrollY() >= ceilingView.getTop())) {
                    isKeepTop = getScrollY() >= ceilingView.getTop();
                    onScrollListener.onHeadViewKeepTop(ceilingView, isKeepTop);
                }
            }
        }
    }

    /**
     * 判断child是否在ceilingView 上面，用于区别ceilingView 上下方列表
     */
    private boolean isAboveCeilingChild(View child) {
        if (ceilingView == null || ceilingView.getVisibility() == GONE) return true;
        return indexOfChildOrParent(child) < indexOfChild(ceilingView);
    }

    private boolean isCeilingVisible() {
        if (ceilingView == null || ceilingView.getVisibility() == GONE) return false;
        MarginLayoutParams p = (MarginLayoutParams) ceilingView.getLayoutParams();
        return getHeight() + getScrollY() > ceilingView.getTop() - p.topMargin;
    }

    private boolean isCeilingTop() {
        if (ceilingView == null || ceilingView.getVisibility() == GONE) return false;
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
                return isCeilingVisible(); // recyclerViewIsTop(recyclerView);
            }
        } else {
            if (dy > 0) {
                return isCeilingVisible() && !isCeilingTop(); //  || recyclerViewIsBotom(recyclerView)
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
        if (axes == ViewCompat.SCROLL_AXIS_HORIZONTAL)
            return false;
        return true;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mParentHelper.onNestedScrollAccepted(child, target, axes);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
        isNestedScroll = true;
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mParentHelper.onStopNestedScroll(target);
        stopNestedScroll();
        isNestedScroll = false;
        if (!isFlinging)
            setScrollState(SCROLL_STATE_IDLE);
        log("onStopNestedScroll : " + getIdName(target));
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null, type);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @Nullable int[] consumed, int type) {
        if (consumed == null || dy == 0) return;
        log("onNestedPreScroll : " + getIdName(target));
        setScrollState(SCROLL_STATE_DRAGGING);
        if (target instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) target;
            if (isScrollFlingParent(recyclerView, dy)) {
                consumed[1] = computeScrollY(dy, 1);
            } else {
                ViewParentCompat.onNestedPreScroll(getParent(), this, dx, dy, consumed, type);
            }
        } else {
            if (isScrollFlingParent(dy)) {
                consumed[1] = computeScrollY(dy, 1);
            } else {
                ViewParentCompat.onNestedPreScroll(getParent(), this, dx, dy, consumed, type);
            }
        }
        notifyScroll(target);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        log("onNestedFling : " + getIdName(target));
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (target instanceof RecyclerView) {
            log("onNestedPreFling : " + getIdName(target) + " : " + velocityY);
            mViewFlinger.fling((RecyclerView) target, velocityY);
            return true;
        } else if (ceilingView != null && ceilingView.getVisibility() != GONE) {
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

    public void scrollToTop() {
        setScrollState(SCROLL_STATE_SETTLING);
        for (int i = getChildCount(); i >= 0; i--) {
            View child = getChildAt(i);
            RecyclerView recyclerView = findRecyclerView(child);
            if (recyclerView != null) {
                recyclerView.scrollToPosition(0);
                notifyScroll(BaseNestScrollView.this);
            } else {
                computeScrollY(-getScrollY(), SCROLL_STATE_IDLE);
                notifyScroll(BaseNestScrollView.this);
            }
        }
        if (getScrollY() != 0) {
            computeScrollY(-getScrollY(), SCROLL_STATE_IDLE);
            notifyScroll(BaseNestScrollView.this);
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyScroll(BaseNestScrollView.this);
                setScrollState(SCROLL_STATE_IDLE);
            }
        }, 50);
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
    private int computeScrollY(int y, int state) {
        int sy = 0;
        if (y >= 0) {
            int ceilingViewTop = ceilingView != null && ceilingView.getVisibility() != GONE ? ceilingView.getTop() : getHeight();
            int range = scrollVerticalrange - ceilingViewTop > getHeight() ? ceilingViewTop : scrollVerticalrange - getHeight();
            // 确保上拉不会滑出
            if (range <= 0) return 0;
            sy = getScrollY() + y > range ? range - getScrollY() : y;
        } else {
            // 确保下拉不会滑出
            sy = getScrollY() < Math.abs(y) ? -getScrollY() : y;
        }
        scrollBy(0, sy);
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
        return recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent();
    }

    public RecyclerView getCurrentView() {
        View maxVisiableChild = null;
        int visibleHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View c = getChildAt(i);
            int h = getVisibleHeight(c);
            if (h > visibleHeight) {
                visibleHeight = h;
                maxVisiableChild = c;
            }
        }
        return findRecyclerView(maxVisiableChild);
    }

    private int getVisibleHeight(View child) {
        Rect visibleRect = new Rect();
        child.getLocalVisibleRect(visibleRect);

        if (visibleRect.bottom > 0 && visibleRect.top < getHeight()) {
            //  visibleRect.left < getWidth() && visibleRect.right > 0   viewpager left = position * screenWidth，所以不做水平方向判断
            return Math.abs(visibleRect.bottom - visibleRect.top);
        } else {
            return 0;
        }
    }

    RecyclerView findRecyclerView(View view) {
        if (view == null || view.getVisibility() == GONE) {
            return null;
        } else {
            if (view instanceof RecyclerView) {
                return (RecyclerView) view;
            } else if (view instanceof ViewPager) {
                ViewPager pager = (ViewPager) view;
                if (pager.getChildCount() > 0) {
                    if (pager.getChildAt(pager.getCurrentItem()) instanceof RecyclerView) {
                        return pager.findViewWithTag(pager.getCurrentItem());
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
                if (scrollByY > 0) {
                    //先判断是否 fling 第一个recycleview
                    RecyclerView recyclerView = findRecyclerView(BaseNestScrollView.this);
                    if (!recyclerViewIsBotom(recyclerView)) {
                        scrollByY = flingTarget(recyclerView, scrollByY);
                    }
                    // 在判断是否 fling 当前HomeNestedScrollView
                    if (scrollByY != 0 && isScrollFlingParent(recyclerView, scrollByY)) {
                        scrollByY = flingParent(scrollByY);
                    }

                    // 最后判断是否fling 下方recycleView
                    if (scrollByY != 0) {
                        RecyclerView rv = findRecyclerView(getChildAt(2));
                        if (rv != null && !recyclerViewIsBotom(rv)) {
                            scrollByY = flingTarget(rv, scrollByY);
                        }
                    }
                } else if (scrollByY < 0) {
                    // 先判断是否fling 下方recycleView
                    RecyclerView rv = findRecyclerView(getChildAt(2));
                    if (rv != null && !recyclerViewIsTop(rv)) {
                        scrollByY = flingTarget(rv, scrollByY);
                    }

                    // 在判断是否 fling 当前HomeNestedScrollView
                    if (scrollByY != 0 && isScrollFlingParent(rv, scrollByY)) {
                        scrollByY = flingParent(scrollByY);
                    }
                    //最后判断是否 fling 第一个recycleview
                    RecyclerView recyclerView = findRecyclerView(BaseNestScrollView.this);
                    if (!recyclerViewIsTop(recyclerView)) {
                        scrollByY = flingTarget(recyclerView, scrollByY);
                    }
                }
                //如果 scrollByY 还是没有完全消耗 说明已经没有可以fling的距离，则停止fling；
                if (scrollByY == 0) {
                    fling = true;
                }
                if (fling) {
                    postOnAnimation();
                } else {
                    isFlinging = false;
                    setScrollState(SCROLL_STATE_IDLE);
                }
            } else {
                isFlinging = false;
                setScrollState(SCROLL_STATE_IDLE);
            }
            enableRunOnAnimationRequests();
        }

        int flingParent(int scrollByY) {
            //计算parent能滚动的距离
            int sy = computeCanScrollY(scrollByY);
            if (sy != 0) {
                computeScrollY(sy, 2);
                log("Parent  fling " + sy);
                notifyScroll(BaseNestScrollView.this);
            }
            return scrollByY - sy;
        }

        int flingTarget(RecyclerView target, int scrollByY) {
            // 计算RecycleView 能滚动的距离 y或者 <y;
            int targetScrollY = computeTargetFlingDistance(target, scrollByY);
            if (targetScrollY != 0) {
                target.scrollBy(0, targetScrollY);
                log("RecyclerView  fling " + targetScrollY);
                notifyScroll(target);
            }
            return scrollByY - targetScrollY;
        }

        public void fling(RecyclerView target, float velocityY) {
            log("开始fling");
            isFlinging = true;
            setScrollState(SCROLL_STATE_SETTLING);
            mTarget = target;
            lastCurrY = 0;
            mScroller.fling(0, 0, 0, (int) velocityY, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            postOnAnimation();
        }

        public void stop() {
            isFlinging = false;
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
                int ceilingViewTop = ceilingView != null && ceilingView.getVisibility() != GONE ? ceilingView.getTop() : getHeight();
                int range = scrollVerticalrange - ceilingViewTop > getHeight() ? ceilingViewTop : scrollVerticalrange - getHeight();
                if (range <= 0) return 0;
                sy = getScrollY() + y > range ? range - getScrollY() : y;
            } else {
                sy = getScrollY() + y < 0 ? -getScrollY() : y;
            }
            return sy;
        }
    }

    public abstract static class OnScrollListener {
        public void onScrollStateChanged(int newState) {
        }

        public void onScrolled(View view) {
        }

        public void onHeadViewKeepTop(View view, boolean isKeepTop) {
        }
    }

}
