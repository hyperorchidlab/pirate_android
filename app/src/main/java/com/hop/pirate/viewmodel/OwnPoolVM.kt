package com.hop.pirate.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.viewModelScope
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import me.tatarka.bindingcollectionadapter2.ItemBinding
import com.hop.pirate.BR
import com.hop.pirate.R
import com.hop.pirate.model.bean.OwnPool
import com.hop.pirate.model.OwnPoolModel
import com.nbs.android.lib.event.SingleLiveEvent
import kotlinx.coroutines.launch


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class OwnPoolVM : BaseViewModel() {
    val model = OwnPoolModel()
    val showEmptyLayoutEvent = SingleLiveEvent<Boolean>().apply { value = false }

    var finishRefreshingEvent = SingleLiveEvent<Any>()
    val items: ObservableList<OwnPoolItemVM> = ObservableArrayList()
    val itemBinding = ItemBinding.of<OwnPoolItemVM>(BR.item, R.layout.item_own)
    val refreshCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            getOwnPool()

        }
    })

    fun getOwnPool() {
        viewModelScope.launch {
            runCatching {
                model.getPoolDataOfUser()
            }.onSuccess {
                requestSuccess(it)
            }.onFailure {
                requestFailure(it)

            }

        }
    }

    private fun requestFailure(t: Throwable) {
        finishRefreshingEvent.call()
        showErrorToast(R.string.get_data_failed,t)
        showEmptyLayoutEvent.value = items.size == 0
    }

    private fun requestSuccess(ownPools: List<OwnPool>?) {
        finishRefreshingEvent.call()
        items.clear()
        var index = 0
        ownPools?.forEach {
            items.add(OwnPoolItemVM(this, it, index))
            index++
        }
        showEmptyLayoutEvent.value = items.size == 0
    }

}