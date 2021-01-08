package com.hop.pirate.util

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.provider.MediaStore
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.hop.pirate.HopApplication
import com.hop.pirate.R
import com.hop.pirate.callback.AlertDialogOkCallBack
import com.hop.pirate.ui.activity.MineMachineListActivity.Companion.sMinerBeans
import com.hop.pirate.ui.activity.MinePoolListActivity.Companion.sMinePoolBeans
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.util.InputInfo
import com.kongzue.dialog.v3.InputDialog
import com.kongzue.dialog.v3.MessageDialog
import com.nbs.android.lib.utils.AppManager
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.net.NetworkInterface
import java.util.*
import java.util.regex.Pattern
import kotlin.jvm.Throws

object Utils {
    const val RC_LOCAL_MEMORY_PERM = 123
    const val RC_CAMERA_PERM = 124
    const val RC_SELECT_FROM_GALLERY = 125
    const val COIN_DECIMAL = 1e18
    private val appContext: Context = HopApplication.instance
    private val sharedPref = appContext.getSharedPreferences("pirateManager", Context.MODE_PRIVATE)

    fun convertCoin(coinV: Double): String {
        return String.format(Locale.CHINA, "%.4f ", coinV / COIN_DECIMAL)
    }

    fun convertBandWidth(packetsV: Double): String {
        return when {
            packetsV > 1e12 -> {
                String.format(Locale.CHINA, "%.2f T", packetsV / 1e12)
            }
            packetsV > 1e9 -> {
                String.format(Locale.CHINA, "%.2f G", packetsV / 1e9)
            }
            packetsV > 1e6 -> {
                String.format(Locale.CHINA, "%.2f M", packetsV / 1e6)
            }
            packetsV > 1e3 -> {
                String.format(Locale.CHINA, "%.2f K", packetsV / 1e3)
            }
            else -> {
                String.format(Locale.CHINA, "%.2f B", packetsV)
            }
        }
    }

    fun saveData(key: String?, value: String?) {
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, defaultVal: String): String {
        return sharedPref.getString(key, defaultVal).toString()
    }

    fun saveBoolean(key: String?, value: Boolean?) {
        val editor = sharedPref.edit()
        editor.putBoolean(key, value!!)
        editor.apply()
    }

    fun getBoolean(key: String?, defaultVal: Boolean?): Boolean {
        return sharedPref.getBoolean(key, defaultVal!!)
    }

    fun clearSharedPref() {
        val edit = sharedPref.edit()
        edit.clear()
        edit.commit()
    }

    fun clearAllData() {
        sMinerBeans = null
        sMinePoolBeans = null
        clearSharedPref()
    }

    fun showOkAlert(context: AppCompatActivity,
        tittleID: Int,
        messageId: Int,
        callBack: AlertDialogOkCallBack?) {
        MessageDialog.show(context,
                context.getString(tittleID),
                context.getString(messageId),
                context.getString(R.string.sure)).onOkButtonClickListener =
            OnDialogButtonClickListener { baseDialog, v ->
                callBack?.onClickOkButton("")
                false
            }
    }

    fun showOkOrCancelAlert(context: AppCompatActivity,
        tittleID: Int,
        messageId: Int,
        callBack: AlertDialogOkCallBack?) {
        MessageDialog.show(context,
                context.getString(tittleID),
                context.getString(messageId),
                context.getString(R.string.sure),
                context.getString(R.string.cancel)).setOnOkButtonClickListener { baseDialog, v ->
            callBack?.onClickOkButton("")
            false
        }.onCancelButtonClickListener = OnDialogButtonClickListener { baseDialog, v ->
            callBack?.onClickCancelButton()
            false
        }
    }


    fun showPassword(context: AppCompatActivity, callBack: AlertDialogOkCallBack) {
        InputDialog.build(context) //.setButtonTextInfo(new TextInfo().setFontColor(Color.GREEN))
            .setTitle(R.string.tips).setMessage(R.string.create_account_enter_password)
            .setOkButton(R.string.sure) { baseDialog, v, inputStr ->
                callBack.onClickOkButton(inputStr)
                false
            }.setCancelButton(R.string.cancel)
            .setHintText(context.getString(R.string.create_account_enter_ethereum_password))
            .setInputInfo(InputInfo().setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD))
            .setCancelable(false).show()
    }

    fun copyToMemory(context: Context, src: String?) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("pirate memory string", src)
        clipboard.setPrimaryClip(clip)
    }

    fun QRStr2Bitmap(data: String?): Bitmap? {
        val barcodeEncoder = BarcodeEncoder()
        try {
            return barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 600, 600)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class, WriterException::class)
    fun saveStringQrCode(cr: ContentResolver, data: String?, fileName: String) {
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400)
        saveImageToLocal(cr, bitmap, fileName)
    }

    @Throws(IOException::class)
    private fun saveImageToLocal(cr: ContentResolver, bitmap: Bitmap, fileName: String) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, fileName)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        val path = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        path?.let {
            val imageOut = cr.openOutputStream(path)
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, imageOut)
            } finally {
                imageOut?.close()
            }
        }

    }

    @Throws(Exception::class)
    fun parseQRCodeFile(uri: Uri, cr: ContentResolver): String {
        val bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri))
        return parseQRcodeFromBitmap(bitmap)
    }

    @Throws(Exception::class)
    private fun parseQRcodeFromBitmap(bitmap: Bitmap): String {
        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source: LuminanceSource = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        val bb = BinaryBitmap(HybridBinarizer(source))
        val hints: MutableMap<DecodeHintType, Any?> = LinkedHashMap()
        hints[DecodeHintType.PURE_BARCODE] = java.lang.Boolean.TRUE
        val reader: Reader = MultiFormatReader()
        val r = reader.decode(bb, hints)
        return r.text
    }

    fun hasStoragePermission(ctx: Context?): Boolean {
        return EasyPermissions.hasPermissions(ctx!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun checkStorage(ctx: Activity): Boolean {
        if (!EasyPermissions.hasPermissions(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(ctx,
                    ctx.getString(R.string.rationale_extra_write),
                    RC_LOCAL_MEMORY_PERM,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return false
        }
        return true
    }

    fun hasCameraPermission(ctx: Context?): Boolean {
        return EasyPermissions.hasPermissions(ctx!!, Manifest.permission.CAMERA)
    }

    fun checkCamera(ctx: Activity): Boolean {
        if (!hasCameraPermission(ctx)) {
            EasyPermissions.requestPermissions(ctx,
                    ctx.getString(R.string.camera),
                    RC_CAMERA_PERM,
                    Manifest.permission.CAMERA)
            return false
        }
        return true
    }

    fun getBaseDir(context: Context): String {
        return context.filesDir.absolutePath
    }

    fun getVersionCode(context: Context): Int {
        val packageManager = context.packageManager
        try {
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 1
    }

    fun getVersionName(context: Context): String {
        val packageManager = context.packageManager
        try {
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            return "V" + packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "V0.0"
    }

    fun isNetworkAvailable(activity: AppCompatActivity): Boolean {
        val context = activity.applicationContext
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager == null) {
            return false
        } else {
            val networkInfo = connectivityManager.allNetworkInfo
            if (networkInfo.size > 0) {
                for (i in networkInfo.indices) {
                    if (networkInfo[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun isServiceWork(mContext: Context, serviceName: String): Boolean {
        var isWork = false
        val myAM = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val myList = myAM.getRunningServices(100)
        if (myList.size <= 0) {
            return false
        }
        for (i in myList.indices) {
            val mName = myList[i].service.className.toString()
            if (mName == serviceName) {
                isWork = true
                break
            }
        }
        return isWork
    }

    fun checkVPN(): Boolean {
        val networkList: MutableList<String> = ArrayList()
        try {
            for (networkInterface in Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp) {
                    networkList.add(networkInterface.name)
                }
            }
        } catch (ex: Exception) {
            return false
        }
        return networkList.contains("tun0") || networkList.contains("ppp0")
    }

    private const val MIN_CLICK_DELAY_TIME = 500
    private var lastClickTime: Long = 0
    val isFastClick: Boolean
        get() {
            val curClickTime = System.currentTimeMillis()
            if (curClickTime - lastClickTime >= MIN_CLICK_DELAY_TIME) {
                lastClickTime = curClickTime
                return true
            }
            return false
        }

    fun openAppDownloadPage(context: Context) {
        val it = Intent(Intent.ACTION_VIEW, Uri.parse("https://tsfr.io/6yyarz"))
        context.startActivity(it)
    }

    fun isIpAddress(address: String?): Boolean {
        val regex =
            "^([1-9]|([1-9][0-9])|(1[0-9][0-9])|(2[0-4][0-9])|(25[0-5]))(\\.([0-9]|([1-9][0-9])|(1[0-9][0-9])|(2[0-4][0-9])|(25[0-5]))){3}$"
        val p = Pattern.compile(regex)
        val m = p.matcher(address)
        return m.matches()
    }

    fun clearLocalData(context: Context) {
        deleteFile(File(getBaseDir(context), "data"))
    }

    fun deleteFile(file: File?) {
        if (file == null || !file.exists()) {
            return
        }
        val files = file.listFiles()
        for (f in files) {
            val name = file.name
            println(name)
            if (f.isDirectory) {
                deleteFile(f)
            } else {
                f.delete()
            }
        }
        file.delete()
    }

    fun formatText(start: String, end: String): SpannableString {
        val spannableString = SpannableString(start + end)
        val colorSpan = ForegroundColorSpan(Color.parseColor("#aaffffff"))
        val relativeSizeSpan = RelativeSizeSpan(0.8f)
        spannableString.setSpan(colorSpan,
                start.length - 1,
                (start + end).length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(relativeSizeSpan,
                start.length - 1,
                (start + end).length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    fun showExitAppDialog(activity:AppCompatActivity,msgId: Int){
        val instance = HopApplication.instance
        MessageDialog.show(activity, instance.getString(R.string.tips), instance.getString(msgId), instance.getString(R.string.sure)).setOnOkButtonClickListener { baseDialog, v ->
            AppManager.removeAllActivity()
            return@setOnOkButtonClickListener false
        }
    }


}