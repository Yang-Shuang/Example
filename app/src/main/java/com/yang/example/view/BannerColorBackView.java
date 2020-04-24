package com.yang.example.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class BannerColorBackView extends View {

    private int color = -1;
    private Paint colorPaint;
    private RectF mRectF = new RectF();
    private RectF mOvalRectF = new RectF();

    public BannerColorBackView(Context context) {
        super(context);
    }

    public BannerColorBackView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public BannerColorBackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        colorPaint = new Paint();
        colorPaint.setStyle(Paint.Style.FILL);
        colorPaint.setAntiAlias(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mRectF.top = 0;
        mRectF.right = 0;
        mRectF.left = getWidth();
        mRectF.bottom = getHeight() - (getWidth() / 750f * 50);// 底部留白

        float ovalWidth = getWidth() / 750f * 1250; //椭圆宽
        float ovalHeight = getWidth() / 750f * 420; //椭圆高

        mOvalRectF.left = (getWidth() - ovalWidth) / 2;
        mOvalRectF.right = mOvalRectF.left + ovalWidth;

        mOvalRectF.top = getHeight() - ovalHeight - (getWidth() / 750f * 50);
        mOvalRectF.bottom = mOvalRectF.top + ovalHeight;

    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
        color = -1;
    }

    public void setColor(int color) {
        setBackground(null);
        this.color = color;
        colorPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getBackground() != null) return;

//        canvas.drawRect(mRectF, colorPaint);
        canvas.drawOval(mOvalRectF, colorPaint);
    }
}
