package com.yang.example.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.yang.base.utils.LogUtil;
import com.yang.base.utils.ScreenUtil;

public class ImageUtils {

    public static void loadBannerBackgroud(Context context, String url, onLoadBitmapListener listener) {

    }

    public static void loadBannerBackgroud(Context context, int id, int h2, onLoadBitmapListener listener) {
        LogUtil.e("loadBannerBackgroud : " + id);
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id, bfoOptions);

        int viewWidth = ScreenUtil.SCREEN_WIDTH;

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        if (bitmapHeight / 1.0f / bitmapWidth < h2 / 1.0f / viewWidth) {
            int width = (int) (bitmapHeight * viewWidth / 1f / h2);
            int x = (bitmapWidth - width) / 2;
            Bitmap title = Bitmap.createBitmap(bitmap, x, 0, width, bitmapHeight, null, false);
            bitmap.recycle();
            if (listener != null) {
                listener.onLoadComplete(title);
            }
        } else {
            int height = (int) (h2 / 1f / viewWidth * bitmapWidth);
            int y = bitmapHeight - height;
            Bitmap title = Bitmap.createBitmap(bitmap, 0, y, bitmapWidth, height, null, false);
            bitmap.recycle();
            if (listener != null) {
                listener.onLoadComplete(title);
            }
        }
    }

    public static void loadTitleBackground(String url, onLoadBitmapListener listener) {

    }

    public static Bitmap loadTitleBackground(Context context, int id, int titleHeight, int h2) {
        LogUtil.e("loadTitleBackground : " + id);
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id, bfoOptions);
        return crop(bitmap, titleHeight, h2);
    }

    public static Bitmap crop(Bitmap bitmap, int viewHeight, int h2) {
        int viewWidth = ScreenUtil.SCREEN_WIDTH;

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        if (bitmapHeight / 1.0f / bitmapWidth < h2 / 1.0f / viewWidth) {
            int width = (int) (bitmapHeight * viewWidth / 1f / h2);
            int x = (bitmapWidth - width) / 2;
            int height = (int) (viewHeight / 1f / viewWidth * width);
            Bitmap title = Bitmap.createBitmap(bitmap, x, 0, width, height, null, false);
            return title;
        } else {
            int y = bitmapHeight - (int) (h2 / 1f / viewWidth * bitmapWidth);
            int height = (int) (viewHeight / 1f / viewWidth * bitmapWidth);
            Bitmap title = Bitmap.createBitmap(bitmap, 0, y, bitmapWidth, height, null, false);
            return title;
        }
    }

    public static interface onLoadBitmapListener {
        void onLoadComplete(Bitmap drawable);
    }
}
