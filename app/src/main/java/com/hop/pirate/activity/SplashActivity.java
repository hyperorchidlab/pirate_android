package com.hop.pirate.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.hop.pirate.PError;
import com.hop.pirate.R;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.AlertDialogOkCallBack;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.SplashModel;
import com.hop.pirate.model.bean.AppVersionBean;
import com.hop.pirate.model.impl.SplashModelImpl;
import com.hop.pirate.service.HopService;
import com.hop.pirate.util.Utils;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 10:37 AM
 */
public class SplashActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, Handler.Callback {
    private SplashModel mSplashModel;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSplashModel = new SplashModelImpl();
        mHandler = new Handler(this);
        if (!checkNetwork()) {
            return;
        }


        if (Utils.checkVPN(SplashActivity.this) && (!Utils.isServiceWork(SplashActivity.this, HopService.class.getName()))) {
            Utils.showOkAlert(SplashActivity.this, R.string.tips, R.string.close_other_vpn_app, new AlertDialogOkCallBack() {
                @Override
                public void onClickOkButton(String parameter) {
                    finish();
                }
            });
            return;
        }

        mSplashModel.checkVersion(this, new ResultCallBack<AppVersionBean>() {
            @Override
            public void onError(Throwable e) {
                delayLoadwallet();
            }

            @Override
            public void onSuccess(AppVersionBean versionBean) {
                if (versionBean.getNewVersion() > Utils.getVersionCode(SplashActivity.this)) {
                    showUpdataAppDialog(versionBean);
                } else {
                    delayLoadwallet();
                }
            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void delayLoadwallet() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Utils.checkStorage(SplashActivity.this)) {
                    loadWallet();
                }
            }
        }, 1500);
    }

    private void showUpdataAppDialog(AppVersionBean versionBean) {
        String updateMsg;
        String able = getResources().getConfiguration().locale.getCountry();
        if (able.equals("CN")) {
            updateMsg = versionBean.getUpdateMsgCN();
        } else {
            updateMsg = versionBean.getUpdateMsgEN();
        }
        MessageDialog messageDialog = MessageDialog.build(SplashActivity.this)
                .setCancelable(false)
                .setTitle(getString(R.string.new_version))
                .setMessage(updateMsg)
                .setOkButton(getString(R.string.update_version))
                .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        Utils.openAppDownloadPage(SplashActivity.this);
                        finish();
                        return false;
                    }
                });
        if (Utils.getVersionCode(SplashActivity.this) > versionBean.getMinversion()){
            messageDialog.setCancelButton(getString(R.string.cancel)).setOnCancelButtonClickListener(new OnDialogButtonClickListener() {
                @Override
                public boolean onClick(BaseDialog baseDialog, View v) {
                    delayLoadwallet();
                    return false;
                }
            });


        }
        messageDialog.show();
    }


    private boolean checkNetwork() {
        if (!Utils.isNetworkAvailable(SplashActivity.this)) {
            Utils.showOkAlert(SplashActivity.this, R.string.tips, R.string.network_unavailable, new AlertDialogOkCallBack() {
                @Override
                public void onClickOkButton(String parameter) {
                    finish();
                }
            });
            return false;
        }

        return true;
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
        mSplashModel.loadWallet(SplashActivity.this, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof PError) {
                    startActivity(CreateAccountActivity.class);
                    SplashActivity.this.finish();
                } else {
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

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
