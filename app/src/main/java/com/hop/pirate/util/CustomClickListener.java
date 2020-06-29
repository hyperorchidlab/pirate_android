package com.hop.pirate.util;

import android.view.View;

public abstract class CustomClickListener implements View.OnClickListener {
    private long mLastClickTime;
    private long timeInterval = 1000L;

    public CustomClickListener() {

    }

    public CustomClickListener(long interval) {
        this.timeInterval = interval;
    }

    @Override
    public void onClick(View v) {
        long nowTime = System.currentTimeMillis();
        if (nowTime - mLastClickTime > timeInterval) {
            onSingleClick();
            mLastClickTime = nowTime;
        } else {
            onFastClick();
        }
    }

    protected abstract void onSingleClick();
    protected abstract void onFastClick();
}
