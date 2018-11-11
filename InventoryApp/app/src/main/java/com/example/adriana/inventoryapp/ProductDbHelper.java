package com.example.adriana.inventoryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.TABLE_NAME;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry._ID;

/**
 * Created by Adriana on 7/22/2018.
 */

public class ProductDbHelper extends SQLiteOpenHelper {
    /**
     * Name of the database
     */
    private static final String DATABASE_NAME = "inventory.db";

    private static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE "
                + TABLE_NAME
                + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + COLUMN_PRODUCT_PRICE + " DOUBLE NOT NULL, "
                + COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL, "
                + COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
