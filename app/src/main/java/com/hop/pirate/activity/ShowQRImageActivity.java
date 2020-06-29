package com.hop.pirate.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;

import com.hop.pirate.R;
import com.hop.pirate.base.BaseActivity;

public class ShowQRImageActivity extends BaseActivity implements View.OnClickListener {
    public static Bitmap QRBitmap;
    private ImageView mQRIV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_qr_image);
    }

    @Override
    public void initViews() {
        mQRIV = findViewById(R.id.QRIV);
       findViewById(R.id.constraintLayout).setOnClickListener(this);

        mQRIV.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mQRIV.setImageBitmap(QRBitmap);
    }

    @Override
    public void onClick(View v) {
     ActivityCompat.finishAfterTransition(this);
    }
}
