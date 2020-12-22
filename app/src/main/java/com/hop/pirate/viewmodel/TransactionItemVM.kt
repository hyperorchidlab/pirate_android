package com.hop.pirate.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.hop.pirate.Constants
import com.hop.pirate.HopApplication
import com.hop.pirate.R
import com.hop.pirate.model.bean.TransactionBean
import com.hop.pirate.util.Utils
import com.nbs.android.lib.base.ItemViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import kotlinx.coroutines.launch

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TransactionItemVM (VM:TransactionVM, var transaction: TransactionBean): ItemViewModel<TransactionVM>(VM)  {
    val status = ObservableField<String>()
    init {
        if(transaction.status !=Constants.TRANSACTION_STATUS_COMPLETED){
            getTransactionStatus()
        }else{
            setStatusStr()
        }
    }

    fun longClickCommand() = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            Utils.copyToMemory(HopApplication.instance,transaction.hash)
        }

    })

    private fun getTransactionStatus(){
            viewModelScope.launch {
                viewModel.transactionModel.txStatus(transaction)
                setStatusStr()
            }
    }

    private fun setStatusStr(){
        val statusStr = when(transaction.status){
            0 -> ""
            Constants.TRANSACTION_STATUS_PENDING -> HopApplication.instance.resources.getString(R.string.transaction_pending)
            Constants.TRANSACTION_STATUS_COMPLETED -> HopApplication.instance.resources.getString(R.string.transaction_completed)
            else -> HopApplication.instance.resources.getString(R.string.transaction_error)
        }
        status.set(statusStr)
    }
}