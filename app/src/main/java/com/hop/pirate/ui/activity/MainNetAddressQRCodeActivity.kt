package com.hop.pirate.ui.activity

import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.hop.pirate.BR
import com.hop.pirate.R
import com.hop.pirate.databinding.ActivityMainNetAddressQrCodeBinding
import com.hop.pirate.service.WalletWrapper
import com.hop.pirate.util.Utils
import com.hop.pirate.viewmodel.MainNetAddressQRCodeVM
import com.nbs.android.lib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main_net_address_qr_code.*

class MainNetAddressQRCodeActivity : BaseActivity<MainNetAddressQRCodeVM,ActivityMainNetAddressQrCodeBinding>() {

    override fun initData() {
        val qrStr2Bitmap = Utils.QRStr2Bitmap(WalletWrapper.MainAddress)
        qr_iv.setImageBitmap(qrStr2Bitmap)
    }

    override fun getLayoutId(): Int = R.layout.activity_main_net_address_qr_code

    override fun initView() {
    }

    override fun initObserve() {
        mViewModel.finishAfterTransitionEvent.observe(this,
            Observer<Boolean> { ActivityCompat.finishAfterTransition(this@MainNetAddressQRCodeActivity) })

    }

    override fun initVariableId(): Int = BR.viewModel
}