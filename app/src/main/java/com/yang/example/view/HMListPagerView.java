package com.yang.example.view;

import android.content.Context;
import android.graphics.Canvas;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yang.example.R;

import java.util.ArrayList;
import java.util.List;

public class HMListPagerView extends LinearLayout {

    private String TAG = "HMPager-" + System.identityHashCode(this);

    // 滑动方向
    private static final int UP = 1;
    private static final int DOWN = 2;
    private static final int RIGHT = 3;
    private static final int LEFT = 4;

    // 滑动状态
    public static final int SCROLL_IDLE = 100;
    public static final int TOUCH_SCROLLING = 200;
    public static final int AUTO_SCROLLING = 300;

    private float mLastDownX;
    private float mLastDownY;

    private int mLastScrollY;

    private float mLastX;
    private float mLastY;

    private int pageCount = 0;
    private int currentPage = 0;
    private int lastPage = 0;
    private int mScrollPointerId = 0;

    private int scrollDirection = UP;
    private int mTouchSlop;
    private int viewStatus = SCROLL_IDLE;

    private int mChildCount = 3; // 实际view数量 3 + 1
    private List<PageHolder> mHolders = new ArrayList<>();

    private PageAdapter adapter;
    private ChildCalculator mCalculator;
    private boolean isAllowAdd = false;
    private boolean hasNoInit = false;

    //滚动辅助  速度计算
    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private int mMinFlingVelocity, mMaxFlingVelocity;

    ScrollCallBack scrollCallBack = new ScrollCallBack() {
        @Override
        public void onScrollEnd() {
            viewStatus = SCROLL_IDLE;
            currentPage = getScrollY() / getHeight();
            mLastScrollY = getScrollY();
        }
    };
    ScrollRunable mScrollRunable = new ScrollRunable(scrollCallBack);

    public HMListPagerView(Context context) {
        this(context, null);
    }

    public HMListPagerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HMListPagerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs, defStyleAttr);
        setWillNotDraw(false);
    }


    private void initViews(AttributeSet attrs, int defStyleAttr) {
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = configuration.getScaledMaximumFlingVelocity();

        mCalculator = new ChildCalculator();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "-- onDraw --");
        super.onDraw(canvas);
        isAllowAdd = true;
        if (hasNoInit) {
            hasNoInit = false;
            mCalculator.calculate();
        }
    }

    @Override
    public void onViewAdded(View child) {
        Log.i(TAG, "-- onViewAdded --");
        super.onViewAdded(child);
        if (child.getTag(R.id.holder) != null) {
            PageHolder holder = (PageHolder) child.getTag(R.id.holder);
            if (!holder.isBind) {
                adapter.onBindViewHolder(holder, holder.getAdapterPos());
                holder.itemView.invalidate();
                invalidate();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "-- onMeasure --");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, "-- onLayout --" + changed);
        if (b == 0) return;
        layoutVertical(l, t, r, b);
    }

    private void layoutVertical(int l, int t, int r, int b) {
        // 当前所有child数量
        final int count = getChildCount();
        Log.i(TAG, "-- layoutVertical --" + count);

        // 当前页面高度
        int pageHeight = getHeight();

        for (int i = 0; i < count; i++) {
            PageHolder holder = (PageHolder) getChildAt(i).getTag(R.id.holder);
            int position = holder.getAdapterPos();

            int left = 0;
            // 计算view所在位置
            int top = position * pageHeight;
            int bottom = top + pageHeight;
            int right = getWidth();
            View child = getChildAt(i);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            child.layout(left, top, right, bottom);
        }
    }

    public void setAdapter(PageAdapter adapter) {
        if (adapter == null) return;
        if (this.adapter != null) {
            this.adapter.setListPagerView(null);
            this.adapter = null;
            mHolders.clear();
            removeAllViews();
        }

        this.adapter = adapter;
        this.adapter.setListPagerView(this);
        if (isAllowAdd)
            mCalculator.calculate();
        else
            hasNoInit = true;

    }

    /**
     * 根据当前滚动位置及速度判断是否要滚动到下一页或上一页，判断逻辑为  当前位置 || 当前滚动速率
     */
    private void mesureNextPage() {

        // 计算滑动速度
        mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
        float yVelocity = mVelocityTracker.getYVelocity(mScrollPointerId);
        Log.e(TAG, "-- mesureNextPage -- yVelocity : " + yVelocity);
        if (Math.abs(yVelocity) > 300) {
            scrollToPage(scrollDirection == UP ? currentPage - 1 : currentPage + 1);
        } else {
            int currentScrollY = getScrollY();
            if (currentScrollY == mLastScrollY) {
                viewStatus = SCROLL_IDLE;
                currentPage = mLastScrollY / getHeight();
            } else if (Math.abs(mLastScrollY - currentScrollY) > getHeight() / 3) {
                scrollToPage(mLastScrollY > currentScrollY ? currentPage - 1 : currentPage + 1);
            } else {
                scrollToPage(currentPage);
            }
        }

//
//        int pageSize = getHeight();
//        int scrollDis = getScrollY();
//        if (scrollDis == 0) {
//            changePage(0);
//        }
//        int p = scrollDis / pageSize;
//        int n = 1; // n表示加一页或者减一页
//        if (scrollDirection == LEFT || scrollDirection == UP) {
//            n = -1;
//        }
//        int d = p > 0 ? scrollDis - (pageSize * p) : scrollDis;
//        p = currentPage + ((d > pageSize / 2 || isNextPage) ? n : 0);
//        changePage(p);
    }

    private void scrollToPage(int page) {
        if (page < 0) {
            page = 0;
        }
        if (page > adapter.getItemCount() - 1) {
            page = adapter.getItemCount() - 1;
        }
        mScrollRunable.scrollTo(page);
    }


    private void computeDirection(float x, float y) {
        if (y <= 0) {
            scrollDirection = UP;
        } else {
            scrollDirection = DOWN;
        }
    }

    private void computeScrollBy(int x, int y) {
        if (adapter == null) return;
        int sx = 0;
        int sy = 0;
        int mContentHeight = adapter.getItemCount() * getHeight();
        switch (scrollDirection) {
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
        mCalculator.calculate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.e(TAG, "dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.e(TAG, "onInterceptTouchEvent");
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final MotionEvent vtev = MotionEvent.obtain(event);
        mVelocityTracker.addMovement(vtev);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
//                if (getScrollY() % getHeight() == 0) {
//                    mLastScrollY = getScrollY();
//                }

                // 记录按下坐标
                mLastDownX = event.getX();
                mLastDownY = event.getY();

                // 重置滑动状态
                viewStatus = SCROLL_IDLE;

                // 停止filing滑动
                mScrollRunable.stopAny();
                mScrollPointerId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                resetTouch();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
                break;
        }
        return true;
    }

    private boolean touchMove(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        computeDirection(mLastDownX - x, mLastDownY - y);

        if (viewStatus == SCROLL_IDLE) {
            if (Math.abs(mLastDownY - y) > mTouchSlop) viewStatus = TOUCH_SCROLLING;
            mLastY = mLastDownY;
        }

        if (viewStatus == TOUCH_SCROLLING) {
            computeScrollBy(0, (int) (mLastY - y));
            mLastY = y;
        }
        return true;
    }

    private void touchUp() {
        if (viewStatus == TOUCH_SCROLLING) {
            mesureNextPage();
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

    private void notifyDataChange() {
        for (int i = 0; i < getChildCount(); i++) {
            PageHolder holder = (PageHolder) getChildAt(i).getTag(R.id.holder);
            holder.isBind = false;
        }
        mCalculator.calculate();
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
                    computeScrollBy(0, this.end - currentScroll);
                } else {
                    int b = (this.end - currentScroll) / perSize / 4;
                    b = Math.abs(b) > 0 ? b : this.end - currentScroll > 0 ? 1 : -1;
                    computeScrollBy(0, b * perSize);
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
            return getScrollY();
        }

        void scrollToEnd(int end) {
            perSize = getHeight() / 60;
            this.end = end;
            removeCallbacks(this);
            ViewCompat.postOnAnimation(HMListPagerView.this, this);
        }

        public void scrollTo(int position) {
            viewStatus = AUTO_SCROLLING;
            Log.i(TAG, "-- scrollTo --" + position);
            scrollToEnd(position * getHeight());
        }


        private void postOnAnimation() {
            removeCallbacks(this);
            ViewCompat.postOnAnimation(HMListPagerView.this, this);
        }

        public void stopAny() {
            removeCallbacks(this);
        }
    }


    public static abstract class PageAdapter extends RecyclerView.Adapter<PageHolder> {
        private HMListPagerView hmListPagerView;

        public void setListPagerView(HMListPagerView hmListPagerView) {
            this.hmListPagerView = hmListPagerView;
        }

        public void notifyDataChange() {
            this.hmListPagerView.post(new Runnable() {
                @Override
                public void run() {
                    hmListPagerView.notifyDataChange();
                }
            });
        }
    }

    public static abstract class PageHolder extends RecyclerView.ViewHolder {

        private int position;
        boolean isBind = false;

        public PageHolder(View itemView) {
            super(itemView);
        }

        public int getAdapterPos() {
            return position;
        }

        public void setAdapterPos(int position) {
            this.position = position;
        }
    }

    private final class ChildCalculator {

        public void calculate() {
            int pageHeight = getHeight();
            if (pageHeight == 0) return;
            if (adapter == null) return;
            if (adapter.getItemCount() == 0) return;

            int firstPos = getFirstPosition();
            int currentPosition = getScrollY() / getHeight();
            int itemCount = adapter.getItemCount();
            int i = 0;
            while (i < 4) {
                if (getChildCount() > 4 || getChildCount() > itemCount) {
                    View child = getChildAt(getChildCount() - 1);
                    PageHolder holder = (PageHolder) child.getTag(R.id.holder);
                    removeItem(scrollDirection == UP ? holder.getAdapterPos() : 0);
                    continue;
                }

                int position = firstPos + i;

                if (position > itemCount - 1) break;

                // 最多预加载两个
                if (position > currentPosition + 2) break;

                View child = getChildAt(i);
                if (child != null) {
                    PageHolder holder = (PageHolder) child.getTag(R.id.holder);
                    if (holder.getAdapterPos() < position) {
                        removeItem(holder.getAdapterPos());
                    } else if (holder.getAdapterPos() > position) {
                        addItem(position, i);
                        i++;
                    } else {
                        if (!holder.isBind) {
                            holder.isBind = true;
                            adapter.onBindViewHolder(holder, position);
                        }
                        i++;
                    }
                } else {
                    addItem(position, i);
                    i++;
                }
            }


        }

        private void calculateByUp() {
            int firstPos = getFirstPosition();
            int i = 0;
            int itemCount = adapter.getItemCount();
            int b = getScrollY() / getHeight();
            while (i < 4) {
                if (getChildCount() > 4 || getChildCount() > itemCount) {
                    View child = getChildAt(getChildCount() - 1);
                    PageHolder holder = (PageHolder) child.getTag(R.id.holder);
                    removeItem(holder.getAdapterPos());
                    continue;
                }

                int position = firstPos + i;
                if (position > itemCount - 1) break;
                if (position > b + 2) break;
                View child = getChildAt(i);
                if (child != null) {
                    PageHolder holder = (PageHolder) child.getTag(R.id.holder);
                    if (holder.getAdapterPos() < position) {
                        removeItem(holder.getAdapterPos());
                    } else if (holder.getAdapterPos() > position) {
                        addItem(position, i);
                        i++;
                    } else {
                        if (!holder.isBind) {
                            holder.isBind = true;
                            adapter.onBindViewHolder(holder, position);
                        }
                        i++;
                    }
                } else {
                    addItem(position, i);
                    i++;
                }
            }
        }

        private void calculateByDown() {
            int lastPos = getLastPosition();
            int i = lastPos > 2 ? 3 : lastPos;
            int itemCount = adapter.getItemCount();
            while (i >= 0) {
                if (getChildCount() > 4 || getChildCount() > itemCount) {
                    View child = getChildAt(0);
                    PageHolder holder = (PageHolder) child.getTag(R.id.holder);
                    removeItem(holder.getAdapterPos());
                    continue;
                }
                int position = lastPos;
                if (position < 0) break;
                View child = getChildAt(i);
                if (child != null) {
                    PageHolder holder = (PageHolder) child.getTag(R.id.holder);
                    if (holder.getAdapterPos() < position) {
                        addItem(position, getChildCount());
                        i--;
                        lastPos--;
                    } else if (holder.getAdapterPos() > position) {
                        removeItem(holder.getAdapterPos());
                    } else {
                        if (!holder.isBind) {
                            holder.isBind = true;
                            adapter.onBindViewHolder(holder, position);
                        }
                        i--;
                        lastPos--;
                    }
                } else {
                    addItem(position, getChildCount());
                    i--;
                    lastPos--;
                }
            }
        }

        private int getFirstPosition() {
            int position = 0;
            int currentScrollY = getScrollY();
            int b = currentScrollY / getHeight();
            position = b - 1 < 0 ? 0 : b - 1;
            Log.i(TAG, "-- getFirstPosition --" + position);
            return position;
        }

        private int getLastPosition() {
            int position = 0;
            int currentScrollY = getScrollY();
            int b = currentScrollY / getHeight();
            position = b + 2 > adapter.getItemCount() - 1 ? adapter.getItemCount() - 1 : b + 2;
            Log.i(TAG, "-- getLastPosition --" + position);
            return position;
        }

        private void addItem(int pos, int index) {
            Log.i(TAG, "-- addItem --" + pos);
            int p = pos % 5;
            PageHolder holder = null;
            if (p > mHolders.size() - 1) {
                holder = adapter.createViewHolder(HMListPagerView.this, 0);
                holder.itemView.setTag(R.id.holder, holder);
                mHolders.add(holder);
            } else {
                holder = mHolders.get(p);
            }
            holder.setAdapterPos(pos);
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(getWidth(), getHeight());
            }
            params.width = getWidth();
            params.height = getHeight();
            holder.itemView.setLayoutParams(params);
            addView(holder.itemView, index);
        }

        private void removeItem(int pos) {
            Log.i(TAG, "-- removeItem --" + pos);
            int p = pos % 5;
            PageHolder holder = mHolders.get(p);
            holder.isBind = false;
            removeView(holder.itemView);
        }
    }

}
