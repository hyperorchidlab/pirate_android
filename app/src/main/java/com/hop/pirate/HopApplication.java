package com.hop.pirate;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.kongzue.dialog.v2.DialogSettings;
import com.tencent.bugly.crashreport.CrashReport;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

public class HopApplication extends Application {
    private static final String TAG = "HopApplication";
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        HopApplication.context = getApplicationContext();
        DialogSettings.style = DialogSettings.STYLE_KONGZUE;
        if(!isApkInDebug()){
            CrashReport.initCrashReport(getApplicationContext(), "27449a53f9", false);
        }
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d(TAG, "accept: "+throwable.getMessage());
            }
        });

    }

    public static Context getAppContext() {
        return HopApplication.context;
    }


    public static boolean isApkInDebug() {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}
