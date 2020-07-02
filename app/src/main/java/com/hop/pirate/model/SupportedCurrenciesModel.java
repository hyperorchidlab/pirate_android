package com.hop.pirate.model;

import android.content.Context;

import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.bean.ExtendToken;
import com.hop.pirate.model.bean.MinePoolBean;

import java.util.List;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 6:16 PM
 */
public interface SupportedCurrenciesModel {
    void getSupportedCurrencies(Context context, String address, ResultCallBack<List<ExtendToken>> resultCallBack);

    void removeAllSubscribe();
}
