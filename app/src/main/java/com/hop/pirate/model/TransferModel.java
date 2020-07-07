package com.hop.pirate.model;

import com.hop.pirate.callback.ResultCallBack;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/27 12:53 PM
 */
public interface TransferModel {

    void transferEth(String password, String toAddress, String num, ResultCallBack<String> resultCallBack);

    void transferToken(String password, String toAddress, String num, ResultCallBack<String> resultCallBack);

    void queryTxProcessStatus(String tx, final ResultCallBack<Boolean> resultCallBack);

    void removeAllSubscribe();
}
