package com.hop.pirate.service;

import androidLib.AndroidLib;

import static java.lang.Thread.sleep;

public class WalletWrapper {

    public static String MainAddress = "";
    public static String SubAddress = "";
    public static double EthBalance;
    public static double HopBalance;
    public static double Approved;

    public static String walletJsonData() {
        return AndroidLib.walletJson();
    }

    public static void closeWallet() {
        AndroidLib.closeWallet();
    }

    private static void resetProtocol() throws Exception {
        AndroidLib.stopProtocol();
        sleep(1000);
        AndroidLib.initProtocol();
        AndroidLib.startProtocol();
    }


    public static boolean isOpen() {
        return AndroidLib.isWalletOpen();
    }


}
