package com.hop.pirate.service;

import com.hop.pirate.model.bean.ExtendToken;
import com.hop.pirate.util.Utils;

public class SysConf {

    public static final String KEY_CACHED_POOL_IN_USE = "KEY_CACHED_POOL_IN_USE_%s";
    public static final String KEY_CACHED_POOL_NAME_IN_USE = "KEY_CACHED_POOL_NAME_IN_USE_%s";
    public static final String KEY_CACHED_MINER_ID_IN_USE = "KEY_CACHED_MINER_ID_IN_USE_OF_%s";

    public static String CurPoolAddress = "";
    public static String CurPoolName = "";
    public static String CurMinerID = "";
    public static double PacketsBalance;
    public static double PacketsCredit;


    public static void changeCurPool(String address, String newPool) {

        CurPoolName = newPool;
        if (CurPoolAddress.equals(address)) {
            return;
        }

        CurPoolAddress = address;
        String poolAddress = String.format(KEY_CACHED_POOL_IN_USE, ExtendToken.CurSymbol);
        String poolName = String.format(KEY_CACHED_POOL_NAME_IN_USE, ExtendToken.CurSymbol);
        Utils.saveData(poolAddress, address);
        Utils.saveData(poolName, CurPoolName);

        String mKey = String.format(KEY_CACHED_MINER_ID_IN_USE, CurPoolAddress);
        CurMinerID = Utils.getString(mKey, "");

    }

    public static void setCurMiner(String newMiner) {
        if (CurMinerID.equals(newMiner)) {
            return;
        }

        CurMinerID = newMiner;
        String mKey = String.format(KEY_CACHED_MINER_ID_IN_USE, CurPoolAddress);
        Utils.saveData(mKey, newMiner);
    }

}
