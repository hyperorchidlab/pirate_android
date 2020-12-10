package com.hop.pirate.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.hop.pirate.R
import com.hop.pirate.event.EventRechargeSuccess
import com.hop.pirate.model.RechargeModel
import com.hop.pirate.service.WalletWrapper
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.event.SingleLiveEvent
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import kotlin.math.pow

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class RechargePacketsVM:BaseViewModel() {
    val model = RechargeModel()
    private val AUTHORIZE_TOKEN = 4.2e8
    val poolAddress = ObservableField<String>("")
    var tokenNO = 0.0
    val bytePreTokenEvent = SingleLiveEvent<Double>()
    val syncPoolSuccessEvent = SingleLiveEvent<Boolean>()

     fun initFlows(){
        viewModelScope.launch {
            kotlin.runCatching {
                model.getBytesPerToken()
            }.onSuccess {
                onInitFlowsSuccess(it)
            }.onFailure {
                onInitFlowsFailure(it)
            }
        }

    }

    private fun onInitFlowsFailure(t: Throwable) {
        showErrorToast(R.string.get_data_failed,t)
    }

    private fun onInitFlowsSuccess(it: Double) {
        bytePreTokenEvent.value = it
    }

    fun openWallet(password: String){
        showDialog(R.string.open_wallet)
        viewModelScope.launch {
            kotlin.runCatching {
                model.openWallet(password)
            }.onSuccess {
                onOpenWalletSuccess()
            }.onFailure {
                onOpenWalletFailure(it)
            }
        }
    }

    private fun onOpenWalletFailure(t: Throwable) {
        dismissDialog()
        showErrorToast(R.string.password_error,t)
    }

    private fun onOpenWalletSuccess() {
        dismissDialog()
        if (WalletWrapper.Approved / 10.0.pow(18.0) >= tokenNO) {
            buyPacket()
        } else {
            approve()
        }
    }

    fun buyPacket(){
        showDialogNotCancel(R.string.recharge_buy_packets)
        viewModelScope.launch {
            kotlin.runCatching {
                model.buyPacket(WalletWrapper.MainAddress,poolAddress.get()!!,tokenNO)
            }.onSuccess {
                onBuyPacketSuccess(it)
            }.onFailure {
                onBuyPacketFailure(it)
            }
        }
    }

    private fun onBuyPacketFailure(t: Throwable) {
        dismissDialog()
        showErrorToast(R.string.recharge_buy_packets_error,t)

    }

    private fun onBuyPacketSuccess(tx: String) {
        queryTxStatus(tx, false)
    }

    private fun approve(){
        showDialogNotCancel(R.string.approving)
        viewModelScope.launch {
            kotlin.runCatching {
                model.approve(AUTHORIZE_TOKEN)
            }.onSuccess {
                onApproveSuccess(it)
            }.onFailure {
                onApproveFailure(it)
            }
        }
    }

    private fun onApproveFailure(t: Throwable) {
        dismissDialog()
        showErrorToast(R.string.approve_error,t)
    }

    private fun onApproveSuccess(tx: String) {
        queryTxStatus(tx,true)

    }

    fun queryTxStatus(tx: String, isProve: Boolean){
        viewModelScope.launch {
            kotlin.runCatching {
                model.queryTxStatus(tx)
            }.onSuccess {
                onQueryTxStatusSuccess(isProve)
            }.onFailure {
                onQueryTxStatusFailure(it)
            }
        }
    }

    private fun onQueryTxStatusFailure(t: Throwable) {
        dismissDialog()
        showErrorToast(R.string.blockchain_time_out,t)
        EventBus.getDefault().post(EventRechargeSuccess())
    }

    private fun onQueryTxStatusSuccess(prove: Boolean) {
        dismissDialog()
        EventBus.getDefault().post(EventRechargeSuccess())
        if (prove) {
            buyPacket()
        } else {
            showDialogNotCancel(R.string.recharge_sync_pool)
            EventBus.getDefault().post(EventRechargeSuccess())
            syncPool()
        }
    }

    private fun syncPool() {
        viewModelScope.launch {
            kotlin.runCatching {
                model.syncPool(poolAddress.get()!!)
            }.onSuccess {
                onSyncPoolSuccess(it)
            }.onFailure {
                onSyncPoolFailure(it)
            }
        }
    }

    private fun onSyncPoolFailure(t: Throwable) {
        dismissDialog()
        showErrorToast(R.string.recharge_sync_pool_failed,t)
    }

    private fun onSyncPoolSuccess(syncSuccess: Boolean) {
        dismissDialog()
        if (syncSuccess) {
            syncPoolSuccessEvent.value = true
        } else {
            showToast(R.string.recharge_sync_pool_failed)
        }
    }

}