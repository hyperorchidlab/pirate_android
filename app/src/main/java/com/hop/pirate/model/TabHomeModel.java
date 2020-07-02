package com.hop.pirate.model;

import android.content.Context;

import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.bean.UserAccountData;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/6/4 12:10 PM
 */
public interface TabHomeModel {

    void getUserDataOfPool(String user, String pool, ResultCallBack<UserAccountData> resultCallBack);

    void openWallet(Context context, String password, ResultCallBack<Boolean> resultCallBack);

    void removeAllSubscribe();
}
