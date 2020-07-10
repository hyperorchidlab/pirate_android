package com.hop.pirate.greendao;

import android.content.Context;

import com.hop.pirate.model.bean.MinePoolBean;
import com.hop.pirate.model.bean.MinerBean;
import com.hop.pirate.model.bean.OwnPool;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * @description:
 * @author: Mr.x
 * @date :   2020/6/2 9:01 AM
 */
public class MinerDaoUtil {

    private static final boolean DUBUG = true;
    private DaoManager manager;
    private MinerBeanDao mMinerBeanDao;
    private DaoSession daoSession;

    public MinerDaoUtil(Context context) {
        manager = DaoManager.getInstance();
        manager.init(context);
        daoSession = manager.getDaoSession();
        manager.setDebug(DUBUG);
    }


    public void insertMineBeans(List<MinerBean> beans) {
        MinerBeanDao minerBeanDao = daoSession.getMinerBeanDao();
        minerBeanDao.insertInTx(beans);
    }

    public void deleteAll() {
        MinerBeanDao minerBeanDao = daoSession.getMinerBeanDao();
        minerBeanDao.deleteAll();
    }

    public void deleteMineBeansByMinePoolAddress(String minePoolAddress) {
        MinerBeanDao minerBeanDao = daoSession.getMinerBeanDao();
        List<MinerBean> list = minerBeanDao.queryBuilder().where(MinerBeanDao.Properties.MinerPoolAdd.eq(minePoolAddress)).list();
        minerBeanDao.deleteInTx(list);
    }

    public List<MinerBean> queryMineByMinePoolAddress(String minePoolAddress) {
        return daoSession.getMinerBeanDao().queryBuilder().where(MinerBeanDao.Properties.MinerPoolAdd.eq(minePoolAddress)).list();
    }


    public List<MinerBean> queryAll() {
        return daoSession.getMinerBeanDao().loadAll();
    }


}
