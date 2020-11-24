package com.hop.pirate.model.impl;

import com.hop.pirate.activity.MainActivity;
import com.hop.pirate.base.BaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.fragment.TabPacketsMarketFragment;
import com.hop.pirate.model.TabPacketsMarketModel;
import com.hop.pirate.model.bean.MinePoolBean;

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
 * @date :   2020/5/26 2:59 PM
 */
public class TabPacketsMarketModelImpl extends BaseModel implements TabPacketsMarketModel {
    @Override
    public void getPoolInfo(final boolean syncAllPools, final ResultCallBack<List<MinePoolBean>> resultCallBack) {
        schedulers(Observable.create(new ObservableOnSubscribe<List<MinePoolBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MinePoolBean>> emitter) throws Exception {
                if(syncAllPools){
                    AndroidLib.syncAllPoolsData();
                }
                String jsonStr = AndroidLib.poolInfosInMarket();
                List<MinePoolBean> minePoolBeans = new ArrayList<>();
                JSONObject pools = new JSONObject(jsonStr);
                Iterator it = pools.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    JSONObject p = pools.optJSONObject(key);
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
                emitter.onNext(minePoolBeans);
                emitter.onComplete();
            }
        })).subscribe(new Observer<List<MinePoolBean>>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscribe(d);
            }

            @Override
            public void onNext(List<MinePoolBean> minePoolBeans) {
                resultCallBack.onSuccess(minePoolBeans);
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
