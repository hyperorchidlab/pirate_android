package com.hop.pirate.model.impl;

import com.hop.pirate.Constants;
import com.hop.pirate.base.WaitTxBaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.TransferModel;

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
 * @date :   2020/5/27 12:56 PM
 */
public class TransferModelImpl extends WaitTxBaseModel implements TransferModel {
    @Override
    public void transferEth(final String password, final String toAddress, final String num, final ResultCallBack<String> resultCallBack) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                String s = AndroidLib.transferEth(password, toAddress, Double.parseDouble(num));
                emitter.onNext(s);
                emitter.onComplete();

            }
        }).timeout(Constants.BLOCK_CHAIN_TIME_OUT, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscribe(d);
            }

            @Override
            public void onNext(String data) {
                resultCallBack.onSuccess(data);
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
    public void transferToken(final String password, final String toAddress, final String num, final ResultCallBack<String> resultCallBack) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                String s = AndroidLib.transferToken(password, toAddress, Double.parseDouble(num));
                emitter.onNext(s);
                emitter.onComplete();

            }
        }).timeout(Constants.BLOCK_CHAIN_TIME_OUT, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscribe(d);
            }

            @Override
            public void onNext(String data) {
                resultCallBack.onSuccess(data);
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
    public void removeAllSubscribe() {
        removeAllDisposable();
    }
}
