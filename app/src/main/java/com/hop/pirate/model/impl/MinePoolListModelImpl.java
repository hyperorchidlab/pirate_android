package com.hop.pirate.model.impl;

import android.content.Context;
import android.text.TextUtils;

import com.hop.pirate.base.BaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.MinePoolListModel;
import com.hop.pirate.model.bean.MinePoolBean;
import com.hop.pirate.model.bean.OwnPool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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
 * @date :   2020/5/26 4:07 PM
 */
public class MinePoolListModelImpl extends BaseModel implements MinePoolListModel {


    @Override
    public void getPoolDataOfUser(final Context context, final String address, final ResultCallBack<List<MinePoolBean>> resultCallBack) {
        schedulers(Observable.create(new ObservableOnSubscribe<List<MinePoolBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MinePoolBean>> emitter) throws Exception {
                List<MinePoolBean> minePoolBeans;

                String poolsStr = AndroidLib.getSubPools();
                minePoolBeans = new ArrayList<>();

                if (!TextUtils.isEmpty(poolsStr)&& !poolsStr.equals("null")) {
                    JSONArray jsonArray = new JSONArray(poolsStr);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject p = jsonArray.getJSONObject(i);
                        if (p == null) {
                            continue;
                        }
                        MinePoolBean bean = new MinePoolBean();

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
        })).subscribe(new Observer<List<MinePoolBean>>() {
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
