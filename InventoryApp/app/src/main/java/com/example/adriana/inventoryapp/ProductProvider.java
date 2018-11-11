package com.example.adriana.inventoryapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.adriana.inventoryapp.ProductContract.ProductEntry;

import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.TABLE_NAME;

/**
 * Created by Adriana on 8/4/2018.
 */

public class ProductProvider extends ContentProvider {
    public static final int PRODUCTS = 200;
    public static final int PRODUCT_ID = 201;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);

        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    private ProductDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();

        Cursor cursor;

        int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = readableDatabase.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = readableDatabase.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalStateException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;

            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        String productName = values.getAsString(COLUMN_PRODUCT_NAME);
        if (productName == null || productName.isEmpty())
            Toast.makeText(getContext(), R.string.product_name_mandatory, Toast.LENGTH_SHORT).show();

        Double productPrice = values.getAsDouble(COLUMN_PRODUCT_PRICE);
        if (productPrice == 0.0 || productPrice < 0.0 || productPrice == null)
            Toast.makeText(getContext(), R.string.product_price_mandatory, Toast.LENGTH_SHORT).show();

        Integer productQuantity = values.getAsInteger(COLUMN_PRODUCT_QUANTITY);
        if (productQuantity < 0)
            Toast.makeText(getContext(), R.string.positive_quantity_mandatory, Toast.LENGTH_SHORT).show();

        String supplierName = values.getAsString(COLUMN_PRODUCT_SUPPLIER_NAME);
        if (supplierName == null || supplierName.isEmpty())
            Toast.makeText(getContext(), R.string.supplier_name_manadatory, Toast.LENGTH_SHORT).show();

        String supplierPhoneNr = values.getAsString(COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        if (supplierPhoneNr == null || supplierPhoneNr.isEmpty())
            Toast.makeText(getContext(), R.string.supplier_phone_nr_mandatory, Toast.LENGTH_SHORT).show();

        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

        long id = writableDatabase.insert(TABLE_NAME, null, values);
        if (id == -1) {
            Toast.makeText(getContext(), R.string.failed_inserting_product, Toast.LENGTH_SHORT).show();
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

        int deletedRows;
        int match = uriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                deletedRows = writableDatabase.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = writableDatabase.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            Toast.makeText(getContext(), deletedRows + " entries were deleted", Toast.LENGTH_SHORT).show();
        }

        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);

            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }

        if (values.containsKey(COLUMN_PRODUCT_NAME)) {
            String productName = values.getAsString(COLUMN_PRODUCT_NAME);
            if (productName == null || productName.isEmpty())
                Toast.makeText(getContext(), R.string.product_name_mandatory, Toast.LENGTH_SHORT).show();
        }

        if (values.containsKey(COLUMN_PRODUCT_PRICE)) {
            Double productPrice = values.getAsDouble(COLUMN_PRODUCT_PRICE);
            if (productPrice == 0 || productPrice < 0)
                Toast.makeText(getContext(), R.string.product_price_mandatory, Toast.LENGTH_SHORT).show();
        }

        if (values.containsKey(COLUMN_PRODUCT_QUANTITY)) {
            Integer productQuantity = values.getAsInteger(COLUMN_PRODUCT_QUANTITY);
            if (productQuantity < 0)
                Toast.makeText(getContext(), R.string.positive_quantity_mandatory, Toast.LENGTH_SHORT).show();
        }

        if (values.containsKey(COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(COLUMN_PRODUCT_SUPPLIER_NAME);
            if (supplierName == null || supplierName.isEmpty())
                Toast.makeText(getContext(), R.string.supplier_name_manadatory, Toast.LENGTH_SHORT).show();
        }

        if (values.containsKey(COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNr = values.getAsString(COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            if (supplierPhoneNr == null || supplierPhoneNr.isEmpty())
                Toast.makeText(getContext(), R.string.supplier_phone_nr_mandatory, Toast.LENGTH_SHORT).show();
        }

        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        int updatedRows = writableDatabase.update(TABLE_NAME, values, selection, selectionArgs);

        if (updatedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updatedRows;
    }
}
