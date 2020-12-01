package com.hop.pirate.ui.activity

import android.app.Activity
import androidx.lifecycle.Observer
import com.hop.pirate.BR
import com.hop.pirate.R
import com.hop.pirate.databinding.ActivityMineMachineBinding
import com.hop.pirate.model.bean.MinerBean
import com.hop.pirate.service.SysConf
import com.hop.pirate.viewmodel.MineMachineListVM
import com.nbs.android.lib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_mine_machine.*

class MineMachineListActivity : BaseActivity<MineMachineListVM, ActivityMineMachineBinding>(){
    override fun getLayoutId(): Int = R.layout.activity_mine_machine

    override fun initView() {
        swipeRefreshLayout.setRefreshing(true)
        recyclerView.itemAnimator = null
    }

    override fun initObserve() {
        mViewModel.finishRefreshingEvent.observe(this,  Observer() {
            swipeRefreshLayout.setRefreshing(false)
        });

        mViewModel.finishAndResultOk.observe(this,  Observer() {
            setResult(Activity.RESULT_OK,null)
            finish()
        });
    }

    override fun initVariableId(): Int = BR.viewModel


    override fun initData() {
       mViewModel.getMachineList(SysConf.CurPoolAddress,16)
    }


    companion object {
        @JvmStatic
       var sMinerBeans: List<MinerBean>? = null
    }

}