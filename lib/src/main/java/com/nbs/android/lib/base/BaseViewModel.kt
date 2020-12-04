package com.nbs.android.lib.base

import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.event.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import java.util.*


/**
 * @description:
 * @author:  Mr.x
 * @date :   2020/11/3 8:15 AM
 */

open class BaseViewModel : ViewModel() {
    var title = ObservableField<String>("")
    val showBackImage = ObservableField<Boolean>(false)
    val showRightImage = ObservableField<Boolean>(false)
    val showRightText = ObservableField<Boolean>(false)
    val rightText = ObservableField<String>("")

    val jobs = mutableListOf<Job>()

    val clickBackCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            clickBack()
        }

    })

    val clickRightIvCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            clickRightIv()
        }

    })

    val clickRightTvCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            clickRightTv()
        }

    })

    open fun clickBack() {

    }

    open fun clickRightIv() {

    }

    open fun clickRightTv() {

    }

    val uc: UIChangeLiveData by lazy {
        UIChangeLiveData()
    }

    fun showToast(msgId:Int){
        uc.toastEvent.postValue(msgId)
    }

    open fun showDialog(titleId: Int) {
        uc.showDialogEvent.postValue(titleId)
    }
    open fun showDialog(title: String) {
        uc.showDialogStrEvent.postValue(title)
    }

    open fun dismissDialog() {
        println("+++++++++dismissDialog")
        uc.dismissDialogEvent.postValue(System.currentTimeMillis())
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    open fun startActivity(clz: Class<*>) {
        startActivity(clz, null)
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    open fun startActivity(clz: Class<*>, bundle: Bundle?) {
        val params: MutableMap<String, Any> =
            HashMap()
        params[ParameterField.CLASS] = clz
        if (bundle != null) {
            params[ParameterField.BUNDLE] = bundle
        }
        uc.startActivityEvent.postValue(params)
    }


    fun startWebActivity(url:String){
        uc.startWebActivityEvent.postValue(url)
    }

    /**
     * 关闭界面
     */
    open fun finish() {
        uc.finishEvent.call()
    }

    class UIChangeLiveData : SingleLiveEvent<Any>() {
        public val toastEvent: SingleLiveEvent<Int> by lazy {
            SingleLiveEvent<Int>()
        }

        public val showDialogEvent: SingleLiveEvent<Int> by lazy {
            SingleLiveEvent<Int>()
        }
        public val showDialogStrEvent: SingleLiveEvent<String> by lazy {
            SingleLiveEvent<String>()
        }
        public val dismissDialogEvent: SingleLiveEvent<Long> by lazy {
            SingleLiveEvent<Long>()
        }
        public val startActivityEvent: SingleLiveEvent<Map<String, Any>> by lazy {
            SingleLiveEvent<Map<String, Any>>()
        }
        public val startWebActivityEvent: SingleLiveEvent<String> by lazy {
            SingleLiveEvent<String>()
        }
        public val finishEvent: SingleLiveEvent<Void> by lazy {
            SingleLiveEvent<Void>()
        }
        public val onBackPressedEvent: SingleLiveEvent<Void> by lazy {
            SingleLiveEvent<Void>()
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<in Any>) {
            super.observe(owner, observer)
        }
    }

    fun cancelRequest(){
        println("-----cancelRequest")
        viewModelScope.launch {
            jobs.forEach {
                it.cancelAndJoin()
            }

        }

    }


}

object ParameterField {
    var CLASS = "CLASS"
    var CANONICAL_NAME = "CANONICAL_NAME"
    var BUNDLE = "BUNDLE"
}

