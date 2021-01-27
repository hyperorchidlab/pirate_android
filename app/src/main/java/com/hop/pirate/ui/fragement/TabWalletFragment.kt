package com.hop.pirate.ui.fragement

import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import com.hop.pirate.*
import com.hop.pirate.callback.AlertDialogOkCallBack
import com.hop.pirate.databinding.FragmentWalletBinding
import com.hop.pirate.event.EventLoadWalletSuccess
import com.hop.pirate.event.EventReLoadWallet
import com.hop.pirate.event.EventRechargeSuccess
import com.hop.pirate.model.TabWalletModel
import com.hop.pirate.service.HopService
import com.hop.pirate.service.WalletWrapper
import com.hop.pirate.ui.activity.CreateAccountActivity
import com.hop.pirate.ui.activity.MainActivity
import com.hop.pirate.ui.activity.MainNetAddressQRCodeActivity
import com.hop.pirate.util.Utils
import com.hop.pirate.viewmodel.TabWalletVM
import com.kongzue.dialog.interfaces.OnInputDialogButtonClickListener
import com.kongzue.dialog.v3.InputDialog
import com.kongzue.dialog.v3.MessageDialog
import com.nbs.android.lib.base.BaseFragment
import com.nbs.android.lib.utils.toast
import kotlinx.android.synthetic.main.fragment_wallet.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TabWalletFragment : BaseFragment<TabWalletVM, FragmentWalletBinding>() {
    private var mTabSettingModel: TabWalletModel? = null

    override fun getLayoutId(): Int = R.layout.fragment_wallet

    override fun initView() {
        mViewModel.title.set(getString(R.string.tab_wallet))
        mViewModel.showRightImage.set(true)
        mTabSettingModel = TabWalletModel()
        EventBus.getDefault().register(this)
        val netType = Utils.getInt(Constants.NET_TYPE, Constants.DEFAULT_MAIN_NET)
        if (netType != Constants.TEST_NET) {
            hineGetFreeCoin()

        }
        val newDns = Utils.getString(Constants.NEW_DNS, Constants.DNS)
        mViewModel.dnsObservable.set(resources.getString(R.string.wallet__dns) + newDns)
        val versionText = getString(R.string.splash_current_version_name) + Utils.getVersionName(mActivity)
        mViewModel.versionObservable.set(versionText)

        main_network_address_value_tv.setOnLongClickListener(View.OnLongClickListener {
            if (!TextUtils.isEmpty(main_network_address_value_tv.text.toString())) {
                Utils.copyToMemory(mActivity, main_network_address_value_tv.text.toString())
                toast(getString(R.string.copy_success))
            }
            return@OnLongClickListener false
        })
    }

    private fun hineGetFreeCoin() {
        title_get_free_coin.visibility = View.GONE
        bg_get_free_hop_coin.visibility = View.GONE
        bg_get_free_eth_coin.visibility = View.GONE
        apply_free_token_btn.visibility = View.GONE
        get_free_hop_title.visibility = View.GONE
        get_free_eth_title.visibility = View.GONE
        apply_free_eth_btn.visibility = View.GONE
        line5.visibility = View.GONE
    }

    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
        mViewModel.showqrImageEvent.observe(this, Observer {
            showAddressImage()
        })
        mViewModel.exportEvent.observe(this, Observer {
            exportWallet()
        })

        mViewModel.queryTxEvent.observe(this, Observer {
            showQueryTxDialog(it)
        })

        mViewModel.hopBalanceEvent.observe(this, Observer {
            if (it == "0" || it == null) {
                apply_free_token_btn.isEnabled = true
                return@Observer
            }
            apply_free_token_btn.isEnabled = Utils.convertCoin(it.toDouble()).toDouble() <= FREE_HOP_MAX_VALUE
        })

        mViewModel.clearDBEvent.observe(this, Observer {
            showClearLocalDataDialog()

        })

        mViewModel.dnsEvent.observe(this, Observer {
            showChangeDNSDialog()
        })

        mViewModel.createAccountEvent.observe(this, Observer {
            showCreateAccountAlert()
        })
    }

    private fun showClearLocalDataDialog() {
        Utils.showOkOrCancelAlert(mActivity, R.string.tips, R.string.wallet_clear_local_data, object : AlertDialogOkCallBack() {
            override fun onClickOkButton(parameter: String) {
                Utils.clearLocalData(mActivity)
                toast(getString(R.string.wallet_clear_success))
            }
        })
    }

    private fun showQueryTxDialog(isFreeHop: Boolean) {
        val title: String
        if (isFreeHop) {
            title = "Hop"
            apply_free_token_btn.isEnabled = false
        } else {
            title = "Eth"
            apply_free_eth_btn.isEnabled = false
        }
        MessageDialog.show(mActivity, title, getString(R.string.wallet_apply_success), getString(R.string.sure))
        EventBus.getDefault().post(EventReLoadWallet(false))
    }


    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun onShow() {
        super.onShow()
        initData()
    }


    override fun initData() {

        mViewModel.hopBalance()
        main_network_address_value_tv.text = WalletWrapper.MainAddress
        apply_free_eth_btn.isEnabled = WalletWrapper.EthBalance <= FREE_ETH_MAX_VALUE
        MainActivity.walletBean?.let {
            hop_number_tv.text = Utils.convertCoin(MainActivity.walletBean!!.hop)
            eth_number_tv.text = Utils.convertCoin(MainActivity.walletBean!!.eth)
        }

    }


    private fun showAddressImage() {
        val intent = Intent(mActivity, MainNetAddressQRCodeActivity::class.java)
        val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, qr_code_iv, "image").toBundle()
        startActivity(intent, bundle)
    }

    private fun showCreateAccountAlert() {
        Utils.showOkOrCancelAlert(mActivity, R.string.wallet_replace_account_title, R.string.wallet__replace_msg, object : AlertDialogOkCallBack() {
            override fun onClickOkButton(parameter: String) {
                if (HopApplication.instance.isRunning) {
                    HopService.stop()
                }
                val createIntent = Intent(mActivity, CreateAccountActivity::class.java)
                createIntent.putExtra(IntentKey.SHOW_BACK_BUTTON, true)
                startActivity(createIntent)
            }
        })
    }

    private fun showChangeDNSDialog() {
        InputDialog.build(mActivity).setTitle(R.string.tips).setMessage(R.string.wallet_dns_empty).setOkButton(R.string.sure, OnInputDialogButtonClickListener { _, _, inputStr ->
                    if (TextUtils.isEmpty(inputStr)) {
                        toast(getString(R.string.wallet_dns_empty))
                        return@OnInputDialogButtonClickListener true
                    }
                    if (!Utils.isIpAddress(inputStr)) {
                        toast(getString(R.string.wallet_dns_failed))
                        return@OnInputDialogButtonClickListener true
                    }
                    Utils.saveData(Constants.NEW_DNS, inputStr)
                    toast(getString(R.string.wallet_update_dns_success))
                    mActivity.finish()
                    false
                }).setCancelButton(R.string.cancel).show()
    }


    private fun exportWallet() {
        if (!Utils.checkStorage(mActivity)) {
            return
        }
        if (WalletWrapper.MainAddress === "") {
            return
        }
        val accountData = WalletWrapper.walletJsonData()
        if (TextUtils.isEmpty(accountData)) {
            toast(getString(R.string.create_account_empty_account))
            return
        }

        showDialog(R.string.loading)
        mViewModel.exportAccount(mActivity, accountData, getString(R.string.wallet_pirate_account))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun rechargeSuccess(eventRechargeSuccess: EventRechargeSuccess) {
        EventBus.getDefault().post(EventReLoadWallet(false))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loadWalletSuccess(eventLoadWalletSuccess: EventLoadWalletSuccess) {
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        const val FREE_HOP_MAX_VALUE = 500
        const val FREE_ETH_MAX_VALUE = 0.05
    }
}