package com.yang.example.activity.other;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.yang.base.activity.SimpleBarActivity;
import com.yang.example.R;

public class GlideTestActivity extends SimpleBarActivity {

    private ImageView image1;
    private ImageView image2;
    private final static String URL = "https://desk-fd.zol-img.com.cn/t_s5120x2880c5/g7/M00/08/0A/ChMkLGPgV6eIBg-SAEOOciEyxZEAAMhtwAFdj0AQ46K960.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide_test);

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
    }

    public void onButtonClick(View v) {
        Glide.with(this).load(URL).diskCacheStrategy(DiskCacheStrategy.NONE).dontAnimate().placeholder(R.drawable.image_prts_senran).error(R.drawable.image_prts_senran).into(image2);
        Glide.with(this).load(URL).placeholder(R.drawable.image_prts_senran).error(R.drawable.image_prts_senran).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                if (isFinishing()) return true;
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (isFinishing()) return true;
                return false;
            }
        }).into(new GlideDrawableImageViewTarget(image1));//1代表播放一次
    }
}
