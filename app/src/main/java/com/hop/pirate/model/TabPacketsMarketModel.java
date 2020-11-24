package com.hop.pirate.model;

import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.bean.MinePoolBean;

import java.util.List;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 2:57 PM
 */
public interface TabPacketsMarketModel {
    void getPoolInfo(boolean syncAllPools,ResultCallBack<List<MinePoolBean>> resultCallBack);

    void removeAllSubscribe();
}
