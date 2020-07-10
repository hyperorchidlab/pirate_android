package com.hop.pirate.greendao;

import android.content.Context;

import com.hop.pirate.model.bean.MinePoolBean;

import java.util.List;

/**
 * @description:
 * @author: Mr.x
 * @date :   2020/6/2 9:01 AM
 */
public class MinePoolBeanDaoUtil {
    private static final boolean DUBUG = true;
    private DaoManager manager;
    private MinePoolBeanDao mMinePoolBeanDao;
    private DaoSession daoSession;

    public MinePoolBeanDaoUtil(Context context) {
        manager = DaoManager.getInstance();
        manager.init(context);
        daoSession = manager.getDaoSession();
        manager.setDebug(DUBUG);
    }


    public void insertMinePoolBeans(List<MinePoolBean> beans) {
        MinePoolBeanDao minePoolBeanDao = daoSession.getMinePoolBeanDao();
        minePoolBeanDao.deleteAll();
        minePoolBeanDao.insertInTx(beans);
    }

    public void deleteAll() {
        MinePoolBeanDao minePoolBeanDao = daoSession.getMinePoolBeanDao();
        minePoolBeanDao.deleteAll();
    }

    public List<MinePoolBean> queryAll() {
        return daoSession.getMinePoolBeanDao().loadAll();
    }

}
