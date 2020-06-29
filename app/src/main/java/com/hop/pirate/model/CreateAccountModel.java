package com.hop.pirate.model;

import android.content.Context;

import com.hop.pirate.callback.ResultCallBack;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 9:49 AM
 */
public interface CreateAccountModel {

    void createAccount(String password, ResultCallBack<String> resultCallBack);

    void importWallet(Context context, String walletStr, String password, ResultCallBack<String> resultCallBack);


    void removeAllSubscribe();
}
