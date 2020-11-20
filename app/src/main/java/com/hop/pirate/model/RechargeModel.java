package com.hop.pirate.model;

import android.content.Context;

import com.hop.pirate.callback.ResultCallBack;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/30 2:28 PM
 */
public interface RechargeModel {

    void authorizeTokenSpend(Context context, double tokenNO, ResultCallBack<String> resultCallBack);

    void buyPacket(Context context, String userAddress, String pollAddress, double tokenNO, ResultCallBack<String> resultCallBack);

    void getBytesPerToken(Context context, ResultCallBack<Double> resultCallBack);

    void openWallet(Context context, String password, ResultCallBack<Boolean> resultCallBack);

    void queryTxProcessStatus(String tx, final ResultCallBack<Boolean> resultCallBack);

    void syncPool(String poolAddress, final ResultCallBack<Boolean> resultCallBack);


    void removeAllSubscribe();
}
