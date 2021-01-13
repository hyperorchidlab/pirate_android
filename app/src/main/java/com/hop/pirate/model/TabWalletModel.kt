package com.hop.pirate.model

import android.content.ContentResolver
import androidLib.AndroidLib
import com.hop.pirate.Constants
import com.hop.pirate.base.WaitTxBaseModel
import com.hop.pirate.model.bean.TransactionBean
import com.hop.pirate.room.DataBaseManager
import com.hop.pirate.util.CommonSchedulers
import com.hop.pirate.util.Utils
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/27 9:09 AM
 */
class TabWalletModel : WaitTxBaseModel() {

    suspend fun exportAccount(cr: ContentResolver, data: String, fileName: String):String? {
       return withContext(Dispatchers.IO) {
            Utils.saveStringQrCode(cr, data, fileName)
        }

    }

    fun applyFreeEth(address: String): Single<String> {
        return Single.create(SingleOnSubscribe<String> { emitter ->
            val tx = AndroidLib.applyFreeEth(address)
            val transactionBean = TransactionBean(0,Constants.TRANSACTION_APPLY_FREE_ETH,tx,Constants.TRANSACTION_STATUS_PENDING)
            DataBaseManager.addTransaction(transactionBean)
            emitter.onSuccess(tx)
        }).compose(CommonSchedulers.io2mainAndTimeout<String>())

    }

    fun applyFreeHop(address: String): Single<String>  {
        return Single.create(SingleOnSubscribe<String> { emitter ->
            val tx = AndroidLib.applyFreeToken(address)
            val transactionBean = TransactionBean(0,Constants.TRANSACTION_APPLY_FREE_HOP,tx,Constants.TRANSACTION_STATUS_PENDING)
            DataBaseManager.addTransaction(transactionBean)
            emitter.onSuccess(tx)
        }).compose(CommonSchedulers.io2mainAndTimeout<String>())
    }

    fun queryHopBalance(address: String): Single<String> {
        return Single.create(SingleOnSubscribe<String> { emitter ->
            emitter.onSuccess( AndroidLib.tokenBalance(address))
        }).compose(CommonSchedulers.io2mainAndTimeout<String>())
    }

}