package com.hop.pirate.viewmodel

import android.content.ContentResolver
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.hop.pirate.Constants
import com.hop.pirate.R
import com.hop.pirate.event.EventReLoadWallet
import com.hop.pirate.model.TabWalletModel
import com.hop.pirate.ui.fragement.TabWalletFragment
import com.hop.pirate.service.WalletWrapper
import com.hop.pirate.ui.activity.GuideActivity
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.event.SingleLiveEvent
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TabWalletVM : BaseViewModel() {
    val model = TabWalletModel()
    val dnsObservable = ObservableField<String>()
    val versionObservable = ObservableField<String>()
    val showqrImageEvent = SingleLiveEvent<Boolean>()
    val exportEvent = SingleLiveEvent<Boolean>()
    val queryTxEvent = SingleLiveEvent<Boolean>()
    val hopBalanceEvent = SingleLiveEvent<String>()
    val clearDBEvent = SingleLiveEvent<Boolean>()
    val dnsEvent = SingleLiveEvent<Boolean>()
    val createAccountEvent = SingleLiveEvent<Boolean>()

    override fun clickRightIv() {
        EventBus.getDefault().post(EventReLoadWallet(true))
    }

    val showAddressCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showqrImageEvent.call()
        }
    })

    val createAccountCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            createAccountEvent.call()
        }
    })

    val applyFreeEthCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            applyFreeEth()
        }
    })


    val applyFreeTokenCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            applyFreeToken()
        }
    })


    val exportCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            exportEvent.call()
        }
    })

    val updateAppCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startWebActivity(Constants.APP_DOWNLOAD_URL)
        }
    })

    val dnsCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            dnsEvent.call()
        }
    })

    val guideCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startActivity(GuideActivity::class.java)
        }
    })

    val helpCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startWebActivity(Constants.APP_HELP_URL)
        }
    })
    val courseAddressCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startWebActivity(Constants.APP_COURSE_URL)
        }
    })
    val clearDBCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            clearDBEvent.call()
        }
    })

    private fun applyFreeToken() {

        if (TextUtils.isEmpty(WalletWrapper.MainAddress)) {
            showToast(R.string.please_create_account)
            return
        }
        if (WalletWrapper.HopBalance >= TabWalletFragment.FREE_HOP_MAX_VALUE) {
            showToast(R.string.apply_free_token_des)
            return
        }
        showDialog(R.string.creating_tx)
        viewModelScope.launch {
            kotlin.runCatching {
                model.applyFreeHop(WalletWrapper.MainAddress)
            }.onSuccess {
                queryTxStatus(it, true)
            }.onFailure {
                dismissDialog()
                showErrorToast(R.string.apply_fail, it)
            }
        }
    }

    private fun applyFreeEth() {
        if (TextUtils.isEmpty(WalletWrapper.MainAddress)) {
            showToast(R.string.please_create_account)
            return
        }
        if (WalletWrapper.EthBalance > TabWalletFragment.FREE_ETH_MAX_VALUE) {
            showToast(R.string.apply_free_token_des)
            return
        }
        showDialogNotCancel(R.string.creating_tx)
        viewModelScope.launch {
            kotlin.runCatching { model.applyFreeEth(WalletWrapper.MainAddress) }
                .onSuccess {
                    queryTxStatus(it, false)
                }
                .onFailure {
                    dismissDialog()
                    showErrorToast(R.string.apply_fail, it)
                }

        }

    }

    fun queryTxStatus(tx: String, isFreeHop: Boolean) {
        val msg = "\nHop TX:[$tx]"
        showDialogNotCancel(msg)
        viewModelScope.launch {
            kotlin.runCatching { model.queryTxStatus(tx) }
                .onSuccess {
                    queryTxEvent.postValue(isFreeHop)
                    dismissDialog()
                }
                .onFailure {
                    dismissDialog()
                    showErrorToast(R.string.apply_fail, it)
                }

        }
    }

    fun hopBalance() {
        viewModelScope.launch {
            kotlin.runCatching {
                model.queryHopBalance(WalletWrapper.MainAddress)
            }.onSuccess {
                if (it.isEmpty()) {
                    return@launch
                }
                hopBalanceEvent.postValue(it)
            }.onFailure {

            }

        }
    }

    fun exportAccount(cr: ContentResolver, data: String, fileName: String) {
        showDialog(R.string.loading)
        viewModelScope.launch {
            kotlin.runCatching {
                model.exportAccount(cr, data, fileName)
            }.onSuccess {
                dismissDialog()
                showToast(R.string.wallet_export_success)
            }.onFailure {
                dismissDialog()
                showErrorToast(R.string.transfer_fail, it)
            }

        }
    }
}