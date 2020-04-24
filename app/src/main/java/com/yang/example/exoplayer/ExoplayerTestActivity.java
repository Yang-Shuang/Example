package com.yang.example.exoplayer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.yang.example.R;
import com.yang.example.activity.SimpleBarActivity;
import com.yang.example.adapter.LivePageAdapter;
import com.yang.example.utils.LogUtil;
import com.yang.example.utils.ScreenUtil;
import com.yang.example.view.HMViewPager;

public class ExoplayerTestActivity extends SimpleBarActivity implements ExoPlayer.EventListener {

        private SimpleExoPlayerView mPlayerView;
        private EditText urlEdt;
        private FrameLayout mPlayerContent;
        private RelativeLayout mPlayerControler;
        private ViewPager player_view_pager;
        private HMViewPager hmViewPager;

        private SimpleExoPlayer player;
        private TrackSelector trackSelector;
        private Handler mainHandler;

        //    private String videoUrl = "http://mvideo.ugoshop.com/microvideo/629642/video.m3u8";
//    private String videoUrl = "http://10.9.123.55:8080/demo/video.m3u8";
//    private String videoUrl = "http://10.9.123.55:8080/demo/video1.m3u8";
        private String videoUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_exoplayer_test);

                initView();

                urlEdt.setText(videoUrl);

                initPalyer();
        }

        private void initView() {
                mPlayerView = findViewById(R.id.simple_player);
                urlEdt = findViewById(R.id.player_url_edt);
                mPlayerContent = findViewById(R.id.player_content);
                mPlayerControler = findViewById(R.id.player_control);
                hmViewPager = findViewById(R.id.hmViewPager);
//                player_view_pager = findViewById(R.id.player_view_pager);


//                player_view_pager.setFocusable(false);
//                player_view_pager.setClickable(false);
//                player_view_pager.setEnabled(false);
//                player_view_pager.setFocusableInTouchMode(false);
//                player_view_pager.setAdapter(new LivePageAdapter(getSupportFragmentManager()));

                configSettings();
        }

        private void initPalyer() {
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelection.Factory videoTrackSelectionFactory =
                        new AdaptiveTrackSelection.Factory(bandwidthMeter);
                trackSelector =
                        new DefaultTrackSelector(videoTrackSelectionFactory);

                mainHandler = new Handler();
        }

        private void configSettings() {
                int o = getRequestedOrientation();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mPlayerContent.getLayoutParams();
                if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        int screenWidth = ScreenUtil.SCREEN_WIDTH;
                        lp.width = screenWidth;
                        lp.height = (int) (screenWidth * 9f / 16);
                } else {
                        lp.width = -1;
                        lp.height = -1;
                }
        }

        public void onClick(View view) {
                super.onClick(view);
                switch (view.getId()) {
                        case R.id.iv_quanping:

                                break;
                        case R.id.simple_player_start:
                                String url = urlEdt.getText().toString();
                                if (TextUtils.isEmpty(url)) return;
                                play(url);
                                break;
                        case R.id.simple_player_stop:
                                mPlayerView.setVisibility(View.INVISIBLE);
                                player.stop();
                                break;
                        case R.id.simple_player_release:
                                mPlayerView.setVisibility(View.INVISIBLE);
                                player.stop();
                                releasePlayer();
                                break;
                }
        }

        @Override
        protected void onRightButtonClick() {
                super.onRightButtonClick();
                int orientation = getRequestedOrientation();
                if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                configSettings();
        }

        private void play(String url) {
                initPlayer(this);
                // Measures bandwidth during playback. Can be null if not required.
                DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                // Produces DataSource instances through which media data is loaded.
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                        Util.getUserAgent(this, "com.huimai365"), bandwidthMeter);
                // Produces Extractor instances for parsing the media data.
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                // This is the MediaSource representing the media to be played.

                //创建一个hls m3u8格式的播放源
//        MediaSource source = new ExtractorMediaSource(
//                Uri.parse("http://10.9.123.55:8080/demo/yiren.mp4"),
//                dataSourceFactory, extractorsFactory, null, null);
                MediaSource videoSource;
                if (url.endsWith(".m3u8")) {
                        videoSource = new HlsMediaSource(Uri.parse(url),
                                dataSourceFactory, mainHandler, null);
                } else {
                        videoSource = new ExtractorMediaSource(Uri.parse(url),
                                dataSourceFactory, extractorsFactory, null, null);
                }
                player.setVolume(0);
                //设置循环播放
//        LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);
                player.prepare(videoSource, false, false);
        }

        private void reset() {
                if (player != null)
                        player.stop();
        }

        private void releasePlayer() {
                if (player != null) {
                        player.release();
                        player = null;
                }
        }

        private void initPlayer(Context context) {
                if (player != null) {
                        releasePlayer();
                }
                player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                // Bind the player to the view.
                mPlayerView.setUseController(false);
                mPlayerView.setPlayer(player);
                player.setPlayWhenReady(true);
                player.addListener(this);
        }


        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                LogUtil.e("onPlayerStateChanged", "playbackState " + playbackState);
                switch (playbackState) {
                        case Player.STATE_BUFFERING:
                                mPlayerView.setVisibility(View.VISIBLE);
                                break;
                        case Player.STATE_READY:
                                mPlayerView.setVisibility(View.VISIBLE);
                                break;
                        case Player.STATE_IDLE:
                                //重播
                                reset();
                                play(videoUrl);
                                break;
                        case Player.STATE_ENDED:
                                mPlayerView.setVisibility(View.INVISIBLE);
                                break;
                }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
                LogUtil.e("onPlayerError", "playbackState " + error.getMessage());
        }

        @Override
        public void onPositionDiscontinuity() {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
                LogUtil.e("Activity----dispatchTouchEvent -- " + ev.getAction());
                boolean b = false;
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                        onUserInteraction();
                }
                if (getWindow().superDispatchTouchEvent(ev)) {
                        b = true;
                        if (hmViewPager.isExOnTouch()){
                                LogUtil.e("isExOnTouch");
                        }
                }
                LogUtil.e("Activity----dispatchTouchEvent -- " + b);
                return b;
        }


        @Override
        public boolean onTouchEvent(MotionEvent event) {
                LogUtil.e("Activity----onTouchEvent -- " + event.getAction());
                boolean b = super.onTouchEvent(event);
                LogUtil.e("Activity----onTouchEvent -- " + b);
                return b;
        }
}
