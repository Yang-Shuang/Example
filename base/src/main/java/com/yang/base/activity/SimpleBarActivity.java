package com.yang.base.activity;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yang.base.R;
import com.yang.base.utils.LogUtil;
import com.yang.base.utils.ExStatusBarUtil;

public abstract class SimpleBarActivity extends BaseActivity implements View.OnClickListener {

    protected String TAG = this.getClass().getSimpleName();

    private ImageView left, right;
    private TextView title;
    private View line;
    private ConstraintLayout titleBar;
    private boolean leftButtonVisible = true, rightButtonVisible = true, showLine = true;

    private int rightResID = -1;
    private int LeftResID = -1;
    private String titleText = null;

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
        parent.addView(view, new LinearLayout.LayoutParams(-1, -1));
        super.setContentView(parent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initTitleBar();
        if (titleBar.getBackground() instanceof ColorDrawable) {
            ColorDrawable drawable = (ColorDrawable) titleBar.getBackground();
            ExStatusBarUtil.setStatusBarColor(this, drawable.getColor());
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup parent = createParent();
        parent.addView(LayoutInflater.from(this).inflate(layoutResID, null), new LinearLayout.LayoutParams(-1, -1));
        super.setContentView(parent);
    }

    private ViewGroup createParent() {
        LayoutInflater inflater = LayoutInflater.from(this);
        return (ViewGroup) inflater.inflate(R.layout.activity_simple_bar, null);
    }

    protected String getTitleText() {
        if (titleText == null) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(
                        getPackageName(), PackageManager.GET_ACTIVITIES);
                for (ActivityInfo info : packageInfo.activities) {
                    if (info.nonLocalizedLabel == null) continue;
                    if (info.name.equals(getClass().getName())) {
                        titleText = info.nonLocalizedLabel.toString();
                        break;
                    }
                }
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }
        }
        return titleText;
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
        LeftResID = resId;
        if (left != null)
            left.setImageResource(resId);
    }

    protected void setRightImage(int resId) {
        rightResID = resId;
        if (right != null)
            right.setImageResource(resId);
    }


    private void initTitleBar() {
        titleBar = (ConstraintLayout) findViewById(R.id.titlebar_layout);
        left = (ImageView) findViewById(R.id.titlebar_left_tv);
        right = (ImageView) findViewById(R.id.titlebar_right_tv);

        title = (TextView) findViewById(R.id.titlebar_title_tv);
        line = findViewById(R.id.titlebar_line);
        title.setText(getTitleText() == null ? "新页面" : getTitleText());
        left.setVisibility(leftButtonVisible ? View.VISIBLE : View.INVISIBLE);
        right.setVisibility(rightButtonVisible ? View.VISIBLE : View.INVISIBLE);
        if (LeftResID != -1) {
            left.setImageResource(LeftResID);
        }
        if (rightResID != -1) {
            right.setImageResource(rightResID);
        }

        line.setVisibility(showLine ? View.VISIBLE : View.INVISIBLE);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
    }

    protected void setTitleStr(String t) {
        titleText = t;
        if (title != null)
            title.setText(t);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_left_tv) {
            onLeftButtonClick();
        } else if (id == R.id.titlebar_right_tv) {
            onRightButtonClick();
        }
    }
}
