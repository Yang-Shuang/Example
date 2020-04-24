package com.yang.example.view;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.scwang.smartrefresh.layout.util.SmartUtil;

public class HMBallPulseHeader extends InternalAbstract implements RefreshHeader {


    protected boolean mManualNormalColor;
    protected boolean mManualAnimationColor;

    protected Paint mPaint;

    protected int mNormalColor = 0xffeeeeee;
    protected int mAnimatingColor = 0xffe75946;

    protected float mCircleSpacing;

    protected long mStartTime = 0;
    protected boolean mIsStarted = false;
    protected TimeInterpolator mInterpolator = new AccelerateDecelerateInterpolator();

    public HMBallPulseHeader(Context context) {
        this(context, null);
    }

    protected HMBallPulseHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        final View thisView = this;
        thisView.setMinimumHeight(SmartUtil.dp2px(60));
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mSpinnerStyle = SpinnerStyle.Translate;
        mCircleSpacing = SmartUtil.dp2px(4);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsStarted = false;
        mStartTime = 0;
        mPaint.setColor(mNormalColor);
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull final RefreshLayout refreshLayout, int height, int maxDragHeight) {
        if (mIsStarted) return;

        final View thisView = this;
        thisView.invalidate();
        mIsStarted = true;
        mStartTime = System.currentTimeMillis();
        mPaint.setColor(mAnimatingColor);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        mIsStarted = false;
        mStartTime = 0;
        mPaint.setColor(mNormalColor);
        return 0;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {

    }



    @Override
    protected void dispatchDraw(Canvas canvas) {
        final View thisView = this;
        final int width = thisView.getWidth();
        final int height = thisView.getHeight();
        float radius = (Math.min(width, height) - mCircleSpacing * 2) / 6;
        float x = width / 2f - (radius * 2 + mCircleSpacing);
        float y = height / 2f;

        final long now = System.currentTimeMillis();

        for (int i = 0; i < 3; i++) {

            long time = now - mStartTime - 120 * (i + 1);
            float percent = time > 0 ? ((time%750)/750f) : 0;
            percent = mInterpolator.getInterpolation(percent);

            canvas.save();

            float translateX = x + (radius * 2) * i + mCircleSpacing * i;
            canvas.translate(translateX, y);

            if (percent < 0.5) {
                float scale = 1 - percent * 2 * 0.7f;
                canvas.scale(scale, scale);
            } else {
                float scale = percent * 2 * 0.7f - 0.4f;
                canvas.scale(scale, scale);
            }

            canvas.drawCircle(0, 0, radius, mPaint);
            canvas.restore();
        }

        super.dispatchDraw(canvas);

        if (mIsStarted) {
            thisView.invalidate();
        }
    }
}
