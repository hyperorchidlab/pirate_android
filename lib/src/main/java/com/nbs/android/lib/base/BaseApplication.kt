package com.nbs.android.lib.base

import android.app.Application
import kotlin.properties.Delegates

open class BaseApplication: Application() {


    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    companion object {
        var instance: BaseApplication by Delegates.notNull()
    }
}