package com.hop.pirate.greendao;

import android.content.Context;

import com.hop.pirate.model.bean.WalletBean;

import java.util.List;

/**
 * @description:
 * @author: Mr.x
 * @date :   2020/6/2 9:01 AM
 */
public class WalletBeanDaoUtil {

    private static final boolean DUBUG = true;
    private DaoManager manager;
    private WalletBeanDao mWalletBeanDao;
    private DaoSession daoSession;

    public WalletBeanDaoUtil(Context context) {
        manager = DaoManager.getInstance();
        manager.init(context);
        daoSession = manager.getDaoSession();
        manager.setDebug(DUBUG);
    }


    public void insertWallet(WalletBean beans) {
        WalletBeanDao walletBeanDao = daoSession.getWalletBeanDao();
        walletBeanDao.insertOrReplace(beans);
    }

    public void deleteAll() {
        WalletBeanDao walletBeanDao = daoSession.getWalletBeanDao();
        walletBeanDao.deleteAll();
    }

    public WalletBean queryWallet() {
        List<WalletBean> walletBeans = daoSession.getWalletBeanDao().loadAll();
        if (walletBeans == null || walletBeans.size() == 0) {
            return new WalletBean();
        }
        return walletBeans.get(0);
    }
}
