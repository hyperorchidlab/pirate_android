package com.hop.pirate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hop.pirate.R;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.AlertDialogOkCallBack;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.TransferModel;
import com.hop.pirate.model.impl.TransferModelImpl;
import com.hop.pirate.service.WalletWrapper;
import com.hop.pirate.util.Utils;
import com.kongzue.dialog.v3.MessageDialog;

public class TransferOutActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private TransferModel mTransferModel;
    private EditText mToAddressEt;
    private boolean checkedEth = true;
    private EditText mTransferNumberEt;
    private TextView mTokenCountTv;
    private TextView mEthCountTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_out);
        mTransferModel = new TransferModelImpl();
    }

    @Override
    public void initViews() {

        TextView titleTv = findViewById(R.id.titleTv);
        mToAddressEt = findViewById(R.id.toAddressEt);
        RadioGroup transferTypeRg = findViewById(R.id.transferTypeRg);
        mEthCountTv = findViewById(R.id.ethCountTv);
        mTokenCountTv = findViewById(R.id.tokenCountTv);
        mTransferNumberEt = findViewById(R.id.transferNumberEt);
        EditText remarksEt = findViewById(R.id.remarksEt);

        mTokenCountTv.setText(String.format(getString(R.string.transfer_token_balance), Utils.ConvertCoin(WalletWrapper.HopBalance)));
        mEthCountTv.setText(String.format(getString(R.string.transfer_eth_balance), Utils.ConvertCoin(WalletWrapper.EthBalance)));
        TextView transferOutTv = findViewById(R.id.transferOutTv);
        ImageView titleRightIv = findViewById(R.id.titleRight1Iv);
        visiable(titleRightIv);
        titleRightIv.setBackgroundResource(R.drawable.scan);
        titleTv.setText(getResources().getString(R.string.hop_turn_out));

        transferOutTv.setOnClickListener(this);
        titleRightIv.setOnClickListener(this);
        transferTypeRg.setOnCheckedChangeListener(this);

    }

    private void openQR() {
        if (!Utils.checkCamera(TransferOutActivity.this)) {
            return;
        }
        IntentIntegrator ii = new IntentIntegrator(TransferOutActivity.this);
        ii.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        ii.setCaptureActivity(ScanActivity.class);
        ii.setPrompt(getString(R.string.scan_eth_address_qr));
        ii.setCameraId(0);
        ii.setBarcodeImageEnabled(true);
        ii.initiateScan();
    }

    @Override
    public void initData() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleRight1Iv:
                openQR();
                break;
            case R.id.transferOutTv:
                final String toAddress = mToAddressEt.getText().toString().trim();
                if (TextUtils.isEmpty(toAddress)) {
                    Utils.toastTips(getString(R.string.transfer_input_money_address));
                    return;
                }
                final String transferNumber = mTransferNumberEt.getText().toString().trim();
                if (TextUtils.isEmpty(transferNumber)) {
                    Utils.toastTips(getString(R.string.transfer_input_transfer_out_money));
                    return;
                }

                Utils.showPassword(this, new AlertDialogOkCallBack() {
                    @Override
                    public void onClickOkButton(String password) {
                        showDialogFragment(R.string.transferring, false);
                        if (checkedEth) {
                            transferEth(password, toAddress, transferNumber);
                        } else {
                            transferToken(password, toAddress, transferNumber);
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    private void transferToken(String password, String toAddress, final String transferNumber) {
        mTransferModel.transferToken(password, toAddress, transferNumber, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                if (e.getMessage().equals("password error")) {
                    dismissDialogFragment();
                    Utils.toastTips(getString(R.string.password_error));
                } else {
                    showErrorDialog(R.string.transfer_fail);
                }

            }

            @Override
            public void onSuccess(String tx) {
                dismissDialogFragment();
                showDialogFragment(getString(R.string.transferring) + "\ntx[" + tx + "]", false);
                queryTxStatus(tx, transferNumber, false);
            }

            @Override
            public void onComplete() {


            }
        });
    }

    private void transferEth(String password, String toAddress, final String num) {
        mTransferModel.transferEth(password, toAddress, num, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                if (e.getMessage().equals("password error")) {
                    dismissDialogFragment();
                    Utils.toastTips(getString(R.string.password_error));
                } else {
                    showErrorDialog(R.string.transfer_fail);
                }
            }

            @Override
            public void onSuccess(String tx) {
                dismissDialogFragment();
                showDialogFragment(getString(R.string.transferring) + "\ntx[" + tx + "]", false);
                queryTxStatus(tx, num, true);

            }

            @Override
            public void onComplete() {

            }
        });
    }


    private void queryTxStatus(final String tx, final String num, final boolean isEth) {
        mTransferModel.queryTxProcessStatus(tx, new ResultCallBack<Boolean>() {
            @Override
            public void onError(Throwable e) {
                dismissDialogFragment();
                showErrorDialog(R.string.transfer_fail);
            }

            @Override
            public void onSuccess(Boolean isSuccess) {
                String content;
                if (isEth) {
                    content = "Eth Tx:[" + tx + "]";
                    double ethBalance = WalletWrapper.EthBalance - Double.parseDouble(num) * Utils.COIN_DECIMAL;
                    WalletWrapper.EthBalance = ethBalance;
                    mEthCountTv.setText(String.format(getString(R.string.transfer_eth_balance), Utils.ConvertCoin(WalletWrapper.EthBalance)));
                } else {
                    content = "Eth Tx:[" + tx + "]";
                    double tokenBalance = WalletWrapper.HopBalance - Double.parseDouble(num) * Utils.COIN_DECIMAL;
                    WalletWrapper.HopBalance = tokenBalance;
                    mTokenCountTv.setText(String.format(getString(R.string.transfer_token_balance), Utils.ConvertCoin(WalletWrapper.HopBalance)));
                }
                showErrorDialog(R.string.transfer_fail);
                showSuccessDialog(R.string.transfer_success);
                MessageDialog.show(TransferOutActivity.this, getString(R.string.tips), content, getString(R.string.sure));
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.ethRb:
                checkedEth = true;
                break;
            case R.id.tokenRb:
                checkedEth = false;
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result == null) {
            return;
        }
        if (result.getContents() == null) {
            Utils.toastTips(getString(R.string.invalid_scan_code));
            return;
        }
        try {
            final String address = result.getContents();
            mToAddressEt.setText(address);
        } catch (Exception ex) {
            Utils.toastTips(getString(R.string.import_account_failed) + ex.getLocalizedMessage());
        }
    }
}
