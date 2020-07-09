package com.hop.pirate.greendao;

import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * @description:
 * @author: Mr.x
 * @date :   2020/6/2 8:43 AM
 */
public class DaoManager {

    public static final String DB_NAME = "pirate_data.db";
    private volatile static DaoManager mDaoManager;
    private static DaoMaster.DevOpenHelper mHelper;
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;
    private Context context;


    public static DaoManager getInstance() {
        DaoManager instance = null;
        if (mDaoManager == null) {
            synchronized (DaoManager.class) {
                if (instance == null) {
                    instance = new DaoManager();
                    mDaoManager = instance;
                }
            }
        }
        return mDaoManager;
    }


    public void init(Context context) {
        this.context = context;
    }


    public DaoMaster getDaoMaster() {
        if (null == mDaoMaster) {
            PirateSQLHelp bpsqlHelp = new PirateSQLHelp(context, DB_NAME, null);
            mDaoMaster = new DaoMaster(bpsqlHelp.getWritableDatabase());
        }
        return mDaoMaster;
    }


    public DaoSession getDaoSession() {
        if (null == mDaoSession) {
            if (null == mDaoMaster) {
                mDaoMaster = getDaoMaster();
            }
            mDaoSession = mDaoMaster.newSession();
        }
        return mDaoSession;
    }


    public void setDebug(boolean flag) {
        QueryBuilder.LOG_SQL = flag;
        QueryBuilder.LOG_VALUES = flag;
    }


    public void closeDataBase() {
        closeHelper();
        closeDaoSession();
    }

    public void closeDaoSession() {
        if (null != mDaoSession) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }

    public void closeHelper() {
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
    }
}
