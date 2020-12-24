package com.nbs.android.lib.utils

import android.content.Context
import android.os.Looper
import android.widget.Toast
import com.nbs.android.lib.base.BaseApplication

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
private var toast: Toast? = null
fun toast(msg: String) {
    val context: Context = BaseApplication.instance
    try {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        } else {
            toast!!.setText(msg)
        }
        toast!!.show()
    } catch (e: Exception) {
        Looper.prepare()
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}