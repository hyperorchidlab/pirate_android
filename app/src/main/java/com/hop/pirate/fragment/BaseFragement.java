package com.hop.pirate.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.hop.pirate.base.BaseActivity;

public abstract class BaseFragement extends Fragment {
    public BaseActivity mActivity;
    private boolean isShown = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }

    public void onShow() {
    }

    public void onHide() {
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isShown = true;
            onShow();
        } else {
            isShown = false;
            onHide();
        }
    }


    public abstract void initViews(View view);
}
