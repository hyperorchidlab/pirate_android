package com.hop.pirate.model.impl;

import android.content.Context;
import android.text.TextUtils;

import com.hop.pirate.base.BaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.TabHomeModel;
import com.hop.pirate.model.bean.UserAccountData;

import org.json.JSONObject;

import androidLib.AndroidLib;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/6/4 12:11 PM
 */
public class TabHomeModelImpl extends BaseModel implements TabHomeModel {
    @Override
    public void getUserDataOfPool(final String user, final String pool, final ResultCallBack<UserAccountData> resultCallBack) {
        schedulers(Observable.create(new ObservableOnSubscribe<UserAccountData>() {
            @Override
            public void subscribe(ObservableEmitter<UserAccountData> emitter) throws Exception {
                String jsonStr = AndroidLib.userDataOfPool(user, pool);
                if (TextUtils.isEmpty(jsonStr)) {
                    emitter.onNext(new UserAccountData());
                    emitter.onComplete();
                    return;
                }
                JSONObject obj = new JSONObject(jsonStr);
                UserAccountData bean = new UserAccountData();
                bean.setUser(user);
                bean.setPool(pool);
                bean.setInRecharge(obj.optDouble("charging"));

                JSONObject ua = obj.optJSONObject("ua");
                bean.setExpire(ua.optString("expire"));
                bean.setNonce(ua.optInt("nonce"));
                bean.setToken(ua.optDouble("balance"));
                bean.setPackets(ua.optDouble("reminder"));

                bean.setEpoch(ua.optInt("epoch"));
                bean.setCredit(ua.optDouble("credit"));
                bean.setMicroNonce(ua.optInt("microNonce"));
                emitter.onNext(bean);
                emitter.onComplete();
            }
        })).subscribe(new Observer<UserAccountData>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscribe(d);
            }

            @Override
            public void onNext(UserAccountData walletBean) {
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
    public void openWallet(final Context context, final String password, final ResultCallBack<Boolean> resultCallBack) {
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
    public void removeAllSubscribe() {
        removeAllDisposable();
    }
}
