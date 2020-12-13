package com.hop.pirate.model

import android.content.Context
import androidLib.AndroidLib
import com.hop.pirate.Constants
import com.hop.pirate.R
import com.hop.pirate.util.Utils
import com.nbs.android.lib.base.BaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.apache.commons.io.IOUtils

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
open class InitServiceModel : BaseModel() {
    suspend fun initService(context: Context) {
        withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                val ipInput = context.resources.openRawResource(R.raw.bypass)
                val bypassIPs = IOUtils.toString(ipInput)
                val newDns = Utils.getString(Constants.NEW_DNS, Constants.DNS)
                AndroidLib.stopProtocol()
                AndroidLib.initSystem(
                    bypassIPs,
                    Utils.getBaseDir(context),
                    Constants.TOKEN_ADDRESS,
                    Constants.MICROPAY_SYS_ADDRESS,
                    Constants.ETH_API_URL,
                    newDns
                )
                AndroidLib.initProtocol()
                AndroidLib.startProtocol()
            }
        }


    }
}