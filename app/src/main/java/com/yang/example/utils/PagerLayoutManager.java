package com.yang.example.utils;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnFlingListener;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

import com.yang.base.utils.LogUtil;

public class PagerLayoutManager extends RecyclerView.LayoutManager {


    private final static int MIN_VELOCITY_FOR_NEXT = 2500;
    private int minDistanceForNext;
    private int orientation = RecyclerView.VERTICAL;
    private RecyclerView mRecyclerView;
    private int offset; // 垂直 或 水平方向偏移量
    private int startOffset;

    private int currentPosition; // 当前位置
    private int scrollState = RecyclerView.SCROLL_STATE_IDLE;

    private OnFlingListener onFlingListener = new OnFlingListener() {
        @Override
        public boolean onFling(int velocityX, int velocityY) {

            return calculateFling(velocityX, velocityY);
        }
    };

    private OnScrollListener onScrollListener = new OnScrollListener() {
        boolean mScrolled = false;

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            scrollState = newState;
            showLog("onScrollStateChanged : " + newState);
            if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                if (mScrolled) {
                    snapToTargetExistingView();
                }
                startOffset = offset;
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dx != 0 || dy != 0) {
                mScrolled = true;
            }
        }
    };

    public PagerLayoutManager(int orientation, RecyclerView mRecyclerView) {
        this.orientation = orientation;
        this.mRecyclerView = mRecyclerView;
        this.mRecyclerView.addOnScrollListener(onScrollListener);
        this.mRecyclerView.setOnFlingListener(onFlingListener);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(-1, -1);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        layoutChild(recycler);
    }

    private void showLog(String msg) {
        Log.i("PagerLayoutManager", "" + msg);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return this.scrollBy(recycler, dx, 0);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        showLog("scrollVerticallyBy : " + dy);
        return this.scrollBy(recycler, 0, dy);
    }

    @Override
    public boolean canScrollHorizontally() {
        return orientation == RecyclerView.HORIZONTAL;
    }

    @Override
    public boolean canScrollVertically() {
        return orientation == RecyclerView.VERTICAL;
    }

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        super.smoothScrollToPosition(recyclerView, state, position);
    }

    private void layoutChild(RecyclerView.Recycler recycler) {
        int size = orientation == RecyclerView.HORIZONTAL ? mRecyclerView.getWidth() : mRecyclerView.getHeight();
        if (size == 0) return;

        minDistanceForNext = (int) (size * 0.2f);
        int firstPosition = offset / size; // 计算当前屏幕第一个visible 位置
        int childCount = getChildCount();
        int itemCount = getItemCount();

        // 以firstPosition为锚点  上下/前后 延伸2 共5个item
        // parent保持4个child，除当前屏幕上下两个外   预加载优先滑动方向那一边

        int position = offset - startOffset < 0 ? firstPosition - 2 : firstPosition - 1;
        int endPosition = offset - startOffset < 0 ? firstPosition + 1 : firstPosition + 2;


        int oldPosition = -1;
        int oldEndPosition = -1;
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View view = getChildAt(i);
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
                int p = holder.getAdapterPosition();
                if (oldPosition == -1) {
                    oldPosition = p;
                } else {
                    oldPosition = Math.min(p, oldPosition);
                }
                if (oldEndPosition == -1) {
                    oldEndPosition = p;
                } else {
                    oldEndPosition = Math.max(p, oldEndPosition);
                }
            }
        }

        int startIndex = Math.min(position, oldPosition);
        int endIndex = Math.max(endPosition, oldEndPosition);
        for (int i = startIndex; i <= endIndex; i++) {
            if (i < 0 || i >= itemCount) continue;
            View view = super.findViewByPosition(i);


            if (i >= position && i <= endPosition) {
                if (view == null) {
                    addItemView(recycler, i, size);
                }
            } else if (view != null) {
                removeItemView(view, recycler);
            }
        }
    }

    private void addItemView(RecyclerView.Recycler recycler, int position, int pageSize) {
        LogUtil.w("addItemView -- " + position);
        View view = recycler.getViewForPosition(position);
        addView(view);
        measureChildWithMargins(view, 0, 0);

        int widthSpace = getDecoratedMeasuredWidth(view);
        int top = position * pageSize - offset;
        layoutDecoratedWithMargins(view, 0, top, widthSpace, top + pageSize);
    }

    private void removeItemView(View view, RecyclerView.Recycler recycler) {
        LogUtil.w("removeItemView -- " + mRecyclerView.getChildViewHolder(view).getAdapterPosition());
        removeAndRecycleView(view, recycler);
    }

    private int scrollBy(RecyclerView.Recycler recycler, int dx, int dy) {
        int width = mRecyclerView.getWidth();
        int height = mRecyclerView.getHeight();

        int rdx = 0;
        int rdy = 0;
        int dis = 0;
        if (Math.abs(dx) > 0) {
            if (Math.abs(offset + dx - startOffset) > width) {
                if (dx > 0) {
                    rdx = width + startOffset - offset;
                } else {
                    rdx = width - startOffset + offset;
                }
            } else {
                rdx = dx;
            }
            dis = rdx;
            mRecyclerView.offsetChildrenHorizontal(-rdx);
            offset += dis;
        } else if (Math.abs(dy) > 0) {
            if (Math.abs(offset + dy - startOffset) > height) {
                if (dy > 0) {
                    rdy = height + startOffset - offset;
                } else {
                    rdy = height - startOffset + offset;
                }
            } else {
                rdy = dy;
            }
            dis = rdy;
            mRecyclerView.offsetChildrenVertical(-rdy);
            offset += dis;
        }
        layoutChild(recycler);
        return dis;
    }

    private boolean calculateFling(int velocityX, int velocityY) {
        int minFlingVelocity = mRecyclerView.getMinFlingVelocity();
        if (Math.abs(velocityY) < minFlingVelocity && Math.abs(velocityX) < minFlingVelocity)
            return false;

        int size = orientation == RecyclerView.HORIZONTAL ? mRecyclerView.getWidth() : mRecyclerView.getHeight();
        int scrollDistance = offset % size;
        if (scrollDistance == 0) return false;

        int velocity = orientation == RecyclerView.HORIZONTAL ? velocityX : velocityY;
        int itemCount = getItemCount();
        int maxOffset = itemCount > 0 ? (itemCount - 1) * size : 0;
        int minOffset = 0;

        if (offset - startOffset > 0) {
            // 边界处理
            if (offset > maxOffset) {
                mRecyclerView.smoothScrollBy(0, -scrollDistance);
                return true;
            }
            if (Math.abs(velocity) < MIN_VELOCITY_FOR_NEXT) {
                mRecyclerView.smoothScrollBy(0, -scrollDistance);
            } else {
                mRecyclerView.smoothScrollBy(0, size - scrollDistance);
            }
        } else {
            // 边界处理
            if (offset < minOffset) {
                mRecyclerView.smoothScrollBy(0, -offset);
                return true;
            }
            if (Math.abs(velocity) < MIN_VELOCITY_FOR_NEXT) {
                mRecyclerView.smoothScrollBy(0, size - scrollDistance);
            } else {
                mRecyclerView.smoothScrollBy(0, -scrollDistance);
            }
        }

        return true;
    }

    private void snapToTargetExistingView() {
        showLog("snapToTargetExistingView");

        int size = orientation == RecyclerView.HORIZONTAL ? mRecyclerView.getWidth() : mRecyclerView.getHeight();
        int scrollDistance = offset % size;
        if (scrollDistance == 0) return;

        int itemCount = getItemCount();
        int maxOffset = itemCount > 0 ? (itemCount - 1) * size : 0;
        int minOffset = 0;

        if (offset - startOffset > 0) {
            // 边界处理
            if (offset > maxOffset) {
                mRecyclerView.smoothScrollBy(0, -scrollDistance);
                return;
            }
            if (scrollDistance < minDistanceForNext) {
                mRecyclerView.smoothScrollBy(0, -scrollDistance);
            } else {
                mRecyclerView.smoothScrollBy(0, size - scrollDistance);
            }
        } else {
            // 边界处理
            if (offset < minOffset) {
                mRecyclerView.smoothScrollBy(0, -offset);
                return;
            }
            if ((size - scrollDistance) < minDistanceForNext) {
                mRecyclerView.smoothScrollBy(0, size - scrollDistance);
            } else {
                mRecyclerView.smoothScrollBy(0, -scrollDistance);
            }
        }
    }
}
