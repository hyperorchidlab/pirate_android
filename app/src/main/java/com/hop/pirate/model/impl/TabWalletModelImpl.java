package com.hop.pirate.model.impl;

import android.content.Context;
import android.text.TextUtils;

import com.hop.pirate.Constants;
import com.hop.pirate.base.BaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.fragment.TabWalletFragment;
import com.hop.pirate.greendao.MinerDaoUtil;
import com.hop.pirate.greendao.OwnPoolDaoUtil;
import com.hop.pirate.model.TabWalletModel;
import com.hop.pirate.model.bean.MinerBean;
import com.hop.pirate.model.bean.OwnPool;
import com.hop.pirate.service.HopService;

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
    public void getPoolDataOfUser(final Context context, final String address, final ResultCallBack<List<OwnPool>> resultCallBack) {
        Observable.create(new ObservableOnSubscribe<List<OwnPool>>() {
            @Override
            public void subscribe(ObservableEmitter<List<OwnPool>> emitter) throws Exception {
                List<OwnPool> minePoolBeans = new ArrayList<>();
                OwnPoolDaoUtil ownPoolDaoUtil = new OwnPoolDaoUtil(context);
                if(HopService.IsRunning){
                    minePoolBeans = ownPoolDaoUtil.queryAll();
                    emitter.onNext(minePoolBeans);
                    emitter.onComplete();
                    return ;
                }
                if(!TabWalletFragment.initsyncPoolsAndUserData){
                    AndroidLib.syncPoolsAndUserData();
                    TabWalletFragment.initsyncPoolsAndUserData = true;
                }
                String poolsStr = AndroidLib.poolDataOfUser(address);

                if (!TextUtils.isEmpty(poolsStr)) {
                    JSONObject poolMap = new JSONObject(poolsStr);
                    Iterator it = poolMap.keys();

                    while (it.hasNext()) {
                        String key = it.next().toString();
                        JSONObject p = poolMap.optJSONObject(key);
                        if (p == null) {
                            continue;
                        }
                        OwnPool bean = new OwnPool();

                        bean.setAddress(key);
                        bean.setName(p.optString("Name"));
                        bean.setEmail(p.optString("Email"));
                        bean.setMortgageNumber(p.optDouble("GTN"));
                        bean.setWebsiteAddress(p.optString("Url"));
                        minePoolBeans.add(bean);
                    }

                }

                ownPoolDaoUtil.deleteAll();
                ownPoolDaoUtil.insertOwnPool(minePoolBeans);
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
