package com.hop.pirate.callback

abstract class AlertDialogOkCallBack {
    abstract fun onClickOkButton(parameter: String)
    fun onClickCancelButton() {}
}