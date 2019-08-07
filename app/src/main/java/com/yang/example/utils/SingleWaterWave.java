package com.yang.example.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;

public class SingleWaterWave {


    static {
        System.loadLibrary("native-lib");
    }

    protected Bitmap bitmap;
    protected int width, height;
    float x0, y0;
    float len = 10;//波作用的幅度,波长,或叫波宽
    float r = 20;//波扩散距离,即平均半径
    float weight = 6;//波幅,即水波的最大高度

    //初始值
    float len0 = 10;//波作用的幅度,波长
    float r0 = 20;//波扩散距离
    float weight0 = 6;//波幅
    int[] b1,b2;

    OnFrameCreateListener listener;
    public void setOnFrameCreateListener(OnFrameCreateListener listener){
        this.listener = listener;
    }

    public SingleWaterWave(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        b1 = new int[width * height];
        b2 = new int[width * height];
        bitmap.getPixels(b1, 0, width, 0, 0, width, height);
        this.bitmap = bitmap;
        int min = width;
        len0 = min * 0.02f;
        r0 = min * 0.05f;
        weight0 = min * 0.015f;
    }

    //波形渲染
    void rippleRender() {
        int offset;
        int i = 0;
        int length = width * height;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++, i++) {
                //像素点到波心的距离
                double disance = Math.sqrt((x0 - x) * (x0 - x) + (y0 - y) * (y0 - y));

                if (disance < r - len || disance > r + len) {//不在水波影响范围
                    b2[i] = 0x00ffffff;//透明像素
                    continue;
                }
                double d = (disance - r) / len;//这个值在区间内由-1变到0再到1

                //在[-1,1]区间,形成水波曲线函数,
                d = Math.cos(d * Math.PI / 2) * -weight;//向外的像素偏移值;

                int dx = (int) (d * (x - x0) / r);//x方向的偏移值
                int dy = (int) (d * (y - y0) / r);//y方向的偏移值

                // 计算出偏移象素和原始象素的内存地址偏移量 :
                offset = dy * width + dx;
                // 判断坐标是否在范围内
                if (i + offset > 0 && i + offset < length) {
                    b2[i] = b1[i + offset];
                } else {
                    b2[i] = 0x00ffffff;////透明像素
                }
            }
        }
    }

    //波形传播  扩散的方法仅仅是半径增加,,波幅减弱,,波长稍加
    void rippleSpread() {
        //weight -= dw;
        //if (weight <= 0) {
        //    end();
        //    return;
        //}
        //len += dlen;
    }

    ValueAnimator animator;
    //丢一颗石头,即形成一圈水波
    public void dropStone(float x, float y) {
        x0 = x;
        y0 = y;
        //初始化状态
        len = len0;
        r = r0;
        weight = weight0;
        final float maxR = Math.max(width, height);
        //如果有上次的动画,取消上次的动画.
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (Float) animation.getAnimatedValue();
                r = (maxR - r0) * v + r0;
                weight = weight0 - weight0 * v;
                len = len0 * v + len0;
                //调用C++获取b2数组
                JNIRender.render(b1, b2, width, height, r, len, weight, x0, y0);
                //Animator动画计算出来的帧,转成图片,回调给调用者控件,这样控件可以实时地显示出来
                if (listener != null) {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                    bitmap.setPixels(b2, 0, width, 0, 0, width, height);
                    listener.onFrame(bitmap);
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    listener.onEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
//        animator.addListener(new SimpleAnimListener() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                if (listener != null) {
//                    listener.onEnd();
//                }
//            }
//        });
        animator.setDuration(2000);
        animator.start();
    }

    public interface OnFrameCreateListener {

        public void onFrame(Bitmap bitmap);

        public void onEnd();
    }
}
