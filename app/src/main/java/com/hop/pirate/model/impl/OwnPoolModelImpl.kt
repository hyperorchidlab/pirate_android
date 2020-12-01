package com.hop.pirate.model.impl

import androidLib.AndroidLib
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hop.pirate.Constants
import com.hop.pirate.model.bean.OwnPool
import com.hop.pirate.model.bean.UserPoolData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout


/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 4:07 PM
 */
class OwnPoolModelImpl {
    suspend fun getPoolDataOfUser(): ArrayList<OwnPool>? {
        return withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                val poolsStr = AndroidLib.getSubPools()
                poolsStr?.let {
                    val groupListType = object : TypeToken<ArrayList<OwnPool>>() {}.type
                    Gson().fromJson<ArrayList<OwnPool>>(poolsStr, groupListType)
                }

            }
        }

    }

    suspend fun getPacketsByPool(user: String, pool: String): UserPoolData {
        return withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                val jsonStr = AndroidLib.getUserDate(user, pool)
                Gson().fromJson<UserPoolData>(jsonStr, UserPoolData::class.java)
            }
        }

    }
}