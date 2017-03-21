package com.udacity.ranjitha.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.udacity.ranjitha.inventoryapp.data.DbContract;


public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int INVENTORY_LOADER = 0;

    DbCursorAdapter mDbCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        ListView inventoryItemListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        inventoryItemListView.setEmptyView(emptyView);

        mDbCursorAdapter = new DbCursorAdapter(this, null);
        inventoryItemListView.setAdapter(mDbCursorAdapter);

        inventoryItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                Uri currentInventoryItemUri = ContentUris.withAppendedId(DbContract.TableInfo.CONTENT_PATH, id);
                intent.setData(currentInventoryItemUri);
                startActivity(intent);
            }
        });


        Button addItemButton = (Button) findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                DbContract.TableInfo._ID,
                DbContract.TableInfo.COLUMN_ITEM_NAME,
                DbContract.TableInfo.COLUMN_ITEM_PRICE,
                DbContract.TableInfo.COLUMN_ITEM_QUANTITY};

        return new CursorLoader(this,
                DbContract.TableInfo.CONTENT_PATH,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDbCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDbCursorAdapter.swapCursor(null);
    }
}
