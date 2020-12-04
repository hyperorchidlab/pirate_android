package com.hop.pirate.ui.activity

import android.app.Activity
import androidx.lifecycle.Observer
import com.hop.pirate.BR
import com.hop.pirate.R
import com.hop.pirate.databinding.ActivityMinePoolBinding
import com.hop.pirate.model.bean.MinePoolBean
import com.hop.pirate.viewmodel.MinePoolVM
import com.nbs.android.lib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_own_pool.*

class MinePoolListActivity : BaseActivity<MinePoolVM,ActivityMinePoolBinding>() {

    companion object {
        var mCurrentMinePoolBean: MinePoolBean? = null
        var sMinePoolBeans: List<MinePoolBean>? = null
    }

    override fun getLayoutId(): Int = R.layout.activity_mine_pool

    override fun initView() {
        mViewModel.title.set(getString(R.string.my_pool))
        mViewModel.showBackImage.set(true)
        recyclerView.itemAnimator = null
    }

    override fun initData() {
        mViewModel.getMinePool()
    }

    override fun initObserve() {
        mViewModel.finishRefreshingEvent.observe(this,  Observer {
            swipeRefreshLayout.isRefreshing = false
        })

        mViewModel.finishAndResultOkEvent.observe(this, Observer {
            setResult(Activity.RESULT_OK)
            finish()
        })
    }

    override fun initVariableId(): Int = BR.viewModel

}