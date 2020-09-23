package com.yang.example.activity.view;

import android.os.Bundle;

import com.yang.example.R;
import com.yang.example.activity.SimpleBarActivity;
import com.yang.example.view.NumberPassWordEditView;
import com.yang.example.view.NumberPassWordInputView;

public class SetPassWordActivity extends SimpleBarActivity {

    private NumberPassWordEditView editView;
    private NumberPassWordInputView inputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pass_word);

        editView = findViewById(R.id.number_editview);
        inputView = findViewById(R.id.number_inputview);

        editView.setInputView(inputView);

        editView.setOnEditListener(new NumberPassWordEditView.OnEditListener() {
            @Override
            public void onInputComplete(String pwd) {
                showToast(pwd);
            }
        });
    }

    @Override
    protected void onRightButtonClick() {
        super.onRightButtonClick();
        editView.clear();
    }
}
