package com.hop.pirate.model.impl;

import android.content.Context;
import android.text.TextUtils;

import com.hop.pirate.Constants;
import com.hop.pirate.PError;
import com.hop.pirate.R;
import com.hop.pirate.activity.RechargePacketsActivity;
import com.hop.pirate.base.BaseModel;
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
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/30 2:31 PM
 */
public class RechargeModelImpl extends BaseModel implements RechargeModel {

    private Disposable mDisposable;

    @Override
    public void authorizeTokenSpend(final Context context, final double tokenNO, final ResultCallBack<String> resultCallBack) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String approveTX = AndroidLib.authorizeTokenSpend(tokenNO);

                if (TextUtils.isEmpty(approveTX)) {
                    emitter.onError(new PError(context.getString(R.string.password_eror)));
                } else {
                    emitter.onNext(approveTX);
                    emitter.onComplete();
                }

            }
        }).timeout(Constants.BLOCKCHAIN_TIME_OUT, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
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
                String buyTX = AndroidLib.buyPacket(userAddress, pollAddress, tokenNO);
                if (TextUtils.isEmpty(buyTX)) {
                    emitter.onError(new PError(context.getString(R.string.password_eror)));
                } else {
                    emitter.onNext(buyTX);
                    emitter.onComplete();
                }

            }
        }).timeout(Constants.BLOCKCHAIN_TIME_OUT, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
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
                if(!RechargePacketsActivity.isInitSysSeting){
                    AndroidLib.initSysSeting();
                    RechargePacketsActivity.isInitSysSeting=true;
                }
                String sysStr = AndroidLib.systemSettings();
                if (!sysStr.equals("")) {
                    JSONObject json = new JSONObject(sysStr);
                    double mBytesPerToken = json.optDouble("MBytesPerToken");
                    emitter.onNext(mBytesPerToken);
                    emitter.onComplete();
                } else {
                    emitter.onError(new PError(context.getString(R.string.get_data_failed)));
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

    private void queryTxProcess(final String tx, final ResultCallBack<Boolean> resultCallBack) {

    }

    @Override
    public void removeAllSubscribe() {

    }
}
