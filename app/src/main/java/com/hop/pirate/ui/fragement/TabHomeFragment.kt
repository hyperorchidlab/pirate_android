package com.hop.pirate.ui.fragement

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.text.TextUtils
import androidLib.AndroidLib
import androidx.lifecycle.Observer
import com.hop.pirate.BR
import com.hop.pirate.Constants
import com.hop.pirate.HopApplication
import com.hop.pirate.R
import com.hop.pirate.callback.AlertDialogOkCallBack
import com.hop.pirate.databinding.FragmentHomeBinding
import com.hop.pirate.event.*
import com.hop.pirate.model.bean.ExtendToken
import com.hop.pirate.service.HopService
import com.hop.pirate.service.SysConf
import com.hop.pirate.service.WalletWrapper
import com.hop.pirate.ui.activity.MineMachineListActivity
import com.hop.pirate.ui.activity.MinePoolListActivity
import com.hop.pirate.util.Utils
import com.hop.pirate.viewmodel.TabHomeVM
import com.nbs.android.lib.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TabHomeFragment : BaseFragment<TabHomeVM, FragmentHomeBinding>() {
    private var mHopIntent: Intent? = null
    override fun getLayoutId(): Int = R.layout.fragment_home
    override fun initView() {
        mViewModel.title.set(getString(R.string.app_name))
        EventBus.getDefault().register(this)
        loadLocalConf()
        showPacketsData()

        if (AndroidLib.isGlobalMode()) {
            global_model_rbtn.isChecked = true
        } else {
            intelligent_model_rbtn.isChecked = true
        }
        if (HopApplication.getApplication().isRunning) {
            pirate_network_status_tv.setText(getString(R.string.use))
        } else {
            pirate_network_status_tv.setText(getString(R.string.disconnected))
        }
    }

    override fun initData() {}
    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
        mViewModel.selectPoolLiveEvent.observe(this, Observer {
            val intent = Intent(mActivity, MinePoolListActivity::class.java)
            startActivityForResult(intent, Constants.REQUEST_MINE_POOL_CODE)
        })

        mViewModel.selectMinerLiveEvent.observe(this, Observer {
            if (TextUtils.isEmpty(SysConf.CurPoolAddress)) {
                Utils.toastTips(getString(R.string.select_subscribed_mining_pool))
                return@Observer
            }
            val intent = Intent(mActivity, MineMachineListActivity::class.java)
            startActivityForResult(intent, Constants.REQUEST_MINE_MACHINE_CODE)
        })

        mViewModel.changeVPNStatusEvent.observe(this,
            Observer { changeVPNStatus() })

        mViewModel.getPoolSuccessEvent.observe(this,
            Observer { showPacketsData() })

        mViewModel.openWalletSuccessEvent.observe(this,
            Observer {
                showDialog(R.string.connect)
                mHopIntent = Intent(mActivity, HopService::class.java)
                mActivity.startService(mHopIntent)
            })
    }

    private fun showPacketsData() {
        if (TextUtils.isEmpty(SysConf.CurPoolAddress)) {
            miner_pool_tv.text = resources.getString(R.string.select_subscribed_mining_pool)
        } else {
            miner_pool_tv.text = SysConf.CurPoolName
        }
        if (TextUtils.isEmpty(SysConf.CurMinerID)) {
            miner_machin_tv.text = resources.getString(R.string.select_miner)
        } else {
            miner_machin_tv.text = SysConf.CurMinerID
        }
        use_flow_tv.text = Utils.ConvertBandWidth(SysConf.PacketsBalance)
        uncleared_tv.text = Utils.ConvertBandWidth(SysConf.PacketsCredit)
    }

    private fun loadLocalConf() {
        SysConf.CurPoolAddress = Utils.getString(SysConf.KEY_CACHED_POOL_IN_USE, "")
        SysConf.CurPoolName = Utils.getString(SysConf.KEY_CACHED_POOL_NAME_IN_USE, "")
        val mKey = String.format(SysConf.KEY_CACHED_MINER_ID_IN_USE, SysConf.CurPoolAddress)
        SysConf.CurMinerID = Utils.getString(mKey, "")
        if (TextUtils.isEmpty(SysConf.CurPoolAddress) || TextUtils.isEmpty(WalletWrapper.MainAddress)) {
            SysConf.PacketsBalance = 0.0
            SysConf.PacketsCredit = 0.0
            showPacketsData()
        }
    }


    private fun changeVPNStatus() {
        if (!Utils.isFastClick()) {
            return
        }
        if (!checkMessageForStartVpnService()) {
            return
        }
        if (HopApplication.getApplication().isRunning) {
            HopApplication.getApplication().isRunning = false
            HopService.stop()
        } else {
            vpnPrepare()
        }
    }

    private fun vpnPrepare() {
        val ii = VpnService.prepare(mActivity)
        if (ii != null) {
            startActivityForResult(ii,
                RC_VPN_RIGHT
            )
        } else {
            onActivityResult(RC_VPN_RIGHT, Activity.RESULT_OK, null)
        }
    }

    private fun checkMessageForStartVpnService(): Boolean {
        if (TextUtils.isEmpty(SysConf.CurPoolAddress)) {
            Utils.toastTips(getString(R.string.select_subscribed_mining_pool))
            return false
        }
        if (TextUtils.isEmpty(SysConf.CurMinerID)) {
            Utils.toastTips(getString(R.string.select_miner))
            return false
        }
        return true
    }

    private fun startVpnService() {
        showDialog(R.string.connect)
        mHopIntent = Intent(mActivity, HopService::class.java)
        mActivity.startService(mHopIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == Constants.REQUEST_MINE_POOL_CODE || requestCode == Constants.REQUEST_MINE_MACHINE_CODE) {
            if (HopApplication.getApplication().isRunning) {
                HopApplication.getApplication().isRunning = false
                HopService.stop()
            }
            showPacketsData()
        } else if (RC_VPN_RIGHT == requestCode) {
            if (WalletWrapper.isOpen()) {
                startVpnService()
            } else {
                showInputPasswordDialog()
            }
        }
    }

    private fun showInputPasswordDialog() {
        Utils.showPassword(mActivity, object : AlertDialogOkCallBack() {
            override fun onClickOkButton(password: String) {
                mViewModel.openWallet(password)
            }
        })
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun VPNOpen(eventVPNOpen: EventVPNOpen?) {
        pirate_network_status_tv.text = getString(R.string.use)
        dismissDialog()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun VPNClose(eventVPNClosed: EventVPNClosed?) {
        pirate_network_status_tv.text = getString(R.string.disconnected)
        dismissDialog()
        mViewModel.getPool()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun rechargeSuccess(eventRechargeSuccess: EventRechargeSuccess?) {
        mViewModel.getPool()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventReloadPoolsMarket(eventReloadPoolsMarket: EventReloadPoolsMarket?) {
        mViewModel.getPool()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loadWalletSuccess(eventLoadWalletSuccess: EventLoadWalletSuccess?) {
        loadLocalConf()
        mViewModel.getPool()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        private const val RC_VPN_RIGHT = 126
    }
}