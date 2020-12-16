package com.hop.pirate.viewmodel

import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.hop.pirate.HopApplication
import com.hop.pirate.R
import com.hop.pirate.ui.activity.MainActivity
import com.hop.pirate.event.EventNewAccount
import com.hop.pirate.model.CreateAccountModel
import com.hop.pirate.service.WalletWrapper
import com.hop.pirate.util.Utils
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.event.SingleLiveEvent
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class CreateAccountVM : BaseViewModel() {
    val model = CreateAccountModel()
    val password = ObservableField<String>()
    val confirmPassword = ObservableField<String>()
    val showImportDialogEvent = SingleLiveEvent<Any?>()
    val exitEvent = SingleLiveEvent<Any?>()


    val finisCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            finish()
        }

    })
    val createCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            if (verifyPassword()) {
                createAccount()
            }
        }

    })

    private fun verifyPassword(): Boolean {
        if (TextUtils.isEmpty(password.get())) {
            uc.toastEvent.value = R.string.enter_password
            return false
        } else if (!confirmPassword.get().equals(password.get())) {
            uc.toastEvent.value = R.string.twice_password_not_same
            return false
        }
        return true
    }

    fun createAccount() {
        showDialog(R.string.creating_account)
        val job = viewModelScope.launch {
            runCatching {
                model.createAccount(password.get()!!)
            }.onSuccess {
                createSuccess(it)
            }.onFailure {
                createFailure(it)
            }
        }

        jobs.add(job)

    }

    private fun createSuccess(it: String) {
        WalletWrapper.MainAddress = JSONObject(it).optString("mainAddress")
       initService(true)
    }

    private fun createFailure(t: Throwable) {
        dismissDialog()
        showErrorToast(R.string.create_account_failed,t)
    }

    val importCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showImportDialogEvent.call()
        }

    })


    fun importWallet(password: String, walletStr: String) {
        showDialog(R.string.password_error)
        val job = viewModelScope.launch {
            runCatching {
                model.importWallet(walletStr, password)
            }.onSuccess {
                importWalletSuccess(walletStr)
            }.onFailure {
                importWalletFailure(it)

            }
        }
        jobs.add(job)
    }


    private fun importWalletSuccess(walletStr: String) {
        WalletWrapper.MainAddress = JSONObject(walletStr).optString("mainAddress")
        initService(false)
    }

    private fun importWalletFailure(t: Throwable) {
        dismissDialog()
        showErrorToast(R.string.password_error,t)
    }

    fun initService(isCreated: Boolean) {
        viewModelScope.launch {
            kotlin.runCatching {
                model.initService(HopApplication.instance.applicationContext)
            }.onSuccess {
                initServiceSuccess(isCreated)
            }.onFailure {
                initServiceFailure()
            }
        }

    }

    private fun initServiceFailure() {
        exitEvent.call()
    }

    private fun initServiceSuccess(isCreated: Boolean) {
        dismissDialog()
        Utils.clearAllData()
        if(isCreated){
            showToast(R.string.create_account_success)
            EventBus.getDefault().post(EventNewAccount())
        }else{
            showToast(R.string.import_success)
            EventBus.getDefault().post(EventNewAccount())
        }

        startActivity(MainActivity::class.java)
        finish()
    }

}