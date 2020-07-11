package com.hop.pirate.model.impl;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hop.pirate.PError;
import com.hop.pirate.R;
import com.hop.pirate.base.BaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.SupportedCurrenciesModel;
import com.hop.pirate.model.bean.ExtendToken;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import androidLib.AndroidLib;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 6:18 PM
 */
public class SupportedCurrenciesModelImpl extends BaseModel implements SupportedCurrenciesModel {
    @Override
    public void getSupportedCurrencies(final Context context, final String address, final ResultCallBack<List<ExtendToken>> resultCallBack) {
        schedulers(Observable.create(new ObservableOnSubscribe<List<ExtendToken>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ExtendToken>> emitter) throws Exception {
                String jsonStr = AndroidLib.extendTokens(address);
                if (TextUtils.isEmpty(jsonStr)) {
                    emitter.onError(new PError(context.getString(R.string.get_data_failed)));
                    return;
                }
                Type type = new TypeToken<List<ExtendToken>>() {
                }.getType();

                List<ExtendToken> extendTokens = new Gson().fromJson(jsonStr, type);
                Iterator<ExtendToken> iterator = extendTokens.iterator();

                while (iterator.hasNext()) {
                    ExtendToken next = iterator.next();
                    if (next.getPaymentContract().equals("0x0000000000000000000000000000000000000000")) {
                        iterator.remove();
                    }
                }

                emitter.onNext(extendTokens);
                emitter.onComplete();
            }
        })).subscribe(new Observer<List<ExtendToken>>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscribe(d);
            }

            @Override
            public void onNext(List<ExtendToken> extendTokens) {
                resultCallBack.onSuccess(extendTokens);
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
