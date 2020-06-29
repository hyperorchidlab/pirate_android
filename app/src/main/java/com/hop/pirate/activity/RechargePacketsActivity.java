package com.hop.pirate.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hop.pirate.Constants;
import com.hop.pirate.PError;
import com.hop.pirate.R;
import com.hop.pirate.adapter.FlowSelectAdapter;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.dialog.PayPasswordDialog;
import com.hop.pirate.event.EventShowTabHome;
import com.hop.pirate.model.RechargeModel;
import com.hop.pirate.model.bean.MinePoolBean;
import com.hop.pirate.model.impl.RechargeModelImpl;
import com.hop.pirate.service.WalletWrapper;
import com.hop.pirate.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import androidLib.AndroidLib;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class RechargePacketsActivity extends BaseActivity implements FlowSelectAdapter.RechargeFlowState {
    private RechargeModel mRechargeModel;
    private RecyclerView mFlowRecyclerview;
    private ImageView mRechargeStateMaskIv;
    private GifImageView mRechargeStateIv;
    private GifImageView rechargeStateTopIv;
    private TextView mRechargeStateTitleTv;
    private TextView mRechargeStateDesTv;
    private TextView mRechargeStateOperationTv;
    private ProgressBar mRechargeProgress;
    private int rechargeState;
    private String PoolAddress;
    private EditText mHopAddressET;
    private TextView mHopBalanceTV;
    public static final String PoolKey = "PoolAddress";
    private TextView mMinePoolAddressTv;
    private TextView mHopAddressTitleTv;
    private TextView mHopCoinTv;
    String sysbol;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PoolAddress = getIntent().getStringExtra(PoolKey);
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
                Utils.toastException(RechargePacketsActivity.this, e, Constants.REQUEST_BYTESPERTOKEN_ERROR);
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
        mRechargeStateMaskIv = findViewById(R.id.rechargeStateMaskIv);
        mRechargeStateIv = findViewById(R.id.rechargeStateBottomIv);
        rechargeStateTopIv = findViewById(R.id.rechargeStateTopIv);
        mRechargeStateTitleTv = findViewById(R.id.rechargeStateTitleTv);
        mRechargeStateDesTv = findViewById(R.id.rechargeStateDesTv);
        mRechargeStateOperationTv = findViewById(R.id.rechargeStateOperationTv);
        mRechargeProgress = findViewById(R.id.rechargeProgress);
        mMinePoolAddressTv = findViewById(R.id.minePoolAddressTv);
        mHopAddressTitleTv = findViewById(R.id.hopAddressTitleTv);
        mHopCoinTv = findViewById(R.id.hopCoinTv);

        mHopAddressET = findViewById(R.id.hopAddressET);
        mHopBalanceTV = findViewById(R.id.hopCoinNumberTv);

        mHopAddressET.setText(WalletWrapper.MainAddress);
        mHopBalanceTV.setText(Utils.ConvertCoin(WalletWrapper.HopBalance));

        ((TextView) findViewById(R.id.titleTv)).setText(getResources().getString(R.string.recharge_recharge_flow));

        mRechargeStateOperationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        mRechargeStateMaskIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mRechargeStateIv.setBackgroundResource(R.drawable.recharge_authorization_bottom);
        rechargeStateTopIv.setBackgroundResource(R.drawable.recharge_authorization_top);
        mFlowRecyclerview.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
    }

    @Override
    public void initData() {
        mMinePoolAddressTv.setText(PoolAddress);
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


    private void setTransferStatusBg(int transfer_error) {
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), transfer_error);
            mRechargeStateIv.setBackground(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recharge(final double tokenNO) {
        if (WalletWrapper.EthBalance/Utils.CoinDecimal <0.0001) {
            Utils.toastTips(getString(R.string.eth_insufficient_balance));
            return;
        }
        if (WalletWrapper.HopBalance < tokenNO) {
            Utils.toastTips(String.format(getString(R.string.token_insufficient_balance),sysbol));
            return;
        }
        new PayPasswordDialog(this, new PayPasswordDialog.PasswordCallBack() {
            @Override
            public void callBack(String password) {
                openWallet(password, tokenNO);

            }
        }).show();

    }

    private void openWallet(String password, final double tokenNO) {
        showDialogFragment(R.string.open_walet);
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
                authorizeTokenSpend(tokenNO);

            }
        });
    }

    public void authorizeTokenSpend(final double tokenNO) {
        visiable(mRechargeStateMaskIv, mRechargeProgress, mRechargeStateTitleTv, mRechargeStateIv, rechargeStateTopIv);

        mRechargeModel.authorizeTokenSpend(this, tokenNO, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof PError) {
                    Utils.toastTips(e.getMessage());
                    gone(mRechargeStateMaskIv, mRechargeProgress, mRechargeStateTitleTv, mRechargeStateIv, rechargeStateTopIv);
                } else {
                    setTransferStatusBg(R.drawable.transfer_error);
                    mRechargeStateTitleTv.setText(getResources().getString(R.string.blockchain_time_out));
                    mRechargeStateOperationTv.setText(getResources().getString(R.string.back));
                    gone(mRechargeProgress, rechargeStateTopIv);
                    visiable(mRechargeStateOperationTv);
                }

            }

            @Override
            public void onSuccess(String s) {
                setTransferStatusBg(R.drawable.rechargeing);
                gone(rechargeStateTopIv);
                mRechargeStateTitleTv.setText("Approve System Success!");
            }

            @Override
            public void onComplete() {
                buyPacket(tokenNO);

            }
        });
    }

    private void buyPacket(double tokenNO) {
        mRechargeModel.buyPacket(this, WalletWrapper.MainAddress, PoolAddress, tokenNO, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof PError) {
                    Utils.toastTips(e.getMessage());
                    gone(mRechargeStateMaskIv, mRechargeProgress, mRechargeStateTitleTv, mRechargeStateIv, rechargeStateTopIv);
                } else {
                    setTransferStatusBg(R.drawable.transfer_error);
                    mRechargeStateTitleTv.setText(getResources().getString(R.string.blockchain_time_out));
                    mRechargeStateOperationTv.setText(getResources().getString(R.string.back));
                    gone(mRechargeProgress, rechargeStateTopIv);
                    visiable(mRechargeStateOperationTv);
                }
            }

            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onComplete() {
                Utils.toastTips(getString(R.string.recharge_success));
                MinePoolBean.syncPoolsAndUserData();
                EventBus.getDefault().postSticky(new EventShowTabHome());
                finish();
            }
        });
    }


    @Override
    public void cancelWaitDialog() {
        super.cancelWaitDialog();
        mRechargeModel.removeAllSubscribe();
    }
}
