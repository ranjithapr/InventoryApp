package com.udacity.ranjitha.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;



public class InventoryProvider extends ContentProvider {

    public static final String TAG = InventoryProvider.class.getSimpleName();

    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(DbContract.AUTHORITY, DbContract.PATH, INVENTORY);
        sUriMatcher.addURI(DbContract.AUTHORITY, DbContract.PATH + "/#", INVENTORY_ID);
    }

    private DbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = database.query(DbContract.TableInfo.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INVENTORY_ID:
                selection = DbContract.TableInfo._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(DbContract.TableInfo.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertInventoryItem(uri, values);
            default:
                throw new IllegalArgumentException("Not SUpported " + uri);
        }
    }

    private Uri insertInventoryItem(Uri uri, ContentValues values) {
        String itemName = values.getAsString(DbContract.TableInfo.COLUMN_ITEM_NAME);
        if (itemName == null) {
            throw new IllegalArgumentException("Name is Mandatory");
        }
        Integer itemPrice = values.getAsInteger(DbContract.TableInfo.COLUMN_ITEM_PRICE);
        if (itemPrice == null || itemPrice < 0) {
            throw new IllegalArgumentException("Price is Mandatory");
        }
        Integer itemQuantity = values.getAsInteger(DbContract.TableInfo.COLUMN_ITEM_QUANTITY);
        if (itemQuantity == null || itemQuantity < 0) {
            throw new IllegalArgumentException("Quantity is Mandatory");
        }
        //check the item quantity is not null and valid
//            String itemImage = values.getAsString(DbContract.TableInfo.COLUMN_ITEM_IMAGE);
//            if (itemImage == null) {
//                throw new IllegalArgumentException(" item requires Image");
//            }
//        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //insert the new inventory item with given values
        long id = database.insert(DbContract.TableInfo.TABLE_NAME, null, values);
        //if ID = -1 then insertion fails Log on the message that insertion is fail
        if (id == -1) {
            Log.e(TAG, "Failed " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int update(Uri uri, ContentValues values, String select, String[] args) {

        int match = sUriMatcher.match(uri);
        System.out.println("-----------update"+match);

        switch (match) {
            case INVENTORY:
                return updateInventoryItem(uri, values, select, args);
            case INVENTORY_ID:
                select = DbContract.TableInfo._ID + "=?";
                args = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventoryItem(uri, values, select, args);
            default:
                throw new IllegalArgumentException("Update  not supported for " + uri);
        }
    }

    private int updateInventoryItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(DbContract.TableInfo.COLUMN_ITEM_NAME)) {
            String itemName = values.getAsString(DbContract.TableInfo.COLUMN_ITEM_NAME);
            if (itemName == null) {
                throw new IllegalArgumentException("Name is Mandatory");
            }
        }
        if (values.containsKey(DbContract.TableInfo.COLUMN_ITEM_PRICE)) {
            Integer itemPrice = values.getAsInteger(DbContract.TableInfo.COLUMN_ITEM_PRICE);
            if (itemPrice == null || itemPrice < 0) {
                throw new IllegalArgumentException("Price is Mandatory");
            }
        }
        if (values.containsKey(DbContract.TableInfo.COLUMN_ITEM_QUANTITY)) {
            Integer itemQuantity = values.getAsInteger(DbContract.TableInfo.COLUMN_ITEM_QUANTITY);
            if (itemQuantity == null || itemQuantity < 0) {
                throw new IllegalArgumentException("Quantity is Mandatory");
            }
        }

       if (values.containsKey(DbContract.TableInfo.COLUMN_ITEM_IMAGE)) {
            String itemImage = values.getAsString(DbContract.TableInfo.COLUMN_ITEM_IMAGE);
            if (itemImage == null) {
                throw new IllegalArgumentException(" item requires Image");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowUpdated = database.update(DbContract.TableInfo.TABLE_NAME, values, selection, selectionArgs);

        if (rowUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowDeleted;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                rowDeleted = database.delete(DbContract.TableInfo.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                selection = DbContract.TableInfo._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = database.delete(DbContract.TableInfo.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowDeleted;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return DbContract.TableInfo.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return DbContract.TableInfo.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }
}
