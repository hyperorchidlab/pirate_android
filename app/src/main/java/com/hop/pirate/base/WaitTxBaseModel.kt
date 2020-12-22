package com.hop.pirate.base

import androidLib.AndroidLib
import com.hop.pirate.Constants
import com.hop.pirate.HopApplication
import com.hop.pirate.event.EventReLoadWallet
import com.hop.pirate.model.bean.OwnPool
import com.hop.pirate.model.bean.TransactionBean
import com.hop.pirate.room.AppDatabase
import com.hop.pirate.util.CommonSchedulers
import com.nbs.android.lib.base.BaseModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.greenrobot.eventbus.EventBus

/**
 * @description:
 * @author: Mr.x
 * @date :   2020/7/10 7:58 AM
 */
open class WaitTxBaseModel : BaseModel() {
     fun queryTxStatus(tx: String): Single<Any> {
        return Single.create(SingleOnSubscribe<Any> { emitter ->
            AndroidLib.waitMined(tx)
            EventBus.getDefault().post(EventReLoadWallet(false))
            AppDatabase.getInstance(HopApplication.instance).transactionDao().updateTransaction(Constants.TRANSACTION_STATUS_COMPLETED,tx)
            emitter.onSuccess("")
        }).compose(CommonSchedulers.io2mainAndTimeout<Any>())

    }

    suspend fun txStatus(transactionBean: TransactionBean):Int {
       return withContext(Dispatchers.IO) {
            transactionBean.status = AndroidLib.checkTransactionStatus(transactionBean.hash)
            AppDatabase.getInstance(HopApplication.instance).transactionDao().updateTransaction( transactionBean.status,transactionBean.hash)
           transactionBean.status
        }
    }


    suspend fun isPending(type: Int): Boolean {
        return withContext(Dispatchers.IO) {
            val transactionBean = AppDatabase.getInstance(HopApplication.instance).transactionDao().getLastTransactionByType(type)
            if (transactionBean == null || transactionBean.status != Constants.TRANSACTION_STATUS_PENDING) {
                false
            } else {
                val txStatus = txStatus(transactionBean)
                txStatus==Constants.TRANSACTION_STATUS_PENDING
            }
        }
    }
}