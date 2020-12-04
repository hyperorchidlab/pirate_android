package com.hop.pirate.viewmodel

import android.os.Handler
import com.hop.pirate.HopApplication
import com.hop.pirate.R
import com.hop.pirate.ui.activity.MineMachineListActivity.Companion.sMinerBeans
import com.hop.pirate.event.EventReloadPoolsMarket
import com.hop.pirate.model.OwnPoolModel
import com.hop.pirate.model.bean.OwnPool
import com.hop.pirate.service.HopService
import com.hop.pirate.service.SysConf
import com.nbs.android.lib.base.ItemViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import org.greenrobot.eventbus.EventBus

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MinePooIItemVM(vm: MinePoolVM, var pool: OwnPool) : ItemViewModel<MinePoolVM>(vm) {


    val clickCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            if (pool.address == SysConf.CurPoolAddress) {
                viewModel.finish()
                return
            }
            if (HopApplication.getApplication().isRunning) {
                HopApplication.getApplication().isRunning = false
                HopService.stop()
            }
            Handler().postDelayed({
                viewModel.showDialog(R.string.mining_pool_exchange_mine_pool)
                Thread(Runnable {
                    sMinerBeans = null
                    SysConf.changeCurPool(pool.address, pool.name)
                    EventBus.getDefault().post(EventReloadPoolsMarket())
                    viewModel.dismissDialog()
                    viewModel.finishAndResultOkEvent.postValue(null)
                }).start()
            }, 200)
        }
    })
}