package com.hop.pirate.model

import android.text.TextUtils
import androidLib.AndroidLib
import com.hop.pirate.model.bean.MinePoolBean
import com.nbs.android.lib.base.BaseModel
import org.json.JSONArray
import org.json.JSONException
import kotlin.jvm.Throws

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 4:07 PM
 */
class MinePoolListModel : BaseModel(){
    @Throws(JSONException::class)
    fun getPoolDataOfUser() :List<MinePoolBean>{
        val minePoolBeans= mutableListOf<MinePoolBean>()
        val poolsStr = AndroidLib.getSubPools()
        if (!TextUtils.isEmpty(poolsStr) && poolsStr != "null") {
            val jsonArray = JSONArray(poolsStr)
            for (i in 0 until jsonArray.length()) {
                val p = jsonArray.getJSONObject(i) ?: continue
                val bean = MinePoolBean()
                bean.address = p.optString("MainAddr")
                bean.name = p.optString("Name")
                bean.email = p.optString("Email")
                bean.mortgageNumber = p.optDouble("GTN")
                bean.websiteAddress = p.optString("Url")
                minePoolBeans.add(bean)
            }
        }

        return minePoolBeans
    }
}