package com.hop.pirate.model.impl;

import android.text.TextUtils;

import com.hop.pirate.Constants;
import com.hop.pirate.base.BaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.TabWalletModel;
import com.hop.pirate.model.bean.MinePoolBean;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
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
public class TabWalletModelImpl extends BaseModel implements TabWalletModel {


    @Override
    public void getPoolDataOfUser(final int currentPoolNum, final String address, final ResultCallBack<List<MinePoolBean>> resultCallBack) {
        Observable.create(new ObservableOnSubscribe<List<MinePoolBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MinePoolBean>> emitter) throws Exception {
                if(currentPoolNum==0){
                    AndroidLib.initSysSeting();
                }
                String poolsStr = AndroidLib.poolDataOfUser(address);
                List<MinePoolBean> minePoolBeans = new ArrayList<>();
                if (!TextUtils.isEmpty(poolsStr)) {
                    JSONObject poolMap = new JSONObject(poolsStr);
                    Iterator it = poolMap.keys();

                    while (it.hasNext()) {
                        String key = it.next().toString();
                        JSONObject p = poolMap.optJSONObject(key);
                        if (p == null) {
                            continue;
                        }
                        MinePoolBean bean = new MinePoolBean();

                        bean.setAddress(key);
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
                .subscribe(new Observer<List<MinePoolBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscribe(d);
                    }

                    @Override
                    public void onNext(List<MinePoolBean> walletBean) {
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
