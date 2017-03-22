package com.udacity.ranjitha.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import static android.provider.CalendarContract.CalendarCache.URI;


public final class DbContract {

    private DbContract() {
    }
    public static final String AUTHORITY = "com.udacity.ranjitha.inventoryapp";

    public static final Uri URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH = "inventory";



    public static final class TableInfo implements BaseColumns {

        public static final Uri CONTENT_PATH = Uri.withAppendedPath(URI, PATH);

        public static final String TABLE_NAME = "inventory";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_NAME = "item_name";
        public static final String COLUMN_ITEM_PRICE = "item_price";
        public static final String COLUMN_ITEM_QUANTITY = "item_quantity";
        public static final String COLUMN_ITEM_IMAGE = "item_image";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH;
    }
}
