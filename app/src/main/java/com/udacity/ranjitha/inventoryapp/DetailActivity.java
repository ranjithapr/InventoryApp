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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.ranjitha.inventoryapp.data.DbContract;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private Uri mCurrentInventoryItemUri;

    private static final int EXISTING_INVENTORY_LOADER = 0;

    private TextView mItemNameTextView;
    private TextView mItemPriceTextView;
    private TextView mItemQuantityTextView;
    private Button mItemOrderButton;

    private Button deleteButton;


    private Uri currentItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_item_detail);

        final Intent intent = getIntent();
        currentItem = intent.getData();

        String itemIdString = intent.getStringExtra(DbCursorAdapter.ITEM_INDEX);
        long itemId = Long.parseLong(itemIdString);

        mItemNameTextView = (TextView) findViewById(R.id.inventory_item_name_view);
        mItemPriceTextView = (TextView)findViewById(R.id.inventory_item_price_text_view);
        mItemQuantityTextView = (TextView) findViewById(R.id.inventory_item_quantity_text_view);
        mItemOrderButton = (Button) findViewById(R.id.inventory_item_order_button);
        deleteButton = (Button) findViewById(R.id.delete_item);

        mCurrentInventoryItemUri = ContentUris.withAppendedId(DbContract.TableInfo.CONTENT_PATH,itemId);

        mItemOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, " Required Quantity for inventory item");
                intent.putExtra(Intent.EXTRA_TEXT, "Required quantity for : \n" +
                        ((TextView) findViewById(R.id.inventory_item_name_view)).getText().toString().trim() +
                        "\nQuantity : " + ((TextView) findViewById(R.id.inventory_item_quantity_text_view))
                        .getText().toString().trim() +
                        "\n\n\nFrom : CS Inventory PVT LTD");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });

        getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
    }


    private void deleteItem() {
        if (mCurrentInventoryItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryItemUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Delete Failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Delete Successfull",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                DbContract.TableInfo._ID,
                DbContract.TableInfo.COLUMN_ITEM_NAME,
                DbContract.TableInfo.COLUMN_ITEM_PRICE,
                DbContract.TableInfo.COLUMN_ITEM_QUANTITY,
                };

        return new CursorLoader(this,
                mCurrentInventoryItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int inventoryItemNameColumnIndex = cursor.getColumnIndex(DbContract.TableInfo.
                    COLUMN_ITEM_NAME);
            int inventoryItemPriceColumnIndex = cursor.getColumnIndex(DbContract.TableInfo.
                    COLUMN_ITEM_PRICE);
            final int inventoryItemQuantityColumnIndex = cursor.getColumnIndex(DbContract.TableInfo.
                    COLUMN_ITEM_QUANTITY);


            String itemName = cursor.getString(inventoryItemNameColumnIndex);
            int itemPrice = cursor.getInt(inventoryItemPriceColumnIndex);
            int itemQuantity = cursor.getInt(inventoryItemQuantityColumnIndex);

            mItemNameTextView.setText(itemName);
            mItemPriceTextView.setText("Rs: "+Integer.toString(itemPrice));
            mItemQuantityTextView.setText("Units: "+Integer.toString(itemQuantity));

        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemNameTextView.setText("");
        mItemPriceTextView.setText("");
        mItemQuantityTextView.setText("");
    }
}
