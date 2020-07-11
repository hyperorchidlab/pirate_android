package com.hop.pirate.service;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.hop.pirate.Constants;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.AlertDialogOkCallBack;
import com.hop.pirate.model.bean.MinePoolBean;
import com.hop.pirate.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidLib.AndroidLib;

import static java.lang.Thread.sleep;

public class WalletWrapper {

    public static String MainAddress = "";
    public static String SubAddress = "";
    public static double EthBalance;
    public static double HopBalance;
    public static double Approved;
    public static final String TAG = "Wallet Wrapper";

    public static List<MinePoolBean> SubPoolBeans = new ArrayList<>();
    public static Map<String, String> PoolNameMap = new HashMap<>();


    public static void ImportWallet(final String jsonStr, final String auth, final Handler handler) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                try {
                    AndroidLib.importWallet(jsonStr, auth);
                    resetProtocol();
                    message.what = Constants.IMPORT_ACCOUNT_SUCCESS_CODE;
                } catch (Exception ex) {
                    message.what = Constants.IMPORT_ACCOUNT_ERR_CODE;
                    message.obj = ex;
                }

                if (handler != null) {
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    public static String WalletJsonData() {
        return AndroidLib.walletJson();
    }

    public static void closeWallet() {
        AndroidLib.closeWallet();
    }

    public static void resetProtocol() throws Exception {
        AndroidLib.stopProtocol();
        sleep(1000);
        AndroidLib.initProtocol();
        AndroidLib.startProtocol();
    }


    public static boolean IsEmpty() {
        return TextUtils.isEmpty(WalletWrapper.MainAddress);
    }


    public static boolean IsOpen() {
        return AndroidLib.isWalletOpen();
    }

    public static void showImportWalletDialog(final BaseActivity mActivity, final String walletStr, final Handler mHandler) {
        Utils.showPassWord(mActivity, new AlertDialogOkCallBack() {
            @Override
            public void onClickOkButton(String parameter) {
                mActivity.showDialogFragment();
                WalletWrapper.ImportWallet(walletStr, parameter, mHandler);
            }
        });
    }

}
