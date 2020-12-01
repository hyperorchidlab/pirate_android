package com.nbs.android.lib.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

//给float增加dp转换为px属性，Resources.getSystem()可以在任何地方进行使用，但是有一个局限，只能获取系统本身的资源，没法获取app里面的资源信息。
val Number.dp
get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this as Float,Resources.getSystem().displayMetrics)

fun Context.showToast(msgId: Int) {
    showToast(resources.getString(msgId))
}

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}


fun <T : View> T.withTrigger(delay: Long = 600): T {
    triggerDelay = delay
    return this
}


fun <T : View> T.click(block: (T) -> Unit) = setOnClickListener {
    block(it as T)
}


fun <T : View> T.clickWithTrigger(time: Long = 600, block: (T) -> Unit){
    triggerDelay = time
    setOnClickListener {
        if (clickEnable()) {
            block(it as T)
        }
    }
}

private var <T : View> T.triggerLastTime: Long
    get() = if (getTag(1123460103) != null) getTag(1123460103) as Long else -601
    set(value) {
        setTag(1123460103, value)
    }

private var <T : View> T.triggerDelay: Long
    get() = if (getTag(1123461123) != null) getTag(1123461123) as Long else 600
    set(value) {
        setTag(1123461123, value)
    }

private fun <T : View> T.clickEnable(): Boolean {
    var flag = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        flag = true
        triggerLastTime = currentClickTime
    }
    return flag
}


interface OnLazyClickListener : View.OnClickListener {

    override fun onClick(v: View?) {
        if (v?.clickEnable() == true) {
            onLazyClick(v)
        }
    }

    fun onLazyClick(v: View)
}

fun <T> String.toBeanList():List<T> = Gson().fromJson(this,object: TypeToken<List<T>>(){}.type)

