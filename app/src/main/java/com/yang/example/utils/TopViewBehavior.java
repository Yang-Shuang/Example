package com.yang.example.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.yang.base.utils.LogUtil;

import java.util.Arrays;

public class TopViewBehavior extends CoordinatorLayout.Behavior<View> {
    private static final String TAG = "TopViewBehavior";
    private RecyclerView recyclerView;

    public TopViewBehavior() {
    }

    public TopViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
    }

    private boolean recyclerViewIsTop() {
        if (recyclerView == null) return false;
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        if (position == 0) {
            return layoutManager.findViewByPosition(0).getTop() == 0;
        } else {
            return false;
        }
    }

    private boolean recyclerViewIsBotom() {
        if (recyclerView == null) return false;
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager.getChildCount() <= 0) return false;
        return layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        if (child instanceof RecyclerView) {
            this.recyclerView = (RecyclerView) child;
            init();
        }
        return false;
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        LogUtil.e(TAG, "onDependentViewChanged");
        child.setY(dependency.getTop() - child.getHeight());
        child.setX(0);
        return true;
    }

    public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency) {
        LogUtil.e(TAG, "onDependentViewRemoved");
    }


    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        LogUtil.e(TAG, "onStartNestedScroll");
//        if (child instanceof RecyclerView){
//            RecyclerView recyclerView = (RecyclerView) child;
//            LogUtil.e(TAG,"RecyclerView.getScrollY : " + recyclerView.getScrollY());
//            recyclerView.getScrollY();
//        }
        return child == recyclerView;
//        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }


    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        LogUtil.e(TAG, "onNestedScroll");
//        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        LogUtil.e(TAG, "onNestedPreScroll (" + dx + "," + dy + ")    " + Arrays.toString(consumed));
        if (child != recyclerView) return;
        if (coordinatorLayout.getScrollY() != 0) {
            consumed[1] = dy;
            coordinatorLayout.scrollBy(0, dy);
            return;
        }
        if (dy > 0) {
            if (coordinatorLayout.getScrollY() == 0 && recyclerViewIsBotom()) {
                consumed[1] = dy;
                coordinatorLayout.scrollBy(0, dy);
            }
        } else if (dy < 0) {
            if (recyclerViewIsTop()) {
                consumed[1] = dy;
                if (coordinatorLayout.getScrollY() > 0) {
                    int y = coordinatorLayout.getScrollY();
                    coordinatorLayout.scrollBy(0, Math.abs(dy) > y ? -y : dy);
                }
            }
        }

//        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int type) {
        LogUtil.e(TAG, "onStopNestedScroll");
//        super.onStopNestedScroll(coordinatorLayout, child, target, type);
    }


    @Override
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        LogUtil.e(TAG, "onNestedScrollAccepted");
//        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, type);

    }

    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, View child, View target,
                                 float velocityX, float velocityY, boolean consumed) {
        LogUtil.e(TAG, "onNestedFling");
        return false;
    }

    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target,
                                    float velocityX, float velocityY) {
        LogUtil.e(TAG, "onNestedPreFling");
        return false;
    }
}
