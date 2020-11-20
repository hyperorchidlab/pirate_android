package com.hop.pirate.model.impl;

import android.content.Context;
import android.text.TextUtils;

import com.hop.pirate.Constants;
import com.hop.pirate.PirateException;
import com.hop.pirate.R;
import com.hop.pirate.activity.MainActivity;
import com.hop.pirate.activity.RechargePacketsActivity;
import com.hop.pirate.base.WaitTxBaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.RechargeModel;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import androidLib.AndroidLib;
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
 * @date :   2020/5/30 2:31 PM
 */
public class RechargeModelImpl extends WaitTxBaseModel implements RechargeModel {


    @Override
    public void authorizeTokenSpend(final Context context, final double tokenNO, final ResultCallBack<String> resultCallBack) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String approveTx = AndroidLib.authorizeTokenSpend(tokenNO);

                if (TextUtils.isEmpty(approveTx)) {
                    emitter.onError(new PirateException(context.getString(R.string.password_error)));
                } else {
                    emitter.onNext(approveTx);
                    emitter.onComplete();
                }

            }
        }).timeout(Constants.BLOCK_CHAIN_TIME_OUT, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
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
    public void buyPacket(final Context context, final String userAddress, final String pollAddress, final double tokenNO, final ResultCallBack<String> resultCallBack) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String buyTx = AndroidLib.buyPacket(userAddress, pollAddress, tokenNO);
                if (TextUtils.isEmpty(buyTx)) {
                    emitter.onError(new PirateException(context.getString(R.string.password_error)));
                } else {
                    emitter.onNext(buyTx);
                    emitter.onComplete();
                }

            }
        }).timeout(Constants.BLOCK_CHAIN_TIME_OUT, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
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
    public void getBytesPerToken(final Context context, final ResultCallBack<Double> resultCallBack) {
        schedulers(Observable.create(new ObservableOnSubscribe<Double>() {
            @Override
            public void subscribe(ObservableEmitter<Double> emitter) throws Exception {

                String sysStr = AndroidLib.systemSettings();
                if (!TextUtils.isEmpty(sysStr)) {
                    JSONObject json = new JSONObject(sysStr);
                    double mBytesPerToken = json.optDouble("MBytesPerToken");
                    emitter.onNext(mBytesPerToken);
                    emitter.onComplete();
                } else {
                    emitter.onError(new PirateException(context.getString(R.string.get_data_failed)));
                }

            }
        })).subscribe(new Observer<Double>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscribe(d);
            }

            @Override
            public void onNext(Double mBytesPerToken) {
                resultCallBack.onSuccess(mBytesPerToken);
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
    public void openWallet(Context context, final String password, final ResultCallBack<Boolean> resultCallBack) {
        schedulers(Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                AndroidLib.openWallet(password);
                emitter.onNext(true);
                emitter.onComplete();

            }
        })).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscribe(d);
            }

            @Override
            public void onNext(Boolean isOpen) {
                resultCallBack.onSuccess(isOpen);
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
    public void queryTxProcessStatus(final String tx, final ResultCallBack<Boolean> resultCallBack) {
        queryTxStatus(tx, resultCallBack);

    }

    @Override
    public void syncPool(final String poolAddress, final ResultCallBack<Boolean> resultCallBack) {
        schedulers(Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {

                boolean syncStatus=AndroidLib.waitSubPool(poolAddress);
                emitter.onNext(syncStatus);
                emitter.onComplete();

            }
        })).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscribe(d);
            }

            @Override
            public void onNext(Boolean syncStatus) {
                resultCallBack.onSuccess(syncStatus);
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

    }
}
