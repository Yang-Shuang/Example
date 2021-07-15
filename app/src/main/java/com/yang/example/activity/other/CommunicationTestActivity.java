package com.yang.example.activity.other;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yang.base.activity.SimpleBarActivity;
import com.yang.base.utils.LogUtil;
import com.yang.example.R;

public class CommunicationTestActivity extends SimpleBarActivity {

    private EditText text;
    private Button file;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication_test);

        text = findViewById(R.id.text);
        file = findViewById(R.id.file);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.submit:
                String str = text.getText().toString();
                LogUtil.i(str);
                break;
        }
    }
}
