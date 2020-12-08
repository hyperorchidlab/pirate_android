package com.hop.pirate.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.viewModelScope
import com.hop.pirate.BR
import com.hop.pirate.R
import com.hop.pirate.model.bean.OwnPool
import com.hop.pirate.model.OwnPoolModel
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.event.SingleLiveEvent
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MinePoolVM : BaseViewModel() {
    val model = OwnPoolModel()
    val showEmptyLayoutEvent = SingleLiveEvent<Boolean>().apply { value = false }
    var finishRefreshingEvent = SingleLiveEvent<Any>()
    var finishAndResultOkEvent = SingleLiveEvent<Any>()
    val items: ObservableList<MinePooIItemVM> = ObservableArrayList()
    val itemBinding = ItemBinding.of<MinePooIItemVM>(BR.item, R.layout.item_mine_pool)
    val refreshCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            getMinePool()

        }
    })

    fun getMinePool(){
        viewModelScope.launch {
            runCatching {
                    model.getPoolDataOfUser()
            }.onSuccess {
                requestSuccess(it)
                finishRefreshingEvent.call()
            }.onFailure {
                requestFailure(it)
                finishRefreshingEvent.call()
            }

        }
    }

    private fun requestFailure(t: Throwable) {
        showErrorToast(R.string.get_data_failed,t)
        if(items.size==0){
            showEmptyLayoutEvent.value= true
        }
    }

    private fun requestSuccess(ownPools: List<OwnPool>?) {
        items.clear()
        var index =0
        ownPools?.forEach {
            items.add(MinePooIItemVM(this,it))
            index++
        }
        showEmptyLayoutEvent.value = items.size==0
    }

}