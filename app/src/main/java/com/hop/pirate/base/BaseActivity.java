package com.hop.pirate.base;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.hop.pirate.R;
import com.kongzue.dialog.interfaces.OnBackClickListener;
import com.kongzue.dialog.interfaces.OnDismissListener;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {
    public ImageView mBackIv;
    public static List<Activity> sActivities = new ArrayList<>();

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initViews();
        initData();
        sActivities.add(this);
        mBackIv = findViewById(R.id.backIv);
        if (mBackIv != null) {
            mBackIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    }

    public abstract void initViews();

    public abstract void initData();

    public void initIntent() {
    }


    public void showDialogFragment(String text, Boolean canCancel) {
        WaitDialog.show(this, text).setCancelable(canCancel);
    }

    public void showDialogFragment(int textId, final Boolean canCancel) {
        WaitDialog.show(this, getString(textId)).setOnBackClickListener(new OnBackClickListener() {
            @Override
            public boolean onBackClick() {
                if (canCancel) {
                    dismissDialogFragment();
                    cancelWaitDialog();
                }
                return false;
            }
        }).setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    public void showDialogFragment(int textId) {
        showDialogFragment(textId, true);
    }

    public void showDialogFragment() {
        showDialogFragment(R.string.waiting);
    }

    public void cancelWaitDialog() {

    }

    /**
     * dismiss
     */
    public void dismissDialogFragment() {
        WaitDialog.dismiss();
    }

    public void showSuccessDialog(int msgId) {
        showTipDialog(true, msgId);
    }

    public void showErrorDialog(int msgId) {
        showTipDialog(false, msgId);
    }

    public void showTipDialog(boolean isSuccess, int msgId) {
        TipDialog.show(this, msgId, isSuccess ? TipDialog.TYPE.SUCCESS : TipDialog.TYPE.ERROR);
    }

    public void visiable(View... views) {
        if (views != null && views.length > 0) {
            for (int i = 0; i < views.length; i++) {
                views[i].setVisibility(View.VISIBLE);
            }
        }
    }

    public void gone(View... views) {
        if (views != null && views.length > 0) {
            for (int i = 0; i < views.length; i++) {
                views[i].setVisibility(View.GONE);
            }
        }
    }

    public void invisible(View... views) {
        if (views != null && views.length > 0) {
            for (int i = 0; i < views.length; i++) {
                views[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    public void startActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sActivities.remove(this);
    }

    public void exitApp() {
        for (int i = 0; i < sActivities.size(); i++) {
            if (sActivities.get(i) != null && (!sActivities.get(i).isFinishing())) {
                sActivities.get(i).finish();
            }
        }
    }
}
