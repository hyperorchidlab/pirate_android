package com.hop.pirate.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hop.pirate.PError;
import com.hop.pirate.R;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.SplashModel;
import com.hop.pirate.model.impl.SplashModelImpl;
import com.hop.pirate.util.Utils;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 10:37 AM
 */
public class SplashActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private SplashModel mSplashModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSplashModel = new SplashModelImpl();
        if (Utils.checkStorage(this)) {
            loadWallet();
        }
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        loadWallet();

    }


    private void loadWallet() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSplashModel.loadWallet(SplashActivity.this,new ResultCallBack<String>() {
                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof PError) {
                            startActivity(CreateAccountActivity.class);
                            SplashActivity.this.finish();
                        }else{
                            Utils.toastTips(e.getMessage());
                            SplashActivity.this.finish();
                        }
                    }

                    @Override
                    public void onSuccess(String o) {

                    }

                    @Override
                    public void onComplete() {

                        startActivity(MainActivity.class);
                        SplashActivity.this.finish();
                    }
                });
            }
        }, 2000);
    }


    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        new AppSettingsDialog
                .Builder(this)
                .setTitle(getString(R.string.tips))
                .setRationale(R.string.forbiden_permission_des)
                .build()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                loadWallet();
            } else {
                finish();
            }
        }
    }
}
