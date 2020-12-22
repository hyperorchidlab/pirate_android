package com.hop.pirate.model

import androidLib.AndroidLib
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hop.pirate.Constants
import com.hop.pirate.model.bean.OwnPool
import com.hop.pirate.model.bean.UserPoolData
import com.hop.pirate.util.CommonSchedulers
import com.nbs.android.lib.base.BaseModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout


/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 4:07 PM
 */
class OwnPoolModel : BaseModel() {
     fun getPoolDataOfUser(): Single<ArrayList<OwnPool>> {
         return Single.create(SingleOnSubscribe<ArrayList<OwnPool>> { emitter ->
             val poolsStr = AndroidLib.getSubPools()
             if(poolsStr == "null"){
                 val ownPools = ArrayList<com.hop.pirate.model.bean.OwnPool>()
                 emitter.onSuccess(ownPools)
                 return@SingleOnSubscribe
             }
             poolsStr?.let {
                 val groupListType = object : TypeToken<ArrayList<OwnPool>>() {}.type
                 val ownPools = Gson().fromJson<ArrayList<OwnPool>>(poolsStr, groupListType)
                 emitter.onSuccess(ownPools)
                 return@SingleOnSubscribe
             }
             emitter.onError(Throwable("data is empty!"))
         }).compose(CommonSchedulers.io2mainAndTimeout<ArrayList<OwnPool>>())

    }

     fun getPacketsByPool(user: String, pool: String):Single<UserPoolData> {
        return Single.create(SingleOnSubscribe<UserPoolData> { emitter ->
            val jsonStr = AndroidLib.getUserDate(user, pool)
            val userPool = Gson().fromJson<UserPoolData>(jsonStr, UserPoolData::class.java)
            emitter.onSuccess(userPool)
        }).compose(CommonSchedulers.io2mainAndTimeout<UserPoolData>())


    }
}