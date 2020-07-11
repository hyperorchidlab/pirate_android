package com.hop.pirate.greendao;

import android.content.Context;

import com.hop.pirate.model.bean.OwnPool;

import java.util.List;

/**
 * @description:
 * @author: Mr.x
 * @date :   2020/6/2 9:01 AM
 */
public class OwnPoolDaoUtil {

    private static final boolean DUBUG = true;
    private DaoManager manager;
    private OwnPoolDao mOwnPoolDao;
    private DaoSession daoSession;

    public OwnPoolDaoUtil(Context context) {
        manager = DaoManager.getInstance();
        manager.init(context);
        daoSession = manager.getDaoSession();
        manager.setDebug(DUBUG);
    }


    public void insertOwnPool(List<OwnPool> beans) {
        OwnPoolDao ownPoolDao = daoSession.getOwnPoolDao();
        ownPoolDao.insertInTx(beans);
    }

    public void deleteAll() {
        OwnPoolDao ownPoolDao = daoSession.getOwnPoolDao();
        ownPoolDao.deleteAll();
    }

    public List<OwnPool> queryAll() {
        return daoSession.getOwnPoolDao().loadAll();
    }
}
