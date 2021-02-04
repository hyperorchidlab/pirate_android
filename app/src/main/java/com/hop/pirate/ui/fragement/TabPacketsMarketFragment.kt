package com.hop.pirate.ui.fragement

import android.os.Handler
import androidx.lifecycle.Observer
import com.hop.pirate.BR
import com.hop.pirate.R
import com.hop.pirate.databinding.FragmentPacketsMarketBinding
import com.hop.pirate.ui.activity.MainActivity
import com.hop.pirate.ui.activity.RechargePacketsActivity
import com.hop.pirate.viewmodel.TabPacketsMarketVM
import com.nbs.android.lib.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_packets_market.*
import zhy.com.highlight.HighLight
import zhy.com.highlight.position.OnBottomPosCallback
import zhy.com.highlight.shape.RectLightShape

class TabPacketsMarketFragment : BaseFragment<TabPacketsMarketVM, FragmentPacketsMarketBinding>() {

    lateinit var higghtLight: HighLight
    var isFirstCreate: Boolean = true

    override fun getLayoutId(): Int = R.layout.fragment_packets_market

    override fun initView() {
        recyclerView.itemAnimator = null
        mViewModel.title.set(getString(R.string.tab_flow_market))
        mViewModel.showRightText.set(true)
        mViewModel.rightText.set(getString(R.string.market_my_pool))
    }

    override fun initData() {
        swipeRefreshLayout.isRefreshing = true
        mViewModel.getPoolInfo(false)
    }

    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
        mViewModel.finishRefreshingEvent.observe(this, Observer {
            swipeRefreshLayout.isRefreshing = false
            if (MainActivity.firstCreateAccountActivity && isFirstCreate) {
                isFirstCreate = false
                if (MainActivity.firstCreateAccountActivity) {
                    showGuide()

                }
            }
        })
    }

    fun showGuide() {
        higghtLight = HighLight(mActivity).autoRemove(true).intercept(true).setClickCallback {
                    startActivity(RechargePacketsActivity::class.java)
                }.setOnLayoutCallback {
                    higghtLight.addHighLight(R.id.recharge_tv, R.layout.guide5, OnBottomPosCallback(10F), RectLightShape()).show()
                }

    }

}