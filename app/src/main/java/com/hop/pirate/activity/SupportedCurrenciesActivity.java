package com.hop.pirate.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.hop.pirate.Constants;
import com.hop.pirate.R;
import com.hop.pirate.adapter.TokenAdapter;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.AlertDialogOkCallBack;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.SupportedCurrenciesModel;
import com.hop.pirate.model.bean.ExtendToken;
import com.hop.pirate.model.impl.SupportedCurrenciesModelImpl;
import com.hop.pirate.service.HopService;
import com.hop.pirate.service.WalletWrapper;
import com.hop.pirate.util.Utils;
import com.hop.pirate.util.WrapContentLinearLayoutManager;

import java.util.List;

public class SupportedCurrenciesActivity extends BaseActivity implements Handler.Callback, TokenAdapter.TokenAdapterItemClick {

    private SupportedCurrenciesModel mSupportedCurrenciesModel;
    public Handler mHandler = new Handler(this);
    private RecyclerView mTokenRecyclerView;
    public static List<ExtendToken> sExtendTokens;
    private TokenAdapter mTokenAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supported_currencies);
        mSupportedCurrenciesModel = new SupportedCurrenciesModelImpl();
        refreshTokens(sExtendTokens == null || sExtendTokens.size() == 0);
    }

    @Override
    public void initViews() {
        ((TextView) findViewById(R.id.titleTv)).setText(getString(R.string.supported_currencies));
        mTokenRecyclerView = findViewById(R.id.tokenRecyclerView);
        mTokenRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void initData() {
        mTokenAdapter = new TokenAdapter(SupportedCurrenciesActivity.this, SupportedCurrenciesActivity.this);
        mTokenRecyclerView.setAdapter(mTokenAdapter);
    }

    @Override
    public void cancelWaitDialog() {
        super.cancelWaitDialog();
        mSupportedCurrenciesModel.removeAllSubscribe();
    }

    public void refreshTokens(final boolean hasLoading) {
        if (hasLoading) {
            showDialogFragment();
        } else {
            mTokenAdapter.setTokenBeans(sExtendTokens);
        }
        mSupportedCurrenciesModel.getSupportedCurrencies(this, WalletWrapper.MainAddress, new ResultCallBack<List<ExtendToken>>() {
            @Override
            public void onError(Throwable e) {
                if (hasLoading) {
                    dismissDialogFragment();
                    Utils.toastException(SupportedCurrenciesActivity.this, e, Constants.REQUEST_SUPPORT_COINS_ERROR);
                }

            }

            @Override
            public void onSuccess(List<ExtendToken> extendTokens) {
                sExtendTokens = extendTokens;
                mTokenAdapter.setTokenBeans(sExtendTokens);
            }

            @Override
            public void onComplete() {
                if (hasLoading) {
                    dismissDialogFragment();
                }
            }
        });
    }


    @Override
    public boolean handleMessage(Message msg) {
        dismissDialogFragment();
        initData();
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mSupportedCurrenciesModel.removeAllSubscribe();
    }

    @Override
    public void itemClick(final ExtendToken extendToken) {
        Utils.showOkOrCancelAlert(SupportedCurrenciesActivity.this, R.string.tips,
                R.string.settting_change_token, new AlertDialogOkCallBack() {

                    @Override
                    public void onClickOkButton(String parameter) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ExtendToken.CurPaymentContract = extendToken.getPaymentContract();
                                ExtendToken.CurTokenI = extendToken.getTokenI();
                                ExtendToken.CurSymbol = extendToken.getSymbol();

                                Utils.saveData(Constants.CUR_PAYMENT_CONTRACT, extendToken.getPaymentContract());
                                Utils.saveData(Constants.CUR_TOKENI, extendToken.getTokenI());
                                Utils.saveData(Constants.CUR_SYMBOL, extendToken.getSymbol());
                                if (HopService.IsRunning) {
                                    HopService.stop();
                                }
                                exitApp();
                                startActivity(SplashActivity.class);

                            }
                        }).start();

                    }
                });
    }
}
