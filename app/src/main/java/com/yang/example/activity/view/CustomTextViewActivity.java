package com.yang.example.activity.view;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.yang.example.R;
import com.yang.base.activity.SimpleBarActivity;
import com.yang.example.view.NoteView;

import java.util.Random;

public class CustomTextViewActivity extends SimpleBarActivity {

    private NoteView mNoteView;
    private final String[] logs = {"System: ClassLoader referenced unknown path: /system/priv-app/PrebuiltGmsCore/lib/arm64", "ApplicationLoaders: ignored Vulkan layer search path /system/priv-app/PrebuiltGmsCore/lib/arm64:/system/fake-libs64:/system/priv-app/PrebuiltGmsCore/PrebuiltGmsCore.apk!/lib/arm64-v8a:/system/lib64:/vendor/lib64 for namespace 0x77df8ff0f0", "PhoneInterfaceManager: [PhoneIntfMgr] getIccId: No UICC", "System.err: java.net.UnknownHostException: Unable to resolve host \"xlog.snssdk.com\": No address associated with hostname"};
    private Switch mSwitch;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            addTestLog();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_text_view);

        mNoteView = (NoteView) findViewById(R.id.custom_textview);
        mSwitch = (Switch) findViewById(R.id.switch1);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    postRunnable(buttonView);
                } else {
                    removeRunable(buttonView);
                }
            }
        });

    }

    boolean a = true;

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void addTestLog() {
        int logIndex = new Random().nextInt(4);
        int level = new Random().nextInt(3) + 1;
        mNoteView.addText(level, logs[logIndex]);
        if (mSwitch.isChecked()) {
            postRunnable(mSwitch);
        } else {
            mSwitch.removeCallbacks(runnable);
        }
    }

    private void postRunnable(final View view) {
        view.postDelayed(runnable, 1000);
    }

    private void removeRunable(View view) {
        view.removeCallbacks(runnable);
    }

    int i = 0;

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.custom_text_add_btn:
                addTestLog();
                break;
        }

    }
}
