package com.yang.example.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ThreeColorProgressBar extends View {


    private Paint redPaint, greenPaint, bluePaint;


    public ThreeColorProgressBar(Context context) {
        super(context);
    }

    public ThreeColorProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ThreeColorProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
