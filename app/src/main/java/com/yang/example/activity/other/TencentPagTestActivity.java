package com.yang.example.activity.other;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.opensource.svgaplayer.utils.log.SVGALogger;
import com.yang.base.activity.SimpleBarActivity;
import com.yang.example.R;

import org.libpag.PAGComposition;
import org.libpag.PAGFile;
import org.libpag.PAGView;

public class TencentPagTestActivity extends SimpleBarActivity {

    private PAGView pag_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tencent_pag_test);
        pag_view = findViewById(R.id.pag_view);

        findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }

    private void submit(){
        PAGFile pagFile1 = PAGFile.Load(getAssets(), "rocket.pag");
        pag_view.setComposition(pagFile1);
        pag_view.setRepeatCount(0);
        pag_view.play();
    }
}
