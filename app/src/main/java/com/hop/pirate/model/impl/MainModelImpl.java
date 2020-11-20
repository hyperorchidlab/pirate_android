package com.hop.pirate.model.impl;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.hop.pirate.Constants;
import com.hop.pirate.PirateException;
import com.hop.pirate.R;
import com.hop.pirate.activity.MainActivity;
import com.hop.pirate.base.BaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.MainModel;
import com.hop.pirate.model.bean.ExtendToken;
import com.hop.pirate.model.bean.WalletBean;
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
                ExtendToken.CurPaymentContract = Utils.getString(Constants.CUR_PAYMENT_CONTRACT, Constants.MICROPAY_SYS_ADDRESS);
                ExtendToken.CurTokenI = Utils.getString(Constants.CUR_TOKEN, Constants.TOKEN_ADDRESS);
                ExtendToken.CurSymbol = Utils.getString(Constants.CUR_SYMBOL, Constants.DEFAULT_SYMBOL);

                InputStream ipInput = context.getResources().openRawResource(R.raw.bypass);
                String bypassIPs = IOUtils.toString(ipInput);
                String newDns = Utils.getString(Constants.NEW_DNS, Constants.DNS);
                AndroidLib.initSystem(bypassIPs, Utils.getBaseDir(context), ExtendToken.CurTokenI, ExtendToken.CurPaymentContract, Constants.ETH_API_URL, newDns, hopDelegate);
                AndroidLib.initProtocol();
                AndroidLib.startProtocol();
                Log.d("~~~", "~~~~~~~~~~~~~~~~~~startProtocol");
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
                if (TextUtils.isEmpty(jsonStr)) {
                    emitter.onError(new PirateException(context.getString(R.string.wallet_read_failed)));
                    return;
                }
                WalletBean walletBean = new Gson().fromJson(jsonStr, WalletBean.class);
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
    public void syncVersion() {
        schedulers(Observable.create(new ObservableOnSubscribe<WalletBean>() {
            @Override
            public void subscribe(ObservableEmitter<WalletBean> emitter) throws Exception {
//                AndroidLib.syncVer();
                emitter.onComplete();
            }
        })).subscribe(new Observer<WalletBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscribe(d);
            }

            @Override
            public void onNext(WalletBean walletBean) {
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("syncAllPoolsData :onError" + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("syncAllPoolsData :onComplete");
            }
        });
    }

    @Override
    public void syncSubPoolsData() {
        schedulers(Observable.create(new ObservableOnSubscribe<WalletBean>() {
            @Override
            public void subscribe(ObservableEmitter<WalletBean> emitter) throws Exception {
                AndroidLib.syncSubPoolsData();
                emitter.onComplete();
            }
        })).subscribe(new Observer<WalletBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscribe(d);
            }

            @Override
            public void onNext(WalletBean walletBean) {
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("syncSubPoolsData :onError" + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("syncSubPoolsData :onComplete");
            }
        });
    }


    @Override
    public void removeAllSubscribe() {
        removeAllDisposable();
    }
}
