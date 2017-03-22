package com.udacity.ranjitha.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DbHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "MyInventory.db";
    public static final int DB_VERSION = 2;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTORY_ITEM_ENTRY =
                "CREATE TABLE " + DbContract.TableInfo.TABLE_NAME + " (" +
                        DbContract.TableInfo._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DbContract.TableInfo.COLUMN_ITEM_NAME + " TEXT, " +
                        DbContract.TableInfo.COLUMN_ITEM_PRICE + " INTEGER, " +
                        DbContract.TableInfo.COLUMN_ITEM_QUANTITY + " INTEGER, " +
                        DbContract.TableInfo.COLUMN_ITEM_IMAGE + " TEXT" + ")";

        db.execSQL(SQL_CREATE_INVENTORY_ITEM_ENTRY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_INVENTORY_ITEM_ENTRY =
                "DELETE FROM " + DbContract.TableInfo.TABLE_NAME;
        db.execSQL(SQL_DELETE_INVENTORY_ITEM_ENTRY);
    }
}
