package com.hop.pirate.viewmodel

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.hop.pirate.HopApplication
import com.hop.pirate.IntentKey
import com.hop.pirate.R
import com.hop.pirate.model.TabPacketsMarketModel
import com.hop.pirate.model.bean.MinePoolBean
import com.hop.pirate.ui.activity.RechargePacketsActivity
import com.nbs.android.lib.base.BaseModel
import com.nbs.android.lib.base.ItemViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.utils.dp

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class PacketsMarketItemVM(VM:TabPacketsMarketVM, var minePool:MinePoolBean, var index: Int):ItemViewModel<TabPacketsMarketVM>(VM) {
    private val colorIds = intArrayOf(R.color.color_6d97ce, R.color.color_f7aa6e, R.color.color_4cc2d0)
    var background = ContextCompat.getDrawable(HopApplication.getApplication().applicationContext,R.drawable.bg_rectangle_round3_ffffff) as GradientDrawable
    var textColor = ContextCompat.getColor(HopApplication.getApplication().applicationContext, colorIds[index%3])
    init{
        background.setColor(textColor)
        background.cornerRadius = 8f.dp
    }
    val rechargeCommand = BindingCommand<Any>(object :BindingAction{
        override fun call() {
            val bundle = Bundle()
            bundle.putString(IntentKey.PoolKey,minePool.address)
            viewModel.startActivity(RechargePacketsActivity::class.java,bundle)
        }

    })
}