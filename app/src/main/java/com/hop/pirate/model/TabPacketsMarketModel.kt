package com.hop.pirate.model

import androidLib.AndroidLib
import com.hop.pirate.Constants
import com.hop.pirate.model.bean.MinePoolBean
import com.nbs.android.lib.base.BaseModel
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.util.*

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 2:59 PM
 */
class TabPacketsMarketModel : BaseModel(){
   suspend fun getPoolInfo(syncAllPools: Boolean):List<MinePoolBean> {
       return withTimeout(Constants.TIME_OUT.toLong()) {
           withContext(Dispatchers.IO){
           if (syncAllPools) {
               AndroidLib.syncAllPoolsData()
           }
           val jsonStr = AndroidLib.poolInfosInMarket()
           val minePoolBeans: MutableList<MinePoolBean> = ArrayList()
           val pools = JSONObject(jsonStr)
           val it: Iterator<*> = pools.keys()
           while (it.hasNext()) {
               val key = it.next() as String
               val p = pools.optJSONObject(key) ?: continue
               val bean = MinePoolBean()
               bean.address = key
               bean.name = p.optString("Name")
               bean.email = p.optString("Email")
               bean.mortgageNumber = p.optDouble("GTN")
               bean.websiteAddress = p.optString("Url")
               minePoolBeans.add(bean)
           }
           minePoolBeans
       }
       }

    }
}