package com.hop.pirate.callback

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/23 6:30 PM
 */
interface ResultCallBack<T> {
    fun onError(e: Throwable?)
    fun onSuccess(t: T)
    fun onComplete()
}