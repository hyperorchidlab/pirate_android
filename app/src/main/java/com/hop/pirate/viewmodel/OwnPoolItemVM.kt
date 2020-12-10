package com.hop.pirate.viewmodel

import android.os.Bundle
import android.text.SpannableString
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.hop.pirate.IntentKey
import com.hop.pirate.ui.activity.RechargePacketsActivity
import com.hop.pirate.model.bean.OwnPool
import com.hop.pirate.model.OwnPoolModel
import com.hop.pirate.service.WalletWrapper
import com.hop.pirate.util.Utils
import com.nbs.android.lib.base.ItemViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import kotlinx.coroutines.launch


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class OwnPoolItemVM(var vm:OwnPoolVM,var own:OwnPool,var index:Int) : ItemViewModel<OwnPoolVM>(vm) {
    private val ownPoolModelImpl = OwnPoolModel()
    val packets = ObservableField<SpannableString>()
    val token = ObservableField<SpannableString>()
    val credit = ObservableField<SpannableString>()

    init {
        getItemPacket()
    }

    private fun getItemPacket() {
        viewModelScope.launch {
            runCatching {
                    own.address?.let {
                        ownPoolModelImpl.getPacketsByPool(
                            WalletWrapper.MainAddress,
                            it
                        )
                    }
            }.onSuccess {
                it?.let {
                    packets.set(Utils.formatText(Utils.convertBandWidth(it.packets),"\nPackets"))
                    token.set(Utils.formatText(Utils.convertCoin(it.token), " HOP\nToken"))
                    credit.set(Utils.formatText(it.credit.toString(), "\nCredit"))
                }
            }.onFailure {
            }

        }

    }

    val rechargeCommand= BindingCommand<Any>(object : BindingAction {
        override fun call() {
            val bundle = Bundle()
            bundle.putString(IntentKey.PoolKey,own.address)
            vm.startActivity(RechargePacketsActivity::class.java,bundle)
        }
    })
}