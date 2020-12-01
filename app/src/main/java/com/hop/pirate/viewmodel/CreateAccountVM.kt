package com.hop.pirate.viewmodel

import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.hop.pirate.R
import com.hop.pirate.ui.activity.MainActivity
import com.hop.pirate.event.EventNewAccount
import com.hop.pirate.model.impl.CreateAccountModelImpl
import com.hop.pirate.service.WalletWrapper
import com.hop.pirate.util.Utils
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.event.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class CreateAccountVM : BaseViewModel() {
    val createAccountModel = CreateAccountModelImpl()
    val password = ObservableField<String>()
    val confirmPassword = ObservableField<String>()
    val showImportDialogEvent = SingleLiveEvent<Any?>()


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
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    createAccountModel.createAccount(password.get()!!)
                }
            }.onSuccess {
                createSuccess(it)
            }.onFailure {
                createFailure()


            }
        }


    }

    private fun createSuccess(it: String) {
        WalletWrapper.MainAddress = JSONObject(it).optString("mainAddress")
        dismissDialog()
        showToast(R.string.create_account_success)
        Utils.clearAllData()
        EventBus.getDefault().post(EventNewAccount())
        startActivity(MainActivity::class.java)
        finish()
    }

    private fun createFailure() {
        dismissDialog()
        showToast(R.string.create_account_failed)
    }

    val importCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showImportDialogEvent.call()
        }

    })


    fun importWallet(password: String, walletStr: String) {
        showDialog(R.string.password_error)
        viewModelScope.launch {
            runCatching {
                createAccountModel.importWallet(walletStr, password)
            }.onSuccess {
                importWalletSuccess(walletStr)
            }.onFailure {
                importWalletFailure()

            }
        }
    }


    private fun importWalletSuccess(walletStr: String) {
        dismissDialog()
        showToast(R.string.import_success)
        WalletWrapper.MainAddress = JSONObject(walletStr).optString("mainAddress")
        EventBus.getDefault().post(EventNewAccount())
        Utils.clearAllData()
        startActivity(MainActivity::class.java)
    }

    private fun importWalletFailure() {
        dismissDialog()
        showToast(R.string.password_error)
    }


}