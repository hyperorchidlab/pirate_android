package com.hop.pirate.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.hop.pirate.R;
import com.hop.pirate.adapter.GuideAdapter;
import com.hop.pirate.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class GuideActivity extends BaseActivity {
    final int[] images = new int[]{R.drawable.guide_01,R.drawable.guide_02,R.drawable.guide_03,R.drawable.guide_04};
    private ViewPager mViewpager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

    }

    @Override
    public void initViews() {
        TextView titleTv = findViewById(R.id.titleTv);
        titleTv.setText(R.string.fragment_account_operation_guide);
        mViewpager = findViewById(R.id.viewpager);
        mViewpager.setOffscreenPageLimit(4);

    }

    @Override
    public void initData() {
        GuideAdapter guideAdapter = new GuideAdapter(this, images);
        mViewpager.setAdapter(guideAdapter);

        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(mViewpager);
        guideAdapter.registerDataSetObserver(indicator.getDataSetObserver());
    }
}
