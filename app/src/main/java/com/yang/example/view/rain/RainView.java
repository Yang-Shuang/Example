package com.yang.example.view.rain;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 红包雨View by yangshuang
 */
public class RainView extends FrameLayout {

    private static final int STATE_DEFAULT = 9001;
    private static final int STATE_PLAYING = 9002;
    private static final int HIGH_INTERVAL = 20;
    private static final int NORMAL_INTERVAL = 5;
    private static final int LOW_INTERVAL = 150;

    private int currentState = STATE_DEFAULT;
    private int frameIntervalTime = NORMAL_INTERVAL;
    private long startTime;
    private DrawHandler mDrawHandler;
    private Adapter mAdapter;

    public RainView(@NonNull Context context) {
        this(context, null);
    }

    public RainView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RainView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDrawHandler = new DrawHandler(this);
    }

    public void setAdapter(Adapter adapter) {
        if (mAdapter != null) {
            mAdapter.stop();
        }
        this.mAdapter = adapter;
    }

    public void start() {
        if (mAdapter == null) {
            return;
        }
        if (currentState == STATE_PLAYING) {
            return;
        }
        startTime = System.currentTimeMillis();
        currentState = STATE_PLAYING;
        mAdapter.start();
        mDrawHandler.start();
    }

    public void stop() {
        currentState = STATE_DEFAULT;
        mDrawHandler.stop();
        mAdapter.stop();
    }

    public int getFrameIntervalTime() {
        return frameIntervalTime;
    }

    private void updateFrame(long currentTime) {
        mAdapter.refreshState(currentTime, this);
        postInvalidate();
    }

    public abstract static class RainItem {
        public enum State {
            WAITING,
            PLAYING,
            COMPLETE
        }

        private int width = -2;
        private int height = -2;

        private View view;
        private long startTime;
        private long duration;
        private long currentTime;
        private int parentHeight;

        private FrameLayout.LayoutParams params = new LayoutParams(width, height);
        private State state = State.WAITING;

        public RainItem(View view, long startTime, long duration) {
            this.view = view;
            this.startTime = startTime;
            this.duration = duration;
        }

        public final void setItemSize(int w, int h) {
            this.width = w;
            this.height = h;
            params.width = w;
            params.height = h;
        }

        public final void setLocationX(int x) {
            params.leftMargin = x;
        }

        public final void setLocationY(int y) {
            params.topMargin = y;
        }

        public final void setParentHeight(int parentHeight) {
            this.parentHeight = parentHeight;
        }

        public final void setCurrentTime(long currentTime) {
            this.currentTime = currentTime;
            refreshLocation(startTime, currentTime, duration, parentHeight);
        }

        public final FrameLayout.LayoutParams getLayoutParams() {
            return params;
        }

        public final void updateItemState(State s) {
            state = s;
        }

        public final State getItemState() {
            return state;
        }

        protected abstract void refreshLocation(long start, long current, long duration, int parentHeight);
    }

    public abstract static class Adapter {
        private final int defaultInterval = 500;
        private ArrayList<RainItem> rainItems = new ArrayList<>();
        protected boolean isPlaying = false;
        private int currentAddCount = 0;
        private final Handler refreshHandler = new Handler();
        private final Runnable autoAddRunnable = new Runnable() {
            @Override
            public void run() {
                addItem();
            }
        };

        private void start() {
            isPlaying = true;
            currentAddCount = 0;
            refreshHandler.postDelayed(autoAddRunnable, 0);
        }

        private void stop() {
            isPlaying = false;
            currentAddCount = 0;
            refreshHandler.removeCallbacks(autoAddRunnable);
            clear();
        }

        private void clear() {
            for (RainItem item : rainItems) {
                View view = item.view;
                if (view != null && view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
            }
            rainItems.clear();
        }

        private void addItem() {
//            Log.i("Adapter", "-- addItem -- ");
            if (isPlaying) {
                ArrayList<RainItem> items = addRainItem();
                if (items != null) {
                    rainItems.addAll(items);
                }
                currentAddCount++;
                int refreshCount = getRefreshCount();
                if (refreshCount > 0 && currentAddCount >= refreshCount) {
                    stop();
                }
                long interval = getRefreshInterval();
                if (interval <= 0) {
                    interval = defaultInterval;
                }
                refreshHandler.postDelayed(autoAddRunnable, interval);
            }
        }

        private void refreshState(long currentTime, RainView parent) {
//            Log.i("Adapter", "-- refreshState -- currentTime : " + currentTime);
            ArrayList<RainItem> deleteItems = new ArrayList<>();
            for (RainItem item : rainItems) {
                item.setCurrentTime(currentTime);
                item.setParentHeight(parent.getHeight());
                if (item.getItemState() == RainItem.State.COMPLETE) {
                    View view = item.view;
                    if (view != null && view.getParent() != null) {
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                    deleteItems.add(item);
                } else if (item.getItemState() == RainItem.State.PLAYING) {
                    View view = item.view;
                    if (view != null) {
                        if (view.getParent() == null) {
                            parent.addView(view, item.getLayoutParams());
                        } else if (view.getParent() != parent) {
                            ((ViewGroup) view.getParent()).removeView(view);
                            parent.addView(view, item.getLayoutParams());
                        } else {
                            view.setLayoutParams(item.getLayoutParams());
                        }
                    }
                }
            }
            rainItems.removeAll(deleteItems);
        }

        /**
         * 添加 ITEM 刷新间隔
         */
        protected abstract int getRefreshInterval();

        /**
         * 添加 ITEM 次数 -1 or 0 表示无数次直到stop   N（n>0） 表示N次
         */
        protected abstract int getRefreshCount();

        protected abstract ArrayList<RainItem> addRainItem();
    }

    private static class DrawHandler extends Handler {
        private WeakReference<RainView> reference;
        private static final int MSG_UPDATE = 201;
        private int interval; // 刷新间隔

        public DrawHandler(RainView rainView) {
            this.reference = new WeakReference<>(rainView);
        }

        void start() {
            interval = reference.get().getFrameIntervalTime();
            sendEmptyMessage(MSG_UPDATE);
        }

        void stop() {
            removeMessages(MSG_UPDATE);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (reference == null || reference.get() == null) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!reference.get().isAttachedToWindow()) {
                    reference.get().stop();
                    return;
                }
            }

            if (msg.what == MSG_UPDATE) {
                sendEmptyMessageDelayed(MSG_UPDATE, interval);
                reference.get().updateFrame(System.currentTimeMillis());
            }
        }
    }

}
