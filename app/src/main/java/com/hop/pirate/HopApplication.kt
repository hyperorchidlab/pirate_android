package com.hop.pirate

import android.app.Application
import android.util.Log
import com.kongzue.dialog.util.DialogSettings
import com.tencent.bugly.crashreport.CrashReport
import io.reactivex.plugins.RxJavaPlugins
import kotlin.properties.Delegates

class HopApplication : Application() {
    var isRunning = false
    override fun onCreate() {
        super.onCreate()
        instance = this
        DialogSettings.style = DialogSettings.STYLE.STYLE_IOS
        DialogSettings.modalDialog = true
        if (!BuildConfig.DEBUG) {
            CrashReport.initCrashReport(applicationContext, "cfdc2c8a96", false)
        }
        RxJavaPlugins.setErrorHandler { throwable -> Log.d(TAG, "accept: " + throwable.message) }
    }

    companion object {
        private const val TAG = "HopApplication"
        var instance: HopApplication by Delegates.notNull()
    }
}