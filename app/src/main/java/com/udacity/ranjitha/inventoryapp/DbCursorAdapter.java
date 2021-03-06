package com.udacity.ranjitha.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.ranjitha.inventoryapp.data.DbContract;



public class DbCursorAdapter extends android.widget.CursorAdapter {

    public static final String ITEM_INDEX = "ItemIndex";

    private Context mContext;

    public DbCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.inventory_list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        //get the reference of the textViews
        TextView inventoryItemNameTextView = (TextView) view.findViewById(R.id.inventory_name);
        TextView inventoryItemPriceTextView = (TextView) view.findViewById(R.id.inventory_price);
        final TextView inventoryItemQuantityTextView = (TextView) view.findViewById(R.id.inventory_quantity);
        Button inventoryItemSaleButton = (Button) view.findViewById(R.id.sale_item_button);
        Button inventoryItemTrackButton = (Button) view.findViewById(R.id.track_item_button);
        final ListView listView = (ListView)view.findViewById(R.id.list);

        inventoryItemSaleButton.setFocusable(false);
        inventoryItemTrackButton.setFocusable(false);

        int inventoryItemNameColumnIndex = cursor.getColumnIndex(DbContract.TableInfo.COLUMN_ITEM_NAME);
        int inventoryItemPriceColumnIndex = cursor.getColumnIndex(DbContract.TableInfo.COLUMN_ITEM_PRICE);
        int inventoryItemQuantityColumnIndex = cursor.getColumnIndex(DbContract.TableInfo.COLUMN_ITEM_QUANTITY);
        final int id = cursor.getPosition();
        String inventoryItemNameString = cursor.getString(inventoryItemNameColumnIndex);
        String inventoryItemPriceString = cursor.getString(inventoryItemPriceColumnIndex);
        final String inventoryItemQuantityString = cursor.getString(inventoryItemQuantityColumnIndex);
        final int[] itemQuantity = {Integer.parseInt(inventoryItemQuantityString)};

        inventoryItemNameTextView.setText(inventoryItemNameString);
        inventoryItemPriceTextView.setText("Rs:"+inventoryItemPriceString);
        inventoryItemQuantityTextView.setText(inventoryItemQuantityString);

        inventoryItemSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemQuantity[0] == 0) {
                    Toast.makeText(mContext, "Item is out of Stock!", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("-----------click");
                    itemQuantity[0] = itemQuantity[0] - 1;
                    ContentValues values = new ContentValues();
                    values.put(DbContract.TableInfo.COLUMN_ITEM_QUANTITY, itemQuantity[0]);
                    inventoryItemQuantityTextView.setText(itemQuantity[0] + "");

                    Uri currentItemUri = Uri.withAppendedPath(DbContract.TableInfo.CONTENT_PATH,
                            getItemId(id) +"");

                    mContext.getContentResolver().update(currentItemUri,
                            values,null,null);
                }
            }
        });

        inventoryItemTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,DetailActivity.class);
                long itemId = getItemId(id);
                intent.putExtra(ITEM_INDEX,itemId + "");
                mContext.startActivity(intent);
            }
        });
    }
}
