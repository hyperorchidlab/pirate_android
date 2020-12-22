package com.hop.pirate.model

import androidLib.AndroidLib
import com.hop.pirate.Constants
import com.hop.pirate.HopApplication
import com.hop.pirate.base.WaitTxBaseModel
import com.hop.pirate.model.bean.TransactionBean
import com.hop.pirate.room.AppDatabase
import com.nbs.android.lib.base.BaseModel
import io.reactivex.Scheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TransactionModel : WaitTxBaseModel() {
    suspend fun getTransactions(): List<TransactionBean> {
        return withContext(Dispatchers.IO) {
            AppDatabase.getInstance(HopApplication.instance).transactionDao().getTransactions()
        }
    }

}