package com.example.adriana.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.CONTENT_URI;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry._ID;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;

    private static final String DUMMY_PRODUCT_NAME = "Samsung Galaxy S9";
    private static final int DUMMY_PRODUCT_PRICE = 800;
    private static final int DUMMY_PRODUCT_QUANTITY = 5;
    private static final String DUMMY_SUPPLIER_NAME = "Media Galaxy";
    private static final int DUMMY_SUPPLIER_PHONE_NUMBER = 0720123456;

    private ProductCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FloatingActionButton fab = ( FloatingActionButton ) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditProductActivity.class);
                startActivity(intent);
            }
        });

        ListView productsList = ( ListView ) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        productsList.setEmptyView(emptyView);

        adapter = new ProductCursorAdapter(this, null, 0);
        productsList.setAdapter(adapter);

        productsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditProductActivity.class);

                Uri currentUri = ContentUris.withAppendedId(CONTENT_URI, id);
                intent.setData(currentUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    private void insertProduct() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, DUMMY_PRODUCT_NAME);
        values.put(COLUMN_PRODUCT_PRICE, DUMMY_PRODUCT_PRICE);
        values.put(COLUMN_PRODUCT_QUANTITY, DUMMY_PRODUCT_QUANTITY);
        values.put(COLUMN_PRODUCT_SUPPLIER_NAME, DUMMY_SUPPLIER_NAME);
        values.put(COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, DUMMY_SUPPLIER_PHONE_NUMBER);

        Uri newUri = getContentResolver().insert(CONTENT_URI, values);
    }

    private void deleteAllProducts() {
        getContentResolver().delete(CONTENT_URI, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                _ID,
                COLUMN_PRODUCT_NAME,
                COLUMN_PRODUCT_PRICE,
                COLUMN_PRODUCT_QUANTITY};

        return new CursorLoader(this,
                CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_product:
                insertProduct();
                return true;

            case R.id.action_delete_all_products:
                deleteAllProducts();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
