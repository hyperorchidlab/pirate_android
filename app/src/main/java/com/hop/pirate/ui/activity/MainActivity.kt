package com.hop.pirate.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidLib.AndroidLib
import androidLib.HopDelegate
import androidx.lifecycle.Observer
import com.hop.pirate.BR
import com.hop.pirate.Constants
import com.hop.pirate.HopApplication
import com.hop.pirate.R
import com.hop.pirate.callback.AlertDialogOkCallBack
import com.hop.pirate.databinding.ActivityMinePoolBinding
import com.hop.pirate.event.EventReLoadWallet
import com.hop.pirate.event.EventRechargeSuccess
import com.hop.pirate.event.EventSkipTabPacketsMarket
import com.hop.pirate.ui.fragement.TabHomeFragment
import com.hop.pirate.ui.fragement.TabPacketsMarketFragment
import com.hop.pirate.ui.fragement.TabWalletFragment
import com.hop.pirate.model.bean.WalletBean
import com.hop.pirate.service.HopService
import com.hop.pirate.service.WalletWrapper
import com.hop.pirate.util.Utils
import com.hop.pirate.viewmodel.MainVM
import com.hop.pirate.widget.navigator.BottomNavigatorAdapter
import com.hop.pirate.widget.navigator.BottomNavigatorView.OnBottomNavigatorViewItemClickListener
import com.hop.pirate.widget.navigator.FragmentNavigator
import com.nbs.android.lib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception

class MainActivity : BaseActivity<MainVM, ActivityMinePoolBinding>(), HopDelegate {

    companion object {
        var walletBean: WalletBean? = null
        const val TAG = "HopProtocol"
        const val ATSysSettingChanged = 1
        const val ATPoolsInMarketChanged = 2
        const val ATCounterDataRead = 3
        const val ATNeedToRecharge = 4
        const val ATRechargeSuccess = 5
    }

    private val mFragmentArray = arrayOf(
        TabHomeFragment::class.java,
        TabPacketsMarketFragment::class.java,
        TabWalletFragment::class.java
    )
    private lateinit var mNavigator: FragmentNavigator

    override fun onContentViewBefor(savedInstanceState: Bundle?) {
        super.onContentViewBefor(savedInstanceState)
        initNavigator(savedInstanceState)
    }

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView() {
        EventBus.getDefault().register(this)
        if (HopApplication.instance.isRunning) {
            return
        }
        AndroidLib.setDelegate(this)
        mViewModel.getWalletInfo(false)
        setCurrentTab(mNavigator.currentPosition)
        main_bottom_navigator_view.setOnBottomNavigatorViewItemClickListener(object :
            OnBottomNavigatorViewItemClickListener {
            override fun onBottomNavigatorViewItemClick(position: Int, view: View?) {
                setCurrentTab(position)
            }
        })
        get_free_coin_tv.setOnClickListener {
            setCurrentTab(Constants.TAB_WALLET)
            get_free_coin_tv.visibility = View.GONE
        }
    }

    override fun initObserve() {
        mViewModel.hindeFreeCoinEvent.observe(this, Observer {
            if (it) {
                get_free_coin_tv.visibility = View.GONE
            } else {
                get_free_coin_tv.visibility = View.VISIBLE
            }
        })


    }

    override fun initVariableId(): Int = BR.viewModel


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (HopApplication.instance.isRunning) {
            return
        }
        mViewModel.getWalletInfo(false)
        AndroidLib.setDelegate(this)
    }


    fun loadWallet(isShowLoading: Boolean) {
        mViewModel.getWalletInfo(isShowLoading)
    }

    private fun initNavigator(savedInstanceState: Bundle?) {
        val bottomNavigatorAdapter = BottomNavigatorAdapter(this)
        for (fragment in mFragmentArray) {
            bottomNavigatorAdapter.addTab(
                BottomNavigatorAdapter.TabInfo(
                    fragment.simpleName,
                    fragment,
                    null
                )
            )
        }
        mNavigator = FragmentNavigator(supportFragmentManager, bottomNavigatorAdapter, R.id.content_frame)
        mNavigator.setDefaultPosition(Constants.TAB_HOME)
        mNavigator.onCreate(savedInstanceState)
    }

    override fun initData() {}
    private fun setCurrentTab(position: Int) {
        mNavigator.showFragment(position)
        if (null != main_bottom_navigator_view) {
            main_bottom_navigator_view.select(position)
        }
        setFreeCoinStatus(position)
    }

    private fun setFreeCoinStatus(position: Int) {
        if (position != Constants.TAB_WALLET && WalletWrapper.EthBalance == 0.0) {
            get_free_coin_tv.visibility = View.VISIBLE
        } else {
            get_free_coin_tv.visibility = View.GONE

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addMiningPool(eventSkipTabPacketsMarket: EventSkipTabPacketsMarket) {
        setCurrentTab(Constants.TAB_RECHARGE)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mNavigator.onSaveInstanceState(outState)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun rechargeSuccess(eventRechargeSuccess: EventRechargeSuccess) {
        mViewModel.syncSubPoolsData()
        loadWallet(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventReLoadWallet(event: EventReLoadWallet) {
        loadWallet(event.showDialog)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(intent)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        WalletWrapper.closeWallet()
        if (!HopApplication.instance.isRunning) {
            AndroidLib.stopProtocol()
        }

        EventBus.getDefault().unregister(this)
    }

    override fun serviceExit(p0: java.lang.Exception?) {
        HopApplication.instance.isRunning = false
        HopService.stop()
    }

    override fun actionNotify(type: Short, msg: String?) {
        Log.w("Go", "actionNotify: type:[$type] msg:=>$msg")
        when (type.toInt()) {
            ATSysSettingChanged -> {
            }
            ATPoolsInMarketChanged -> {
            }
            ATNeedToRecharge -> showDialog(R.string.packets_insufficient_need_recharge)
            ATRechargeSuccess -> EventBus.getDefault()
                .post(EventRechargeSuccess())
            else -> {
            }
        }
    }

    override fun log(str: String) {
        Log.i("G0", str)
    }


}