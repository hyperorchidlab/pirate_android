package com.hop.pirate.viewmodel

import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.hop.pirate.HopApplication
import com.hop.pirate.model.SplashModel
import com.hop.pirate.model.bean.AppVersionBean
import com.hop.pirate.service.WalletWrapper
import com.hop.pirate.ui.activity.CreateAccountActivity
import com.hop.pirate.ui.activity.MainActivity
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.event.SingleLiveEvent
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class SplashVM : BaseViewModel() {
    val model = SplashModel()
    val delayLoadWalletEvent = SingleLiveEvent<AppVersionBean?>()
    val initServiceFailEvent = SingleLiveEvent<Boolean>()

    fun loadWallet() {
        viewModelScope.launch {
            runCatching {
                model.loadWallet(HopApplication.instance.applicationContext)
            }.onSuccess {
                loadWalletSuccess(it)
            }.onFailure {
                loadWalletFailure()
            }

        }

    }

    fun initService() {
        model.initService(HopApplication.instance.applicationContext)
            .subscribe(object : SingleObserver<Any> {
                override fun onSuccess(t: Any) {
                    initServiceSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    addSubscribe(d)
                }

                override fun onError(e: Throwable) {
                    initServiceFailure()
                }
            })
    }

    private fun initServiceSuccess() {
        startActivity(MainActivity::class.java)
        finish()
    }

    private fun initServiceFailure() {
        initServiceFailEvent.postValue(true)
    }

    private fun loadWalletFailure() {
        startActivity(CreateAccountActivity::class.java)
        finish()
    }

    private fun loadWalletSuccess(walletJson: String) {
        if (TextUtils.isEmpty(walletJson)) {
            startActivity(CreateAccountActivity::class.java)
            finish()
        } else {
            WalletWrapper.MainAddress = JSONObject(walletJson).optString("mainAddress")
            initService()
        }

    }


    fun checkVersion() {
        viewModelScope.launch {
            kotlin.runCatching {
                model.checkVersion()
            }.onSuccess {
                checkVersionSuccess(it)
            }.onFailure {
                checkVersionFailure()
            }
        }
    }

    private fun checkVersionFailure() {
        delayLoadWalletEvent.postValue(AppVersionBean())
    }

    private fun checkVersionSuccess(it: AppVersionBean?) {
        delayLoadWalletEvent.postValue(it)
    }


}