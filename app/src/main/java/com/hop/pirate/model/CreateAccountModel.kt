package com.hop.pirate.model

import androidLib.AndroidLib
import com.hop.pirate.util.CommonSchedulers
import io.reactivex.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.core.SingleOnSubscribe
import io.reactivex.rxjava3.disposables.Disposable
import kotlin.reflect.KFunction1


/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/25 11:17 AM
 */
class CreateAccountModel : InitServiceModel() {
    fun createAccount(password: String): Single<String> {
        return Single.create(SingleOnSubscribe<String> { emitter ->
            val newWallet = AndroidLib.newWallet(password)
            emitter.onSuccess(newWallet)
        }).compose(CommonSchedulers.io2mainAndTimeout<String>())
    }


    fun importWallet(walletStr: String, password: String ):Single<Any> {
       return  Single.create(SingleOnSubscribe<Any> { emitter ->
            AndroidLib.importWallet(walletStr, password)
            emitter.onSuccess("")
        }).compose(CommonSchedulers.io2mainAndTimeout<Any>())

    }

}