package com.yang.example.activity.androidApi;


import android.content.res.Configuration;
import android.os.Bundle;

import com.yang.base.activity.SimpleBarActivity;
import com.yang.example.R;
import com.yang.example.view.NoteView;

public class FontSizeTestActivity extends SimpleBarActivity {

    private NoteView noteview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_size_test);

        noteview = findViewById(R.id.noteview);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Configuration configuration = getResources().getConfiguration();
        noteview.addText("config.fontScale : " + configuration.fontScale);
        noteview.addText("config.densityDpi : " + configuration.densityDpi);
        noteview.addText("config.screenWidthDp : " + configuration.screenWidthDp);

        noteview.addText("mMetrics.xdpi : " + getResources().getDisplayMetrics().xdpi);
        noteview.addText("mMetrics.density : " + getResources().getDisplayMetrics().density);
        noteview.addText("mMetrics.densityDpi : " + getResources().getDisplayMetrics().densityDpi);
        noteview.addText("mMetrics.scaledDensity : " + getResources().getDisplayMetrics().scaledDensity);
    }
}