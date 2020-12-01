package com.nbs.android.lib.command

interface BindingConsumer<T> {
    fun call(t: T)
}