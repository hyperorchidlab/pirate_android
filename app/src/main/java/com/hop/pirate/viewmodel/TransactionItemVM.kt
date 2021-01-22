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
import com.nbs.android.lib.utils.toast
import kotlinx.coroutines.launch

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TransactionItemVM(VM: TransactionVM, var transaction: TransactionBean) : ItemViewModel<TransactionVM>(VM) {
    val status = ObservableField<String>()

    init {
        if (transaction.status != Constants.TRANSACTION_STATUS_COMPLETED) {
            getTransactionStatus()
        } else {
            status.set(HopApplication.instance.resources.getString(R.string.transaction_completed))
        }
    }

    fun longClickCommand() = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            Utils.copyToMemory(HopApplication.instance, transaction.hash)
            toast(HopApplication.instance.getString(R.string.copy_success))
        }

    })

    private fun getTransactionStatus() {
        viewModelScope.launch {
            val transactionStatus = viewModel.transactionModel.getTransactionStatus(transaction.hash)
            transaction.status = transactionStatus
            viewModel.transactionModel.updateDBTransaction(transactionStatus, transaction.hash)
            status.set(getStatusStr())
        }
    }

    private fun getStatusStr(): String {
        return when (transaction.status) {
            0 -> ""
            Constants.TRANSACTION_STATUS_PENDING -> HopApplication.instance.resources.getString(R.string.transaction_pending)
            Constants.TRANSACTION_STATUS_COMPLETED -> HopApplication.instance.resources.getString(R.string.transaction_completed)
            else -> HopApplication.instance.resources.getString(R.string.transaction_error)
        }

    }
}