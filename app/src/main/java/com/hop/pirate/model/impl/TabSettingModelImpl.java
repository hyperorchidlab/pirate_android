package com.hop.pirate.model.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.text.TextUtils;

import com.hop.pirate.Constants;
import com.hop.pirate.PError;
import com.hop.pirate.base.BaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.callback.SaveQRCodeCallBack;
import com.hop.pirate.model.TabSettingModel;
import com.hop.pirate.util.Utils;

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
 * @date :   2020/5/27 9:09 AM
 */
public class TabSettingModelImpl extends BaseModel implements TabSettingModel {

    private Disposable mDisposable;

    @Override
    public void exportAccount(final ContentResolver cr, final String data, final String fileName, final ResultCallBack<String> resultCallBack) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                Utils.SaveStringQRCode(cr, data, fileName, new SaveQRCodeCallBack() {
                    @Override
                    public void save(String msg) {
                        emitter.onComplete();
                    }
                });


            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
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
    public void getFreeEth(final String address, final ResultCallBack<String> resultCallBack) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                String result = AndroidLib.applyFreeEth(address);
                if (result.startsWith("0x")) {
                    emitter.onNext(result);
                    emitter.onComplete();
                } else {
                    emitter.onError(new PError("apply error"));
                }


            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
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
    public void getFreeHop(final String address, final ResultCallBack<String> resultCallBack) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                String result = AndroidLib.applyFreeToken(address);
                if (result.startsWith("0x")) {
                    emitter.onNext(result);
                    emitter.onComplete();
                } else {
                    emitter.onError(new PError("apply error"));
                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
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
    public void getHopBalance(final String address, final ResultCallBack<String> resultCallBack) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                String balance = AndroidLib.tokenBalance(address);
                if (!TextUtils.isEmpty(balance)) {
                    emitter.onNext(balance);
                    emitter.onComplete();
                } else {
                    emitter.onError(new PError("query  balance error"));
                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscribe(d);
                    }

                    @Override
                    public void onNext(String balance) {
                        resultCallBack.onSuccess(balance);
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
