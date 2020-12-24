package com.hop.pirate.viewmodel

import androidx.lifecycle.viewModelScope
import com.hop.pirate.R
import com.hop.pirate.ui.activity.MainActivity
import com.hop.pirate.event.EventLoadWalletSuccess
import com.hop.pirate.model.bean.WalletBean
import com.hop.pirate.model.MainModel
import com.hop.pirate.service.WalletWrapper
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.event.SingleLiveEvent
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MainVM : BaseViewModel() {
    val model = MainModel()
    val hindeFreeCoinEvent = SingleLiveEvent<Boolean>()

    fun getWalletInfo(isShowLoading: Boolean) {
        model.getWalletInfo().subscribe(object : SingleObserver<WalletBean> {
            override fun onSuccess(wallet: WalletBean?) {
                walletInfoSuccess(isShowLoading, wallet)
            }

            override fun onSubscribe(d: Disposable) {
                if (isShowLoading) {
                    showDialog(R.string.loading)
                }
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                walletInfoFailure(e)
            }
        })
    }

    fun syncSubPoolsData() {
        viewModelScope.launch {
            model.syncSubPoolsData()
        }
    }

    private fun walletInfoSuccess(isShowLoading: Boolean, walletBean: WalletBean?) {
        if (isShowLoading) {
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

    fun walletInfoFailure(throwable: Throwable) {
        showErrorToast(R.string.get_data_failed, throwable)
    }

}
