package com.yang.example.exoplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import com.yang.example.utils.LogUtil;
import com.yang.example.utils.ScreenUtil;

public class ExoplayerTestActivity extends SimpleBarActivity implements ExoPlayer.EventListener {


    private SimpleExoPlayerView mPlayerView;
    private EditText urlEdt;

    private SimpleExoPlayer player;
    private TrackSelector trackSelector;
    private Handler mainHandler;

    //    private String videoUrl = "http://mvideo.ugoshop.com/microvideo/629642/video.m3u8";
    private String videoUrl = "http://10.9.123.55:8080/demo/video.m3u8";
//    private String videoUrl = "http://10.9.123.55:8080/demo/video1.m3u8";
//    private String videoUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer_test);

        mPlayerView = findViewById(R.id.simple_player);
        urlEdt = findViewById(R.id.player_url_edt);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mPlayerView.getLayoutParams();
        int screenWidth = ScreenUtil.SCREEN_WIDTH;
        lp.width = screenWidth;
        lp.height = (int) (screenWidth * 400f / 750);

        urlEdt.setText(videoUrl);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        mainHandler = new Handler();
    }

    public void onClick(View view) {
        switch (view.getId()) {
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
//        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri,
//                dataSourceFactory, extractorsFactory, null, null);
        //创建一个hls m3u8格式的播放源
//        MediaSource source = new ExtractorMediaSource(
//                Uri.parse("http://10.9.123.55:8080/demo/yiren.mp4"),
//                dataSourceFactory, extractorsFactory, null, null);
        MediaSource videoSource = new HlsMediaSource(Uri.parse(url),
                dataSourceFactory, mainHandler, null);
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
}
