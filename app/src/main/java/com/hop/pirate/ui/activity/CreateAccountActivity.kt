package com.hop.pirate.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidLib.AndroidLib
import androidx.lifecycle.Observer
import com.google.zxing.integration.android.IntentIntegrator
import com.hop.pirate.BR
import com.hop.pirate.IntentKey
import com.hop.pirate.R
import com.hop.pirate.callback.AlertDialogOkCallBack
import com.hop.pirate.databinding.ActivityCreateAccountBinding
import com.hop.pirate.util.Utils
import com.hop.pirate.viewmodel.CreateAccountVM
import com.kongzue.dialog.v3.BottomMenu
import com.nbs.android.lib.base.BaseActivity
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import pub.devrel.easypermissions.EasyPermissions.RationaleCallbacks


class CreateAccountActivity : BaseActivity<CreateAccountVM, ActivityCreateAccountBinding>(),
    PermissionCallbacks, RationaleCallbacks {
    private val CLICK_SCAN = 0
    private val CLICK_ALBUM = 1
    override fun getLayoutId(): Int = R.layout.activity_create_account

    override fun initView() {
        AndroidLib.initFildPath(Utils.getBaseDir(this))
        val showBackBtn = intent.getBooleanExtra(IntentKey.SHOW_BACK_BUTTON, false)
        if (showBackBtn) {
            mDataBinding.backIv.visibility = View.VISIBLE
        }
    }

    override fun initVariableId(): Int = BR.viewModel


    override fun initData() {}

    override fun initObserve() {
        mViewModel.showImportDialogEvent.observe(this,
            Observer { showImportDialog() })
    }


    private fun showImportDialog() {
        val listItems = arrayOf(
            getString(R.string.scanning_qr_code),
            getString(R.string.read_album),
        )
        BottomMenu.show(this, listItems) { _, index ->
            if (index == CLICK_SCAN) {
                requestCameraPermission()
            } else if (index == CLICK_ALBUM) {
                requestLocalMemoryPermission()
            }
        }.title = getString(R.string.select_import_mode)
    }

    @AfterPermissionGranted(Utils.RC_LOCAL_MEMORY_PERM)
    fun requestLocalMemoryPermission() {
        if (Utils.hasStoragePermission(this)) {
            openAlbum()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_extra_write),
                Utils.RC_LOCAL_MEMORY_PERM,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    @AfterPermissionGranted(Utils.RC_CAMERA_PERM)
    fun requestCameraPermission() {
        if (Utils.hasCameraPermission(this)) {
            val ii = IntentIntegrator(this@CreateAccountActivity)
            ii.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            ii.captureActivity = ScanActivity::class.java
            ii.setPrompt(getString(R.string.scan_pirate_account_qr))
            ii.setCameraId(0)
            ii.setBarcodeImageEnabled(true)
            ii.initiateScan()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.camera),
                Utils.RC_CAMERA_PERM,
                Manifest.permission.CAMERA
            )
        }
    }

    private fun openAlbum() {
        val albumIntent = Intent()
        albumIntent.addCategory(Intent.CATEGORY_OPENABLE)
        albumIntent.type = "image/*"
        albumIntent.action = Intent.ACTION_OPEN_DOCUMENT
        startActivityForResult(albumIntent, Utils.RC_SELECT_FROM_GALLERY)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Utils.RC_SELECT_FROM_GALLERY == requestCode) {
            if (resultCode != Activity.RESULT_OK || null == data) {
                return
            }
            loadAccountFromUri(data.data)
        } else {
            val result =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                    ?: return
            if (result.contents == null) {
                return
            }
            try {
                val walletStr = result.contents
                showPasswordDialog(walletStr)
            } catch (ex: Exception) {
                Utils.toastTips(getString(R.string.import_account_failed) + ex.localizedMessage)
            }
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Log.d(TAG, "onRationaleAccepted:$requestCode")
    }

    override fun onRationaleDenied(requestCode: Int) {
        Log.d(TAG, "onRationaleDenied:$requestCode")
    }

    fun showPasswordDialog(walletStr: String) {
        Utils.showPassword(this, object : AlertDialogOkCallBack() {
            override fun onClickOkButton(parameter: String) {
                showDialog()
                mViewModel.importWallet(parameter, walletStr)
            }
        })
    }


    fun loadAccountFromUri(uri: Uri?) {
        if (null == uri) {
            Utils.toastTips(getString(R.string.error_import_image))
            return
        }
        try {
            val walletStr = Utils.parseQRCodeFile(uri, contentResolver)
            showPasswordDialog(walletStr)
        } catch (e: Exception) {
            Utils.toastTips(getString(R.string.import_error) + e.localizedMessage)
            e.printStackTrace()
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           if(dialog != null && dialog!!.isShowing){
               dialog!!.dismiss()
               mViewModel.cancelRequest()
               return true
           }
        }
        return super.onKeyDown(keyCode, event)
    }
}