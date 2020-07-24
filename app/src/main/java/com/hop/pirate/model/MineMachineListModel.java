package com.hop.pirate.model;

import android.content.Context;

import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.bean.MinerBean;

import java.util.List;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 4:05 PM
 */
public interface MineMachineListModel {

    void getMineMachine(Context context, String address, int random, ResultCallBack<List<MinerBean>> resultCallBack);

    void removeAllSubscribe();
}
