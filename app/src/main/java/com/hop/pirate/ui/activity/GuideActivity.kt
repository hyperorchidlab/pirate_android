package com.hop.pirate.ui.activity

import android.widget.TextView
import com.hop.pirate.R
import com.hop.pirate.ui.adapter.GuideAdapter
import com.hop.pirate.databinding.ActivityGuideBinding
import com.hop.pirate.viewmodel.GuideVM
import com.nbs.android.lib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_guide.*
import com.hop.pirate.BR

class GuideActivity : BaseActivity<GuideVM, ActivityGuideBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_guide

    override fun initView() {
        val titleTv = findViewById<TextView>(R.id.title_tv)
        titleTv.setText(R.string.fragment_account_operation_guide)
        viewpager.offscreenPageLimit = 4
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