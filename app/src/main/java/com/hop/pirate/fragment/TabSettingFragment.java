package com.hop.pirate.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hop.pirate.Constants;
import com.hop.pirate.IntentKey;
import com.hop.pirate.R;
import com.hop.pirate.activity.CreateAccountActivity;
import com.hop.pirate.activity.MainActivity;
import com.hop.pirate.activity.ShowQRImageActivity;
import com.hop.pirate.activity.SupportedCurrenciesActivity;
import com.hop.pirate.activity.TransferOutActivity;
import com.hop.pirate.callback.AlertDialogOkCallBack;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.event.EventLoadWalletSuccess;
import com.hop.pirate.model.TabSettingModel;
import com.hop.pirate.model.bean.ExtendToken;
import com.hop.pirate.model.impl.TabSettingModelImpl;
import com.hop.pirate.service.HopService;
import com.hop.pirate.service.WalletWrapper;
import com.hop.pirate.util.Utils;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TabSettingFragment extends BaseFragement implements View.OnClickListener, Handler.Callback {
    private TabSettingModel mTabSettingModel;
    private Button mApplyFreeEthBtn;
    private Button mApplyFreeTokenBtn;
    private Handler mHandler;

    private TextView mMainNetWorkAddressValueTv;
    private ImageView mQRCodeIv;
    private TextView mSubNetAaddressValueTv;
    private TextView currentCoinTypeTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, null);
        mHandler = new Handler(this);
        mTabSettingModel = new TabSettingModelImpl();
        EventBus.getDefault().register(this);
        hopBalance();
        initViews(view);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void hopBalance() {

        mTabSettingModel.getHopBalance(WalletWrapper.MainAddress, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSuccess(String balance) {
                if (balance.equals("0")) {
                    mApplyFreeTokenBtn.setEnabled(true);
                    return;
                }

                if (Double.parseDouble(Utils.ConvertCoin(Double.parseDouble(balance))) > 500) {
                    mApplyFreeTokenBtn.setEnabled(false);
                } else {
                    mApplyFreeTokenBtn.setEnabled(true);
                }

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void initViews(View view) {
        TextView mainNetWorkAddressLabTv = view.findViewById(R.id.mainNetWorkAddressLabTv);
        mMainNetWorkAddressValueTv = view.findViewById(R.id.mainNetWorkAddressValueTv);
        mSubNetAaddressValueTv = view.findViewById(R.id.subNetAaddressValueTv);
        mQRCodeIv = view.findViewById(R.id.QRCodeIv);
        mApplyFreeEthBtn = view.findViewById(R.id.applyFreeEthBtn);
        mApplyFreeTokenBtn = view.findViewById(R.id.applyFreeTokenBtn);
        TextView createAccountTv = view.findViewById(R.id.createAccountTv);
        TextView transferTv = view.findViewById(R.id.transferTv);
        TextView exportTv = view.findViewById(R.id.exportTv);
        currentCoinTypeTv = view.findViewById(R.id.currentCoinTypeTv);
        TextView supprotCoinTypeTv = view.findViewById(R.id.supprotCoinTypeTv);
        TextView versionTv = view.findViewById(R.id.versionTv);
        TextView updateAppTv = view.findViewById(R.id.updateAppTv);
        versionTv.setText(getString(R.string.current_version_name) + Utils.getVersionName(mActivity));
        updateAppTv.setText(getString(R.string.tab_setting_new_version));
        updateAppTv.setOnClickListener(this);
        versionTv.setOnClickListener(this);
        mApplyFreeEthBtn.setOnClickListener(this);
        mApplyFreeTokenBtn.setOnClickListener(this);
        mainNetWorkAddressLabTv.setOnClickListener(this);
        mMainNetWorkAddressValueTv.setOnClickListener(this);
        createAccountTv.setOnClickListener(this);
        transferTv.setOnClickListener(this);
        exportTv.setOnClickListener(this);
        mQRCodeIv.setOnClickListener(this);
        currentCoinTypeTv.setOnClickListener(this);
        supprotCoinTypeTv.setOnClickListener(this);
        mMainNetWorkAddressValueTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!TextUtils.isEmpty(mMainNetWorkAddressValueTv.getText().toString())) {
                    Utils.CopyToMemory(mActivity, mMainNetWorkAddressValueTv.getText().toString());
                }

                return false;
            }
        });


    }

    private void initData() {
        hopBalance();
        currentCoinTypeTv.setText(ExtendToken.CurSymbol);
        mMainNetWorkAddressValueTv.setText(WalletWrapper.MainAddress);
        mSubNetAaddressValueTv.setText(WalletWrapper.SubAddress);

        if (WalletWrapper.EthBalance > 0.05) {
            mApplyFreeEthBtn.setEnabled(false);
        } else {
            mApplyFreeEthBtn.setEnabled(true);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.QRCodeIv:
                ShowQRImageActivity.QRBitmap = Utils.QRStr2Bitmap(WalletWrapper.MainAddress);
                Intent intent = new Intent(mActivity, ShowQRImageActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, mQRCodeIv, "image").toBundle();
                startActivity(intent, bundle);
                break;
            case R.id.applyFreeEthBtn:
                applyFreeEth();
                break;
            case R.id.applyFreeTokenBtn:
                applyFreeToken();
                break;
            case R.id.createAccountTv:
                Utils.showOkOrCancelAlert(mActivity, R.string.tab_setting_replace_account_title, R.string.tab_setting_replace_msg, new AlertDialogOkCallBack() {
                    @Override
                    public void onClickOkButton(String parameter) {

                        if (HopService.IsRunning) {
                            HopService.stop();
                        }

                        Intent createIntent = new Intent(mActivity, CreateAccountActivity.class);
                        createIntent.putExtra(IntentKey.SHOW_BACK_BUTTON, true);
                        startActivity(createIntent);
                    }
                });
                break;
            case R.id.transferTv:
                startActivity(new Intent(mActivity, TransferOutActivity.class));
                break;
            case R.id.exportTv:
                exportWallet();
                break;
            case R.id.currentCoinTypeTv:
            case R.id.supprotCoinTypeTv:
                startActivity(new Intent(mActivity, SupportedCurrenciesActivity.class));
                break;
            case R.id.versionTv:
            case R.id.updateAppTv:
                Utils.openAppDownloadPage(mActivity);
                break;
            default:
                break;

        }
    }


    private void applyFreeToken() {
        if (TextUtils.isEmpty(WalletWrapper.MainAddress)) {
            Utils.toastTips(getResources().getString(R.string.please_create_account));
            return;
        }
        if (WalletWrapper.HopBalance >= 1000) {
            Utils.toastTips(getResources().getString(R.string.apply_free_token_des));
            return;
        }

        mActivity.showDialogFragment(getString(R.string.creating_tx), false);
        mTabSettingModel.getFreeHop(WalletWrapper.MainAddress, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                mActivity.dismissDialogFragment();
                Utils.toastException(mActivity, e, Constants.REQUEST_FREE_HOP_ERROR);
            }

            @Override
            public void onSuccess(String tx) {
                String msg = "\nHop TX:[" + tx + "]";
                mActivity.showDialogFragment(msg, false);
                queryTxStatus(tx, true);
            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void queryTxStatus(final String tx, final boolean getFreeHop) {
        mTabSettingModel.queryTxProcessStatus(tx, new ResultCallBack<Boolean>() {
            @Override
            public void onError(Throwable e) {
                mActivity.dismissDialogFragment();
                if (getFreeHop) {
                    Utils.toastException(mActivity, e, Constants.REQUEST_FREE_HOP_ERROR);
                } else {
                    Utils.toastException(mActivity, e, Constants.REQUEST_FREE_ETH_ERROR);
                }
            }

            @Override
            public void onSuccess(Boolean isSuccess) {
                String content;
                if (getFreeHop) {
                    content = "Hop Tx:[" + tx + "]";
                    mApplyFreeTokenBtn.setEnabled(false);
                } else {
                    content = "Eth Tx:[" + tx + "]";
                    mApplyFreeEthBtn.setEnabled(false);
                }
                MessageDialog.show(mActivity, getString(R.string.tips), content, getString(R.string.sure));
                mActivity.dismissDialogFragment();
                ((MainActivity) mActivity).loadWallet();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void applyFreeEth() {
        if (TextUtils.isEmpty(WalletWrapper.MainAddress)) {
            Utils.toastTips(getResources().getString(R.string.please_create_account));
            return;
        }
        if (WalletWrapper.EthBalance > 0.05) {
            Utils.toastTips(getResources().getString(R.string.apply_free_token_des));
            return;
        }
        mActivity.showDialogFragment(getString(R.string.creating_tx), false);
        mTabSettingModel.getFreeEth(WalletWrapper.MainAddress, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                mActivity.dismissDialogFragment();
                Utils.toastException(mActivity, e, Constants.REQUEST_FREE_ETH_ERROR);
            }

            @Override
            public void onSuccess(String tx) {
                queryTxStatus(tx, false);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void exportWallet() {
        if (!Utils.checkStorage(mActivity)) {
            return;
        }

        if (WalletWrapper.MainAddress == "") {
            return;
        }

        final String accountData = WalletWrapper.WalletJsonData();
        if (accountData.equals("")) {
            Utils.toastTips(getString(R.string.empty_account));
            return;
        }

        mActivity.showDialogFragment();
        mTabSettingModel.exportAccount(mActivity.getContentResolver(), accountData, getString(R.string.pirate_account), new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                mActivity.dismissDialogFragment();
                Utils.toastTips(e.getMessage());
                TipDialog.show(mActivity, R.string.transfer_fail, TipDialog.TYPE.ERROR);
            }

            @Override
            public void onSuccess(String tx) {

            }

            @Override
            public void onComplete() {
                mActivity.dismissDialogFragment();
                TipDialog.show(mActivity, R.string.wallet_export_success, TipDialog.TYPE.SUCCESS);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadWalletSuccess(EventLoadWalletSuccess eventLoadWalletSuccess) {
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {

            case Constants.QUERY_TXSTATUS_TIME_OUT_CODE:
                mActivity.dismissDialogFragment();
                Utils.toastTips(getResources().getString(R.string.apply_timeout));
                break;
            case Constants.IMPORT_ACCOUNT_ERR_CODE:
                Utils.toastTips("err:" + msg.obj);
                break;
            default:
                break;
        }
        return false;
    }


}
