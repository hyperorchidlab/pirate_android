package com.hop.pirate.model.impl;

import android.content.Context;

import com.hop.pirate.PError;
import com.hop.pirate.base.BaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.SplashModel;
import com.hop.pirate.util.Utils;

import java.io.File;

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
 * @date :   2020/5/26 11:10 AM
 */
public class SplashModelImpl extends BaseModel implements SplashModel {
    @Override
    public void loadWallet(final Context context, final ResultCallBack<String> resultCallBack) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                 File file = new File(Utils.getBaseDir(context)+"/wallet.json");
                if (file.exists()){
                    emitter.onComplete();
                }else{
                    emitter.onError(new PError("no wallet"));
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
