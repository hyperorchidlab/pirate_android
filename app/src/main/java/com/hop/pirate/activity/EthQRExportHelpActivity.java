package com.hop.pirate.activity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.hop.pirate.Constants;
import com.hop.pirate.R;
import com.hop.pirate.base.BaseActivity;

public class EthQRExportHelpActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eth_qr_export_help);

    }

    @Override
    public void initViews() {

        final VideoView videoView = findViewById(R.id.videoView);
        final ProgressBar loadingPb = findViewById(R.id.loadingPb);
        ((TextView) findViewById(R.id.titleTv)).setText(getResources().getString(R.string.Account_address_getImtoken));

        videoView.setVideoURI(Uri.parse(Constants.EXPORT_IMTOKEN));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.setVisibility(View.VISIBLE);
                gone(loadingPb);
                mp.setLooping(true);
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            videoView.setBackgroundColor(Color.TRANSPARENT);
                        }
                        return false;
                    }
                });
            }
        });

        videoView.start();
    }

    @Override
    public void initData() {

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backTv:
                finish();
                break;
        }
    }


}
