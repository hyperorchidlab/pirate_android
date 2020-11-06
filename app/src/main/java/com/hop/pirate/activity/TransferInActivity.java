package com.hop.pirate.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.hop.pirate.R;
import com.hop.pirate.base.BaseActivity;

public class TransferInActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_in);
    }

    @Override
    public void initViews() {

        TextView titleTv = findViewById(R.id.titleTv);
        mBackIv = findViewById(R.id.backIv);
        mBackIv.setBackgroundResource(R.drawable.back_white);
        titleTv.setTextColor(getResources().getColor(R.color.color_ffffff));
        titleTv.setText(getResources().getString(R.string.wallet_flow_unit_hop));
    }

    @Override
    public void initData() {

    }
}
