package com.yang.example.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.yang.example.R;

public class MiddleViewBehavior extends CoordinatorLayout.Behavior<View> {

    private static final String TAG = "MiddleViewBehavior";

    public MiddleViewBehavior() {
    }

    public MiddleViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency.getId() == R.id.behavior_top_view;
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        LogUtil.e(TAG, "onDependentViewChanged");
        child.setY(dependency.getHeight() + dependency.getY());
        child.setX(0);
        return true;
    }

    public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency) {
        LogUtil.e(TAG, "onDependentViewRemoved");
    }


    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        LogUtil.e(TAG, "onStartNestedScroll");
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        LogUtil.e(TAG, "onNestedScrollAccepted");
//        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, type);

    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int type) {
        LogUtil.e(TAG, "onStopNestedScroll");
//        super.onStopNestedScroll(coordinatorLayout, child, target, type);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        LogUtil.e(TAG, "onNestedScroll");
//        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        LogUtil.e(TAG, "onNestedPreScroll");
//        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
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
