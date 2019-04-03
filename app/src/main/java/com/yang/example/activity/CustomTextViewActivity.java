package com.yang.example.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.yang.example.R;
import com.yang.example.utils.LogUtil;
import com.yang.example.view.NoteView;

public class CustomTextViewActivity extends SimpleBarActivity {

    private NoteView mNoteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_text_view);

        mNoteView = findViewById(R.id.custom_textview);


    }

    boolean a = true;

    @Override
    protected void onStart() {
        super.onStart();
        mNoteView.addText("哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈");
    }

    int i = 0;

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (a) {
            mNoteView.addText(NoteView.LIGHT_LEVEL, i + "哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈");
        } else {
            mNoteView.addText(i + "哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈");
        }
        a = !a;
        i++;
    }
}
