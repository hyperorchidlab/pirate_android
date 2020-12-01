package com.hop.pirate.model.impl

import android.content.ContentResolver
import android.text.TextUtils
import androidLib.AndroidLib
import com.hop.pirate.Constants
import com.hop.pirate.PirateException
import com.hop.pirate.base.WaitTxBaseModel
import com.hop.pirate.callback.ResultCallBack
import com.hop.pirate.callback.SaveQRCodeCallBack
import com.hop.pirate.model.TabSettingModel
import com.hop.pirate.util.Utils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/27 9:09 AM
 */
class TabSettingModelImpl : WaitTxBaseModel() {

     suspend fun exportAccount(cr: ContentResolver, data: String, fileName: String) {
       withTimeout(Constants.TIME_OUT.toLong()){
            withContext(Dispatchers.IO){
                Utils.saveStringQrCode(cr, data, fileName)
            }
        }

    }

     suspend fun applyFreeEth(address: String):String {
        return withTimeout(Constants.TIME_OUT.toLong()){
             withContext(Dispatchers.IO){
                 AndroidLib.applyFreeEth(address)
             }
         }

    }

     suspend fun applyFreeHop(address: String) :String {
         return withTimeout(Constants.TIME_OUT.toLong()){
             withContext(Dispatchers.IO){
                 AndroidLib.applyFreeToken(address)
             }
         }
    }

     suspend fun queryHopBalance(address: String):String {
         return withTimeout(Constants.TIME_OUT.toLong()){
             withContext(Dispatchers.IO){
                 AndroidLib.tokenBalance(address)
             }
         }

    }

}