package com.hop.pirate.model.impl;

import android.content.Context;
import android.text.TextUtils;

import com.hop.pirate.Constants;
import com.hop.pirate.base.BaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.PurchasedPoolModel;
import com.hop.pirate.model.bean.OwnPool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
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
 * @date :   2020/5/26 4:07 PM
 */
public class PurchasedPoolModelImpl extends BaseModel implements PurchasedPoolModel {


    @Override
    public void getPoolDataOfUser(final Context context, final String address, final ResultCallBack<List<OwnPool>> resultCallBack) {
        Observable.create(new ObservableOnSubscribe<List<OwnPool>>() {
            @Override
            public void subscribe(ObservableEmitter<List<OwnPool>> emitter) throws Exception {
                List<OwnPool> minePoolBeans = new ArrayList<>();
                AndroidLib.syncSubPoolsData();
                String poolsStr = AndroidLib.getSubPools();

                if (!TextUtils.isEmpty(poolsStr) && !poolsStr.equals("null")) {
                    JSONArray jsonArray = new JSONArray(poolsStr);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject p = jsonArray.getJSONObject(i);
                        if (p == null) {
                            continue;
                        }
                        OwnPool bean = new OwnPool();

                        bean.setAddress(p.optString("MainAddr"));
                        bean.setName(p.optString("Name"));
                        bean.setEmail(p.optString("Email"));
                        bean.setMortgageNumber(p.optDouble("GTN"));
                        bean.setWebsiteAddress(p.optString("Url"));
                        minePoolBeans.add(bean);
                    }


                }

                emitter.onNext(minePoolBeans);
                emitter.onComplete();
            }
        }).timeout(Constants.TIME_OUT, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<OwnPool>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscribe(d);
                    }

                    @Override
                    public void onNext(List<OwnPool> walletBean) {
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
    public void removeAllSubscribe() {
        removeAllDisposable();
    }
}
