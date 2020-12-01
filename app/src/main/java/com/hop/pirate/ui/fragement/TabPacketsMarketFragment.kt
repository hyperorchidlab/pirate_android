package com.hop.pirate.ui.fragement

import androidx.lifecycle.Observer
import com.hop.pirate.BR
import com.hop.pirate.R
import com.hop.pirate.databinding.FragmentPacketsMarketBinding
import com.hop.pirate.viewmodel.TabPacketsMarketVM
import com.nbs.android.lib.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_packets_market.*

class TabPacketsMarketFragment : BaseFragment<TabPacketsMarketVM, FragmentPacketsMarketBinding>(){

    override fun getLayoutId(): Int = R.layout.fragment_packets_market

    override fun initView() {
        recyclerView.itemAnimator = null
        mViewModel.title.set(getString(R.string.tab_flow_market))
        mViewModel.showRightText.set(true)
        mViewModel.rightText.set(getString(R.string.my_pool))
    }
    override fun initData() {
        swipeRefreshLayout.isRefreshing = true
        mViewModel.getPoolInfo(false)
    }
    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
        mViewModel.finishRefreshingEvent.observe(this, Observer {
            swipeRefreshLayout.isRefreshing = false
        })
    }


}