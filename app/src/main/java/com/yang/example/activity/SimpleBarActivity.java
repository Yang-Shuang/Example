package com.yang.example.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.Trace;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yang.example.R;
import com.yang.example.utils.LogUtil;
import com.yang.example.utils.StatusBarUtil;

public abstract class SimpleBarActivity extends BaseActivity implements View.OnClickListener {

    private ImageView left, right;
    private TextView title;
    private View line;
    private ConstraintLayout titleBar;
    private boolean leftButtonVisible = true, rightButtonVisible = true, showLine = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void setContentView(View view) {
        ViewGroup parent = createParent();
        parent.addView(view);
        super.setContentView(parent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initTitleBar();
        if (titleBar.getBackground() instanceof ColorDrawable) {
            ColorDrawable drawable = (ColorDrawable) titleBar.getBackground();
            StatusBarUtil.setStatusBarColor(this, drawable.getColor());
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup parent = createParent();
        parent.addView(LayoutInflater.from(this).inflate(layoutResID, null));
        super.setContentView(parent);
    }

    private ViewGroup createParent() {
        LayoutInflater inflater = LayoutInflater.from(this);
        return (ViewGroup) inflater.inflate(R.layout.activity_simple_bar, null);
    }

    protected String getTitleText() {
        String title = "未命名";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_ACTIVITIES);
            for (ActivityInfo info : packageInfo.activities) {
                if (info.nonLocalizedLabel == null) continue;
                if (info.name.equals(getClass().getName())) {
                    title = info.nonLocalizedLabel.toString();
                    break;
                }
            }
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
        return title;
    }

    protected void onLeftButtonClick() {
        onBackPressed();
    }

    protected void onRightButtonClick() {

    }

    protected void showLeftImage(boolean show) {
        leftButtonVisible = show;
        if (left == null) return;
        left.setVisibility(leftButtonVisible ? View.VISIBLE : View.INVISIBLE);
    }

    protected void showRightImage(boolean show) {
        rightButtonVisible = show;
        if (right == null) return;
        right.setVisibility(rightButtonVisible ? View.VISIBLE : View.INVISIBLE);
    }

    protected void showTitleLine(boolean show) {
        showLine = show;
        if (line == null) return;
        line.setVisibility(showLine ? View.VISIBLE : View.INVISIBLE);
    }

    protected void setLeftImage(int resId) {
        left.setImageResource(resId);
    }

    protected void setRightImage(int resId) {
        right.setImageResource(resId);
    }


    private void initTitleBar() {
        titleBar = findViewById(R.id.titlebar_layout);
        left = findViewById(R.id.titlebar_left_tv);
        right = findViewById(R.id.titlebar_right_tv);
        title = findViewById(R.id.titlebar_title_tv);
        line = findViewById(R.id.titlebar_line);
        title.setText(getTitleText() == null ? "新页面" : getTitleText());
        left.setVisibility(leftButtonVisible ? View.VISIBLE : View.INVISIBLE);
        right.setVisibility(rightButtonVisible ? View.VISIBLE : View.INVISIBLE);
        line.setVisibility(showLine ? View.VISIBLE : View.INVISIBLE);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_tv:
                onLeftButtonClick();
                break;
            case R.id.titlebar_right_tv:
                onRightButtonClick();
                break;
        }
    }
}
