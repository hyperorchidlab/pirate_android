package com.hop.pirate.viewmodel

import android.os.Bundle
import android.text.SpannableString
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.hop.pirate.IntentKey
import com.hop.pirate.ui.activity.RechargePacketsActivity
import com.hop.pirate.model.bean.OwnPool
import com.hop.pirate.model.OwnPoolModel
import com.hop.pirate.model.bean.UserPoolData
import com.hop.pirate.service.WalletWrapper
import com.hop.pirate.util.Utils
import com.nbs.android.lib.base.ItemViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class OwnPoolItemVM(var vm: OwnPoolVM, var own: OwnPool, var index: Int) :
    ItemViewModel<OwnPoolVM>(vm) {
    private val model = OwnPoolModel()
    val packets = ObservableField<SpannableString>()
    val token = ObservableField<SpannableString>()
    val credit = ObservableField<SpannableString>()

    init {
        getItemPacket()
    }

    private fun getItemPacket() {
        own.address?.let {
            model.getPacketsByPool(WalletWrapper.MainAddress, it)
                .subscribe(object : SingleObserver<UserPoolData> {
                    override fun onSuccess(userPool: UserPoolData) {
                        packets.set(Utils.formatText(Utils.convertBandWidth(userPool.packets), "\nPackets"))
                        token.set(Utils.formatText(Utils.convertCoin(userPool.token), " HOP\nToken"))
                        credit.set(Utils.formatText(userPool.credit.toString(), "\nCredit"))
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        println(e.message)
                    }
                })
        }

    }

    val rechargeCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            val bundle = Bundle()
            bundle.putString(IntentKey.PoolKey, own.address)
            vm.startActivity(RechargePacketsActivity::class.java, bundle)
        }
    })
}