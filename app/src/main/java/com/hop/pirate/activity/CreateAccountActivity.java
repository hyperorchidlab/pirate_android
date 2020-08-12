package com.hop.pirate.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hop.pirate.Constants;
import com.hop.pirate.IntentKey;
import com.hop.pirate.R;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.AlertDialogOkCallBack;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.event.EventNewAccount;
import com.hop.pirate.model.CreateAccountModel;
import com.hop.pirate.model.impl.CreateAccountModelImpl;
import com.hop.pirate.service.WalletWrapper;
import com.hop.pirate.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidLib.AndroidLib;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class CreateAccountActivity extends BaseActivity implements View.OnClickListener ,EasyPermissions.PermissionCallbacks,EasyPermissions.RationaleCallbacks{
    private static final String TAG = "CreateAccountActivity";
    private CreateAccountModel mCreateAccountModel;
    private EditText mPasswordEt;
    private EditText mConfirmPasswordEt;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        AndroidLib.initFildPath(Utils.getBaseDir(this));
        mCreateAccountModel = new CreateAccountModelImpl();
        boolean showBackBtn = getIntent().getBooleanExtra(IntentKey.SHOW_BACK_BUTTON, false);
        if (showBackBtn) {
            mBackIv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initViews() {
        mConfirmPasswordEt = findViewById(R.id.confirmPasswordEt);
        mPasswordEt = findViewById(R.id.passwordEt);
    }

    @Override
    public void initData() {
    }

    @Override
    public void cancelWaitDialog() {
        mCreateAccountModel.removeAllSubscribe();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createBtn:
                createAccount();
                break;
            case R.id.importBtn:
                showImportQrChoice();
                break;
            case R.id.backIv:
                finish();
                break;
            default:
                break;
        }
    }

    private void createAccount() {

        final String password = mPasswordEt.getText().toString().trim();
        String confirmPassword = mConfirmPasswordEt.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Utils.toastTips(getString(R.string.enter_password));
            return;
        }
        if (!password.equals(confirmPassword)) {
            Utils.toastTips(getString(R.string.twice_password_not_same));
            return;
        }

        showDialogFragment(R.string.creating_account);
        mCreateAccountModel.createAccount(password, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                dismissDialogFragment();
                Utils.toastException(CreateAccountActivity.this, e, Constants.REQUEST_CREATE_ACCOUNT_ERROR);
            }

            @Override
            public void onSuccess(String walletJson) {
                try {
                    WalletWrapper.MainAddress = new JSONObject(walletJson).optString("mainAddress");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onComplete() {
                dismissDialogFragment();
                Utils.toastTips(getResources().getString(R.string.create_account_success));
                Utils.clearAllData(CreateAccountActivity.this);
                EventBus.getDefault().post(new EventNewAccount());
                startActivity(MainActivity.class);
                finish();
            }
        });
    }


    void showImportQrChoice() {
        final String[] listItems = {getString(R.string.scanning_qr_code), getString(R.string.read_album), getString(R.string.cancel)};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle(getString(R.string.select_import_mode));

        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (0 == i) {
                    cameraTask();
                } else if (1 == i) {

                    checkStorage();


                }
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    @AfterPermissionGranted(Utils.RC_LOCAL_MEMORY_PERM)
    public  void checkStorage() {
        if (Utils.hasStoragePermission(this)) {
            openAlbum();
        }else{
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_extra_write),
                    Utils.RC_LOCAL_MEMORY_PERM,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @AfterPermissionGranted(Utils.RC_CAMERA_PERM)
    public void cameraTask() {
        if (Utils.hasCameraPermission(this)) {
            IntentIntegrator ii = new IntentIntegrator(CreateAccountActivity.this);
            ii.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            ii.setCaptureActivity(ScanActivity.class);
            ii.setPrompt(getString(R.string.scan_pirate_account_qr));
            ii.setCameraId(0);
            ii.setBarcodeImageEnabled(true);
            ii.initiateScan();
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.camera),
                    Utils.RC_CAMERA_PERM,
                    Manifest.permission.CAMERA);
        }
    }

    private void openAlbum() {
        Intent albumIntent = new Intent();
        albumIntent.addCategory(Intent.CATEGORY_OPENABLE);
        albumIntent.setType("image/*");
        albumIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);

        startActivityForResult(albumIntent, Utils.RC_SELECT_FROM_GALLARY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode,  List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode,  List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Utils.RC_SELECT_FROM_GALLARY == requestCode) {
            if (resultCode != RESULT_OK || null == data) {
                return;
            }
            loadAccountFromGalleryQRCode(data.getData());
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result == null) {
                return;
            }
            if (result.getContents() == null) {
                return;
            }
            try {
                final String walletStr = result.getContents();
                showPasswordDialog(walletStr);

            } catch (Exception ex) {
                Utils.toastTips(getString(R.string.import_account_failed) + ex.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        Log.d(TAG, "onRationaleAccepted:" + requestCode);
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        Log.d(TAG, "onRationaleDenied:" + requestCode);
    }

    void showPasswordDialog(final String walletStr) {
        Utils.showPassword(this, new AlertDialogOkCallBack() {

            @Override
            public void onClickOkButton(String password) {
                showDialogFragment();
                importWallet(password, walletStr);
            }
        });
    }

    private void importWallet(String password, String walletStr) {
        mCreateAccountModel.importWallet(CreateAccountActivity.this, walletStr, password, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                dismissDialogFragment();
                Utils.toastException(CreateAccountActivity.this, e, Constants.REQUEST_IMPORT_ACCOUNT_ERROR);
            }

            @Override
            public void onSuccess(String walletJson) {
                try {
                    WalletWrapper.MainAddress = new JSONObject(walletJson).optString("mainAddress");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onComplete() {
                dismissDialogFragment();
                Utils.toastTips(getResources().getString(R.string.import_success));
                EventBus.getDefault().post(new EventNewAccount());
                Utils.clearAllData(CreateAccountActivity.this);
                startActivity(MainActivity.class);
                finish();
            }
        });
    }

    void loadAccountFromGalleryQRCode(Uri uri) {

        if (null == uri) {
            Utils.toastTips(getString(R.string.error_import_image));
            return;
        }
        try {
            String walletStr = Utils.parseQRCodeFile(uri, getContentResolver());
            showPasswordDialog(walletStr);
        } catch (Exception e) {
            Utils.toastTips(getString(R.string.import_error) + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCreateAccountModel != null) {
            mCreateAccountModel.removeAllSubscribe();
        }

    }
}
