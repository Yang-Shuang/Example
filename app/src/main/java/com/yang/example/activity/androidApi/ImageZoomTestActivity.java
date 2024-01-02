package com.yang.example.activity.androidApi;

import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.yang.base.activity.SimpleBarActivity;
import com.yang.example.R;


public class ImageZoomTestActivity extends SimpleBarActivity {

    private ImageView image_zoom;
    private PhotoView photoView;
    private float currentScale = 1;
    private float[] values = new float[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom_test);

        photoView = findViewById(R.id.photoView);
        photoView.enable();
        image_zoom = findViewById(R.id.image_zoom);

        values[0] = 1;
        values[1] = 0;
        values[2] = 0;

        values[3] = 0;
        values[4] = 1;
        values[5] = 0;

        values[6] = 0;
        values[7] = 0;
        values[8] = 1;

        image_zoom.setImageMatrix(matrix);
    }

    Matrix matrix = new Matrix();

    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.scale_to_big:
                values[0] += 0.2;
                values[4] += 0.2;
//                values[8] += 0.2;
                break;
            case R.id.scale_to_small:
                values[0] -= 0.2;
                values[4] -= 0.2;
//                values[8] -= 0.2;
                break;
            case R.id.translate_left:
                values[2] += 100;
                break;
            case R.id.translate_right:
                values[2] -= 100;

                break;
        }
//        showToast("scale : " + scale + " translate : " + translateOffset);
        matrix.setValues(values);
        image_zoom.setImageMatrix(matrix);
    }
}