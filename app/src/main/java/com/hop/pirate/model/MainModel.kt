package com.hop.pirate.model

import android.content.Context
import androidLib.AndroidLib
import androidLib.HopDelegate
import com.google.gson.Gson
import com.hop.pirate.Constants
import com.hop.pirate.R
import com.hop.pirate.model.bean.MinerBean
import com.hop.pirate.model.bean.WalletBean
import com.hop.pirate.util.CommonSchedulers
import com.hop.pirate.util.Utils
import com.nbs.android.lib.base.BaseModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.core.SingleOnSubscribe
import io.reactivex.rxjava3.disposables.Disposable
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


    fun getWalletInfo():Single<WalletBean> {
        return Single.create(SingleOnSubscribe<WalletBean> { emitter ->
            val walletInfo = AndroidLib.walletInfo()
            val wallet = Gson().fromJson(walletInfo, WalletBean::class.java)
            emitter.onSuccess(wallet)
        }).compose(CommonSchedulers.io2mainAndTimeout<WalletBean>())


    }

    suspend fun syncSubPoolsData() {
        withContext(Dispatchers.IO) {
            AndroidLib.syncSubPoolsData()
        }
    }
}