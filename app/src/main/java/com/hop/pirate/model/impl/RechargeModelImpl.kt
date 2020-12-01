package com.hop.pirate.model.impl

import android.content.Context
import android.text.TextUtils
import androidLib.AndroidLib
import com.hop.pirate.Constants
import com.hop.pirate.PirateException
import com.hop.pirate.R
import com.hop.pirate.base.WaitTxBaseModel
import com.hop.pirate.callback.ResultCallBack
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/30 2:31 PM
 */
class RechargeModelImpl : WaitTxBaseModel() {
    suspend fun approve(tokenNO: Double): String {
        return withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                AndroidLib.authorizeTokenSpend(tokenNO)
            }
        }


//                if (TextUtils.isEmpty(approveTx)) {
//                    emitter.onError(new PirateException(context.getString(R.string.password_error)));
//                } else {
//                    emitter.onNext(approveTx);
//                    emitter.onComplete();
//                }
    }

    suspend fun buyPacket( userAddress: String, pollAddress: String, tokenNO: Double): String {
        return withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                AndroidLib.buyPacket(userAddress, pollAddress, tokenNO)
            }
        }
    }

    suspend fun getBytesPerToken(): Double {
        return withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                val sysStr = AndroidLib.systemSettings()
                val json = JSONObject(sysStr)
                json.optDouble("MBytesPerToken")
            }
        }
    }

    suspend fun openWallet(password: String) {
        withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                AndroidLib.openWallet(password)
            }
        }

    }

    suspend fun syncPool(poolAddress: String): Boolean {
        return withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                AndroidLib.waitSubPool(poolAddress)
            }
        }
    }
}