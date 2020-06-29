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
import com.hop.pirate.Constants;
import com.hop.pirate.R;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.AlertDialogOkCallBack;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.dialog.TransferStateDialog;
import com.hop.pirate.model.TransferModel;
import com.hop.pirate.model.impl.TransferModelImpl;
import com.hop.pirate.service.WalletWrapper;
import com.hop.pirate.util.Utils;

public class TransferOutActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private TransferModel mTransferModel;
    private TextView mTitleTv;
    private EditText mToAddressEt;
    private EditText mRemarksEt;
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

        mTitleTv = findViewById(R.id.titleTv);
        mToAddressEt = findViewById(R.id.toAddressEt);
        RadioGroup transferTypeRg = findViewById(R.id.transferTypeRg);
        mEthCountTv = findViewById(R.id.ethCountTv);
        mTokenCountTv = findViewById(R.id.tokenCountTv);
        mTransferNumberEt = findViewById(R.id.transferNumberEt);
        mRemarksEt = findViewById(R.id.remarksEt);

        mTokenCountTv.setText(String.format(getString(R.string.transfer_token_balance), Utils.ConvertCoin(WalletWrapper.HopBalance)));
        mEthCountTv.setText(String.format(getString(R.string.transfer_eth_balance), Utils.ConvertCoin(WalletWrapper.EthBalance)));
        TextView transferOutTv = findViewById(R.id.transferOutTv);
        ImageView titleRightIv = findViewById(R.id.titleRight1Iv);
        visiable(titleRightIv);
        titleRightIv.setBackgroundResource(R.drawable.scan);
        mTitleTv.setText(getResources().getString(R.string.hop_turn_out));

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
        ii.setPrompt(getString(R.string.sacn_eth_address_qr));
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

                Utils.showPassWord(this, new AlertDialogOkCallBack() {
                    @Override
                    public void OkClicked(String password) {
                        showDialogFragment(R.string.transferring,false);
                        if (checkedEth) {
                            transferEth(password, toAddress, transferNumber);
                        } else {
                            transferToken(password, toAddress, transferNumber);
                        }
                    }
                });


                break;
        }
    }

    private void transferToken(String password, String toAddress, final String transferNumber) {
        mTransferModel.transferToken(password, toAddress, transferNumber, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                dismissDialogFragment();
                if(e.getMessage().equals("password error")){
                    Utils.toastTips(getString(R.string.password_eror));
                }else{
                    Utils.toastException(TransferOutActivity.this,e, Constants.REQUEST_TRANSFER_ERROR);
                }

            }

            @Override
            public void onSuccess(String s) {
                boolean transferStatus = TextUtils.isEmpty(s);
                new TransferStateDialog(TransferOutActivity.this, !transferStatus).show();
            }

            @Override
            public void onComplete() {
                double tokenBalance = WalletWrapper.HopBalance - Double.parseDouble(transferNumber)*Utils.CoinDecimal;
                WalletWrapper.HopBalance = tokenBalance;
                mTokenCountTv.setText(String.format(getString(R.string.transfer_token_balance), Utils.ConvertCoin( WalletWrapper.HopBalance)));

                dismissDialogFragment();

            }
        });
    }

    private void transferEth(String password, String toAddress, final String num) {
        mTransferModel.transferEth(password, toAddress, num, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                dismissDialogFragment();
                if(e.getMessage().equals("password error")){
                    Utils.toastTips(getString(R.string.password_eror));
                }else{
                    Utils.toastException(TransferOutActivity.this,e, Constants.REQUEST_TRANSFER_ERROR);
                }
            }

            @Override
            public void onSuccess(String s) {
                boolean transferStatus = TextUtils.isEmpty(s);
                new TransferStateDialog(TransferOutActivity.this, !transferStatus).show();
            }

            @Override
            public void onComplete() {
                double ethBalance = WalletWrapper.EthBalance - Double.parseDouble(num)*Utils.CoinDecimal;
                WalletWrapper.EthBalance = ethBalance;
                mEthCountTv.setText(String.format(getString(R.string.transfer_eth_balance), Utils.ConvertCoin(WalletWrapper.EthBalance)));
                dismissDialogFragment();
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
