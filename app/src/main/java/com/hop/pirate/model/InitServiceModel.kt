package com.hop.pirate.model

import android.content.Context
import androidLib.AndroidLib
import com.hop.pirate.Constants
import com.hop.pirate.R
import com.hop.pirate.util.CommonSchedulers
import com.hop.pirate.util.Utils
import com.nbs.android.lib.base.BaseModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.core.SingleOnSubscribe
import io.reactivex.rxjava3.disposables.Disposable
import org.apache.commons.io.IOUtils
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
open class InitServiceModel : BaseModel() {
    fun initService(context: Context):Single<Any> {
       return Single.create(SingleOnSubscribe<Any> { emitter ->
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
            emitter.onSuccess("")
        }).compose(CommonSchedulers.io2mainAndTimeout<Any>())

    }
}