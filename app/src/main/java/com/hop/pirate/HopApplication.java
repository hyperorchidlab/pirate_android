package com.hop.pirate;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.kongzue.dialog.util.DialogSettings;
import com.tencent.bugly.crashreport.CrashReport;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

public class HopApplication extends Application {
    private static final String TAG = "HopApplication";
    private static HopApplication sApplication;
    private boolean isRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        HopApplication.sApplication = this;
        DialogSettings.style = DialogSettings.STYLE.STYLE_IOS;
        DialogSettings.modalDialog = true;
        if (!BuildConfig.DEBUG) {
            CrashReport.initCrashReport(getApplicationContext(), "cfdc2c8a96", false);
        }
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d(TAG, "accept: " + throwable.getMessage());
            }
        });

    }

    public static HopApplication getApplication() {
        return HopApplication.sApplication;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

}
