package com.yang.example.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.yang.base.utils.ScreenUtil;
import com.yang.example.R;
import com.yang.example.view.rain.RainView;

import java.util.ArrayList;
import java.util.Random;

public class RainAdapter extends RainView.Adapter {

    private int[] mResIds = {R.drawable.icon_hongbao_1, R.drawable.icon_hongbao_2, R.drawable.icon_hongbao_3, R.drawable.icon_hongbao_4};
    private Random mRandom = new Random();
    private Context mContext;
    private int screenWidth, itemWidth, itemHeight, limitWidth, maxRandomX;
    private int lastX;
    private boolean randomDuration = true;
    private long mDuration = 5500;
    private long mInterval = 500;

    public void setRandomDuration(boolean randomDuration) {
        this.randomDuration = randomDuration;
    }

    public boolean isRandomDuration() {
        return randomDuration;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public long getInterval() {
        return mInterval;
    }

    public void setInterval(long interval) {
        this.mInterval = interval;
    }

    public RainAdapter(Context context) {
        this.mContext = context;
        screenWidth = ScreenUtil.SCREEN_WIDTH;
        itemWidth = (int) (screenWidth / 375f * 58);
        itemHeight = (int) (screenWidth / 375f * 77);
        limitWidth = screenWidth / 3;
        maxRandomX = limitWidth - itemWidth;
    }

    @Override
    protected int getRefreshInterval() {
        return (int) mInterval;
    }

    @Override
    protected int getRefreshCount() {
        return 0;
    }

    @Override
    protected ArrayList<RainView.RainItem> addRainItem() {
        ArrayList<RainView.RainItem> items = new ArrayList<>();
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.icon_hm_hongbao);
//        imageView.setImageResource(mResIds[mRandom.nextInt(4)]);
        CustomRainItem item = new CustomRainItem(imageView, System.currentTimeMillis(), getRandomDuration());
        int x = getRandomX(lastX);
        item.setLocationX(x);
        lastX = x;
        item.setItemSize(itemWidth, itemHeight);
        items.add(item);
        return items;
    }

    private long getRandomDuration() {
        if (randomDuration) {
            return mRandom.nextInt(3000) + 3000;
        } else {
            return mDuration;
        }
    }

    private int getRandomX(int lastX) {
        int start = 0;
        if (lastX < limitWidth) {
            start = mRandom.nextInt(2) == 0 ? limitWidth : limitWidth * 2;
        } else if (lastX < limitWidth * 2) {
            start = mRandom.nextInt(2) == 0 ? 0 : limitWidth * 2;
        } else {
            start = mRandom.nextInt(2) == 0 ? 0 : limitWidth;
        }
        return start + mRandom.nextInt(maxRandomX);
    }

    private static class CustomRainItem extends RainView.RainItem {

        public CustomRainItem(View view, long startTime, long duration) {
            super(view, startTime, duration);
        }

        @Override
        protected void refreshLocation(long start, long current, long duration, int parentHeight) {
            if (parentHeight <= 0) { // 可能还没attachView
                return;
            }
            if (duration <= 0) return; // 没设置时间
            if (current < start) return; // 还没到出场时间
            if (current > start + duration) {
                updateItemState(State.COMPLETE); // 已经过了时间
            } else {
                updateItemState(State.PLAYING);
                int y = (int) ((current - start) / 1f / duration * (parentHeight + getLayoutParams().height) - getLayoutParams().height);
                setLocationY(y);
            }
        }
    }
}
