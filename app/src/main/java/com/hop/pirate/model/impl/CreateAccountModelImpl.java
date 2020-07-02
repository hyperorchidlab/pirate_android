package com.hop.pirate.model.impl;

import android.content.Context;


import com.hop.pirate.R;
import com.hop.pirate.base.BaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.CreateAccountModel;

import org.greenrobot.eventbus.EventBus;

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
 * @date :   2020/5/25 11:17 AM
 */
public class CreateAccountModelImpl extends BaseModel implements CreateAccountModel {
    @Override
    public void createAccount(final String password, final ResultCallBack<String> resultCallBack) {
        schedulers(Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                AndroidLib.newWallet(password);
                emitter.onComplete();
            }
        })).subscribe(new Observer<String>() {
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
    public void importWallet(final Context context, final String walletStr, final String password, final ResultCallBack<String> resultCallBack) {
        schedulers(Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                AndroidLib.importWallet(walletStr, password);
                emitter.onComplete();
            }
        })).subscribe(new Observer<String>() {
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
    public void removeAllSubscribe() {
        removeAllDisposable();
    }
}
