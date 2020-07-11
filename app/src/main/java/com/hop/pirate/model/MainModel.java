package com.hop.pirate.model;

import android.content.Context;

import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.bean.WalletBean;

import androidLib.HopDelegate;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 2:46 PM
 */
public interface MainModel {
    void initService(Context context, HopDelegate hopDelegate, ResultCallBack<String> resultCallBack);

    void getWalletInfo(Context context, ResultCallBack<WalletBean> resultCallBack);

    void syncAllPoolsData();

    void initSysSeting();

    void initsyncPoolsAndUserData();

    void removeAllSubscribe();

}
