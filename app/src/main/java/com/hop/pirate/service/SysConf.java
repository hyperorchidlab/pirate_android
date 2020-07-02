package com.hop.pirate.service;

import com.hop.pirate.model.bean.ExtendToken;
import com.hop.pirate.model.bean.MinerBean;
import com.hop.pirate.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidLib.AndroidLib;

public class SysConf {

    public static final String _KEY_CACHED_POOL_IN_USE_ = "_KEY_CACHED_POOL_IN_USE_%s";
    public static final String _KEY_CACHED_POOL_NAME_IN_USE_ = "_KEY_CACHED_POOL_NAME_IN_USE_%s";
    public static final String _KEY_CACHED_MINER_ID_IN_USE_ = "_KEY_CACHED_MINER_ID_IN_USE_OF_%s";

    public static String CurPoolAddress = "";
    public static String CurPoolName = "";
    public static String CurMinerID = "";
    public static double PacketsBalance;
    public static double PacketsCredit;

    public static double MicroNonce;
    public static double Epoch;


    public static boolean ChangeCurPool(String address, String newPool) {

        CurPoolName = newPool;
        if (CurPoolAddress.equals(address)) {
            return false;
        }

        CurPoolAddress = address;
        String poolAddress = String.format(_KEY_CACHED_POOL_IN_USE_, ExtendToken.CurSymbol);
        String poolName = String.format(_KEY_CACHED_POOL_NAME_IN_USE_, ExtendToken.CurSymbol);
        Utils.saveData(poolAddress, address);
        Utils.saveData(poolName, CurPoolName);

        String mKey = String.format(_KEY_CACHED_MINER_ID_IN_USE_, CurPoolAddress);
        CurMinerID = Utils.getString(mKey, "");

        return true;
    }

    public static boolean SetCurMiner(String newMiner) {
        if (CurMinerID.equals(newMiner)) {
            return false;
        }

        CurMinerID = newMiner;
        String mKey = String.format(_KEY_CACHED_MINER_ID_IN_USE_, CurPoolAddress);
        Utils.saveData(mKey, newMiner);
        return true;
    }

}
