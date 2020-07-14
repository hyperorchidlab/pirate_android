package com.hop.pirate.model.impl;

import android.content.Context;
import android.text.TextUtils;

import com.hop.pirate.PError;
import com.hop.pirate.R;
import com.hop.pirate.base.BaseModel;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.greendao.MinerDaoUtil;
import com.hop.pirate.model.MineMachineListModel;
import com.hop.pirate.model.bean.MinerBean;
import com.hop.pirate.service.HopService;
import com.hop.pirate.service.SysConf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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
 * @date :   2020/5/30 2:10 PM
 */
public class MineMachineListModelImpl extends BaseModel implements MineMachineListModel {
    @Override
    public void getMinemachine(final Context context, final String address, final int random, final ResultCallBack<List<MinerBean>> resultCallBack) {
        schedulers(Observable.create(new ObservableOnSubscribe<List<MinerBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MinerBean>> emitter) throws Exception {
                List<MinerBean> minerBeans;
                String miners = AndroidLib.randomMiner(address, random);
                if (miners.equals("null") || TextUtils.isEmpty(miners)) {
                    emitter.onError(new PError(context.getString(R.string.get_data_failed)));
                    return;
                }
                minerBeans = new ArrayList<>();
                JSONArray minerArr = new JSONArray(miners);
                for (int i = 0; i < minerArr.length(); i++) {
                    JSONObject obj = (JSONObject) minerArr.get(i);
                    MinerBean bean = new MinerBean();
                    bean.setMID(obj.optString("SubAddr"));
                    bean.setZone(obj.optString("Zone"));
                    bean.setMinerPoolAdd(SysConf.CurPoolAddress);
                    minerBeans.add(bean);
                }
                emitter.onNext(minerBeans);
                emitter.onComplete();
            }
        })).subscribe(new Observer<List<MinerBean>>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscribe(d);
            }

            @Override
            public void onNext(List<MinerBean> walletBean) {
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
