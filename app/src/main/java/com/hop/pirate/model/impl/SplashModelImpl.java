package com.hop.pirate.model.impl;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hop.pirate.PirateException;
import com.hop.pirate.base.BaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.SplashModel;
import com.hop.pirate.model.bean.AppVersionBean;
import com.hop.pirate.util.AccountUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeoutException;

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

                String walletJson = AccountUtils.loadWallet(context);
                if (TextUtils.isEmpty(walletJson)) {
                    emitter.onError(new PirateException("no wallet"));
                } else {
                    emitter.onNext(walletJson);
                    emitter.onComplete();
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
    public void checkVersion(Context context, final ResultCallBack<AppVersionBean> resultCallBack) {
        Observable.create(new ObservableOnSubscribe<AppVersionBean>() {
            @Override
            public void subscribe(ObservableEmitter<AppVersionBean> emitter) throws Exception {

                URL url = new URL("https://www.fastmock.site/mock/c30676a124bcbd7bfa4d0722a374f899/pirate/api/check_new_version");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String sTempOneLine;
                    while ((sTempOneLine = bufferedReader.readLine()) != null) {
                        stringBuilder.append(sTempOneLine);
                    }

                    AppVersionBean versionBean = new Gson().fromJson(stringBuilder.toString(), AppVersionBean.class);
                    emitter.onNext(versionBean);
                    emitter.onComplete();
                } else {
                    emitter.onError(new TimeoutException());
                }


            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppVersionBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscribe(d);
                    }

                    @Override
                    public void onNext(AppVersionBean versionBean) {
                        resultCallBack.onSuccess(versionBean);
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
