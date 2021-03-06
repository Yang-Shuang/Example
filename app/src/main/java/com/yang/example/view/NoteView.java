package com.yang.example.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.VelocityTrackerCompat;
import androidx.core.view.ViewCompat;
import android.text.BoringLayout;
import android.text.BoringLayout.Metrics;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.OverScroller;

import com.yang.example.R;

import java.util.ArrayList;
import java.util.List;

public class NoteView extends View {

    private int normalColor = Color.BLACK;
    private int warnColor = Color.YELLOW;
    private int lightColor = Color.RED;

    public static final int NORMAL_LEVEL = 1;
    public static final int WARN_LEVEL = 2;
    public static final int LIGHT_LEVEL = 3;

    private TextPaint normalPaint, warnPaint, lightPaint;
    private float fontSize;
    private int contentWidth;
    private float lineHeight = 0;

    private List<TextModel> contentList;
    private int mTouchSlop;
    private int mScrollWidth, mConetntHeight;
    private int lastContentHeight;
    private boolean currentScrollYEND = true;
    private int mMinFlingVelocity, mMaxFlingVelocity;
    private float touchY;
    private final int SCROLL_STATE_IDLE = 0;
    private final int SCROLL_STATE_DRAGGING = 1;
    private final int SCROLL_STATE_SETTLING = 2;
    private int scrollStatus = 0;

    private static final int INVALID_POINTER = -1;
    private int mScrollPointerId = INVALID_POINTER;

    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private ViewFlinger mViewFlinger = new ViewFlinger();

    private int screen_width;
    private int screen_height;

    public NoteView(Context context) {
        super(context);
    }

    public NoteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs, 0);
    }

    public NoteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs, defStyleAttr);
    }

    private void initAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NoteView, defStyleAttr, 0);
        fontSize = typedArray.getDimension(R.styleable.NoteView_fontSize, 50);
        normalColor = typedArray.getColor(R.styleable.NoteView_normalColor, Color.BLACK);
        warnColor = typedArray.getColor(R.styleable.NoteView_warnColor, Color.YELLOW);
        lightColor = typedArray.getColor(R.styleable.NoteView_lightColor, Color.RED);
        typedArray.recycle();

        normalPaint = new TextPaint();
        normalPaint.setColor(normalColor);
        normalPaint.setTextSize(fontSize);
        normalPaint.setHinting(Paint.HINTING_ON);
        normalPaint.setSubpixelText(true);


        warnPaint = new TextPaint();
        warnPaint.setColor(warnColor);
        warnPaint.setTextSize(fontSize);
        warnPaint.setHinting(Paint.HINTING_ON);
        warnPaint.setSubpixelText(true);

        lightPaint = new TextPaint();
        lightPaint.setColor(lightColor);
        lightPaint.setTextSize(fontSize);
        lightPaint.setHinting(Paint.HINTING_ON);
        lightPaint.setSubpixelText(true);

        contentList = new ArrayList<>();
        contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        lineHeight = fontSize / 36.75f * 46.33f; // 具体怎么算得   不知道

        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();

        DisplayMetrics metric = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metric);
        screen_width = metric.widthPixels;
        screen_height = metric.heightPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int measuredWidth = 0;
        int measuredHeight = 0;
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                measuredWidth = screen_width;
                break;
            case MeasureSpec.EXACTLY:
                measuredWidth = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                measuredWidth = widthSize;
                break;
        }

        measuredHeight = heightSize;
        setMeasuredDimension(measuredWidth, measuredHeight);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        final MotionEvent vtev = MotionEvent.obtain(event);
        mVelocityTracker.addMovement(vtev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                currentScrollYEND = false;
                if (scrollStatus == SCROLL_STATE_SETTLING) {
                    mViewFlinger.stop();
                }
                scrollStatus = SCROLL_STATE_IDLE;
                mScrollPointerId = event.getPointerId(0);
                touchY = event.getY();
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                currentScrollYEND = false;
                mScrollPointerId = event.getPointerId(event.getActionIndex());
                touchY = (int) (event.getY(event.getActionIndex()) + 0.5f);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                final int index = event.findPointerIndex(mScrollPointerId);
                if (index < 0) {
                    return false;
                }
                float y = event.getY(index);
                float dy = touchY - y;
                if (scrollStatus != SCROLL_STATE_DRAGGING) {
                    boolean startScroll = false;
                    if (Math.abs(dy) > mTouchSlop) {
                        if (dy > 0) {
                            dy -= mTouchSlop;
                        } else {
                            dy += mTouchSlop;
                        }
                        startScroll = true;
                    }
                    if (startScroll) {
                        scrollStatus = SCROLL_STATE_DRAGGING;
                    }
                }
                if (scrollStatus == SCROLL_STATE_DRAGGING) {
                    touchY = y;
                    constrainScrollBy(0, (int) dy);
                }
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                float yVelocity = -VelocityTrackerCompat.getYVelocity(mVelocityTracker, mScrollPointerId);
                if (Math.abs(yVelocity) < mMinFlingVelocity) {
                    yVelocity = 0F;
                } else {
                    yVelocity = Math.max(-mMaxFlingVelocity, Math.min(yVelocity, mMaxFlingVelocity));
                }
                if (yVelocity != 0 && scrollStatus != SCROLL_STATE_SETTLING) {
                    mViewFlinger.fling((int) yVelocity);
                } else {
                    setScrollState(SCROLL_STATE_IDLE);
                    currentScrollYEND = isScrollYEnd();
                }
                resetTouch();
                break;
        }
        vtev.recycle();
        return true;
    }

    private void resetTouch() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
    }

    private void setScrollState(int state) {
        if (state == scrollStatus) {
            return;
        }
        scrollStatus = state;
        if (state != SCROLL_STATE_SETTLING) {
            mViewFlinger.stop();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if (contentList.size() == 0) return;
        int offsetLeft = getPaddingLeft();
        int offsetTop = getPaddingTop();
        int offsetBottom = getPaddingBottom();
        int contentHeight = 0;
        contentHeight += offsetTop;
        canvas.translate(offsetLeft, offsetTop);
        for (TextModel model : contentList) {
            if (model.layout == null) {
                model.generateLayout(contentWidth);
            }
            model.layout.draw(canvas);
            canvas.translate(0, model.layout.getLineCount() * lineHeight);
            contentHeight += model.layout.getLineCount() * lineHeight;
        }
        contentHeight += offsetBottom;
        if (contentHeight > mConetntHeight) {
            mConetntHeight = contentHeight;
        }
        if (lastContentHeight != mConetntHeight) {
            if (currentScrollYEND && !isScrollYEnd()) {
                scrollToEnd();
            }
            lastContentHeight = mConetntHeight;
        }
    }

    public void addText(String text) {
        if (text == null || text.equals("")) return;
        addText(NORMAL_LEVEL, text);
    }

    public void addText(int level, String text) {
        if (text == null || text.equals("")) return;
        TextPaint p = level == LIGHT_LEVEL ? lightPaint : level == WARN_LEVEL ? warnPaint : normalPaint;
        contentList.add(new TextModel(text, level, p, getWidth() - getPaddingLeft() - getPaddingRight()));
        postInvalidate();
    }

    public void clearText() {
        contentList.clear();
        postInvalidate();
    }

    private void constrainScrollBy(int dx, int dy) {
        int scrollX = getScrollX();
        int scrollY = getScrollY();
//        //右边界
//        if (mWidth - scrollX - dx < width) {
//            dx = mWidth - scrollX - width;
//        }
//        //左边界
//        if (-scrollX - dx > 0) {
//            dx = -scrollX;
//        }
        if (dy > 0) {
            //下边界
            if (scrollY + getHeight() >= mConetntHeight) {
                dy = 0;
            } else if (scrollY + getHeight() + dy > mConetntHeight) {
                dy = mConetntHeight - scrollY - getHeight();
            }
        } else {
            //上边界
            if (scrollY + dy < 0) {
                dy = -scrollY;
            }
        }
        scrollBy(dx, dy);
    }

    private boolean isScrollYEnd() {
        return getScrollY() + getHeight() >= mConetntHeight;
    }

    private void scrollToEnd() {
        constrainScrollBy(0, mConetntHeight - getScrollY() - getHeight());
        currentScrollYEND = true;
    }

    class TextModel {
        public String text;
        public int level;
        public Layout layout;
        public TextPaint paint;

        public TextModel(String text, int level, TextPaint paint, int width) {
            this.text = text;
            this.level = level;
            this.paint = paint;
            if (width <= 0) return;
            generateLayout(width);

        }

        public void generateLayout(int width) {
            if (paint.measureText(text) > width) {
                layout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.1f, 0.0f, false);
            } else {
                Metrics metrics = new Metrics();
                metrics.top = paint.getFontMetricsInt().top;
                metrics.bottom = paint.getFontMetricsInt().bottom;
                metrics.ascent = paint.getFontMetricsInt().ascent;
                metrics.leading = paint.getFontMetricsInt().leading;
                layout = new BoringLayout(text, paint, 0, Layout.Alignment.ALIGN_NORMAL, 1.1f, 0, metrics, false);
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
    }

    public void smoothScrollTo(int x, int y) {
    }

    public void smoothScrollBy(int x, int y) {
        mViewFlinger.smoothScrollBy(x, y);
    }


    private class ViewFlinger implements Runnable {
        private int mLastFlingY = 0;
        private OverScroller mScroller;
        private boolean mEatRunOnAnimationRequest = false;
        private boolean mReSchedulePostAnimationCallback = false;

        public ViewFlinger() {
            mScroller = new OverScroller(getContext());
        }

        public OverScroller getScroller() {
            return mScroller;
        }

        public void smoothScrollTo(int x, int y) {
        }

        public void smoothScrollBy(int x, int y) {
            mLastFlingY = 0;
            mScroller.startScroll(getScrollX(), getScrollY(), x, y);
            postOnAnimation();
        }

        @Override
        public void run() {
            disableRunOnAnimationRequests();
            final OverScroller scroller = mScroller;
            if (scroller.computeScrollOffset()) {
                final int y = scroller.getCurrY();
                int dy = y - mLastFlingY;
                mLastFlingY = y;
                constrainScrollBy(0, dy);
                postOnAnimation();
            } else {
                currentScrollYEND = isScrollYEnd();
            }
            enableRunOnAnimationRequests();
        }

        public void fling(int velocityY) {
            mLastFlingY = 0;
            setScrollState(SCROLL_STATE_SETTLING);
            mScroller.fling(0, 0, 0, velocityY, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            postOnAnimation();
        }

        public void stop() {
            removeCallbacks(this);
            mScroller.abortAnimation();
        }

        private void disableRunOnAnimationRequests() {
            mReSchedulePostAnimationCallback = false;
            mEatRunOnAnimationRequest = true;
        }

        private void enableRunOnAnimationRequests() {
            mEatRunOnAnimationRequest = false;
            if (mReSchedulePostAnimationCallback) {
                postOnAnimation();
            }
        }

        void postOnAnimation() {
            if (mEatRunOnAnimationRequest) {
                mReSchedulePostAnimationCallback = true;
            } else {
                removeCallbacks(this);
                ViewCompat.postOnAnimation(NoteView.this, this);
            }
        }
    }

}
