package com.hop.pirate.viewmodel

import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.hop.pirate.HopApplication
import com.hop.pirate.ui.activity.MainActivity
import com.hop.pirate.model.bean.AppVersionBean
import com.hop.pirate.model.SplashModel
import com.hop.pirate.service.WalletWrapper
import com.hop.pirate.ui.activity.CreateAccountActivity
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.event.SingleLiveEvent
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
        viewModelScope.launch {
            kotlin.runCatching {
                model.initService(HopApplication.instance.applicationContext)
            }.onSuccess {
                onInitServiceSuccess()
            }.onFailure {
                println("~~~~~~~~~~~~~~~${it.message}")
                initServiceFailEvent.postValue(true)
            }
        }

    }

    private fun onInitServiceSuccess() {

        startActivity(MainActivity::class.java)
        finish()
    }

    private fun loadWalletFailure() {
            startActivity(CreateAccountActivity::class.java)
            finish()
    }

    private fun loadWalletSuccess(walletJson: String) {
        if(TextUtils.isEmpty(walletJson)){
            startActivity(CreateAccountActivity::class.java)
            finish()
        }else{
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