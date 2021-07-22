package com.example.myapplication;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;


public class video_play extends AppCompatActivity {


    ImageButton mPrevButton;
    SimpleExoPlayer mPlayer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        Intent intent =getIntent();
        String mUrl =intent.getStringExtra("videoUrl");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        PlayerView playerView = findViewById(R.id.video_view);
        mPrevButton = findViewById(R.id.btn_prev);
        mPrevButton.setOnClickListener(view -> finish());

        playerView.setControllerAutoShow(false);
        mPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(mPlayer);

            playVideo(mUrl);


        playerView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(mPlayer.isPlaying()){
                    mPlayer.setPlayWhenReady(false);
                }
                else mPlayer.setPlayWhenReady(true);
            }
        });

    }

    private void playVideo(String uri) {
        MediaItem mediaItem = MediaItem.fromUri(uri);
        mPlayer.setMediaItem(mediaItem);
        mPlayer.prepare();
        mPlayer.play();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.release();
    }
}
