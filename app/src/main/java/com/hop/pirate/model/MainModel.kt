package com.hop.pirate.model

import androidLib.AndroidLib
import com.google.gson.Gson
import com.hop.pirate.model.bean.WalletBean
import com.hop.pirate.util.CommonSchedulers
import com.nbs.android.lib.base.BaseModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 2:47 PM
 */
class MainModel : BaseModel() {


    fun getWalletInfo(): Single<WalletBean> {
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