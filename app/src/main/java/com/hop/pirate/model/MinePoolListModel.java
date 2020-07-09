package com.hop.pirate.model;

import android.content.Context;

import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.bean.MinePoolBean;

import java.util.List;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 4:05 PM
 */
public interface MinePoolListModel {

    void getPoolDataOfUser(Context context,String address, ResultCallBack<List<MinePoolBean>> resultCallBack);

    void removeAllSubscribe();
}
