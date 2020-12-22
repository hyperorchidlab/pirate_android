package com.hop.pirate.viewmodel

import android.text.TextUtils
import androidx.databinding.ObservableField
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
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
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
            uc.toastEvent.value = R.string.create_account_enter_password
            return false
        } else if (!confirmPassword.get().equals(password.get())) {
            uc.toastEvent.value = R.string.twice_password_not_same
            return false
        }
        return true
    }

    fun createAccount() {

        model.createAccount(password.get()!!).subscribe(object : SingleObserver<String> {
            override fun onSuccess(wallet: String) {
                createSuccess(wallet)
            }

            override fun onSubscribe(d: Disposable) {
                showDialog(R.string.creating_account)
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                createFailure(e)
            }

        })
    }

    private fun createSuccess(it: String) {
        WalletWrapper.MainAddress = JSONObject(it).optString("mainAddress")
        initService(true)
    }

    private fun createFailure(t: Throwable) {
        dismissDialog()
        showErrorToast(R.string.create_account_failed, t)
    }

    val importCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showImportDialogEvent.call()
        }

    })


    fun importWallet(password: String, walletStr: String) {

        model.importWallet(walletStr, password).subscribe(object : SingleObserver<Any> {

            override fun onSubscribe(d: Disposable) {
                showDialog(R.string.loading)
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                importWalletFailure(e)
            }

            override fun onSuccess(t: Any) {
                importWalletSuccess(walletStr)
            }

        })
    }


    private fun importWalletSuccess(walletStr: String) {
        WalletWrapper.MainAddress = JSONObject(walletStr).optString("mainAddress")
        initService(false)
    }

    private fun importWalletFailure(t: Throwable) {
        dismissDialog()
        showErrorToast(R.string.password_error, t)
    }

    fun initService(isCreated: Boolean) {
        model.initService(HopApplication.instance.applicationContext)
            .subscribe(object : SingleObserver<Any> {
                override fun onSuccess(any: Any) {
                    initServiceSuccess(isCreated)
                }

                override fun onSubscribe(d: Disposable) {
                    addSubscribe(d)
                }

                override fun onError(e: Throwable) {
                    initServiceFailure()
                }

            })

    }

    private fun initServiceFailure() {
        exitEvent.call()
    }

    private fun initServiceSuccess(isCreated: Boolean) {
        dismissDialog()
        Utils.clearAllData()
        if (isCreated) {
            showToast(R.string.create_account_success)
            EventBus.getDefault().post(EventNewAccount())
        } else {
            showToast(R.string.import_success)
            EventBus.getDefault().post(EventNewAccount())
        }
        startActivity(MainActivity::class.java)
        finish()
    }

}