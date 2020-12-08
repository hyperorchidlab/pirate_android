package com.hop.pirate.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.viewModelScope
import com.hop.pirate.BR
import com.hop.pirate.R
import com.hop.pirate.model.bean.MinePoolBean
import com.hop.pirate.model.TabPacketsMarketModel
import com.hop.pirate.ui.activity.OwnPoolActivity
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
class TabPacketsMarketVM : BaseViewModel() {
    val model = TabPacketsMarketModel()
    val showEmptyLayoutEvent = SingleLiveEvent<Boolean>().apply { value = false }
    val items: ObservableList<PacketsMarketItemVM> = ObservableArrayList()
    val itemBinding = ItemBinding.of<PacketsMarketItemVM>(BR.item, R.layout.item_market_pool)
    var finishRefreshingEvent = SingleLiveEvent<Any>()
    val refreshCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            getPoolInfo(true)

        }
    })

   override fun clickRightTv(){
       startActivity(OwnPoolActivity::class.java)
    }

    fun getPoolInfo(syncAllPools: Boolean) {
        viewModelScope.launch {
            kotlin.runCatching {
                model.getPoolInfo(syncAllPools)
            }.onSuccess {
                onGetPoolInfoSuccess(it)
            }.onFailure {
                onGetPoolInfoFailure(it)
            }
        }
    }

    private fun onGetPoolInfoFailure(t: Throwable) {
        finishRefreshingEvent.call()
        showErrorToast(R.string.get_data_failed,t)
        showEmptyLayoutEvent.value = items.size==0
    }

    private fun onGetPoolInfoSuccess(minePoolBeans: List<MinePoolBean>) {
        finishRefreshingEvent.call()
        items.clear()
        for ((index,minePoolBean) in minePoolBeans.withIndex()) {
            items.add(PacketsMarketItemVM(this,minePoolBean,index))
        }
        showEmptyLayoutEvent.value = items.size==0
    }

}