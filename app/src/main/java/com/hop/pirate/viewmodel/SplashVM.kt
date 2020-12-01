package com.hop.pirate.viewmodel

import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.hop.pirate.HopApplication
import com.hop.pirate.ui.activity.MainActivity
import com.hop.pirate.model.bean.AppVersionBean
import com.hop.pirate.model.impl.SplashModelImpl
import com.hop.pirate.service.WalletWrapper
import com.hop.pirate.ui.activity.CreateAccountActivity
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.event.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class SplashVM : BaseViewModel() {
    private val splashModelImpl = SplashModelImpl()
    val delayLoadWalletEvent = SingleLiveEvent<AppVersionBean?>()

    fun loadWallet() {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    splashModelImpl.loadWallet(HopApplication.getApplication().applicationContext)
                }
            }.onSuccess {
                loadWalletSuccess(it)
            }.onFailure {
                loadWalletFailure()
            }

        }

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
            startActivity(MainActivity::class.java)
            finish()
        }

    }


    fun checkVersion() {
        viewModelScope.launch {
            kotlin.runCatching {
                splashModelImpl.checkVersion()
            }.onSuccess {
                checkVersionSuccess(it)
            }.onFailure {
                checkVersionFailure()
            }
        }
    }

    private fun checkVersionFailure() {
        delayLoadWalletEvent.postValue(null)
    }

    private fun checkVersionSuccess(it: AppVersionBean?) {
        delayLoadWalletEvent.postValue(it)
    }

}