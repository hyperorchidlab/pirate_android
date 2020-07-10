package com.hop.pirate.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.hop.pirate.model.bean.ExtendToken;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "EXTEND_TOKEN".
 */
public class ExtendTokenDao extends AbstractDao<ExtendToken, Void> {

    public static final String TABLENAME = "EXTEND_TOKEN";

    /**
     * Properties of entity ExtendToken.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property PaymentContract = new Property(0, String.class, "PaymentContract", false, "PAYMENT_CONTRACT");
        public final static Property TokenI = new Property(1, String.class, "TokenI", false, "TOKEN_I");
        public final static Property Symbol = new Property(2, String.class, "Symbol", false, "SYMBOL");
        public final static Property Balance = new Property(3, double.class, "Balance", false, "BALANCE");
        public final static Property Decimal = new Property(4, int.class, "Decimal", false, "DECIMAL");
        public final static Property IsChecked = new Property(5, boolean.class, "isChecked", false, "IS_CHECKED");
    }


    public ExtendTokenDao(DaoConfig config) {
        super(config);
    }

    public ExtendTokenDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"EXTEND_TOKEN\" (" + //
                "\"PAYMENT_CONTRACT\" TEXT," + // 0: PaymentContract
                "\"TOKEN_I\" TEXT," + // 1: TokenI
                "\"SYMBOL\" TEXT," + // 2: Symbol
                "\"BALANCE\" REAL NOT NULL ," + // 3: Balance
                "\"DECIMAL\" INTEGER NOT NULL ," + // 4: Decimal
                "\"IS_CHECKED\" INTEGER NOT NULL );"); // 5: isChecked
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"EXTEND_TOKEN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ExtendToken entity) {
        stmt.clearBindings();

        String PaymentContract = entity.getPaymentContract();
        if (PaymentContract != null) {
            stmt.bindString(1, PaymentContract);
        }

        String TokenI = entity.getTokenI();
        if (TokenI != null) {
            stmt.bindString(2, TokenI);
        }

        String Symbol = entity.getSymbol();
        if (Symbol != null) {
            stmt.bindString(3, Symbol);
        }
        stmt.bindDouble(4, entity.getBalance());
        stmt.bindLong(5, entity.getDecimal());
        stmt.bindLong(6, entity.getIsChecked() ? 1L : 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ExtendToken entity) {
        stmt.clearBindings();

        String PaymentContract = entity.getPaymentContract();
        if (PaymentContract != null) {
            stmt.bindString(1, PaymentContract);
        }

        String TokenI = entity.getTokenI();
        if (TokenI != null) {
            stmt.bindString(2, TokenI);
        }

        String Symbol = entity.getSymbol();
        if (Symbol != null) {
            stmt.bindString(3, Symbol);
        }
        stmt.bindDouble(4, entity.getBalance());
        stmt.bindLong(5, entity.getDecimal());
        stmt.bindLong(6, entity.getIsChecked() ? 1L : 0L);
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }

    @Override
    public ExtendToken readEntity(Cursor cursor, int offset) {
        ExtendToken entity = new ExtendToken( //
                cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // PaymentContract
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // TokenI
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // Symbol
                cursor.getDouble(offset + 3), // Balance
                cursor.getInt(offset + 4), // Decimal
                cursor.getShort(offset + 5) != 0 // isChecked
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, ExtendToken entity, int offset) {
        entity.setPaymentContract(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTokenI(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSymbol(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setBalance(cursor.getDouble(offset + 3));
        entity.setDecimal(cursor.getInt(offset + 4));
        entity.setIsChecked(cursor.getShort(offset + 5) != 0);
    }

    @Override
    protected final Void updateKeyAfterInsert(ExtendToken entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }

    @Override
    public Void getKey(ExtendToken entity) {
        return null;
    }

    @Override
    public boolean hasKey(ExtendToken entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }

}
