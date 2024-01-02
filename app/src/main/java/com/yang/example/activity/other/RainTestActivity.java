package com.yang.example.activity.other;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yang.base.activity.SimpleBarActivity;
import com.yang.example.R;
import com.yang.example.adapter.RainAdapter;
import com.yang.example.view.rain.RainView;

public class RainTestActivity extends SimpleBarActivity {

    private RainView rain_view;
    private RainAdapter mRainAdapter;
    private TextView interval_tv;
    private TextView duration_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rain_test);

        // 58* 77

        interval_tv = findViewById(R.id.interval_tv);
        duration_tv = findViewById(R.id.duration_tv);
        rain_view = findViewById(R.id.rain_view);
        mRainAdapter = new RainAdapter(this);
        rain_view.setAdapter(mRainAdapter);

        interval_tv.setText("间隔:" + mRainAdapter.getInterval());
        duration_tv.setText("时长:" + mRainAdapter.getDuration());
    }

    public void onButtonClick(View view) {
        if (view.getId() == R.id.start_btn) {
            rain_view.start();
        } else if (view.getId() == R.id.stop_btn) {
            rain_view.stop();
        } else if (view.getId() == R.id.random_btn) {
            mRainAdapter.setRandomDuration(!mRainAdapter.isRandomDuration());
        } else if (view.getId() == R.id.interval_add_btn) {
            mRainAdapter.setInterval(mRainAdapter.getInterval() + 100);
            interval_tv.setText("间隔:" + mRainAdapter.getInterval());
        } else if (view.getId() == R.id.interval_reduce_btn) {
            long d = mRainAdapter.getInterval() - 100;
            mRainAdapter.setInterval(d >= 100 ? d : 100);
            interval_tv.setText("间隔:" + mRainAdapter.getInterval());
        } else if (view.getId() == R.id.duration_add_btn) {
            mRainAdapter.setDuration(mRainAdapter.getDuration() + 500);
            duration_tv.setText("时长:" + mRainAdapter.getDuration());
        } else if (view.getId() == R.id.duration_reduce_btn) {
            long d = mRainAdapter.getDuration() - 500;
            mRainAdapter.setDuration(d >= 500 ? d : 500);
            duration_tv.setText("时长:" + mRainAdapter.getDuration());
        }
    }
}
