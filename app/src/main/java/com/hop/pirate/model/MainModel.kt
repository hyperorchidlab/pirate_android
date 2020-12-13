package com.hop.pirate.model

import android.content.Context
import androidLib.AndroidLib
import androidLib.HopDelegate
import com.google.gson.Gson
import com.hop.pirate.Constants
import com.hop.pirate.R
import com.hop.pirate.model.bean.WalletBean
import com.hop.pirate.util.Utils
import com.nbs.android.lib.base.BaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.apache.commons.io.IOUtils

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 2:47 PM
 */
class MainModel : BaseModel() {


    suspend fun getWalletInfo(): WalletBean {
        return withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                val walletInfo = AndroidLib.walletInfo()
                Gson().fromJson(walletInfo, WalletBean::class.java)
            }
        }

    }

    suspend fun syncSubPoolsData() {
        withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                AndroidLib.syncSubPoolsData()
            }
        }

    }
}