package com.hop.pirate.service

import com.hop.pirate.util.Utils

object SysConf {
    const val KEY_CACHED_POOL_IN_USE = "KEY_CACHED_POOL_IN_USE"
    const val KEY_CACHED_POOL_NAME_IN_USE = "KEY_CACHED_POOL_NAME_IN_USE"
    const val KEY_CACHED_MINER_ID_IN_USE = "KEY_CACHED_MINER_ID_IN_USE_OF_%s"
    var CurPoolAddress = ""
    var CurPoolName = ""
    var CurMinerID = ""
    var PacketsBalance = 0.0
    var PacketsCredit = 0.0
    fun changeCurPool(address: String, newPool: String) {
        CurPoolName = newPool
        if (CurPoolAddress == address) {
            return
        }
        CurPoolAddress = address
        Utils.saveData(KEY_CACHED_POOL_IN_USE, address)
        Utils.saveData(KEY_CACHED_POOL_NAME_IN_USE, CurPoolName)
        val mKey =
            String.format(KEY_CACHED_MINER_ID_IN_USE, CurPoolAddress)
        CurMinerID = Utils.getString(mKey, "")
    }

    fun setCurMiner(newMiner: String) {
        if (CurMinerID == newMiner) {
            return
        }
        CurMinerID = newMiner
        val mKey =
            String.format(KEY_CACHED_MINER_ID_IN_USE, CurPoolAddress)
        Utils.saveData(mKey, newMiner)
    }
}