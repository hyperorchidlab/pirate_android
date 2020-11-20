package com.hop.pirate.model;

import android.content.Context;

import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.bean.OwnPool;

import java.util.List;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 4:05 PM
 */
public interface PurchasedPoolModel {


    void getPoolDataOfUser(Context context, String address, ResultCallBack<List<OwnPool>> resultCallBack);

    void removeAllSubscribe();
}
