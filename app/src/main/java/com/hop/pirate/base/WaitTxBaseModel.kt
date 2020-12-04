package com.hop.pirate.base

import androidLib.AndroidLib
import com.hop.pirate.Constants
import com.nbs.android.lib.base.BaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/**
 * @description:
 * @author: Mr.x
 * @date :   2020/7/10 7:58 AM
 */
open class WaitTxBaseModel : BaseModel() {
    suspend fun queryTxStatus(tx: String) {
        withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                AndroidLib.waitMined(tx)
            }
        }


    }
}