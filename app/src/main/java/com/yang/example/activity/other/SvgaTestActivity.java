package com.yang.example.activity.other;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.opensource.svgaplayer.utils.log.SVGALogger;
import com.yang.base.activity.SimpleBarActivity;
import com.yang.example.R;

import java.net.MalformedURLException;
import java.net.URL;

public class SvgaTestActivity extends SimpleBarActivity {

    private EditText url_edt;
    private SVGAImageView svgaImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svga_test);

        svgaImageView = findViewById(R.id.svga_image);
        svgaImageView.setBackgroundColor(Color.GRAY);
        url_edt = findViewById(R.id.url_edt);

        url_edt.setText("https://app.ugoshop.com/hmelive/files/testgift.svga");
        SVGALogger.INSTANCE.setLogEnabled(true);

        findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit(){
        String url = url_edt.getText().toString();
        if (TextUtils.isEmpty(url)) {
            showToast("url null");
            return;
        }
//        try { // new URL needs try catch.
            SVGAParser svgaParser = SVGAParser.Companion.shareParser();
//            svgaParser.setFrameSize(100,100);
            svgaParser.init(this);
            svgaParser.decodeFromAssets("testgift.svga", new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                    svgaImageView.setVideoItem(svgaVideoEntity);
                    svgaImageView.startAnimation();
                }

                @Override
                public void onError() {

                }
            });
//            svgaParser.decodeFromURL(new URL(url), new SVGAParser.ParseCompletion() {
//                @Override
//                public void onComplete(SVGAVideoEntity videoItem) {
//                    svgaImageView.setVideoItem(videoItem);
//                    svgaImageView.startAnimation();
//                }
//                @Override
//                public void onError() {
//
//                }
//            });
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }

//        Glide.with(SvgaTestActivity.this).load(url).into(svgaImageView);
    }
}
