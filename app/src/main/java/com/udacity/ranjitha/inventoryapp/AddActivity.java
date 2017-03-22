package com.udacity.ranjitha.inventoryapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.udacity.ranjitha.inventoryapp.data.DbContract;


public class AddActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;

    private EditText itemName,itemPrice,itemQuantity;

    private Button addButton, deleteButton, uploadImageButton;

    private Uri currentItem,selectedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_add);
        addButton = (Button) findViewById(R.id.add_item);
        deleteButton = (Button) findViewById(R.id.delete_item);
        uploadImageButton = (Button) findViewById(R.id.upload_button);
        final Intent intent = getIntent();
        currentItem = intent.getData();



        if (currentItem == null) {
            setTitle("Add Item");
            addButton.setText("Add");
            selectedImage = null;
            deleteButton.setVisibility(View.INVISIBLE);

        } else {
            setTitle("Edit Item");
            addButton.setText("Update");
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }
        itemName = (EditText) findViewById(R.id.inventory_title_edit_text);
        itemPrice = (EditText) findViewById(R.id.inventory_item_price_edit_text);
        itemQuantity = (EditText) findViewById(R.id.inventory_item_quantity_edit_text);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInventoryItem();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmation();
            }
        });
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
            }
        });
    }

    private void saveInventoryItem() {
        String nameStr = itemName.getText().toString().trim();
        String priceStr = itemPrice.getText().toString().trim();
        String quanStr = itemQuantity.getText().toString().trim();
        if(nameStr.isEmpty() || priceStr.isEmpty() || quanStr.isEmpty()){
            Toast.makeText(this, "Fill Mandatory Fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int itemPrice = Integer.parseInt(priceStr);
            int itemQuantity = Integer.parseInt(quanStr);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please Enter Valid Numbers",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(selectedImage == null){
            Toast.makeText(this,"Please Choose a Valid Image",Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.TableInfo.COLUMN_ITEM_NAME, nameStr);
        contentValues.put(DbContract.TableInfo.COLUMN_ITEM_PRICE, priceStr);
        contentValues.put(DbContract.TableInfo.COLUMN_ITEM_QUANTITY, quanStr);
        contentValues.put(DbContract.TableInfo.COLUMN_ITEM_IMAGE,selectedImage.toString().trim());


        if (currentItem == null) {

            Uri newUri = getContentResolver().insert(DbContract.TableInfo.CONTENT_PATH, contentValues);

            if (newUri == null) {
                Toast.makeText(this, "Save Error",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Save Succesfull",
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(currentItem, contentValues, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Update Error",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update Succesfull",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                selectedImage = data.getData();
                ((ImageView)findViewById(R.id.inventory_item_image_view)).setImageURI(selectedImage);
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
                DbContract.TableInfo.COLUMN_ITEM_IMAGE};

        return new CursorLoader(this,
                currentItem,
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
            int inventoryItemQuantityColumnIndex = cursor.getColumnIndex(DbContract.TableInfo.
                    COLUMN_ITEM_QUANTITY);
            int inventoryItemImageColumnIndex = cursor.getColumnIndex(DbContract.TableInfo.
                    COLUMN_ITEM_IMAGE);

            String itemName = cursor.getString(inventoryItemNameColumnIndex);
            int itemPrice = cursor.getInt(inventoryItemPriceColumnIndex);
            int itemQuantity = cursor.getInt(inventoryItemQuantityColumnIndex);
            String itemImage = cursor.getString(inventoryItemImageColumnIndex);

            this.itemName.setText(itemName);
            this.itemPrice.setText(Integer.toString(itemPrice));
            this.itemQuantity.setText(Integer.toString(itemQuantity));
            if(itemImage == null)
                Toast.makeText(this,"Please choose Image",Toast.LENGTH_SHORT).show();
            else {
                selectedImage = Uri.parse(itemImage);
                ((ImageView)findViewById(R.id.inventory_item_image_view)).setImageURI(selectedImage);
            }
        }
    }

    private void showConfirmation() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this item?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        if (currentItem != null) {
            int rowsDeleted = getContentResolver().delete(currentItem, null, null);
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
    public void onLoaderReset(Loader<Cursor> loader) {
        itemName.setText("");
        itemPrice.setText("");
        itemQuantity.setText("");
    }
}
