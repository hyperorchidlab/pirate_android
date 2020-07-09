package com.hop.pirate.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.hop.pirate.model.bean.ExtendToken;

import org.greenrobot.greendao.database.Database;

/**
 * @description:
 * @author: Mr.x
 * @date :   2020/7/8 2:30 PM
 */
public class PirateSQLHelp extends DaoMaster.OpenHelper {
    public PirateSQLHelp(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, ExtendTokenDao.class, MinePoolBeanDao.class,MinerBeanDao.class,TokenBeanDao.class,UserAccountDataDao.class,OwnPoolDao.class,WalletBeanDao.class);
    }
}
