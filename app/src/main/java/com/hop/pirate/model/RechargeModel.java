package com.hop.pirate.model;

import android.content.Context;

import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.bean.MinePoolBean;

import java.util.List;

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

    void removeAllSubscribe();
}
