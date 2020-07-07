package com.hop.pirate.model.impl;

import com.hop.pirate.Constants;
import com.hop.pirate.base.BaseModel;
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
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/27 12:56 PM
 */
public class TransferModelImpl extends BaseModel implements TransferModel {
    @Override
    public void transferEth(final String password, final String toAddress, final String num, final ResultCallBack<String> resultCallBack) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                String s = AndroidLib.transferEth(password, toAddress, Double.parseDouble(num));
                emitter.onNext(s);
                emitter.onComplete();

            }
        }).timeout(Constants.BLOCKCHAIN_TIME_OUT, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
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
        }).timeout(Constants.BLOCKCHAIN_TIME_OUT, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
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

    private Disposable mDisposable;

    @Override
    public void queryTxProcessStatus(final String tx, final ResultCallBack<Boolean> resultCallBack) {
        final Observable<Boolean> schedulers = schedulers(Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean isSuccess = AndroidLib.txProcessStatus(tx);
                if (isSuccess) {
                    mDisposable.dispose();
                }
                emitter.onNext(isSuccess);
                emitter.onComplete();

            }
        }));

        mDisposable = Observable.interval(2, TimeUnit.SECONDS).timeout(Constants.BLOCKCHAIN_TIME_OUT, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        schedulers.subscribe(new Observer<Boolean>() {
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

                });

    }

    @Override
    public void removeAllSubscribe() {
        removeAllDisposable();
    }
}
