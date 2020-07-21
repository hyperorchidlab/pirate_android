package com.hop.pirate.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.hop.pirate.model.CreateAccountModel;
import com.hop.pirate.model.impl.CreateAccountModelImpl;
import com.hop.pirate.util.Utils;

import androidLib.AndroidLib;

public class CreateAccountActivity extends BaseActivity implements View.OnClickListener {
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
                showImportQRChoice();
                break;
            case R.id.backIv:
                finish();
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

        showDialogFragment(R.string.creatding_account);
        mCreateAccountModel.createAccount(password, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                dismissDialogFragment();
                Utils.toastException(CreateAccountActivity.this, e, Constants.REQUEST_CREATE_ACCOUNT_ERROR);
            }

            @Override
            public void onSuccess(String o) {

            }

            @Override
            public void onComplete() {
                dismissDialogFragment();
                Utils.toastTips(getResources().getString(R.string.create_account_success));
                Utils.clearAllData(CreateAccountActivity.this);
                MineMachineListActivity.sMinerBeans = null;
                MinePoolListActivity.sMinePoolBeans = null;
                startActivity(MainActivity.class);
                finish();
            }
        });
    }


    void showImportQRChoice() {
        final String[] listItems = {getString(R.string.scanning_qr_code), getString(R.string.read_album), getString(R.string.cancel)};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle(getString(R.string.select_import_mode));

        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (0 == i) {
                    if (!Utils.checkCamera(CreateAccountActivity.this)) {
                        return;
                    }
                    IntentIntegrator ii = new IntentIntegrator(CreateAccountActivity.this);
                    ii.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    ii.setCaptureActivity(ScanActivity.class);
                    ii.setPrompt(getString(R.string.sacn_porton_account_qr));
                    ii.setCameraId(0);
                    ii.setBarcodeImageEnabled(true);
                    ii.initiateScan();
                } else if (1 == i) {

                    if (!Utils.checkStorage(CreateAccountActivity.this)) {
                        return;
                    }

                    openAlbum();

                }
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void openAlbum() {
        Intent albumIntent = new Intent();
        albumIntent.addCategory(Intent.CATEGORY_OPENABLE);
        albumIntent.setType("image/*");
        albumIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);

        startActivityForResult(albumIntent, Utils.RC_SELECT_FROM_GALLARY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Utils.RC_SELECT_FROM_GALLARY == requestCode) {
            if (resultCode != RESULT_OK || null == data) {
                Utils.toastTips(getString(R.string.unread_data));
                return;
            }
            loadAccountFromGalleryQRCode(data.getData());
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result == null) {
                return;
            }
            if (result.getContents() == null) {
                Utils.toastTips(getString(R.string.invalid_scan_code));
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

    void showPasswordDialog(final String walletStr) {
        Utils.showPassWord(this, new AlertDialogOkCallBack() {

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
            public void onSuccess(String str) {

            }

            @Override
            public void onComplete() {
                dismissDialogFragment();
                Utils.toastTips(getResources().getString(R.string.import_success));
                MineMachineListActivity.sMinerBeans = null;
                MinePoolListActivity.sMinePoolBeans = null;
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
            String walletStr = Utils.ParseQRCodeFile(uri, getContentResolver());
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
