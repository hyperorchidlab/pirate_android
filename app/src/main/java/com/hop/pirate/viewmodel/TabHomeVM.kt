package com.hop.pirate.viewmodel

import androidLib.AndroidLib
import androidx.lifecycle.viewModelScope
import com.hop.pirate.HopApplication
import com.hop.pirate.R
import com.hop.pirate.model.TabHomeModel
import com.hop.pirate.model.bean.UserPoolData
import com.hop.pirate.service.SysConf
import com.hop.pirate.service.WalletWrapper
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.command.BindingConsumer
import com.nbs.android.lib.event.SingleLiveEvent
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TabHomeVM : BaseViewModel() {
    val model = TabHomeModel()
    val selectPoolLiveEvent = SingleLiveEvent<Boolean>()
    val selectMinerLiveEvent = SingleLiveEvent<Boolean>()
    val changeVPNStatusEvent = SingleLiveEvent<Boolean>()
    val getPoolSuccessEvent = SingleLiveEvent<Boolean>()
    val openWalletSuccessEvent = SingleLiveEvent<Boolean>()
    val changeModelCommand = BindingCommand(null, object : BindingConsumer<String> {
        override fun call(t: String) {
            AndroidLib.setGlobalModel(t == HopApplication.instance.getString(R.string.globalModelTips))
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
        model.getPool(WalletWrapper.MainAddress, SysConf.CurPoolAddress)
            .subscribe(object : SingleObserver<UserPoolData> {
                override fun onSuccess(userPool: UserPoolData) {
                    SysConf.PacketsBalance = userPool.packets
                    SysConf.PacketsCredit = userPool.credit
                    getPoolSuccessEvent.call()
                }

                override fun onSubscribe(d: Disposable) {
                    addSubscribe(d)
                }

                override fun onError(e: Throwable) {

                }

            })

    }

    fun openWallet(password: String) {
        model.openWallet(password).subscribe(object : SingleObserver<Any> {
            override fun onSuccess(t: Any?) {
                openWalletSuccessEvent.call()
                dismissDialog()
            }

            override fun onSubscribe(d: Disposable) {
                showDialog(R.string.open_wallet)
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                dismissDialog()
                showErrorToast(R.string.password_error, e)
            }

        })
    }

}