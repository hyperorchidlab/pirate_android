package com.hop.pirate.model

import android.text.TextUtils
import androidLib.AndroidLib
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hop.pirate.Constants
import com.hop.pirate.model.bean.MinerBean
import com.nbs.android.lib.base.BaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.util.*

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/30 2:10 PM
 */
class MineMachineListModel : BaseModel(){
    suspend fun getMineMachine(address: String, random: Int): List<MinerBean> {
        return withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                val minersMachineStr = AndroidLib.randomMiner(address, random.toLong())
                val type = object : TypeToken<ArrayList<MinerBean>>() {}.type
                Gson().fromJson<ArrayList<MinerBean>>(minersMachineStr, type)
            }
        }
    }

    suspend fun ping(address: String): Double {
        return withTimeout(Constants.TIME_OUT.toLong()) {
            withContext(Dispatchers.IO) {
                val pingJson = AndroidLib.testPing(address)
                if (TextUtils.isEmpty(pingJson)) {
                    return@withContext -1.0
                } else {
                    val obj = JSONObject(pingJson)
                    obj.optDouble("ping")
                }
            }
        }

    }

}