package com.yang.example.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.yang.example.utils.SingleWaterWave;


/**
 * Created by user on 2016/12/2.
 */
public class WaterFrameLayout extends FrameLayout implements SingleWaterWave.OnFrameCreateListener {
    private Rect rect;

    public WaterFrameLayout(Context context) {
        super(context);
        init();
    }

    public WaterFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (rect == null) {
            rect = new Rect();
        }
        rect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (waveBitmap != null)
            canvas.drawBitmap(waveBitmap, null, rect, null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if ((e.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            float ex = e.getX();
            float ey = e.getY();

            wave(ex, ey);
        }
        super.dispatchTouchEvent(e);
        return true;
    }

    public void wave(float ex, float ey) {
        //取控件绘制的缓存图像
        buildDrawingCache();
        Bitmap bitmap = getDrawingCache();
        Matrix matrix = new Matrix();
        float scale = 360f / getWidth();
        if (scale > 1) {
            scale = 1;
        }
        matrix.setScale(scale, scale);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, rect.right, rect.bottom, matrix, false);
        //销毁控件绘制的缓存图像
        destroyDrawingCache();
        SingleWaterWave waterWave = new SingleWaterWave(bitmap);
        waterWave.setOnFrameCreateListener(this);

        int x = (int) (ex * bitmap.getWidth() / getWidth());
        int y = (int) (ey * bitmap.getHeight() / getHeight());
        waterWave.dropStone(x, y);
    }

    private Bitmap waveBitmap;

    @Override
    public void onFrame(Bitmap bitmap) {
        waveBitmap = bitmap;
        invalidate();
    }

    @Override
    public void onEnd() {
        waveBitmap = null;
        invalidate();
    }
}