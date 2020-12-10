package com.hop.pirate.ui.activity

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.hop.pirate.BR
import com.hop.pirate.IntentKey
import com.hop.pirate.R
import com.hop.pirate.ui.adapter.FlowSelectAdapter
import com.hop.pirate.ui.adapter.FlowSelectAdapter.RechargeFlowState
import com.hop.pirate.databinding.ActivityRechargePacketsBinding
import com.hop.pirate.dialog.PayPasswordDialog
import com.hop.pirate.dialog.PayPasswordDialog.PasswordCallBack
import com.hop.pirate.service.WalletWrapper
import com.hop.pirate.util.Utils
import com.hop.pirate.viewmodel.RechargePacketsVM
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.v3.MessageDialog
import com.nbs.android.lib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_recharge_packets.*

class RechargePacketsActivity : BaseActivity<RechargePacketsVM,ActivityRechargePacketsBinding>(), RechargeFlowState {
    private var mPoolAddress: String? = null
    private var tokenNO = 0.0

    override fun getLayoutId(): Int = R.layout.activity_recharge_packets
    override fun initView() {
        mViewModel.title.set(getString(R.string.recharge_recharge_flow))
        mViewModel.showBackImage.set(true)
        hop_address_et.text = WalletWrapper.MainAddress
        hop_coin_number_tv.text = Utils.convertCoin(WalletWrapper.HopBalance)
        flow_recyclerview.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        mViewModel.initFlows()
    }

    override fun initData() {
        mPoolAddress = intent.getStringExtra(IntentKey.PoolKey)
        mViewModel.poolAddress.set(mPoolAddress)
        hop_coin_tv.text = getString(R.string.recharge_hop_coin)
    }

    override fun initObserve() {
        mViewModel.bytePreTokenEvent.observe(this, Observer {
            flow_recyclerview.adapter = FlowSelectAdapter(this@RechargePacketsActivity, it, this@RechargePacketsActivity)
        })

        mViewModel.syncPoolSuccessEvent.observe(this, Observer {
            showSyncPoolSuccessDialog()
        })
    }

    private fun showSyncPoolSuccessDialog() {
        val content = getString(R.string.recharge_sync_pool_success)
        MessageDialog.show(this@RechargePacketsActivity, getString(R.string.tips), content, getString(R.string.sure)).onOkButtonClickListener = OnDialogButtonClickListener { _, _ ->
            setResult(Activity.RESULT_OK)
            finish()
            false
        }

    }

    override fun initVariableId(): Int = BR.viewModel

    override fun recharge(tokenNO: Double) {
        this.tokenNO = tokenNO
        mViewModel.tokenNO = tokenNO
        if (WalletWrapper.EthBalance / Utils.COIN_DECIMAL < 0.0001) {
            Utils.toastTips(getString(R.string.eth_insufficient_balance))
            return
        }
        if (WalletWrapper.HopBalance < tokenNO) {
            Utils.toastTips(getString(R.string.token_insufficient_balance))
            return
        }
        PayPasswordDialog(this, object : PasswordCallBack {
            override fun callBack(password: String) {
                mViewModel.openWallet(password)
            }
        }).show()
    }






}