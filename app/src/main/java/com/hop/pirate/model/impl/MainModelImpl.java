package com.hop.pirate.model.impl;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.hop.pirate.Constants;
import com.hop.pirate.PError;
import com.hop.pirate.R;
import com.hop.pirate.base.BaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.MainModel;
import com.hop.pirate.model.bean.ExtendToken;
import com.hop.pirate.model.bean.WalletBean;
import com.hop.pirate.service.MicroChainService;
import com.hop.pirate.service.WalletWrapper;
import com.hop.pirate.util.Utils;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;

import androidLib.AndroidLib;
import androidLib.HopDelegate;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 2:47 PM
 */
public class MainModelImpl extends BaseModel implements MainModel {
    @Override
    public void initService(final Context context, final HopDelegate hopDelegate, final ResultCallBack<String> resultCallBack) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                ExtendToken.CurPaymentContract = Utils.getString(Constants.CUR_PAYMENT_CONTRACT, Constants.MICROPAY_SYSADDRESS);
                ExtendToken.CurTokenI = Utils.getString(Constants.CUR_TOKENI, Constants.TOKEN_ADDRESS);
                ExtendToken.CurSymbol = Utils.getString(Constants.CUR_SYMBOL, Constants.DEFAULT_SYMBOL);

                InputStream ipInput = context.getResources().openRawResource(R.raw.bypass);
                String bypassIPs = IOUtils.toString(ipInput);
                AndroidLib.initSystem(bypassIPs, Utils.getBaseDir(context), ExtendToken.CurTokenI, ExtendToken.CurPaymentContract, Constants.ETH_API_URL, Constants.DNS, hopDelegate);
                AndroidLib.initProtocol();
                AndroidLib.syncAllPoolsData();
                AndroidLib.initSysSeting();
                AndroidLib.syncPoolsAndUserData();
                Intent i = new Intent(context, MicroChainService.class);
                context.startService(i);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscribe(d);
                    }

                    @Override
                    public void onNext(String tx) {
                        resultCallBack.onSuccess(tx);
                    }

                    @Override
                    public void onError(Throwable e) {
                        resultCallBack.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        resultCallBack.onComplete();
                    }
                });
    }

    @Override
    public void getWalletInfo(final Context context, final ResultCallBack<WalletBean> resultCallBack) {
        schedulers(Observable.create(new ObservableOnSubscribe<WalletBean>() {
            @Override
            public void subscribe(ObservableEmitter<WalletBean> emitter) throws Exception {
                String jsonStr = AndroidLib.walletInfo();
                if (jsonStr.equals("")) {
                    emitter.onError(new PError(context.getString(R.string.wallet_read_failed)));
                    return;
                }
                WalletBean walletBean = new Gson().fromJson(jsonStr, WalletBean.class);
                WalletWrapper.MainAddress = walletBean.getMain();
                WalletWrapper.SubAddress = walletBean.getSub();
                WalletWrapper.EthBalance = walletBean.getEth();
                WalletWrapper.HopBalance = walletBean.getHop();
                WalletWrapper.Approved = walletBean.getApproved();
                emitter.onNext(walletBean);
                emitter.onComplete();
            }
        })).subscribe(new Observer<WalletBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscribe(d);
            }

            @Override
            public void onNext(WalletBean walletBean) {
                resultCallBack.onSuccess(walletBean);
            }

            @Override
            public void onError(Throwable e) {
                resultCallBack.onError(e);
            }

            @Override
            public void onComplete() {
                resultCallBack.onComplete();
            }
        });
    }

    @Override
    public void initSysSeting(final ResultCallBack<String> resultCallBack) {
        schedulers(Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                AndroidLib.initSysSeting();
                emitter.onNext("");
                emitter.onComplete();
            }
        })).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscribe(d);
            }

            @Override
            public void onNext(String str) {
                resultCallBack.onSuccess(str);
            }

            @Override
            public void onError(Throwable e) {
                resultCallBack.onError(e);
            }

            @Override
            public void onComplete() {
                resultCallBack.onComplete();
            }
        });
    }

    @Override
    public void removeAllSubscribe() {
        removeAllDisposable();
    }
}
