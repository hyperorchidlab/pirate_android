package com.hop.pirate.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hop.pirate.Constants;
import com.hop.pirate.PirateException;
import com.hop.pirate.R;
import com.hop.pirate.adapter.FlowSelectAdapter;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.dialog.PayPasswordDialog;
import com.hop.pirate.event.EventRechargeSuccess;
import com.hop.pirate.model.RechargeModel;
import com.hop.pirate.model.impl.RechargeModelImpl;
import com.hop.pirate.service.WalletWrapper;
import com.hop.pirate.util.Utils;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;

import org.greenrobot.eventbus.EventBus;

public class RechargePacketsActivity extends BaseActivity implements FlowSelectAdapter.RechargeFlowState {
    private static double AUTHORIZE_TOKEN = 4.2e8;
    private RechargeModel mRechargeModel;
    private RecyclerView mFlowRecyclerview;
    private String mPoolAddress;
    private TextView mHopAddressET;
    private TextView mHopBalanceTV;
    public static final String PoolKey = "PoolAddress";
    private TextView mMinePoolAddressTv;
    private TextView mHopAddressTitleTv;
    private TextView mHopCoinTv;
    String sysbol;
    private double tokenNO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPoolAddress = getIntent().getStringExtra(PoolKey);
        mRechargeModel = new RechargeModelImpl();
        setContentView(R.layout.activity_recharge_packets);
        sysbol = Utils.getString(Constants.CUR_SYMBOL, "HOP");
        initFlows();
    }

    private void initFlows() {
        showDialogFragment(R.string.waiting, false);
        mRechargeModel.getBytesPerToken(this, new ResultCallBack<Double>() {
            @Override
            public void onError(Throwable e) {
                dismissDialogFragment();
                Utils.toastException(RechargePacketsActivity.this, e, Constants.REQUEST_BUY_TESPER_TOKEN_ERROR);
            }

            @Override
            public void onSuccess(Double mBytesPerToken) {
                mFlowRecyclerview.setAdapter(new FlowSelectAdapter(RechargePacketsActivity.this, mBytesPerToken, RechargePacketsActivity.this));
            }

            @Override
            public void onComplete() {
                dismissDialogFragment();
            }
        });


    }

    @Override
    public void initViews() {
        mFlowRecyclerview = findViewById(R.id.flowRecyclerview);
        mMinePoolAddressTv = findViewById(R.id.minePoolAddressTv);
        mHopAddressTitleTv = findViewById(R.id.hopAddressTitleTv);
        mHopCoinTv = findViewById(R.id.hopCoinTv);

        mHopAddressET = findViewById(R.id.hopAddressET);
        mHopBalanceTV = findViewById(R.id.hopCoinNumberTv);

        mHopAddressET.setText(WalletWrapper.MainAddress);
        mHopBalanceTV.setText(Utils.ConvertCoin(WalletWrapper.HopBalance));

        ((TextView) findViewById(R.id.titleTv)).setText(getResources().getString(R.string.recharge_recharge_flow));


        mFlowRecyclerview.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
    }

    @Override
    public void initData() {
        mMinePoolAddressTv.setText(mPoolAddress);
        mHopAddressTitleTv.setText(Utils.TokenNameFormat(this, R.string.hop_address));
        mHopCoinTv.setText(Utils.TokenNameFormat(this, R.string.recharge_hop_coin));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRechargeModel != null) {
            mRechargeModel.removeAllSubscribe();
        }
    }


    @Override
    public void recharge(final double tokenNO) {
        this.tokenNO = tokenNO;
        if (WalletWrapper.EthBalance / Utils.COIN_DECIMAL < 0.0001) {
            Utils.toastTips(getString(R.string.eth_insufficient_balance));
            return;
        }
        if (WalletWrapper.HopBalance < tokenNO) {
            Utils.toastTips(String.format(getString(R.string.token_insufficient_balance), sysbol));
            return;
        }
        new PayPasswordDialog(this, new PayPasswordDialog.PasswordCallBack() {
            @Override
            public void callBack(String password) {
                openWallet(password);

            }
        }).show();

    }

    private void openWallet(String password) {
        showDialogFragment(R.string.open_wallet);
        mRechargeModel.openWallet(this, password, new ResultCallBack<Boolean>() {
            @Override
            public void onError(Throwable e) {
                dismissDialogFragment();
                Utils.toastException(RechargePacketsActivity.this, e, Constants.REQUEST_OPEN_WALLET_ERROR);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onComplete() {
                dismissDialogFragment();
                if (WalletWrapper.Approved / Math.pow(10, 18) >= tokenNO){
                    buyPacket();
                }else{
                    authorizeTokenSpend();
                }


            }
        });
    }

    public void authorizeTokenSpend() {
        showDialogFragment(R.string.approving, false);
        mRechargeModel.authorizeTokenSpend(this, AUTHORIZE_TOKEN, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                dismissDialogFragment();
                if (e instanceof PirateException) {
                    Utils.toastTips(e.getMessage());
                } else {
                    Utils.toastTips(getString(R.string.approve_error));
                }

            }

            @Override
            public void onSuccess(String tx) {
                dismissDialogFragment();
                showDialogFragment(getString(R.string.approving) + "\nat[" + tx + "]", false);
                queryTxStatus(tx, true);

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void buyPacket() {
        showDialogFragment(R.string.recharge_buy_packets, false);
        mRechargeModel.buyPacket(this, WalletWrapper.MainAddress, mPoolAddress, tokenNO, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof PirateException) {
                    Utils.toastTips(e.getMessage());
                } else {
                    Utils.toastTips(getString(R.string.recharge_buy_packets_error));
                }
                dismissDialogFragment();
            }

            @Override
            public void onSuccess(String tx) {
                dismissDialogFragment();
                showDialogFragment(getString(R.string.recharge_buy_packets) + "\nat[" + tx + "]", false);
                queryTxStatus(tx, false);


            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void queryTxStatus(final String tx, final boolean isProve) {
        mRechargeModel.queryTxProcessStatus(tx, new ResultCallBack<Boolean>() {
            @Override
            public void onError(Throwable e) {
                dismissDialogFragment();
                Utils.toastTips(getString(R.string.blockchain_time_out));
                if (!isProve) {
                    EventBus.getDefault().post(new EventRechargeSuccess());
                }
            }

            @Override
            public void onSuccess(Boolean isSuccess) {
                if (isProve) {
                    dismissDialogFragment();
                    buyPacket();
                } else {
                    showDialogFragment(getString(R.string.recharge_buy_packets), false);
                    EventBus.getDefault().post(new EventRechargeSuccess());
                    waitSyncSubPool();

                }
            }

            @Override
            public void onComplete() {
            }
        });
    }

    private void waitSyncSubPool() {
        mRechargeModel.syncPool(mPoolAddress, new ResultCallBack<Boolean>() {
            @Override
            public void onError(Throwable e) {
                Utils.toastTips(getResources().getString(R.string.recharge_sync_pool_failed));
            }

            @Override
            public void onSuccess(Boolean syncSuccess) {
                dismissDialogFragment();
                if(syncSuccess){
                    String content = getString(R.string.recharge_sync_pool_success);
                    MessageDialog.show(RechargePacketsActivity.this, getString(R.string.tips), content, getString(R.string.sure)).setOnOkButtonClickListener(new OnDialogButtonClickListener() {

                        @Override
                        public boolean onClick(BaseDialog baseDialog, View v) {
                            setResult(RESULT_OK);
                            finish();
                            return false;
                        }
                    });
                }else{
                    Utils.toastTips(getResources().getString(R.string.recharge_sync_pool_failed));
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }


    @Override
    public void cancelWaitDialog() {
        super.cancelWaitDialog();
        mRechargeModel.removeAllSubscribe();
    }
}
