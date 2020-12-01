package com.hop.pirate.model.impl

import androidLib.AndroidLib
import com.hop.pirate.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/25 11:17 AM
 */
class CreateAccountModelImpl {
    suspend fun createAccount(password: String): String {
        return withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                AndroidLib.newWallet(password)
            }
        }


    }

    suspend fun importWallet(walletStr: String, password: String) {
        withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                AndroidLib.importWallet(walletStr, password)
            }
        }
    }

}