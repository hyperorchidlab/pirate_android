package com.hop.pirate.model

import androidLib.AndroidLib
import com.hop.pirate.Constants
import com.hop.pirate.base.WaitTxBaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/30 2:31 PM
 */
class RechargeModel : WaitTxBaseModel() {
    suspend fun approve(tokenNO: Double): String {
        return withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                AndroidLib.authorizeTokenSpend(tokenNO)
            }
        }
    }

    suspend fun buyPacket( userAddress: String, pollAddress: String, tokenNO: Double): String {
        return withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                AndroidLib.buyPacket(userAddress, pollAddress, tokenNO)
            }
        }
    }

    suspend fun getBytesPerToken(): Double {
        return withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                val sysStr = AndroidLib.systemSettings()
                val json = JSONObject(sysStr)
                json.optDouble("MBytesPerToken")
            }
        }
    }

    suspend fun openWallet(password: String) {
        withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                AndroidLib.openWallet(password)
            }
        }

    }

    suspend fun syncPool(poolAddress: String): Boolean {
        return withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                AndroidLib.waitSubPool(poolAddress)
            }
        }
    }
}