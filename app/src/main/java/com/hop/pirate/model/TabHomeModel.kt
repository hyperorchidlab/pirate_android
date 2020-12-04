package com.hop.pirate.model

import androidLib.AndroidLib
import com.google.gson.Gson
import com.hop.pirate.Constants
import com.hop.pirate.model.bean.UserPoolData
import com.nbs.android.lib.base.BaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/**
 * @description:
 * @author: mr.x
 * @date :   2020/6/4 12:11 PM
 */
class TabHomeModel : BaseModel(){
     suspend fun getPool(user: String, pool: String):UserPoolData {
        return withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                val jsonStr = AndroidLib.getUserDate(user, pool)
                Gson().fromJson<UserPoolData>(jsonStr, UserPoolData::class.java)
            }
        }
    }

     suspend fun openWallet( password: String){
         withTimeout(Constants.TIME_OUT.toLong()) {
             withContext(Dispatchers.IO) {
                 AndroidLib.openWallet(password)
             }
         }
    }

}