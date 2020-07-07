package com.hop.pirate.model;

import android.content.Context;

import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.bean.MinePoolBean;
import com.hop.pirate.model.bean.WalletBean;

import java.util.List;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 4:05 PM
 */
public interface TabWalletModel {


    void getPoolDataOfUser(int currentPoolNum,String address, ResultCallBack<List<MinePoolBean>> resultCallBack);

    void removeAllSubscribe();
}
