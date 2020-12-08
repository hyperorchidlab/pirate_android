package com.hop.pirate.ui.activity

import com.hop.pirate.BR
import com.hop.pirate.R
import com.hop.pirate.databinding.ActivityGuideBinding
import com.hop.pirate.ui.adapter.GuideAdapter
import com.hop.pirate.viewmodel.GuideVM
import com.nbs.android.lib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_guide.*

class GuideActivity : BaseActivity<GuideVM, ActivityGuideBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_guide

    override fun initView() {
        mViewModel.showBackImage.set(true)
        mViewModel.title.set(getString(R.string.tab_account_operation_guide))
    }

    override fun initData() {
        val guideAdapter =
            GuideAdapter(this, mViewModel.images)
        viewpager.adapter = guideAdapter
        indicator.setViewPager(viewpager)
        guideAdapter.registerDataSetObserver(indicator.dataSetObserver)
    }

    override fun initObserve() {
    }

    override fun initVariableId(): Int =BR.viewModel
}