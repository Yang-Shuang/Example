package com.yang.example.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatImageView;


import com.yang.example.R;

import java.util.Arrays;

/**
 * /**
 *
 * @author yangshuang
 * @version V1.0
 * @date 2023-7-24
 * @Description ImageView 圆角  实现原理为在onDraw中裁剪Canvas  不适用于频繁刷新使用
 */

public class RoundedImageView extends AppCompatImageView {

    /*圆角的半径，左上 右上 右下 左下*/
    private static final float[] DEFAULT_RADIOS = {10, 10, 10, 10, 10, 10, 10, 10};
    private float[] mRadios;
    private RectF mBoundsF;
    private Path mPath = new Path();

    public RoundedImageView(Context context) {
        this(context, null);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView);
        float radius = a.getDimension(R.styleable.RoundedImageView_cardRadius, -1);
        float leftTopRadios = a.getDimension(R.styleable.RoundedImageView_leftTopRadius, -1);
        float rightTopRadios = a.getDimension(R.styleable.RoundedImageView_rightTopRadius, -1);
        float rightBottomRadios = a.getDimension(R.styleable.RoundedImageView_rightBottomRadius, -1);
        float leftBottomRadios = a.getDimension(R.styleable.RoundedImageView_leftBottomRadius, -1);
        if (radius != -1) {
            mRadios = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
        } else if (leftTopRadios != -1 || rightTopRadios != -1 || rightBottomRadios != -1 || leftBottomRadios != -1) {
            mRadios = new float[8];
            mRadios[0] = leftTopRadios == -1 ? 0 : leftTopRadios;
            mRadios[1] = leftTopRadios == -1 ? 0 : leftTopRadios;
            mRadios[2] = rightTopRadios == -1 ? 0 : rightTopRadios;
            mRadios[3] = rightTopRadios == -1 ? 0 : rightTopRadios;
            mRadios[4] = rightBottomRadios == -1 ? 0 : rightBottomRadios;
            mRadios[5] = rightBottomRadios == -1 ? 0 : rightBottomRadios;
            mRadios[6] = leftBottomRadios == -1 ? 0 : leftBottomRadios;
            mRadios[7] = leftBottomRadios == -1 ? 0 : leftBottomRadios;
        } else {
            mRadios = DEFAULT_RADIOS;
        }
        Log.w("RoundedImageView","mRadios : " + Arrays.toString(mRadios));
        a.recycle();
        mBoundsF = new RectF();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mBoundsF.right = getWidth();
        mBoundsF.bottom = getHeight();
        mPath.reset();
        mPath.addRoundRect(mBoundsF, mRadios, Path.Direction.CW);
        Log.w("RoundedImageView","onLayout : left : " + left + " right : " + right + " top : " + top + " bottom : " + bottom);
    }

    protected void onDraw(Canvas canvas) {
        canvas.clipPath(mPath);
        Log.w("RoundedImageView","onDraw mBoundsF : " + mBoundsF.left + " ," + mBoundsF.right + " ," +  mBoundsF.top + " ," +  mBoundsF.bottom);
        super.onDraw(canvas);
    }
}