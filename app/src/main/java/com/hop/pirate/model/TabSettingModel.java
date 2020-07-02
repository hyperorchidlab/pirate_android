package com.hop.pirate.model;

import android.content.ContentResolver;

import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.bean.MinePoolBean;

import java.util.List;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/27 9:04 AM
 */
public interface TabSettingModel {

    void exportAccount(ContentResolver cr, String data, String fileName, ResultCallBack<String> resultCallBack);

    void getFreeEth(final String address, ResultCallBack<String> resultCallBack);

    void getFreeHop(final String address, ResultCallBack<String> resultCallBack);

    void removeAllSubscribe();
}
