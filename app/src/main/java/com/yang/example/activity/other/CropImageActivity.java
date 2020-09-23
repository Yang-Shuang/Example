package com.yang.example.activity.other;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.model.CropParameters;
import com.yalantis.ucrop.model.ExifInfo;
import com.yalantis.ucrop.model.ImageState;
import com.yalantis.ucrop.task.BitmapCropTask;
import com.yalantis.ucrop.util.BitmapLoadUtils;
import com.yang.example.R;
import com.yang.example.activity.SimpleBarActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CropImageActivity extends SimpleBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        copyAssert();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void copyAssert() {
        File file = new File(getFilesDir() + "/img_horizontal.jpg");
        File file1 = new File(getFilesDir() + "/img_vertical.jpeg");
        if (!file.exists()) {
            copyAssetsFile("img_horizontal.jpg", getFilesDir() + "/img_horizontal.jpg");
        }
        if (!file1.exists()) {
            copyAssetsFile("img_vertical.jpeg", getFilesDir() + "/img_vertical.jpeg");
        }
    }

    private void copyAssetsFile(String assetsPath, String outPath) {
        try {
            OutputStream myOutput = new FileOutputStream(outPath);
            InputStream myInput = getAssets().open(assetsPath);
            byte[] buffer = new byte[1024 * 8];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_crop_h:
                float hb = 16f / 9;
                String inputPath = getFilesDir() + "/img_horizontal.jpg";
                String outPath = getFilesDir() + "/crop/img_horizontal.jpg";

                autoCropImage(this, inputPath, outPath, hb, new BitmapCropCallback() {
                    @Override
                    public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                        Log.e("", "onBitmapCropped: resultUri : " + resultUri.toString());
                        Log.e("", "onBitmapCropped: imageWidth : " + imageWidth + " ,imageHeight : " + imageHeight);

                    }

                    @Override
                    public void onCropFailure(@NonNull Throwable t) {
                        Log.e(TAG, "onCropFailure: Throwable : " + t.getMessage());
                    }
                });
                break;
            case R.id.btn_crop_v:
                float vb = 3f / 4;
                String inputPath1 = getFilesDir() + "/img_vertical.jpeg";
                String outPath1 = getFilesDir() + "/crop/img_vertical.jpeg";
                autoCropImage(this, inputPath1, outPath1, vb, new BitmapCropCallback() {
                    @Override
                    public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                        Log.e("", "onBitmapCropped: resultUri : " + resultUri.toString());
                        Log.e("", "onBitmapCropped: imageWidth : " + imageWidth + " ,imageHeight : " + imageHeight);

                    }

                    @Override
                    public void onCropFailure(@NonNull Throwable t) {
                        Log.e(TAG, "onCropFailure: Throwable : " + t.getMessage());
                    }
                });
                break;
        }
    }

    private static RectF getBitmapRect(Bitmap bitmap) {
        RectF rectF = new RectF();
        rectF.top = 0;
        rectF.left = 0;
        rectF.right = bitmap.getWidth();
        rectF.bottom = bitmap.getHeight();
        return rectF;
    }

    private static RectF getCropRect(RectF bitmapRectf, float b) {
        RectF f = new RectF();
        float w = bitmapRectf.right;
        float h = bitmapRectf.bottom;
        if (w / h > b) {
            float newW = h * b;
            f.top = 0;
            f.bottom = h;
            f.left = (w - newW) / 2;
            f.right = f.left + newW;
        } else {
            float newH = w / b;
            f.left = 0;
            f.right = w;
            f.top = (h - newH) / 2;
            f.bottom = f.top + newH;
        }
        return f;
    }

    /**
     * @param inputPath 原图片地址
     * @param outPath   新图片地址
     * @param ratio     宽 / 高
     */
    public static void autoCropImage(Context context, String inputPath, String outPath, final float ratio, final BitmapCropCallback cropCallback) {
        if (inputPath == null || "".equals(inputPath)) {
            if (cropCallback != null) {
                cropCallback.onCropFailure(new IllegalArgumentException("inputPath is " + inputPath));
            }
            return;
        }
        if (outPath == null || "".equals(outPath)) {
            if (cropCallback != null) {
                cropCallback.onCropFailure(new IllegalArgumentException("outPath is " + outPath));
            }
            return;
        }
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            if (cropCallback != null) {
                cropCallback.onCropFailure(new FileNotFoundException(inputPath + "not found"));
            }
            return;
        }
        File outFile = new File(outPath);
        if (!outFile.getParentFile().exists()) {
            boolean r = outFile.getParentFile().mkdirs();
            if (!r) {
                if (cropCallback != null) {
                    cropCallback.onCropFailure(new Exception("create imageFile failed"));
                }
            }
        }
        int bitmapSize = BitmapLoadUtils.calculateMaxBitmapSize(context);
        BitmapLoadUtils.decodeBitmapInBackground(context, Uri.fromFile(inputFile), Uri.fromFile(outFile), bitmapSize, bitmapSize,
                new BitmapLoadCallback() {

                    @Override
                    public void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull ExifInfo exifInfo, @NonNull String imageInputPath, @Nullable String imageOutputPath) {
                        Log.e("", "onBitmapLoaded");
                        Log.e("", "imageInputPath : " + imageInputPath);
                        Log.e("", "imageOutputPath : " + imageOutputPath);

                        RectF currentRect = getBitmapRect(bitmap);
                        Log.e("", "Bitmap : " + currentRect.right + " - " + currentRect.bottom);
                        RectF mCropRect = getCropRect(currentRect, ratio);
                        Log.e("", "CropRectF : left:" + mCropRect.left + " - right:" + mCropRect.right + " - top:" + mCropRect.top + " - bottom:" + mCropRect.bottom);

                        final ImageState imageState = new ImageState(
                                mCropRect, currentRect,
                                1, 0);

                        final CropParameters cropParameters = new CropParameters(
                                3000, 3000,
                                Bitmap.CompressFormat.JPEG, 90,
                                imageInputPath, imageOutputPath, exifInfo);

                        new BitmapCropTask(bitmap, imageState, cropParameters, cropCallback)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                    @Override
                    public void onFailure(@NonNull Exception bitmapWorkerException) {
                        Log.e("", "onFailure: setImageUri", bitmapWorkerException);

                    }
                });

    }
}
