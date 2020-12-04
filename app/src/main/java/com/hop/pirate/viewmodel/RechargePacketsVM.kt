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
        showDialog(R.string.waiting)
        viewModelScope.launch {
            kotlin.runCatching {
                model.getBytesPerToken()
            }.onSuccess {
                oninitFlowsSuccess(it)
            }.onFailure {
                oninitFlowsFailure()
            }
        }

    }

    private fun oninitFlowsFailure() {
        dismissDialog()
        showToast(R.string.get_data_failed)
    }

    private fun oninitFlowsSuccess(it: Double) {
        dismissDialog()
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
                onOpenWalletFailure()
            }
        }
    }

    private fun onOpenWalletFailure() {
        dismissDialog()
        showToast(R.string.password_error)
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
        showDialog(R.string.recharge_buy_packets)
        println("~~~~~~~~~show recharge_buy_packets${System.currentTimeMillis()}")
        viewModelScope.launch {
            kotlin.runCatching {
                model.buyPacket(WalletWrapper.MainAddress,poolAddress.get()!!,tokenNO)
            }.onSuccess {
                onBuyPacketSuccess(it)
            }.onFailure {
                onBuyPacketFailure()
            }
        }
    }

    private fun onBuyPacketFailure() {
        println("~~~~~~~~~onBuyPacketFailure${System.currentTimeMillis()}")
        dismissDialog()
        showToast(R.string.recharge_buy_packets_error)

    }

    private fun onBuyPacketSuccess(tx: String) {
        queryTxStatus(tx, false)
    }

    private fun approve(){
        showDialog(R.string.approving)
        viewModelScope.launch {
            kotlin.runCatching {
                model.approve(AUTHORIZE_TOKEN)
            }.onSuccess {
                onApproveSuccess(it)
            }.onFailure {
                onApproveFailure()
            }
        }
    }

    private fun onApproveFailure() {
        dismissDialog()
        showToast(R.string.approve_error)
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
                onQueryTxStatusFailure()
            }
        }
    }

    private fun onQueryTxStatusFailure() {
        dismissDialog()
        showToast(R.string.blockchain_time_out)
        EventBus.getDefault().post(EventRechargeSuccess())
    }

    private fun onQueryTxStatusSuccess(prove: Boolean) {
        dismissDialog()
        EventBus.getDefault().post(EventRechargeSuccess())
        if (prove) {
            buyPacket()
        } else {
           showDialog(R.string.recharge_sync_pool)
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
                onSyncPoolFailure()
            }
        }
    }

    private fun onSyncPoolFailure() {
        dismissDialog()
        showToast(R.string.recharge_sync_pool_failed)
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