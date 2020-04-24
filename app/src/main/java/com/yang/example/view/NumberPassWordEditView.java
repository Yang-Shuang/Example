package com.yang.example.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yang.example.R;

public class NumberPassWordEditView extends LinearLayout {

    private String[] mPwd = new String[6];
    private NumberPassWordInputView inputView;
    private TextView[] textViews = new TextView[6];
    private int index = 0;
    private OnEditListener mOnEditListener;

    public void clear() {
        index = 0;
        for (int i = 0; i < mPwd.length; i++) {
            mPwd[i] = null;
        }
        for (int i = 0; i < textViews.length; i++) {
            textViews[i].setText("");
        }
    }

    public void setOnEditListener(OnEditListener mOnEditListener) {
        this.mOnEditListener = mOnEditListener;
    }

    public void setInputView(final NumberPassWordInputView inputView) {
        this.inputView = inputView;
        this.inputView.setOnInputWordListener(new NumberPassWordInputView.OnInputWordListener() {
            @Override
            public void onInput(String word) {
                if (index < 6 && word != null && !"".equals(word)) {
                    textViews[index].setText(word);
                    mPwd[index] = word;
                    if (index == mPwd.length - 1 && mOnEditListener != null) {
                        StringBuilder buffer = new StringBuilder();
                        for (String s : mPwd) {
                            buffer.append(s);
                        }
                        mOnEditListener.onInputComplete(buffer.toString());
                    }

                    index++;
                }
            }

            @Override
            public void onDelete() {
                if (index != 0) {
                    index--;
                    mPwd[index] = null;
                    textViews[index].setText("");
                }
            }
        });
    }

    public NumberPassWordEditView(Context context) {
        super(context);
    }

    public NumberPassWordEditView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPassWordEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(context, attrs, defStyleAttr);
    }


    private void initAttribute(Context context, AttributeSet attrs, int defStyleAttr) {
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.view_number_pass_word_edit_view, this);

        textViews[0] = findViewById(R.id.number_tv1);
        textViews[1] = findViewById(R.id.number_tv2);
        textViews[2] = findViewById(R.id.number_tv3);
        textViews[3] = findViewById(R.id.number_tv4);
        textViews[4] = findViewById(R.id.number_tv5);
        textViews[5] = findViewById(R.id.number_tv6);
    }

    public static interface OnEditListener {
        void onInputComplete(String pwd);
    }
}
