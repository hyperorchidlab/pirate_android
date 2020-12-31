package com.hop.pirate.model

import com.hop.pirate.base.WaitTxBaseModel
import com.hop.pirate.model.bean.TransactionBean
import com.hop.pirate.room.DataBaseManager
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
            DataBaseManager.getTransactions()
        }
    }

}