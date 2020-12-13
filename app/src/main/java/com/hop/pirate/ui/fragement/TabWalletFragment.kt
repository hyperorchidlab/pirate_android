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
import com.nbs.android.lib.utils.showToast
import kotlinx.android.synthetic.main.fragment_wallet.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TabWalletFragment : BaseFragment<TabWalletVM, FragmentWalletBinding>(){
    private var mTabSettingModel: TabWalletModel? = null

    override fun getLayoutId(): Int = R.layout.fragment_wallet

    override fun initView() {
        mViewModel.title.set(getString(R.string.tab_wallet))
        mViewModel.showRightImage.set(true)
        mTabSettingModel = TabWalletModel()
        EventBus.getDefault().register(this)

        val newDns = Utils.getString(Constants.NEW_DNS, Constants.DNS)
        mViewModel.dnsObservable.set(resources.getString(R.string.tab_account_dns) + newDns)
        val versionText = getString(R.string.current_version_name) + Utils.getVersionName(mActivity)
        mViewModel.versionObservable.set(versionText)

        main_network_address_value_tv.setOnLongClickListener(View.OnLongClickListener {
            if (!TextUtils.isEmpty(main_network_address_value_tv.text.toString())) {
                Utils.copyToMemory(mActivity, main_network_address_value_tv.text.toString())
            }
            return@OnLongClickListener false
        })
    }
    override fun initVariableId(): Int =BR.viewModel

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
            if (it == "0" || it ==null) {
                apply_free_token_btn.isEnabled = true
                return@Observer
            }
            apply_free_token_btn.isEnabled = Utils.convertCoin(it.toDouble()).toDouble() <= 500
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
        Utils.showOkOrCancelAlert(mActivity,R.string.tips , R.string.tab_account_clear_local_data, object : AlertDialogOkCallBack() {
            override fun onClickOkButton(parameter: String) {
                Utils.deleteDBData(mActivity)
                mActivity.showToast(R.string.tab_account_clear)
            }
        })
    }

    private fun showQueryTxDialog(isFreeHop: Boolean) {
        val title: String
        if (isFreeHop) { title = "Hop"
            apply_free_token_btn.isEnabled = false
        } else {
            title = "Eth"
            apply_free_eth_btn.isEnabled = false
        }
        MessageDialog.show(mActivity, title, getString(R.string.apply_success), getString(R.string.sure))
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
        apply_free_eth_btn.isEnabled = WalletWrapper.EthBalance <= 0.05
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
        Utils.showOkOrCancelAlert(mActivity, R.string.tab_account_replace_account_title, R.string.tab_account_replace_msg, object : AlertDialogOkCallBack() {
                override fun onClickOkButton(parameter: String) {
                    if (HopApplication.instance.isRunning) {
                        HopApplication.instance.isRunning = false
                        HopService.stop()
                    }
                    val createIntent = Intent(mActivity, CreateAccountActivity::class.java)
                    createIntent.putExtra(IntentKey.SHOW_BACK_BUTTON, true)
                    startActivity(createIntent)
                }
            })
    }

    private fun showChangeDNSDialog() {
        InputDialog.build(mActivity)
            .setTitle(R.string.tips)
            .setMessage(R.string.tab_account_dns_empty)
            .setOkButton(R.string.sure, OnInputDialogButtonClickListener { _, _, inputStr ->
                    if (TextUtils.isEmpty(inputStr)) {
                        Utils.toastTips(getString(R.string.tab_account_dns_empty))
                        return@OnInputDialogButtonClickListener true
                    }
                    if (!Utils.isIpAddress(inputStr)) {
                        Utils.toastTips(getString(R.string.tab_account_dns_failed))
                        return@OnInputDialogButtonClickListener true
                    }
                    Utils.saveData(Constants.NEW_DNS, inputStr)
                    Utils.toastTips(getString(R.string.tab_account_update_dns_success))
                    mActivity.finish()
                    false
                })
            .setCancelButton(R.string.cancel)
            .show()
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
            Utils.toastTips(getString(R.string.empty_account))
            return
        }

        showDialog(R.string.loading)
        mViewModel.exportAccount(mActivity.contentResolver, accountData, getString(R.string.pirate_account))
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
        const val FREE_HOP_MAX_VALUE = 1000
        const val FREE_ETH_MAX_VALUE = 0.05
    }
}