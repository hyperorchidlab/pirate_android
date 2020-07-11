package com.hop.pirate.model;

import android.content.ContentResolver;

import com.hop.pirate.callback.ResultCallBack;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/27 9:04 AM
 */
public interface TabSettingModel {

    void exportAccount(ContentResolver cr, String data, String fileName, ResultCallBack<String> resultCallBack);

    void getFreeEth(final String address, ResultCallBack<String> resultCallBack);

    void getFreeHop(final String address, ResultCallBack<String> resultCallBack);

    void getHopBalance(final String address, ResultCallBack<String> resultCallBack);

    void queryTxProcessStatus(String tx, final ResultCallBack<Boolean> resultCallBack);

    void removeAllSubscribe();
}
