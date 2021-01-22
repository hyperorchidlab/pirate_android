package com.hop.pirate.model

import android.content.Context
import androidLib.AndroidLib
import com.hop.pirate.Constants
import com.hop.pirate.R
import com.hop.pirate.util.CommonSchedulers
import com.hop.pirate.util.GoDelegate
import com.hop.pirate.util.Utils
import com.nbs.android.lib.base.BaseModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import org.apache.commons.io.IOUtils

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
open class InitServiceModel : BaseModel() {
    fun initService(context: Context): Single<Any> {
        val netType = Utils.getInt(Constants.NET_TYPE,Constants.DEFAULT_MAIN_NET)
        val tokenAddress: String
        val micropayAddress: String
        val ethUrl: String
        if (netType == Constants.TEST_NET) {
            tokenAddress = Constants.TOKEN_ADDRESS
            micropayAddress = Constants.MICROPAY_SYS_ADDRESS
            ethUrl = Constants.ETH_API_URL
        } else {
            tokenAddress = Constants.MAIN_TOKEN_ADDRESS
            micropayAddress = Constants.MAIN_MICROPAY_SYS_ADDRESS
            ethUrl = Constants.MAIN_ETH_API_URL
        }
        return Single.create(SingleOnSubscribe<Any> { emitter ->
            val ipInput = context.resources.openRawResource(R.raw.bypass)
            val bypassIPs = IOUtils.toString(ipInput)
            val newDns = Utils.getString(Constants.NEW_DNS, Constants.DNS)
            AndroidLib.stopProtocol()
            AndroidLib.initSystem(bypassIPs,
                    Utils.getBaseDir(context),
                    tokenAddress,
                    micropayAddress,
                    ethUrl,
                    newDns,
                    GoDelegate())
            AndroidLib.initProtocol()
            AndroidLib.startProtocol()
            emitter.onSuccess("")
        }).compose(CommonSchedulers.io2mainAndTimeout<Any>())

    }
}