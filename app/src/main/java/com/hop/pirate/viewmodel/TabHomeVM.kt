package com.hop.pirate.viewmodel

import androidLib.AndroidLib
import androidx.lifecycle.viewModelScope
import com.hop.pirate.HopApplication
import com.hop.pirate.R
import com.hop.pirate.model.impl.TabHomeModelImpl
import com.hop.pirate.service.SysConf
import com.hop.pirate.service.WalletWrapper
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.command.BindingConsumer
import com.nbs.android.lib.event.SingleLiveEvent
import kotlinx.coroutines.launch

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TabHomeVM : BaseViewModel() {
    val model = TabHomeModelImpl()
    val selectPoolLiveEvent = SingleLiveEvent<Boolean>()
    val selectMinerLiveEvent = SingleLiveEvent<Boolean>()
    val changeVPNStatusEvent = SingleLiveEvent<Boolean>()
    val getPoolSuccessEvent = SingleLiveEvent<Boolean>()
    val openWalletSuccessEvent = SingleLiveEvent<Boolean>()
    val changeModelCommand = BindingCommand(null, object : BindingConsumer<String> {
        override fun call(t: String) {
            AndroidLib.setGlobalModel(t == HopApplication.getApplication().getString(R.string.globalModelTips))
        }

    })

    val slelctPoolCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            selectPoolLiveEvent.call()
        }
    })


    val slelctMinerCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            selectMinerLiveEvent.call()
        }
    })

    val changeVPNStatusCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            changeVPNStatusEvent.call()
        }
    })

    fun getPool() {
        viewModelScope.launch {
            kotlin.runCatching {
                model.getPool(WalletWrapper.MainAddress, SysConf.CurPoolAddress)
            }.onSuccess {
                SysConf.PacketsBalance = it.packets
                SysConf.PacketsCredit = it.credit
                getPoolSuccessEvent.call()
            }.onFailure {

            }
        }
    }

    fun openWallet(password:String) {
        showDialog(R.string.open_wallet)
        viewModelScope.launch {
            kotlin.runCatching {
                model.openWallet(password)
            }.onSuccess {
                openWalletSuccessEvent.call()
                dismissDialog()
            }.onFailure {
                dismissDialog()
                showToast(R.string.password_error)
            }
        }
    }
}