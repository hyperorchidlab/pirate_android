package com.hop.pirate.ui.activity

import androidx.lifecycle.Observer
import com.hop.pirate.R
import com.hop.pirate.BR
import com.hop.pirate.databinding.ActivityTransactionBinding
import com.hop.pirate.viewmodel.TransactionVM
import com.nbs.android.lib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_own_pool.*

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TransactionActivity:BaseActivity<TransactionVM,ActivityTransactionBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_transaction

    override fun initView() {
        mViewModel.title.set(getString(R.string.wallet_transaction))
        mViewModel.showBackImage.set(true)
        recyclerView.itemAnimator = null
    }

    override fun initData() {
    }

    override fun initObserve() {
        mViewModel.finishRefreshingEvent.observe(this,  Observer() {
            swipeRefreshLayout.isRefreshing = false
        })
    }

    override fun initVariableId(): Int = BR.viewModel

}