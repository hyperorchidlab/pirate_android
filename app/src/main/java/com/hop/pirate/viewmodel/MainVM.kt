package com.hop.pirate.viewmodel

import android.util.Log
import androidLib.HopDelegate
import androidx.lifecycle.viewModelScope
import com.hop.pirate.Constants
import com.hop.pirate.HopApplication
import com.hop.pirate.R
import com.hop.pirate.ui.activity.MainActivity
import com.hop.pirate.event.EventLoadWalletSuccess
import com.hop.pirate.event.EventRechargeSuccess
import com.hop.pirate.model.bean.WalletBean
import com.hop.pirate.model.MainModel
import com.hop.pirate.service.HopService
import com.hop.pirate.service.WalletWrapper
import com.hop.pirate.util.Utils
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.event.SingleLiveEvent
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MainVM : BaseViewModel(), HopDelegate {
    val model = MainModel()
    val hindeFreeCoinEvent = SingleLiveEvent<Boolean>()
    val initServiceFailEvent = SingleLiveEvent<Boolean>()
    fun initService() {
        viewModelScope.launch {
            kotlin.runCatching {
                model.initService(HopApplication.instance.applicationContext, this@MainVM)
            }.onSuccess {
                onInitServiceSuccess()
            }.onFailure {
                initServiceFailEvent.postValue(true)
            }
        }

    }

    private fun onInitServiceSuccess() {
        getWalletInfo(false)
    }

    fun getWalletInfo(isShowLoading: Boolean) {
        if(isShowLoading){
            showDialog(R.string.loading)
        }
        viewModelScope.launch {
            kotlin.runCatching {
                model.getWalletInfo()
            }.onSuccess {
                onWalletInfoSuccess(isShowLoading,it)
            }.onFailure {
                showErrorToast(R.string.get_data_failed,it)
            }
        }


    }


    fun syncSubPoolsData() {
        viewModelScope.launch {
            model.syncSubPoolsData()
        }


    }

    private fun onWalletInfoSuccess(isShowLoading: Boolean, walletBean: WalletBean?) {
        if(isShowLoading){
            dismissDialog()
        }
        if (walletBean != null) {
            MainActivity.walletBean = walletBean
            WalletWrapper.MainAddress = walletBean.main
            WalletWrapper.SubAddress = walletBean.sub
            WalletWrapper.EthBalance = walletBean.eth
            WalletWrapper.HopBalance = walletBean.hop
            WalletWrapper.Approved = walletBean.approved
            if (MainActivity.walletBean!!.hop != 0.0) {
                hindeFreeCoinEvent.postValue(true)
            }
            EventBus.getDefault().postSticky(EventLoadWalletSuccess())
        } else {
            showToast(R.string.get_data_failed)
        }
    }

    override fun serviceExit(p0: java.lang.Exception?) {
        HopApplication.instance.isRunning = false
        HopService.stop()
    }

    override fun actionNotify(type: Short, msg: String?) {
        Log.w("Go", "actionNotify: type:[$type] msg:=>$msg")
        when (type.toInt()) {
            MainActivity.ATSysSettingChanged -> {
            }
            MainActivity.ATPoolsInMarketChanged -> {
            }
            MainActivity.ATNeedToRecharge -> showDialog(R.string.packets_insufficient_need_recharge)
            MainActivity.ATRechargeSuccess -> EventBus.getDefault()
                .post(EventRechargeSuccess())
            else -> {
            }
        }
    }

    override fun log(str: String) {
        Log.i("G0", str)
    }
}
