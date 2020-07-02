package com.hop.pirate.model;

import android.content.Context;

import com.hop.pirate.callback.ResultCallBack;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 11:09 AM
 */
public interface SplashModel {

    void loadWallet(Context context, ResultCallBack<String> resultCallBack);

    void removeAllSubscribe();
}
