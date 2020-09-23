package com.yang.example.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.yang.example.R;

public class NumberPassWordInputView extends LinearLayout implements View.OnClickListener {

    private OnInputWordListener mOnInputWordListener;

    public void setOnInputWordListener(OnInputWordListener mOnInputWordListener) {
        this.mOnInputWordListener = mOnInputWordListener;
    }

    public NumberPassWordInputView(@NonNull Context context) {
        super(context);
    }

    public NumberPassWordInputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPassWordInputView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.view_number_pass_word_input_view, this);

        findViewById(R.id.input_tv0).setOnClickListener(this);
        findViewById(R.id.input_tv1).setOnClickListener(this);
        findViewById(R.id.input_tv2).setOnClickListener(this);
        findViewById(R.id.input_tv3).setOnClickListener(this);
        findViewById(R.id.input_tv4).setOnClickListener(this);
        findViewById(R.id.input_tv5).setOnClickListener(this);
        findViewById(R.id.input_tv6).setOnClickListener(this);
        findViewById(R.id.input_tv7).setOnClickListener(this);
        findViewById(R.id.input_tv8).setOnClickListener(this);
        findViewById(R.id.input_tv9).setOnClickListener(this);
        findViewById(R.id.input_delete_tv).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (mOnInputWordListener == null) return;
        switch (v.getId()){
            case R.id.input_tv0:
                mOnInputWordListener.onInput("0");
                break;
            case R.id.input_tv1:
                mOnInputWordListener.onInput("1");
                break;
            case R.id.input_tv2:
                mOnInputWordListener.onInput("2");
                break;
            case R.id.input_tv3:
                mOnInputWordListener.onInput("3");
                break;
            case R.id.input_tv4:
                mOnInputWordListener.onInput("4");
                break;
            case R.id.input_tv5:
                mOnInputWordListener.onInput("5");
                break;
            case R.id.input_tv6:
                mOnInputWordListener.onInput("6");
                break;
            case R.id.input_tv7:
                mOnInputWordListener.onInput("7");
                break;
            case R.id.input_tv8:
                mOnInputWordListener.onInput("8");
                break;
            case R.id.input_tv9:
                mOnInputWordListener.onInput("9");
                break;
            case R.id.input_delete_tv:
                mOnInputWordListener.onDelete();
                break;
        }
    }


    public static interface OnInputWordListener {
        void onInput(String word);

        void onDelete();
    }
}
