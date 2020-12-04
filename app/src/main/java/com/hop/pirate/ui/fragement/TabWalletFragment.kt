package com.hop.pirate.ui.fragement

import android.content.Intent
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import com.hop.pirate.*
import com.hop.pirate.callback.AlertDialogOkCallBack
import com.hop.pirate.databinding.FragmentWalletBinding
import com.hop.pirate.event.EventLoadWalletSuccess
import com.hop.pirate.event.EventRechargeSuccess
import com.hop.pirate.model.bean.ExtendToken
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

class TabWalletFragment : BaseFragment<TabWalletVM, FragmentWalletBinding>(), Handler.Callback {
    private var mTabSettingModel: TabWalletModel? = null
    private var mHandler: Handler? = null

    override fun getLayoutId(): Int = R.layout.fragment_wallet

    override fun initView() {
        mViewModel.title.set(getString(R.string.tab_wallet))
        mViewModel.showRightImage.set(true)
        mHandler = Handler(this)
        mTabSettingModel = TabWalletModel()
        EventBus.getDefault().register(this)

        val newDns = Utils.getString(Constants.NEW_DNS, Constants.DNS)
        val dnsText = resources.getString(R.string.tab_setting_dns) + newDns
        dns_tv.text = dnsText
        val versionText = getString(R.string.current_version_name) + Utils.getVersionName(mActivity)
        version_tv.text = versionText
        update_app_tv.text = getString(R.string.tab_setting_new_version)

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
            showqrImage()
        })
        mViewModel.exportEvent.observe(this, Observer {
            exportWallet()
        })

        mViewModel.queryTxEvent.observe(this, Observer {
            showQueryTxDialog(it)
        })

        mViewModel.hopBalanceEvent.observe(this, Observer {
            if (it == "0") {
                apply_free_token_btn.isEnabled = true
                return@Observer
            }
            apply_free_token_btn.isEnabled = Utils.ConvertCoin(it.toDouble()).toDouble() <= 500
        })

        mViewModel.clearDBEvent.observe(this, Observer {
            Utils.deleteDBData(mActivity)
            mActivity.showToast(R.string.fragment_account_clear)
        })

        mViewModel.dnsEvent.observe(this, Observer {
            showChangeDNSDialog()
        })

        mViewModel.createAccountEvent.observe(this, Observer {
            showCreateAccountAlert()
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
        (mActivity as MainActivity).loadWallet(false)
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
            hop_number_tv.text = Utils.ConvertCoin(MainActivity.walletBean!!.hop)
            eth_number_tv.text = Utils.ConvertCoin(MainActivity.walletBean!!.eth)
        }

        hop_tv.text = ExtendToken.CurSymbol
        hop_unit_tv.text = ExtendToken.CurSymbol
    }

    fun onClick(v: View?) {
//        switch (v.getId()) {
//            case R.id.refreshBalanceTv:
//                mActivity.showDialogFragment();
//                ((MainActivity) mActivity).loadWallet(true);
//                break;
//            case R.id.QRCodeIv:
//                showQRImage();
//                break;
//            case R.id.applyFreeEthBtn:
//                applyFreeEth();
//                break;
//            case R.id.applyFreeTokenBtn:
//                applyFreeToken();
//                break;
//            case R.id.dnsTv:
//                showChangeDNSDialog();
//                break;
//            case R.id.createAccountTv:
//                showCreateAccountAlert();
//                break;
//            case R.id.exportTv:
//                exportWallet();
//                break;
//            case R.id.versionTv:
//            case R.id.updateAppTv:
//                Utils.openAppDownloadPage(mActivity);
//                break;
//            case R.id.helpAddressTv:
//                startWebview(R.string.fragment_account_help_address);
//                break;
//            case R.id.courseAddressTv:
//                startWebview(R.string.fragment_account_course_address);
//                break;
//            case R.id.clearDBTv:
//                Utils.deleteDBData(mActivity);
//                Utils.toastTips(getString(R.string.fragment_account_clear));
//                break;
//            case R.id.operationGuideTv:
//                startActivity(new Intent(mActivity, GuideActivity.class));
//                break;
//            default:
//                break;
//
//        }
    }


    private fun showqrImage() {
        val intent =
            Intent(mActivity, MainNetAddressQRCodeActivity::class.java)
        val bundle =
            ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, qr_code_iv, "image")
                .toBundle()
        startActivity(intent, bundle)
    }

    private fun showCreateAccountAlert() {
        Utils.showOkOrCancelAlert(
            mActivity,
            R.string.tab_setting_replace_account_title,
            R.string.tab_setting_replace_msg,
            object : AlertDialogOkCallBack() {
                override fun onClickOkButton(parameter: String) {
                    if (HopApplication.getApplication().isRunning) {
                        HopApplication.getApplication().isRunning = false
                        HopService.stop()
                    }
                    val createIntent =
                        Intent(mActivity, CreateAccountActivity::class.java)
                    createIntent.putExtra(IntentKey.SHOW_BACK_BUTTON, true)
                    startActivity(createIntent)
                }
            })
    }

    private fun showChangeDNSDialog() {
        InputDialog.build(mActivity)
            .setTitle(R.string.tips)
            .setMessage(R.string.tab_setting_dns_empty)
            .setOkButton(
                R.string.sure,
                OnInputDialogButtonClickListener { _, _, inputStr ->
                    if (TextUtils.isEmpty(inputStr)) {
                        Utils.toastTips(getString(R.string.tab_setting_dns_empty))
                        return@OnInputDialogButtonClickListener true
                    }
                    if (!Utils.isIpAddress(inputStr)) {
                        Utils.toastTips(getString(R.string.tab_setting_dns_failed))
                        return@OnInputDialogButtonClickListener true
                    }
                    Utils.saveData(Constants.NEW_DNS, inputStr)
                    Utils.toastTips(getString(R.string.tab_setting_update_dns_success))
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
        (mActivity as MainActivity).loadWallet(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loadWalletSuccess(eventLoadWalletSuccess: EventLoadWalletSuccess?) {
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        mHandler!!.removeCallbacksAndMessages(null)
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            Constants.QUERY_TXSTATUS_TIME_OUT_CODE -> //                mActivity.dismissDialogFragment();
                Utils.toastTips(resources.getString(R.string.apply_timeout))
            Constants.IMPORT_ACCOUNT_ERR_CODE -> Utils.toastTips(
                "err:" + msg.obj
            )
            else -> {
            }
        }
        return false
    }


    companion object {
        const val FREE_HOP_MAX_VALUE = 1000
        const val FREE_ETH_MAX_VALUE = 0.05
    }
}